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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.reader.EpubManipulator;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import nl.siegmann.epublib.util.IOUtil;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {


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

    // fonction qui initialise un menu de lActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_biblio_perso, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // fonction qui gere les actions des items des menus de lActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                // DO SOMETHING
                //Toast.makeText(getApplicationContext(), "action_new", Toast.LENGTH_LONG).show();
                testFilePicker();
                return true;
            case R.id.overflow1:
                // DO SOMETHING
                Toast.makeText(getApplicationContext(), "overflow1", Toast.LENGTH_LONG).show();
                return true;
            case R.id.overflow2:
                // DO SOMETHING
                Toast.makeText(getApplicationContext(), "overflow2", Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;
        }
    }

    // fonction qui gere les actions des items du menu de chaque livre
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_supp:
                // DO SOMETHING
                Toast.makeText(getApplicationContext(), "action_supp", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_details:
                // DO SOMETHING
                Toast.makeText(getApplicationContext(), "action_details", Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            // Choix d'un livre (fichier epub) via le Filepicker
            case FILEPICKER_CODE :
                // Résultat OK
                if (resultCode == Activity.RESULT_OK) {
                    // Tableau contenant les fichiers epubs
                    File[] epubs;
                    // Sélection multiple de fichier
                    if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                        ClipData clip = data.getClipData();
                        epubs = new File[clip.getItemCount()];
                        if (clip != null)
                            for (int i = 0; i < clip.getItemCount(); i++)
                                epubs[i] = new File(clip.getItemAt(i).getUri().getPath());
                    }
                    // Sélection unique de fichier
                    else {
                        epubs = new File[1];
                        epubs[0] = new File(data.getData().getPath());
                    }
                    // On importe le/les epub(s)
                    importEpubs(epubs);
                }
                break;
        }
    }

    public void copy(File src, File dst) {
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

    /**
     * Retourne le chemin du dossier de stockage des epubs.
     * Crée les dossiers si besoin.
     *
     * @return le chemin du dossier de stockage des epubs.
     */
    public String getEpubStoragePath() {
        // Définition du chemin pour le dossier où seront stockés les epubs
        File dossier = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/epubs");

        // Création du dossier s'il n'existe pas déjà
        if (!dossier.exists())
            dossier.mkdirs();

        return dossier.getAbsolutePath();
    }

    private void importEpubs(File[] epubs) {
        String path = "";
        FileInputStream fs = null;
        Book book = null;
        for (int i = 0; i < epubs.length; i++) {
            //ProgressDialog.show(this, "Import", "Import epub").setCancelable(false);
            // On copie le fichier epub dans le dossier dédié de l'application
            String newFilePath = getEpubStoragePath() + "/" + epubs[i].getName();
            copy(epubs[i], new File(newFilePath));
            path = epubs[i].getPath();
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
            String dateString;
            for (nl.siegmann.epublib.domain.Date d : meta.getDates()) {
                if (d.getEvent() == nl.siegmann.epublib.domain.Date.Event.PUBLICATION) date = d;
            }
            if (date == null) dateString = "";
            else dateString = date.toString();

            delim = "";
            StringBuilder resumes = new StringBuilder();
            for (String r : meta.getDescriptions()) {
                titres.append(delim);
                resumes.append(r);
                delim = ", ";
            }
            Dao<Livre, Long> daolivre = getHelper().getLivreDao();
            daolivre.create(new Livre(titres.toString(), auteurs.toString(), meta.getLanguage(), types.toString(), dateString, resumes.toString(), 2, "", ""));

            List<Livre> ll = daolivre.queryForAll();
            for (Livre l : ll){
                Log.e("DIS", l.toString());
            }
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


    // inflate le menu de gestion des livres (suppression, details)
    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_livre);
        popup.show();
    }

    /**
     * Returns the database helper (created if null)
     * @return dbhelper
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

    /**********************
     * classe qui customise les items de la gridview
     */

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

