package com.univ.lorraine.cmi.reader;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.univ.lorraine.cmi.BookDetailsActivity;
import com.univ.lorraine.cmi.MainActivity;
import com.univ.lorraine.cmi.R;
import com.univ.lorraine.cmi.Utilities;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.reader.listener.OnSwipeTouchListener;

public class EndOfBookActivity extends AppCompatActivity {

    private CmidbaOpenDatabaseHelper dbhelper = null;

    private Bibliotheque bibliotheque;

    private ImageView cover;

    private TextView titre;

    private TextView auteur;

    private TextView evaluer;

    private TextView voir_evaluations;

    private TextView retour_debut_livre;

    private TextView retour_biblio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_book);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // On cache la status bar (fullscreen)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        bibliotheque = getIntent().getBundleExtra("bundle").getParcelable("bibliotheque");
        final Livre livre = bibliotheque.getLivre();

        cover = (ImageView) this.findViewById(R.id.cover);
        titre = (TextView) this.findViewById(R.id.titre);
        auteur = (TextView) this.findViewById(R.id.auteur);
        evaluer = (TextView) this.findViewById(R.id.evaluer);
        voir_evaluations = (TextView) this.findViewById(R.id.voir_evaluations);
        retour_debut_livre = (TextView) this.findViewById(R.id.debut_livre);
        retour_biblio = (TextView) this.findViewById(R.id.retour_biblio);

        cover.setImageBitmap(BitmapFactory.decodeFile(Utilities.getBookCoverPath(this, livre)));
        titre.setText(livre.getTitre());
        auteur.setText(livre.getAuteur());

        // Si le livre est local, on cache Evaluer et voir évaluations
        if (livre.estImporteLocalement()) {
            evaluer.setEnabled(false);
            evaluer.setVisibility(View.GONE);
            voir_evaluations.setEnabled(false);
            voir_evaluations.setVisibility(View.GONE);
        }

        // On passe à l'activité d'évaluation
        evaluer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putParcelable("livre", livre);
                b.putBoolean("evaluer", true);
                Intent i = new Intent(getApplicationContext(), BookDetailsActivity.class);
                i.putExtra("bundle", b);
                startActivity(i);
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

        // On retourne au début du livre
        retour_debut_livre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", "retour_debut");
                setResult(EndOfBookActivity.RESULT_OK, resultIntent);
                finish();
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
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }

            public void onTouchLeft() {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", "end_of_book");
        setResult(EndOfBookActivity.RESULT_CANCELED, resultIntent);
        super.onBackPressed();
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
