package com.univ.lorraine.cmi;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.squareup.picasso.Picasso;
import com.univ.lorraine.cmi.R;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.reader.ReaderActivity;

import java.io.File;
import java.sql.SQLException;

/**
 * Activité affichant les détails d'un livre de la bibliothèque ou d'un résultat de recherche.
 */
public class BookDetailsActivity extends AppCompatActivity {

    // Helper pour la database
    private CmidbaOpenDatabaseHelper dbhelper;
    // Livre dont on veut afficher les détails
    private Livre livre;
    // Vue de la couverture du livre
    private ImageView cover;
    // Vue des détails du live
    private TextView details;
    // Bouton ajout bibliothèque
    private Button boutonAjout;
    // Bouton lecture
    private Button boutonLecture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        // Récupération du livre via les extras de l'intent
        livre = getIntent().getBundleExtra("bundle").getParcelable("livre");

        // Titre de l'activité
        if (livre.getTitre() != null) setTitle(livre.getTitre());

        // Initialisation des vues
        cover = (ImageView) findViewById(R.id.details_cover);
        details = (TextView) findViewById(R.id.details_tags);
        boutonAjout = (Button) findViewById(R.id.button_add);
        boutonLecture = (Button) findViewById(R.id.button_readNow);

        // Chargement de l'image de couverture
        if (Utilities.hasACover(getApplicationContext(), livre)) {
            Picasso.with(getApplicationContext()).load(new File(Utilities.getBookCoverPath(getApplicationContext(), livre))).fit().centerInside().into(cover);
        } else {
            Picasso.with(getApplicationContext()).load(R.mipmap.defaultbook).fit().centerInside().into(cover);
        }

        // Création du texte des détails
        details.setText(processText());

        final boolean isInBdd = isInBdd(livre);

        // Si livre déjà téléchargé
        if (isInBdd){
            boutonAjout.setEnabled(false);
            boutonAjout.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            boutonAjout.setText(R.string.button_add_inactive);
            boutonLecture.setText(R.string.button_readNow_alt);
        }

        boutonAjout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToDb(livre);
                finish();
            }
        });

        boutonLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInBdd) launchReading(livre);
                else downloadAndRead(livre);
            }
        });
    }

    /**
     * Overriden in order to close the database.
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
     * Retourne le databaseHelper (crée si il n'existe pas).
     * @return dbhelper.
     */
    private CmidbaOpenDatabaseHelper getHelper(){
        if (dbhelper == null){
            dbhelper = OpenHelperManager.getHelper(this, CmidbaOpenDatabaseHelper.class);
        }
        return dbhelper;
    }

    /**
     * Transforme les données du livre en texte de détails.
     * @return Détails en String.
     */
    private String processText(){
        StringBuilder sb = new StringBuilder();
        if (livre.getTitre() != null) sb.append(livre.getTitre() + '\n');
        else sb.append("Titre inconnu\n");

        if (livre.getAuteur() != null) sb.append(livre.getAuteur() + '\n');
        else sb.append("Auteur inconnu\n");

        if (!livre.getGenre().equals("")) sb.append("Genre : " + livre.getGenre() + '\n');

        if (!livre.getDateParution().equals("")) sb.append("Date de parution : " + livre.getDateParution() + '\n');

        if (!livre.getLangue().equals("")) sb.append("Langue : " + livre.getLangue() + '\n');

        if (!livre.getResume().equals("")) sb.append("Résumé : " + livre.getResume() + '\n');

        return sb.toString();
    }

    /**
     * Télécharge un livre, le stocke dans la base de données et lance la lecture de ce livre.
     * @param l Livre à télécharger.
     */
    private void downloadAndRead(Livre l){
        try {
            // Sauvegarde BDD du livre
            addToDb(l);
            // Téléchargement du livre et extraction de la couverture
            String dest = Utilities.getBookStoragePath(this) + '/' + livre.getIdLivre() + "/livre.epub";
            Utilities.downloadFileAsync(l.getLienDLEpub(), dest);
            Utilities.extractCover(dest);

            // Création de la Biblothèque à partir du livre
            Bibliotheque biblio = new Bibliotheque(livre);
            // Sauvegarde de l'objet Bibliothèque correspondant à ce livre
            Dao<Bibliotheque, Long> daobiblio = getHelper().getBibliothequeDao();
            daobiblio.create(biblio);

            launchReading(l);
        } catch (SQLException e){
            Log.e("EXC", e.getMessage());
        }
    }

    /**
     * Lance la lecture d'un livre (retourne à l'activité précédente une fois la lecture quitée).
     * @param l Livre à lire.
     */
    private void launchReading(Livre l){
        try {
            // Récupération de la Bibliothèque de ce livre
            Dao<Bibliotheque, Long> daoBiblio= getHelper().getBibliothequeDao();
            //TEMPORAIRE ?
            Bibliotheque bibliotheque = daoBiblio.queryForEq("idLivre", l).get(0);
            //TEMPORAIRE ?

            // Lancement reader
            Bundle b = new Bundle();
            b.putParcelable("bibliotheque", bibliotheque);
            Intent i = new Intent(getApplicationContext(), ReaderActivity.class);
            i.putExtra("bundle", b);
            startActivityForResult(i, MainActivity.READER_CODE);

            finish();
        } catch (SQLException e) {
            Log.e("EXC", e.getMessage());
        }
    }

    /**
     * Ajoute un livre à la BDD.
     * @param l Livre à ajouter.
     */
    private void addToDb(Livre l){
        try {
            Dao<Livre, Long> daoLivre = getHelper().getLivreDao();
            daoLivre.create(l);
        } catch (SQLException e) {
            Log.e("EXC", e.getMessage());
        }
    }

    /**
     * Retourne vrai si le livre l existe dans la BDD.
     * @param l Livre.
     */
    private boolean isInBdd(Livre l){
        try {
            Dao<Livre, Long> daoLivre = getHelper().getLivreDao();
            return (daoLivre.queryForId(l.getIdLivre()) != null);
        } catch (SQLException e) {
            Log.e("EXC", e.getMessage());
            return false;
        }
    }
}
