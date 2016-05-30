package com.univ.lorraine.cmi.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;

import com.univ.lorraine.cmi.BookUtilities;
import com.univ.lorraine.cmi.CredentialsUtilities;
import com.univ.lorraine.cmi.Utilities;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelService;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by alexis on 27/05/2016.
 */
public abstract class AjouterLivreBibliothequeAsyncTask extends AsyncTask<Void, Integer, Bibliotheque> {

    protected Activity activity;

    protected CmidbaOpenDatabaseHelper dbHelper;

    protected Livre livre;

    private boolean beforeAjoutLivre;

    private boolean beforeTelechargementLivre;

    public AjouterLivreBibliothequeAsyncTask(Activity a, CmidbaOpenDatabaseHelper dbH, Livre l) {
        activity = a;
        dbHelper = dbH;
        livre = l;
        beforeAjoutLivre = false;
        beforeTelechargementLivre = false;
    }

    public boolean isBeforeAjoutLivre() {
        return beforeAjoutLivre;
    }

    public boolean isBeforeTelechargementLivre() {
        return beforeTelechargementLivre;
    }

    @Override
    protected Bibliotheque doInBackground(Void... params) {
        beforeAjoutLivre = false;
        beforeTelechargementLivre = false;
        Long idUser = CredentialsUtilities.getCurrentUserId();
        Bibliotheque bibliotheque = new Bibliotheque(livre);
        try {
            // Si l'utilisateur est connecté
            if (CredentialsUtilities.isSignedIn()) {
                // On envoie la création de cette bibliothèque au serveur
                Response<Bibliotheque> response = CallMeIshmaelServiceProvider
                        .getService()
                        .createBibliotheque(idUser, bibliotheque)
                        .execute();

                // Erreur
                if (Utilities.isErrorCode(response.code()))
                    return null;

                bibliotheque = response.body();
                if (bibliotheque == null)
                    return null;

                bibliotheque.setLivre(livre);
            }
            beforeAjoutLivre = true;
            publishProgress();

            // On sauvegarde le livre et la bibliothèque dans la BDD locale
            BookUtilities.sauverBibliotheque(bibliotheque, dbHelper);

            beforeTelechargementLivre = true;
            publishProgress();

            // On télécharge le livre sur l'appareil
            BookUtilities.downloadBook(activity, livre);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return bibliotheque;
    }

    @Override
    protected abstract void onPreExecute();

    @Override
    protected abstract void onPostExecute(Bibliotheque bibliotheque);

}
