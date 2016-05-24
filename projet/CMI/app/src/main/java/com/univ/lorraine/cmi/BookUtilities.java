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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by alexis on 23/05/2016.
 */
public class BookUtilities {

    public static void ajouterLivreBibliotheque(final Context context, final Livre livre, final CmidbaOpenDatabaseHelper dbHelper) {
        new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                //TODO récupèrer l'idUser
                Long idUser = Long.valueOf(1);
                Bibliotheque bibliotheque = new Bibliotheque(livre);
                try {
                    // On envoie la création de cette bibliothèque au serveur
                    Bibliotheque bibliothequeServeur = CallMeIshmaelServiceProvider
                            .getService()
                            .createBibliotheque(idUser, bibliotheque)
                            .execute()
                            .body();

                    bibliothequeServeur.setLivre(livre);

                    // On sauvegarde le livre et la bibliothèque dans la BDD locale
                    sauverBibliotheque(bibliothequeServeur, dbHelper);

                    // On télécharge le livre sur l'appareil
                    downloadBook(context, livre);

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                String resultat;
                if (success)
                    resultat = "Le livre " + livre.getTitre() + " a été ajouté à votre bibliothèque";
                else
                    resultat = "L'ajout du livre " + livre.getTitre() + " a échoué";
                Toast.makeText(context, resultat, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    public static void ajouterLivreBibliothequeEtLire(final Activity activity, final Livre livre, final CmidbaOpenDatabaseHelper dbHelper) {
        final ProgressDialog progress = new ProgressDialog(activity);
        new AsyncTask<Void, Integer, Bibliotheque>() {
            @Override
            protected Bibliotheque doInBackground(Void... params) {
                //TODO récupèrer l'idUser
                Long idUser = Long.valueOf(1);
                Bibliotheque bibliotheque = new Bibliotheque(livre);
                try {
                    // On envoie la création de cette bibliothèque au serveur
                    Bibliotheque bibliothequeServeur = CallMeIshmaelServiceProvider
                            .getService()
                            .createBibliotheque(idUser, bibliotheque)
                            .execute()
                            .body();

                    bibliothequeServeur.setLivre(livre);

                    // On sauvegarde le livre et la bibliothèque dans la BDD locale
                    sauverBibliotheque(bibliothequeServeur, dbHelper);

                    // On télécharge le livre sur l'appareil
                    downloadBook(activity, livre);

                } catch (IOException e) {
                    return null;
                } catch (SQLException e) {
                    return null;
                }
                return bibliotheque;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setMessage("Ajout du livre...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.setProgress(0);
                //progress.setCancelable(true);
                progress.setCanceledOnTouchOutside(false);
                /*progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });*/
                progress.show();
            }

            @Override
            protected void onPostExecute(Bibliotheque bibliotheque) {
                String erreur = "L'ajout du livre " + livre.getTitre() + " a échoué";
                if (bibliotheque == null)
                    Toast.makeText(activity, erreur, Toast.LENGTH_SHORT).show();
                else {
                    progress.setMessage("Lancement de la lecture...");
                    // On lance la lecture
                    lancerLecture(activity, bibliotheque);
                }
            }
        }.execute();
    }

    public static void sauverBibliotheque(Bibliotheque bibliotheque, CmidbaOpenDatabaseHelper dbHelper) throws SQLException {
        Dao<Bibliotheque, Long> bibliothequeDao = dbHelper.getBibliothequeDao();
        Dao<Livre, Long> livreDao = dbHelper.getLivreDao();

        livreDao.create(bibliotheque.getLivre());
        bibliothequeDao.create(bibliotheque);
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
            bibliotheque = daoBiblio.queryForEq("idLivre", livre).get(0);
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
     * @param l Livre.
     */
    public static boolean isInBdd(Livre l, CmidbaOpenDatabaseHelper dbHelper){
        try {
            Dao<Livre, Long> daoLivre = dbHelper.getLivreDao();
            return (daoLivre.queryForId(l.getIdLivre()) != null);
        } catch (SQLException e) {
            Log.e("EXC", e.getMessage());
            return false;
        }
    }

}
