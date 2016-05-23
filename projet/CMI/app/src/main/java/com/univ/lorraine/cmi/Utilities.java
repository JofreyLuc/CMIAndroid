package com.univ.lorraine.cmi;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;

import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.ImageView;


import com.j256.ormlite.dao.Dao;
import com.skytree.epub.IOUtils;
import com.squareup.picasso.Picasso;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.reader.ReaderActivity;
import com.univ.lorraine.cmi.retrofit.FileDownloadService;
import com.univ.lorraine.cmi.retrofit.FileDownloadServiceProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.util.IOUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Classe contenant uniquement des méthodes statiques utilitaires.
 * Cette classe ne doit pas être instancié.
 */
public final class Utilities {

    /**
     * Constructeur privé pour éviter l'instanciation.
     */
    private Utilities() {
    }

    /**
     * Retourne le chemin du dossier de l'application.
     * Crée les dossiers si besoin.
     *
     * @param context Le contexte de l'application.
     *
     * @return le chemin du dossier de l'application.
     */
    public static String getAppStoragePath(Context context) {
        // Définition du chemin pour le dossier de l'application
        File dossier = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName());

        // Création du dossier s'il n'existe pas déjà
        if (!dossier.exists())
            dossier.mkdirs();

        return dossier.getAbsolutePath();
    }

    /**
     * Retourne le chemin du dossier de stockage des fichiers de l'application.
     * Crée les dossiers si besoin.
     *
     * @param context Le contexte de l'application.
     *
     * @return le chemin du dossier de stockage des fichiers de l'application.
     */
    public static String getFilesStoragePath(Context context) {
        // Définition du chemin pour le dossier de stockage des fichiers de l'application
        File dossier = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/files");

        // Création du dossier s'il n'existe pas déjà
        if (!dossier.exists())
            dossier.mkdirs();

        return dossier.getAbsolutePath();
    }

    /**
     * Retourne le chemin du dossier de stockage des fichiers des livres.
     * Crée les dossiers si besoin.
     *
     * @param context Le contexte de l'application.
     *
     * @return le chemin du dossier de stockage des fichiers des livres.
     */
    public static String getBookStoragePath(Context context) {
        // Définition du chemin pour le dossier où seront stockés les fichier des livres
        File dossier = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/files/livres");

        // Création du dossier s'il n'existe pas déjà
        if (!dossier.exists())
            dossier.mkdirs();

        return dossier.getAbsolutePath();
    }

    /**
     *  Retourne le chemin du dossier de ce livre.
     *  Crée les dossiers si besoin.
     *
     * @param livre Le livre.
     *
     * @return le chemin du dossier de ce livre.
     */
    public static String getBookDirPath(Context context, Livre livre) {
        File dossier = new File(getBookStoragePath(context)
                + "/" + livre.getIdLivre());

        // Création du dossier s'il n'existe pas déjà
        if (!dossier.exists())
            dossier.mkdirs();

        return dossier.getAbsolutePath();
    }

    /**
     *  Retourne le chemin du fichier epub de ce livre.
     *  Crée les dossiers si besoin.
     *
     * @param livre Le livre.
     *
     * @return le chemin du fichier epub de ce livre.
     */
    public static String getBookFilePath(Context context, Livre livre) {
        return getBookDirPath(context, livre) + "/livre.epub";
    }

    /**
     *  Retourne le chemin de la couverture de ce livre.
     *  Crée les dossiers si besoin.
     *
     * @param livre Le livre.
     *
     * @return le chemin de la couverture de ce livre.
     */
    public static String getBookCoverPath(Context context, Livre livre) {
        return getBookDirPath(context, livre) + "/cover";
    }

    public static void loadCoverInto(Context context, Livre livre, ImageView view){
        if (Utilities.hasACover(context, livre)) {
            Picasso.with(context)
                    .load(new File(Utilities.getBookCoverPath(context, livre)))
                    .fit()
                    .centerInside()
                    .into(view);
        } else  {
            Picasso.with(context)
                    .load(R.mipmap.defaultbook)
                    .fit()
                    .centerInside()
                    .into(view);
        }
    }

    /**
     * Retourne vrai si ce livre possède une couverture enregistrée
     *
     * @param livre Le livre.
     *
     * @return Un booléen.
     */
    public static boolean hasACover(Context context, Livre livre){
        return (new File(getBookCoverPath(context, livre)).exists());
    }

    /**
     * Copie le fichier source à la destination.
     * Utilise IOUtil de la librairie Apache incluse dans la librairie epublib.
     *
     * @param src La source.
     * @param dst La destination
     */
    public static void copyFile(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            IOUtil.copy(in, out);
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crée un fichier à partir d'un stream.
     * Utilise IOUtil de la librairie Apache incluse dans la librairie epublib.
     *
     * @param in Stream source.
     * @param dst La destination
     */
    public static void copyFile(InputStream in, File dst){
        try {
            OutputStream out = new FileOutputStream(dst);
            IOUtil.copy(in, out);
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprime un fichier ou un dossier de manière récursive (tout le contenu du dossier).
     *
     * @param fileOrDirectory Fichier ou dossier.
     */
    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }


    public static void downloadFileSync(String urlSource, final String pathDest) throws IOException {
        final FileDownloadService downloadService = FileDownloadServiceProvider.getService();
        Call<ResponseBody> call = downloadService.downloadFileWithDynamicUrl(urlSource);
        Response<ResponseBody> response = call.execute();
        File file = new File(pathDest);
        file.mkdirs();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        IOUtils.write(response.body().bytes(), fileOutputStream);
    }

    /**
     * Vérifie si l'appareil dispose d'une connexion internet
     * et affiche une popup de dialogue.
     *
     * @param activity L'activité appelante.
     */
    public static boolean checkNetworkAvailable(Activity activity) {
        boolean networkAvailable;
        networkAvailable = isNetworkAvailable(activity);
        if (! networkAvailable)
            launchingConnection(activity);
        return networkAvailable;
    }

    /**
     * Vérifie si l'appareil dispose d'une connexion internet.
     *
     * @param activity L'activité appelante.
     *
     * @return Un booléen indiquant si la connexion internet est disponible.
     */
    private static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }


    /**
     * Popup de dialogue pour activer internet.
     *
     * @param activity L'activité appelante.
     */
    private static void launchingConnection(final Activity activity) {
        // Alert dialog si pas de connexion internet
        // Choix 1 : rien
        // Choix 2 : Ouverture de la page des parametres pour activer internet
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        alertDialogBuilder
                .setMessage(activity.getString(R.string.connection_popup_message))
                .setCancelable(false)
                .setTitle(activity.getString(R.string.connection_popup_name));

        // Choix 2
        alertDialogBuilder.setPositiveButton(activity.getString(R.string.connection_popup_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });

        // Choix 1
        alertDialogBuilder.setNegativeButton(activity.getString(R.string.connection_popup_quit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialogBuilder.show();
    }

    /**
     * Active ou désactive les interactions (touch, swipe, etc.) avec l'utilisateur dans une activité
     *
     * @param activity L'activité concernée.
     * @param value true : activée, false : désactivée.
     */
    public static void enableUserInput(Activity activity, boolean value) {
        if (value)
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        else
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * Méthode surchargée.
     *
     * @param activity L'activité concernée.
     */
    public static void enableUserInput(Activity activity) {
        enableUserInput(activity, true);
    }

    /**
     * Méthode "surchargée".
     *
     * @param activity L'activité concernée.
     */
    public static void disableUserInput(Activity activity) {
        enableUserInput(activity, false);
    }
}
