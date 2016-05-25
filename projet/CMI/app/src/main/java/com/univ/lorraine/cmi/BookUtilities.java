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

    public static void ajouterLivreBibliotheque(final Context context, final Livre livre, final CmidbaOpenDatabaseHelper dbHelper) {
        new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                //TODO récupèrer l'idUser
                Long idUser = Long.valueOf(1);
                Bibliotheque bibliotheque = new Bibliotheque(livre);
                try {
                    // On envoie la création de cette bibliothèque au serveur
                    Response<Bibliotheque> response = CallMeIshmaelServiceProvider
                            .getService()
                            .createBibliotheque(idUser, bibliotheque)
                            .execute();

                    // Erreur
                    if (Utilities.isErrorCode(response.code()))
                        return false;

                    Bibliotheque bibliothequeServeur = response.body();
                    if (bibliothequeServeur == null)
                        return false;

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
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(context, "Ajout du livre en cours...", Toast.LENGTH_SHORT).show();
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
        final ProgressDialog progressBar = new ProgressDialog(activity);
        new AsyncTask<Void, Integer, Bibliotheque>() {
            @Override
            protected Bibliotheque doInBackground(Void... params) {
                //TODO récupèrer l'idUser
                Long idUser = Long.valueOf(1);
                Bibliotheque bibliotheque = new Bibliotheque(livre);
                Bibliotheque bibliothequeServeur;
                try {
                    // On envoie la création de cette bibliothèque au serveur
                    Response<Bibliotheque> response = CallMeIshmaelServiceProvider
                            .getService()
                            .createBibliotheque(idUser, bibliotheque)
                            .execute();

                    publishProgress(33);

                    // Erreur
                    if (Utilities.isErrorCode(response.code()))
                        return null;

                    bibliothequeServeur = response.body();
                    if (bibliothequeServeur == null)
                        return null;

                    publishProgress(66);

                    bibliothequeServeur.setLivre(livre);
                    Log.d("TESTTTTTTTTTTTT", "idBib avant : " + bibliothequeServeur.getIdBibliotheque());

                    // On sauvegarde le livre et la bibliothèque dans la BDD locale
                    sauverBibliotheque(bibliothequeServeur, dbHelper);
                    Log.d("TESTTTTTTTTTTTT", "idBibapres : " + bibliothequeServeur.getIdBibliotheque());

                    // On télécharge le livre sur l'appareil
                    downloadBook(activity, livre);

                } catch (IOException e) {
                    return null;
                } catch (SQLException e) {
                    return null;
                }
                return bibliothequeServeur;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setMessage("Connexion au serveur...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.setIndeterminate(true);
                progressBar.setProgress(0);
                //progressBar.setCancelable(true);
                progressBar.setCanceledOnTouchOutside(false);
                /*progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });*/
                progressBar.show();
            }

            protected void onProgressUpdate(Integer... progress) {
                if (progress[0] <= 33)
                    progressBar.setMessage("Connexion au serveur...");
                else if (progress[0] <= 66)
                    progressBar.setMessage("Ajout du livre...");
                else
                    progressBar.setMessage("Téléchargement du fichier epub...");
            }

            @Override
            protected void onPostExecute(Bibliotheque bibliotheque) {
                String erreur = "L'ajout du livre " + livre.getTitre() + " a échoué";
                if (bibliotheque == null) {
                    progressBar.hide();
                    Toast.makeText(activity, erreur, Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setMessage("Lancement de la lecture...");
                    // On lance la lecture
                    lancerLecture(activity, bibliotheque);
                    progressBar.hide();
                }
            }
        }.execute();
    }

    public static void supprimerBibliothequeSurServeur(final Bibliotheque bibliotheque) {
        //TODO récupèrer l'idUser
        final Long idUser = Long.valueOf(1);
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
        //TODO récupèrer l'idUser
        final Long idUser = Long.valueOf(1);
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
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
        Log.d("TESTTTTTTTTTTTT", "idBib : " + bibliotheque.getIdBibliotheque());
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
