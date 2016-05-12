package com.univ.lorraine.cmi;

import android.content.Context;
import android.os.Environment;

import com.univ.lorraine.cmi.database.model.Livre;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.siegmann.epublib.util.IOUtil;

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
     *  Retourne le chemin du fichier epub de ce livre.
     *
     * @param livre Le livre.
     *
     * @return le chemin du fichier epub de ce livre.
     */
    public static String getBookFilePath(Context context, Livre livre) {
        return getBookStoragePath(context)
                + "/" + livre.getIdLivre()
                + "/livre.epub";
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
