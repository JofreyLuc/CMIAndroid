package com.univ.lorraine.cmi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.reader.EpubManipulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import java.io.File;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

public class MainActivity extends AppCompatActivity {

    private CmidbaOpenDatabaseHelper dbhelper = null;
    private static final int FILEPICKER_CODE = 0;

    Integer[] imageIDs = {
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
    };

    String[] titles = {
            "book1",
            "book2",
            "book3",
            "book4",
            "book5",
            "book6",
            "book7",
            "book8",
            "book9",
            "book10",
            "book11",
            "book12",
            "book13",
            "book14",
            "book15",
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridView gridView = (GridView) findViewById(R.id.grid);
        gridView.setAdapter(new ImageAdapter(this));
        // Création du dossier interne de l'app
        getApplicationContext().getDir("CallMeIshmael", Context.MODE_PRIVATE);

       testFilePicker();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            // Choix d'un livre (fichier epub) via le Filepicker
            case FILEPICKER_CODE :
                // Résultat OK
                if (resultCode == Activity.RESULT_OK) {
                    // Tableau contenant le/les uris
                    Uri[] uriArray;
                    // Sélection multiple de fichier
                    if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                        ClipData clip = data.getClipData();
                        uriArray = new Uri[clip.getItemCount()];
                        if (clip != null)
                            for (int i = 0; i < clip.getItemCount(); i++)
                                uriArray[i] = clip.getItemAt(i).getUri();
                    }
                    // Sélection unique de fichier
                    else {
                        uriArray = new Uri[1];
                        uriArray[0] = data.getData();
                    }
                    // On importe le/les epub(s)
                    importEpubs(uriArray);
                }
                break;
        }
    }

    private void importEpubs(Uri[] epubUris) {
        String path = "";
        String fileName = "";
        FileInputStream fs = null;
        Book book = null;
        for (int i = 0; i < epubUris.length; i++) {
            //ProgressDialog.show(this, "Import", "Import epub").setCancelable(false);
            // import livre local
            path = epubUris[i].getPath();
            fileName = epubUris[i].getLastPathSegment();
            try {
                fs = new FileInputStream(path);
                book = (new EpubReader().readEpub(fs));
                saveBook(book);
            } catch (IOException e){
                Log.e("EXC", e.getMessage());
            }
        }
    }

    private void saveBook(Book book){
        try {
            Metadata meta = book.getMetadata();
            StringBuilder titres = new StringBuilder();
            String delim = "";
            for (String t : meta.getTitles()) {
                titres.append(delim);
                titres.append(t);
                delim = ", ";
            }

            delim = "";
            StringBuilder auteurs = new StringBuilder();
            for (Author a : meta.getAuthors()) {
                titres.append(delim);
                if (a.getFirstname() != null) {
                    auteurs.append(a.getFirstname() + " ");
                }
                if (a.getLastname() != null) {
                    auteurs.append(a.getLastname());
                }
                delim = ", ";
            }

            delim = "";
            StringBuilder types = new StringBuilder();
            for (String g : meta.getTypes()) {
                titres.append(delim);
                types.append(g);
                delim = ", ";
            }

            nl.siegmann.epublib.domain.Date date = null;
            for (nl.siegmann.epublib.domain.Date d : meta.getDates()) {
                if (d.getEvent() == nl.siegmann.epublib.domain.Date.Event.PUBLICATION) date = d;
            }

            delim = "";
            StringBuilder resumes = new StringBuilder();
            for (String r : meta.getDescriptions()) {
                titres.append(delim);
                resumes.append(r);
                delim = ", ";
            }
            Dao<Livre, Long> daolivre = getHelper().getLivreDao();
            daolivre.create(new Livre(titres.toString(), auteurs.toString(), meta.getLanguage(), types.toString(), date.toString(), resumes.toString(), 2, "", ""));
        } catch (SQLException e){
            Log.e("EXC", e.getMessage());
        }
    }

    private void testFilePicker() {
        Intent i = new Intent(this, MyFilePickerActivity.class);

        // Options
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(i, FILEPICKER_CODE);
    }

    /**
     * Returns the database helper (created if null)
     * @return
     */
    private CmidbaOpenDatabaseHelper getHelper(){
        if (dbhelper == null){
            dbhelper = OpenHelperManager.getHelper(this, CmidbaOpenDatabaseHelper.class);
        }
        return dbhelper;
    }

    /**
     * Overriden in order to close the database
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbhelper != null){
            OpenHelperManager.releaseHelper();
            dbhelper = null;
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;

        public ImageAdapter(Context c)
        {
            context = c;
        }


        @Override
        public int getCount() {
            return imageIDs.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView icon;
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=inflater.inflate(R.layout.grid_item, parent, false);
            TextView label=(TextView)row.findViewById(R.id.icon_text);
            label.setText(titles[position]);
            icon=(ImageView)row.findViewById(R.id.icon_image);
            icon.setImageResource(imageIDs[position]);
            return row;
        }
    }
}

