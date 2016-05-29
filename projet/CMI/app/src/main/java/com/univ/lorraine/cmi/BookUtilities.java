package com.univ.lorraine.cmi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Annotation;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.reader.ReaderActivity;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;
import com.univ.lorraine.cmi.synchronize.CallContainerQueue;
import com.univ.lorraine.cmi.synchronize.callContainer.bibliotheque.BibliothequeDeleteCall;
import com.univ.lorraine.cmi.synchronize.callContainer.bibliotheque.BibliothequeUpdateCall;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alexis on 23/05/2016.
 */
public class BookUtilities {

    public static void supprimerBibliothequeSurServeur(final Bibliotheque bibliotheque) {
        final Long idUser = CredentialsUtilities.getCurrentUser().getIdUtilisateur();
        CallMeIshmaelServiceProvider
                .getService()
                .deleteBibliotheque(idUser, bibliotheque.getIdServeur())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        // Ne rien faire
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // En cas d'échec, on place la requête dans la file d'attente
                        CallContainerQueue.getInstance().enqueue(new BibliothequeDeleteCall(idUser, bibliotheque));
                    }
                });
    }

    public static void updateBibliotheque(final Bibliotheque bibliotheque, final CmidbaOpenDatabaseHelper dbHelper) {
        final Long idUser = CredentialsUtilities.getCurrentUser().getIdUtilisateur();
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // On change la date de modification de la bibliothèque
                bibliotheque.setDateModification(new Date());
                try {
                    // Mise à jour de la bibliothèque sur la base de donnée locale
                    Dao<Bibliotheque, Long> daobibliotheque = dbHelper.getBibliothequeDao();
                    daobibliotheque.update(bibliotheque);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                // Si le livre est présent sur le serveur
                if (!bibliotheque.getLivre().estImporteLocalement()) {
                    // Mise à jour de la bibliothèque sur le serveur
                    CallMeIshmaelServiceProvider
                            .getService()
                            .updateBibliotheque(idUser, bibliotheque.getIdServeur(), bibliotheque)
                            .enqueue(new Callback<ResponseBody>() {

                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    // Ne rien faire
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    // En cas d'échec, on place la requête dans la file d'attente
                                    CallContainerQueue.getInstance().enqueue(new BibliothequeUpdateCall(idUser, bibliotheque));
                                }
                            });
                    }
                return null;
            }
        }.execute();
    }

    public static void sauverBibliotheque(Bibliotheque bibliotheque, CmidbaOpenDatabaseHelper dbHelper) throws SQLException {
        Dao<Bibliotheque, Long> bibliothequeDao = dbHelper.getBibliothequeDao();
        Dao<Livre, Long> livreDao = dbHelper.getLivreDao();

        livreDao.create(bibliotheque.getLivre());
        bibliothequeDao.create(bibliotheque);
        Log.d("BIBLIOTHEQUE", bibliotheque.toString());
    }

    public static void downloadBook(Context context, Livre livre) throws IOException {
        // Téléchargement
        Utilities.downloadFileSync(livre.getLienDLEpub(), Utilities.getBookFilePath(context, livre));
        // Extraction de la couverture
        extractCover(context, livre);
    }

    public static void extractCover(Context context, Livre livre) {
        try {
            String coverPath = Utilities.getBookDirPath(context, livre) + "/cover";
            Book book = new EpubReader().readEpub(new FileInputStream(Utilities.getBookFilePath(context, livre)));
            Resource cover = book.getCoverImage();
            if (cover != null) {
                InputStream coverIS = book.getCoverImage().getInputStream();
                Utilities.copyFile(coverIS, new File(coverPath));
                coverIS.close();
            }
        } catch (IOException e) {
            Log.e("EXC", e.getMessage());
        }
    }

    public static void lancerLecture(Activity activity, Livre livre, CmidbaOpenDatabaseHelper dbHelper) {
        // Récupération de la Bibliothèque de ce livre
        Dao<Bibliotheque, Long> daoBiblio= null;
        Bibliotheque bibliotheque = null;
        try {
            daoBiblio = dbHelper.getBibliothequeDao();
            List<Bibliotheque> resultat;
            resultat = daoBiblio.queryForEq("idLivre", livre);
            // Si il n'y a pas de résultat, on crée la bibliothèque
            if (resultat.isEmpty()) {
                bibliotheque = new Bibliotheque(livre);
                daoBiblio.create(bibliotheque);
            }
            else
                bibliotheque = resultat.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        lancerLecture(activity, bibliotheque);
    }

    public static void lancerLecture(Activity activity, Bibliotheque bibliotheque) {
        if (bibliotheque != null) {
            Bundle b = new Bundle();
            b.putParcelable("bibliotheque", bibliotheque);
            Intent i = new Intent(activity, ReaderActivity.class);
            i.putExtra("bundle", b);
            activity.startActivityForResult(i, MainActivity.READER_CODE);
        }
    }

    /**
     * Retourne vrai si le livre l existe dans la BDD.
     * @param livre Livre.
     */
    public static boolean isInBdd(Livre livre, CmidbaOpenDatabaseHelper dbHelper){
        try {
            Dao<Livre, Long> daoLivre = dbHelper.getLivreDao();
            // Si le livre est importé localement
            if (livre.estImporteLocalement())
                return (daoLivre.queryForId(livre.getIdLivre()) != null);
            else
                return (!daoLivre.queryForEq(Livre.ID_SERVEUR_FIELD_NAME, livre.getIdServeur()).isEmpty());
        } catch (SQLException e) {
            Log.e("EXC", e.getMessage());
            return false;
        }
    }

}
