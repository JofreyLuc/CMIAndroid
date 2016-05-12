package com.univ.lorraine.cmi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.SQLException;
import java.util.ArrayList;
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

    private ArrayList<Resource> covers;
    private GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        covers = new ArrayList<>();
        Log.e("INFO", "Covers début");
        setCovers();
        Log.e("INFO", "Covers fin");
        gridView = (GridView) findViewById(R.id.grid);
        gridView.setAdapter(new ImageAdapter(this));
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
                pickEpubFiles();   // On demande à l'utilisateur de choisir les fichiers epub à importer.
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

    // inflate le menu de gestion des livres (suppression, details)
    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_livre);
        popup.show();
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
            return covers.size();
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
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=inflater.inflate(R.layout.grid_item, parent, false);
            TextView label=(TextView)row.findViewById(R.id.icon_text);
            label.setText(titles[position]);
            ImageView icon;
            icon=(ImageView)row.findViewById(R.id.icon_image);
            applyInputStream(icon, position);
            return row;
        }
    }

    private void applyInputStream(ImageView iv, int pos){
        try {
            InputStream is = covers.get(pos).getInputStream();
            iv.setImageBitmap(BitmapFactory.decodeStream(is));
            is.close();
        } catch (IOException e){
            Log.e("EXC", e.getMessage());
        }
    }

    private void setCovers(){
        try {
            covers.clear();
            Dao<Livre, Long> daolivre = getHelper().getLivreDao();
            List<Livre> ll = daolivre.queryForAll();
            for (Livre l : ll) {
                FileInputStream fs = new FileInputStream(Utilities.getBookFilePath(getApplicationContext(), l));
                Book book = new EpubReader().readEpub(fs);
                covers.add(book.getCoverImage());
                fs.close();
            }
        } catch (SQLException e){
            Log.e("EXC", e.getMessage());
        } catch (IOException e){
            Log.e("Exc", e.getMessage());
        }
    }

    /**
     * Lance l'activité du FilePicker et demande à l'utilisateur de sélectionner un/des livre(s) au format epub.
     * Est appelé lorsque l'utilisateur demande à importe un/des livres(s).
     */
    private void pickEpubFiles() {
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

    /**
     * Importe les livres à partir de leurs fichiers epub.
     * Insert le livre dans la base de données locale et copie le fichier epub dans le dossier dédié de l'application.
     *
     * @param epubs Les fichier epub des livres.
     */
    private void importEpubs(File[] epubs) {
        String epubFilePath = "";
        FileInputStream fs = null;
        Book book = null;
        Livre livre = null;
        // Pour chaque fichier epub
        for (int i = 0; i < epubs.length; i++) {
            //ProgressDialog.show(this, "Import", "Import epub").setCancelable(false);
            // On va enregistrer les metadata du livre dans la base de données
            epubFilePath = epubs[i].getPath();
            // Création de l'objet book à partir du fichier epub
            try {
                fs = new FileInputStream(epubFilePath);
                book = (new EpubReader().readEpub(fs));
            } catch (IOException e) {
                Log.e("EXC", e.getMessage());
            }
            // Création du livre à partir de l'objet book
            livre = new Livre(book);
            // Sauvegarde du livre dans la base de données
            try {
                Dao<Livre, Long> daolivre = getHelper().getLivreDao();
                daolivre.create(livre);
            } catch (SQLException e){
                Log.e("EXC", e.getMessage());
            }

            // On va crée le dossier du livre et copier le fichier epub à l'intérieur
            String epubFileNewName = "livre.epub";
            String dirPath = Utilities.getBookStoragePath(this) + "/" + livre.getIdLivre();
            String newFilePath = dirPath + "/" + epubFileNewName;
            new File(dirPath).mkdirs();                             // Création du dossier
            Utilities.copyFile(epubs[i], new File(newFilePath));    // Copie du fichier

            // Test
            List<Livre> ll = null;
            Dao<Livre, Long> daolivre = null;
            try {
                daolivre = getHelper().getLivreDao();
            ll = daolivre.queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for (Livre l : ll){
                Log.e("DIS", l.toString());
            }
        }
        setCovers();
        gridView.setAdapter(new ImageAdapter(this));

    }

}

