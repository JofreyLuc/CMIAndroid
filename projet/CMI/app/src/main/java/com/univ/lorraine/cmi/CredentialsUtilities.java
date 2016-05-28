package com.univ.lorraine.cmi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.StringBuilderPrinter;

import com.google.gson.Gson;
import com.univ.lorraine.cmi.database.model.Utilisateur;

/**
 * Created by jyeil_000 on 27/05/2016.
 */
public final class CredentialsUtilities {

    private final static String SHARED_PREFERENCES_USER = "spuser";

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

    public static boolean isSignedIn(Context context){
        return (getCurrentUser(context) != null);
    }

    public static void disconnect(Context context){
        setDefaults(SHARED_PREFERENCES_USER, null, context);
    }
}
