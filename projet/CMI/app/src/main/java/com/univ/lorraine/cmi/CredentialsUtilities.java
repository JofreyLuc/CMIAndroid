package com.univ.lorraine.cmi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.univ.lorraine.cmi.database.model.Utilisateur;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;
import com.univ.lorraine.cmi.synchronize.CallContainerQueue;

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

    public static String getCurrentToken(Context context){
        return getCurrentUser(context).getToken();
    }

    public static boolean isSignedIn(){
        return (getCurrentUser() != null);
    }

    public static void tryDisconnect(final Context context) {
        // Si il reste des requêtes en attente
        if (!CallContainerQueue.getInstance().isEmpty()) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.ask_disconnect_title)
                    .setMessage(R.string.ask_disconnect_message)
                    .setPositiveButton(R.string.ask_disconnect_yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    CallContainerQueue.getInstance().clear();
                                    disconnect(context);
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
            disconnect(context);
    }

    public static void disconnect(Context context) {
        setDefaults(SHARED_PREFERENCES_USER, null, context);
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
    public static void refreshToken(final Context context) {
        CallMeIshmaelServiceProvider
                .getService()
                .login(getCurrentUser())
                .enqueue(new Callback<Utilisateur>() {
                    @Override
                    public void onResponse(Call<Utilisateur> call, Response<Utilisateur> response) {
                        if (Utilities.isErrorCode(response.code()))
                            return;

                        Utilisateur utilisateur = response.body();
                        if (utilisateur != null) {
                            setCurrentUser(context, utilisateur);
                            initialiseUser(context);
                        }
                    }

                    @Override
                    public void onFailure(Call<Utilisateur> call, Throwable t) {

                    }
                });
    }
}
