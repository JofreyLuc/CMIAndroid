package com.univ.lorraine.cmi;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;

/**
 * Created by alexis on 11/05/2016.
 */
public class MyFilePickerActivity extends AbstractFilePickerActivity<File> {

    public MyFilePickerActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // On affiche un toast informant l'utilisateur qu'il peut choisir des fichiers epub
        Toast.makeText(MyFilePickerActivity.this, "Veuillez sélectionner les livres au format EPUB que vous souhaitez importer", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected AbstractFilePickerFragment<File> getFragment(
            final String startPath, final int mode, final boolean allowMultiple,
            final boolean allowCreateDir) {
        // Only the fragment in this line needs to be changed
        AbstractFilePickerFragment<File> fragment = new MyFilePickerFragment();
        fragment.setArgs(startPath, mode, allowMultiple, allowCreateDir);
        return fragment;
    }

    public static class MyFilePickerFragment extends FilePickerFragment {

        // Filtre sur les extensions de fichier
        private static final String EXTENSION = ".epub";

        /**
         * Retourne l'extension d'un fichier.
         * @param file Le fichier
         * @return L'extension du fichier, si le fichier n'a pas d'extension, retourne null.
         */
        private String getExtension(@NonNull File file) {
            String path = file.getPath();
            int i = path.lastIndexOf(".");
            if (i < 0) {
                return null;
            } else {
                return path.substring(i);
            }
        }

        @Override
        protected boolean isItemVisible(final File file) {
            boolean ret = super.isItemVisible(file);
            if (ret && !isDir(file) && (mode == MODE_FILE || mode == MODE_FILE_AND_DIR)) {
                String ext = getExtension(file);
                return ext != null && EXTENSION.equalsIgnoreCase(ext);
            }
            return ret;
        }
    }
}
