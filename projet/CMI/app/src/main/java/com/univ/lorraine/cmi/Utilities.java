package com.univ.lorraine.cmi;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.skytree.epub.IOUtils;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.retrofit.FileDownloadService;
import com.univ.lorraine.cmi.retrofit.FileDownloadServiceProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
     *
     * @param livre Le livre.
     *
     * @return le chemin du dossier de ce livre.
     */
    public static String getBookDirPath(Context context, Livre livre) {
        return getBookStoragePath(context)
                + "/" + livre.getIdLivre();
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
     *  Retourne le chemin de la couverture de ce livre.
     *
     * @param livre Le livre.
     *
     * @return le chemin de la couverture de ce livre.
     */
    public static String getBookCoverPath(Context context, Livre livre) {
        return getBookStoragePath(context)
                + "/" + livre.getIdLivre()
                + "/cover";
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

    public static void downloadFileAsync(String urlSource, final String pathDest) {
        final FileDownloadService downloadService = FileDownloadServiceProvider.getService();

        Call<ResponseBody> call = downloadService.downloadFileWithDynamicUrl(urlSource);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    File file = new File(pathDest);
                    file.mkdirs();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    IOUtils.write(response.body().bytes(), fileOutputStream);
                } catch (IOException e) {
                    Log.e("TEST", "Error while writing file!");
                    Log.e("TEST", e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }

    public static void extractCover(String epubPath){
        try {
            String coverPath = epubPath + "/../cover";
            Book book = new EpubReader().readEpub(new FileInputStream(epubPath));
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
}
