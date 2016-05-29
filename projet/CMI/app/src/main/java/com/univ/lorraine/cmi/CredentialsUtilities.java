package com.univ.lorraine.cmi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Utilisateur;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;
import com.univ.lorraine.cmi.synchronize.CallContainerQueue;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jyeil_000 on 27/05/2016.
 */
public final class CredentialsUtilities {

    private final static String SHARED_PREFERENCES_USER = "spuser";

    private static Utilisateur currentUser;

    private static void setDefaults(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static Utilisateur getCurrentUser(Context context){
        return new Gson().fromJson(getDefaults(SHARED_PREFERENCES_USER, context), Utilisateur.class);
    }

    public static void setCurrentUser(Context context, Utilisateur utilisateur){
        setDefaults(SHARED_PREFERENCES_USER, new Gson().toJson(utilisateur), context);
    }

    public static String getCurrentToken(){
        if (getCurrentUser() == null) return null;
        return getCurrentUser().getToken();
    }

    public static boolean isSignedIn(){
        return (getCurrentUser() != null);
    }

    public static void tryDisconnect(final Context context, final CmidbaOpenDatabaseHelper dbHelper) {
        // Si il reste des requêtes en attente
        if (!CallContainerQueue.getInstance().isEmpty()) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.ask_disconnect_title)
                    .setMessage(R.string.ask_disconnect_message)
                    .setPositiveButton(R.string.ask_disconnect_yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    CallContainerQueue.getInstance().clear();
                                    disconnect(context, dbHelper);
                                }
                            })
                    .setNegativeButton(R.string.confirmation_suppression_no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Ne rien faire
                                }
                            })
                    .show();
        }
        else
            disconnect(context, dbHelper);
    }

    public static void disconnect(Context context, CmidbaOpenDatabaseHelper dbHelper) {
        ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Suppression du contenu lié au compte...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BookUtilities.removeOnlineContent(context, dbHelper);
        progress.hide();
        setDefaults(SHARED_PREFERENCES_USER, null, context);
        initialiseUser(context);
    }

    public static Utilisateur getCurrentUser(){
        return currentUser;
    }

    public static Long getCurrentUserId(){
        if (getCurrentUser() == null) return null;
        return getCurrentUser().getIdUtilisateur();
    }

    public static void initialiseUser(Context context){
        try {
            currentUser = getCurrentUser(context);
        } catch (NullPointerException e) {
            currentUser = null;
        }
    }

    public static boolean isTokenExpired(int code) {
        return code == 401;
    }

    /**
     * Méthode utilisée pour rafraîchir le token en le demandant au serveur.
     */
    public static String refreshToken() {
        Utilisateur utilisateur;
        try {
            Response<Utilisateur> response = CallMeIshmaelServiceProvider
                    .getService()
                    .login(getCurrentUser())
                    .execute();

            if (Utilities.isErrorCode(response.code()))
                return null;

            utilisateur = response.body();
            if (utilisateur == null)
                return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        currentUser = utilisateur;

        return utilisateur.getToken();
    }
}
