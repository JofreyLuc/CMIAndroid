package com.univ.lorraine.cmi;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.reader.ReaderActivity;

import java.sql.SQLException;

public class EndOfBookActivity extends AppCompatActivity {

    private CmidbaOpenDatabaseHelper dbhelper = null;

    private Bibliotheque bibliotheque;

    private ImageView cover;

    private TextView titre;

    private TextView auteur;

    private TextView evaluer;

    private TextView voir_evaluations;

    private TextView supprimer;

    private TextView retour_debut_livre;

    private TextView retour_biblio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_book);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        bibliotheque = getIntent().getBundleExtra("bundle").getParcelable("bibliotheque");
        final Livre livre = bibliotheque.getLivre();

        cover = (ImageView) this.findViewById(R.id.cover);
        titre = (TextView) this.findViewById(R.id.titre);
        auteur = (TextView) this.findViewById(R.id.auteur);
        evaluer = (TextView) this.findViewById(R.id.evaluer);
        voir_evaluations = (TextView) this.findViewById(R.id.voir_evaluations);
        supprimer = (TextView) this.findViewById(R.id.supprimer);
        retour_debut_livre = (TextView) this.findViewById(R.id.debut_livre);
        retour_biblio = (TextView) this.findViewById(R.id.retour_biblio);

        cover.setImageBitmap(BitmapFactory.decodeFile(Utilities.getBookCoverPath(this, livre)));
        titre.setText(livre.getTitre());
        auteur.setText(livre.getAuteur());

        // On passe à l'activité d'évaluation
        evaluer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EndOfBookActivity.this, "Evaluer", Toast.LENGTH_SHORT).show();
            }
        });

        // On passe à l'activité des détails du livre
        voir_evaluations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putParcelable("livre", livre);
                Intent i = new Intent(getApplicationContext(), BookDetailsActivity.class);
                i.putExtra("bundle", b);
                startActivity(i);
            }
        });

        // On supprime le livre
        supprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EndOfBookActivity.this, "Supprimer", Toast.LENGTH_SHORT).show();
                finish();
                // TODO supprimer
            }
        });

        // On retourne au début du livre
        retour_debut_livre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bibliotheque.setPositionLecture(0.);
                try {
                    Dao<Bibliotheque, Long> daobibliotheque = getHelper().getBibliothequeDao();
                    daobibliotheque.update(bibliotheque);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Bundle b = new Bundle();
                b.putParcelable("bibliotheque", bibliotheque);
                Intent i = new Intent(getApplicationContext(), ReaderActivity.class);
                i.putExtra("bundle", b);
                startActivity(i);
            }
        });

        // On retourne à la bibliothèque
        retour_biblio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        // Swipe de gauche à droite ou touch à gauche
        View view = findViewById(R.id.scrollview);
        view.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                finish();
            }

            public void onTouchLeft() {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
}
