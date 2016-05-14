package com.univ.lorraine.cmi;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.squareup.picasso.Picasso;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.reader.ReaderActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private CmidbaOpenDatabaseHelper dbhelper = null;

    private static final int FILEPICKER_CODE = 0;

    private List<Bibliotheque> bibliotheques;

    private GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bibliotheques = new ArrayList<>();
        setBibliotheques();
        gridView = (GridView) findViewById(R.id.grid);
        gridView.setAdapter(new ImageAdapter(this));
        gridView.setOnItemClickListener(this);
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
     * Retourne le databaseHelper (crée si il n'existe pas)
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

    // Fonction qui gère l'action lors d'un clic sur un livre

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        lancerLecture((Bibliotheque) view.getTag());
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

    // inflate le menu de gestion des livres (suppression, details)
    public void showMenu(View v) {
        // On récupère la bibliotheque lié à cet item de la gridview
        final Bibliotheque bibliotheque = (Bibliotheque)((View)v.getParent().getParent()).getTag();
        final Livre livre = bibliotheque.getLivre();
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    // Visualisation des détails du livre
                    case R.id.action_details:
                        Bundle b = new Bundle();
                        b.putParcelable("livre", livre);
                        Intent i = new Intent(getApplicationContext(), BookDetailsActivity.class);
                        i.putExtra("bundle", b);
                        startActivity(i);
                        return true;
                    // Page d'évaluation
                    case R.id.action_evaluate:
                        // DO SOMETHING
                        Toast.makeText(getApplicationContext(), "action_evaluate" + livre.getIdLivre(), Toast.LENGTH_LONG).show();
                        return true;
                    // Suppression du livre
                    case R.id.action_supp:
                        // DO SOMETHING
                        Toast.makeText(getApplicationContext(), "action_supp" + livre.getIdLivre(), Toast.LENGTH_LONG).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        // On affiche le sous-menu Evaluer si le livre n'est pas un livre importé localement
        if (!livre.estImporteLocalement()) popup.getMenu().getItem(R.id.action_evaluate).setVisible(true);
        popup.inflate(R.menu.menu_livre);
        popup.show();
    }

    /**********************
     * classe qui customise les items de la gridview
     */

    public class ImageAdapter extends BaseAdapter {
        private Context context;

        public ImageAdapter(Context c) { context = c; }

        @Override
        public int getCount() { return bibliotheques.size(); }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) { return position; }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=inflater.inflate(R.layout.grid_item, parent, false);
            TextView label=(TextView)row.findViewById(R.id.icon_text);
            Bibliotheque bibliotheque = bibliotheques.get(position);
            Livre livre = bibliotheque.getLivre();
            // On bind la bibliotheque à la view
            row.setTag(bibliotheque);
            // Récupération du titre
            label.setText(livre.getTitre() + '\n' + livre.getAuteur());
            // Récupération de la couverture
            ImageView icon=(ImageView)row.findViewById(R.id.icon_image);
            if (Utilities.hasACover(getApplicationContext(), livre)) {
                Picasso.with(context).load(new File(Utilities.getBookCoverPath(getApplicationContext(), livre))).fit().centerInside().into(icon);
            } else {
                Picasso.with(context).load(R.mipmap.defaultbook).fit().centerInside().into(icon);
            }
            return row;
        }
    }

    /**
     * Met à jour la liste de bibliothèques actuelle (pour couvertures et titres)
     */
    private void setBibliotheques() {
        try {
            bibliotheques.clear();
            Dao<Bibliotheque, Long> daobibliotheque = getHelper().getBibliothequeDao();
            List<Bibliotheque> lb = daobibliotheque.queryForAll();
            for (Bibliotheque b : lb) {
                bibliotheques.add(b);
            }
            for (Bibliotheque b : bibliotheques)
            Log.e("SKY", b.getLivre().toString());
        } catch (SQLException e) {
            Log.e("EXC", e.getMessage());
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
        try {
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
                fs = new FileInputStream(epubFilePath);
                book = (new EpubReader().readEpub(fs));
                // Création du livre à partir de l'objet book
                livre = new Livre(book);
                // Sauvegarde du livre dans la base de données
                Dao<Livre, Long> daolivre = getHelper().getLivreDao();
                daolivre.create(livre);

                // Création de la Biblothèque à partir du livre
                Bibliotheque biblio = new Bibliotheque(livre);
                // Sauvegarde de l'objet Bibliothèque correspondant à ce livre
                Dao<Bibliotheque, Long> daobiblio = getHelper().getBibliothequeDao();
                daobiblio.create(biblio);

                // On va créer le dossier du livre et copier le fichier epub à l'intérieur
                String epubFileNewName = "livre.epub";
                String dirPath = Utilities.getBookStoragePath(this) + "/" + livre.getIdLivre();
                String newFilePath = dirPath + "/" + epubFileNewName;
                new File(dirPath).mkdirs();                             // Création du dossier
                Utilities.copyFile(epubs[i], new File(newFilePath));    // Copie du fichier

                // Extraction de la couverture dans le dossier crée précédemment
                String coverPath = dirPath + "/cover";
                Resource cover = book.getCoverImage();
                if (cover != null) {
                    InputStream coverIS = book.getCoverImage().getInputStream();
                    Utilities.copyFile(coverIS, new File(coverPath));
                    coverIS.close();
                } else {
                    // Génération d'image automatique ?
                }
            }
        } catch (SQLException e) {
            Log.e("Exc", e.getMessage());
        } catch (IOException e) {
            Log.e("Exc", e.getMessage());
        }
        // Mise à jour de la liste de bibliothèques et des vues
        setBibliotheques();
        gridView.setAdapter(new ImageAdapter(this));
    }

    void lancerLecture(Bibliotheque bibliotheque) {
        Bundle b = new Bundle();
        b.putParcelable("bibliotheque", bibliotheque);
        Intent i = new Intent(getApplicationContext(), ReaderActivity.class);
        i.putExtra("bundle", b);
        startActivity(i);
    }
}

