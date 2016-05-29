package com.univ.lorraine.cmi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Annotation;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelService;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;
import com.univ.lorraine.cmi.synchronize.CallContainerQueue;
import com.univ.lorraine.cmi.synchronize.ServerSynchronizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // Helper permettant d'interagir avec la database
    private CmidbaOpenDatabaseHelper dbhelper = null;

    // Codes de résultats d'activités (publics afin de pouvoir être utilisés dans d'autres activités)
    public static final int FILEPICKER_CODE = 0;
    
    public static final int READER_CODE = 1;

    // Liste des Bibliothèques des livres de l'utilisateur
    private List<Bibliotheque> bibliotheques;
    // Liste des Livres du top 10
    private List<Livre> livresTop;

    // GridView permettant d'afficher les livres
    private GridView gridView;
    // RecyclerView permettant d'afficher le top 10
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Charge la file de requêtes en attente
        CallContainerQueue.getInstance().load(getSharedPreferences(getPackageName(), Context.MODE_PRIVATE));

        setContentView(R.layout.activity_main);
        setTitle(R.string.main_activity_label_alt);

        // Initialisation bibliothèques
        bibliotheques = new ArrayList<>();
        setBibliotheques();

        gridView = (GridView) findViewById(R.id.grid);
        gridView.setAdapter(new ImageAdapter(this));
        gridView.setOnItemClickListener(this);

        livresTop = new ArrayList<>();
        setLivresTop();

        recyclerView = (RecyclerView) findViewById(R.id.top_recyclerview);
        recyclerView.setAdapter(new TopRecyclerAdapter(livresTop, getApplicationContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        CredentialsUtilities.initialiseUser(getApplicationContext());
        if (CredentialsUtilities.isSignedIn())
            CallMeIshmaelServiceProvider.setHeaderAuth(CredentialsUtilities.getCurrentToken());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Sauvegarde la file de requêtes en attente
        CallContainerQueue.getInstance().save(getSharedPreferences(getPackageName(), Context.MODE_PRIVATE));
        CredentialsUtilities.setCurrentUser(getApplicationContext(), CredentialsUtilities.getCurrentUser());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // On synchronise les données du serveur
        new ServerSynchronizer(this, getHelper()) {
            @Override
            protected void onPreExecute() {
                Toast.makeText(MainActivity.this, "Tentative de synchronisation avec le serveur...", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(Boolean succes) {
                rafraichirAffichageBibliotheque();

                if (succes)
                    Toast.makeText(MainActivity.this, "Synchronisation effectuée", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Échec de la synchronisation", Toast.LENGTH_SHORT).show();
            }
        }.execute();
        invalidateOptionsMenu();
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

    /**
     * Initialise un menu de l'ActionBar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_biblio_perso, menu);

        if (CredentialsUtilities.isSignedIn()){
            menu.findItem(R.id.action_login).setVisible(false);
            menu.findItem(R.id.action_signup).setVisible(false);
            menu.findItem(R.id.action_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.action_login).setVisible(true);
            menu.findItem(R.id.action_signup).setVisible(true);
            menu.findItem(R.id.action_disconnect).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Lance la lecture lors d'un clic sur un livre.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BookUtilities.lancerLecture(this, (Bibliotheque) view.getTag());
    }

    /**
     * Gère le menu des options
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.action_new:
                // Choix les fichiers epub à importer
                pickEpubFiles();
                return true;
            case R.id.action_search:
                // Lancement de la page de recherche d'epubs
                i = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(i);
                return true;
            case R.id.action_login:
                // Lancement de la page de connexion
                i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                return true;
            case R.id.action_signup:
                // Lancement de la page d'inscription
                i = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(i);
                return true;
            case R.id.action_disconnect:
                CredentialsUtilities.tryDisconnect(getApplicationContext());
                CallMeIshmaelServiceProvider.unsetHeaderAuth();
                invalidateOptionsMenu();
            default:
                return false;
        }
    }

    /**
     * Inflate le menu de gestion des livres
     */
    public void showMenu(View v) {
        // On récupère la bibliotheque lié à cet item de la gridview
        final Bibliotheque bibliotheque = (Bibliotheque)((View)v.getParent().getParent()).getTag();
        final Livre livre = bibliotheque.getLivre();

        // Popup des options
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Bundle b;
                Intent i;
                switch (item.getItemId()) {
                    case R.id.action_details:
                        // Visualisation des détails du livre
                        b = new Bundle();
                        b.putParcelable("livre", livre);
                        i = new Intent(getApplicationContext(), BookDetailsActivity.class);
                        i.putExtra("bundle", b);
                        startActivity(i);
                        return true;
                    case R.id.action_evaluate:
                        // Page d'évaluation
                        b = new Bundle();
                        b.putParcelable("livre", livre);
                        b.putBoolean("evaluer", true);
                        i = new Intent(getApplicationContext(), BookDetailsActivity.class);
                        i.putExtra("bundle", b);
                        startActivity(i);
                        return true;

                    case R.id.action_supp:
                        // Suppression du livre
                        demanderConfirmationSuppressionLivre(bibliotheque);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.inflate(R.menu.menu_livre);
        // On affiche le sous-menu Evaluer si le livre n'est pas un livre importé localement
        if (!livre.estImporteLocalement())
            popup.getMenu().findItem(R.id.action_evaluate).setVisible(true);
        popup.show();
    }

    /**
     * Recharge la liste de bibliothèques actuelle (pour couvertures et titres)
     */
    private void setBibliotheques() {
        try {
            bibliotheques.clear();
            Dao<Bibliotheque, Long> daobibliotheque = getHelper().getBibliothequeDao();
            List<Bibliotheque> lb = daobibliotheque.queryForAll();
            bibliotheques.addAll(lb);
        } catch (SQLException e) {
            Log.e("EXC", e.getMessage());
        }
    }

    private void setLivresTop() {
        final CallMeIshmaelService cmiservice = CallMeIshmaelServiceProvider.getService();

        Call<List<Livre>> call = cmiservice.getTop10();
        call.enqueue(new Callback<List<Livre>>() {
            @Override
            public void onResponse(Call<List<Livre>> call, Response<List<Livre>> response) {
                boolean newTop = false;
                List<Livre> resLivres = response.body();
                if (livresTop.isEmpty()) {
                    newTop = true;
                } else {
                    for (Livre l : resLivres) {
                        for (Livre m : livresTop) {
                            if (l.getIdServeur() != m.getIdServeur()) {
                                newTop = true;
                            }
                        }
                        if (newTop) break;
                    }
                }
                if (newTop) {
                    livresTop = new ArrayList<>(resLivres);
                    findViewById(R.id.loading_top).setVisibility(View.GONE);
                    recyclerView.setAdapter(new TopRecyclerAdapter(livresTop, getApplicationContext()));
                }
            }

            @Override
            public void onFailure(Call<List<Livre>> call, Throwable t) {
                findViewById(R.id.loading_top).setVisibility(View.GONE);
                TextView tv = (TextView) findViewById(R.id.loading_top_text);
                tv.setText("Impossible de charger le top 10.");
            }
        });
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

    /**
     * Décrit le comportement de l'appli lors du retour à la page principale depuis d'autres activités
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {

            // Choix d'un livre (fichier epub) via le Filepicker
            case FILEPICKER_CODE :
                // Résultat OK
                if (resultCode == Activity.RESULT_OK) {
                    // Tableau contenant les fichiers epubs
                    File[] epubs;

                    // Sélection multiple de fichiers
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

            // Retour depuis le reader
            case READER_CODE :
                // On met à jour les bibliothèques afin que les changements soient pris en compte
                rafraichirAffichageBibliotheque();
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
            String epubFilePath;
            FileInputStream fs;
            Book book;
            Livre livre;
            // Pour chaque fichier epub
            for (int i = 0; i < epubs.length; i++) {

                // Création de l'objet book à partir du fichier epub
                epubFilePath = epubs[i].getPath();
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
                BookUtilities.extractCover(getApplicationContext(), livre);
            }
        } catch (SQLException e) {
            Log.e("Exc", e.getMessage());
        } catch (IOException e) {
            Log.e("Exc", e.getMessage());
        }
        // Mise à jour de la liste de bibliothèques et des vues
        rafraichirAffichageBibliotheque();
    }

    /**
     * Gère la suppression d'un livre de la bibliothèque, en demandant confirmation
     * à l'utilisateur.
     * @param bibliotheque La Bibliothèque contenant le livre à supprimer
     */
    private void demanderConfirmationSuppressionLivre(final Bibliotheque bibliotheque) {
        Livre livre = bibliotheque.getLivre();
        new AlertDialog.Builder(this)
            .setTitle(getResources().getString(R.string.confirmation_suppression_title))
            .setMessage(getResources().getString(R.string.confirmation_suppression_message_start)
                    + livre.getTitre()
                    + getResources().getString(R.string.confirmation_suppression_message_end))
            .setPositiveButton(getResources().getString(R.string.confirmation_suppression_yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            supprimerLivreBibliotheque(bibliotheque);
                        }
                    })
            .setNegativeButton(getResources().getString(R.string.confirmation_suppression_no),
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Rien à faire
                }
            })
            .show();
    }

    private void supprimerLivreBibliotheque(Bibliotheque bibliotheque) {
        Livre livre = bibliotheque.getLivre();
        try {
            // Suppression de la bibliothèque sur le serveur
            if (!livre.estImporteLocalement())  // Si le livre n'est pas importé localement
                BookUtilities.supprimerBibliothequeSurServeur(bibliotheque);

            // Suppression du dossier local du livre (contenant l'epub et la couverture)
            Utilities.deleteRecursive(new File(Utilities.getBookDirPath(getApplicationContext(), livre)));

            // Suppression des annotations de ce livre
            Dao<Annotation, Long> daoannotation = getHelper().getAnnotationDao();
            List<Annotation> listeAnnotations =
                    daoannotation.queryBuilder().where()
                            .eq(Annotation.BIBLIOTHEQUE_FIELD_NAME, bibliotheque.getIdBibliotheque())
                            .query();
            daoannotation.delete(listeAnnotations);

            // Suppression du livre
            Dao<Livre, Long> daolivre = getHelper().getLivreDao();
            daolivre.delete(livre);

            // Suppression de l'objet bibliothèque
            Dao<Bibliotheque, Long> daobibliotheque = getHelper().getBibliothequeDao();
            daobibliotheque.delete(bibliotheque);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // On met à jour l'affichage de la bibliothèque
        rafraichirAffichageBibliotheque();
    }

    /**
     * Classe gérant les items de la GridView
     */
    public class ImageAdapter extends BaseAdapter {
        // Contexte de l'application (principale)
        private Context context;

        // Nombre de lignes de base du titre/auteur
        private static final int TITLE_DEFAULT_LINES = 4;
        private static final int AUTHOR_DEFAULT_LINES = 2;

        public ImageAdapter(Context c) { context = c; }

        @Override
        public int getCount() {
            return bibliotheques.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View grid_item = inflater.inflate(R.layout.grid_item, parent, false);

            final TextView titre = (TextView) grid_item.findViewById(R.id.titre);
            titre.setMaxLines(TITLE_DEFAULT_LINES);

            final TextView auteur = (TextView) grid_item.findViewById(R.id.auteur);
            auteur.setMaxLines(AUTHOR_DEFAULT_LINES);

            // Récupération de la bibliothèque "actuelle"
            Bibliotheque bibliotheque = bibliotheques.get(position);
            Livre livre = bibliotheque.getLivre();

            // On bind la bibliotheque à la view
            grid_item.setTag(bibliotheque);
            // Récupération du titre
            titre.setText(livre.getTitre());

            // Ajustement des lignes
            titre.post(new Runnable() {
                @Override
                public void run() {
                    titre.setMaxLines(titre.getLineCount());
                    auteur.setMaxLines(AUTHOR_DEFAULT_LINES + (TITLE_DEFAULT_LINES - titre.getMaxLines()));
                    auteur.setMinLines(AUTHOR_DEFAULT_LINES + (TITLE_DEFAULT_LINES - titre.getMaxLines()));
                }
            });

            // Récupération de l'auteur
            auteur.setText(livre.getAuteur());

            // Récupération de la couverture
            ImageView icon=(ImageView)grid_item.findViewById(R.id.icon_image);
            Utilities.loadCoverInto(context, livre, icon);

            // Barre de progression de lecture
            // Barre de progression de lecture
            ProgressBar BarreProgressionLecture = (ProgressBar)grid_item.findViewById(R.id.book_reading_progress_bar);
            int progressionLecture = (int)(bibliotheque.getPositionLecture() * 100);
            BarreProgressionLecture.setProgress(progressionLecture);

            // On cache le bandeau web si le livre est importe localement
            if (livre.estImporteLocalement())
                grid_item.findViewById(R.id.bandeau_web).setVisibility(View.GONE);

            return grid_item;
        }
    }

    private void rafraichirAffichageBibliotheque() {
        setBibliotheques();
        gridView.setAdapter(new ImageAdapter(this));    // Màj des vues
    }
}

