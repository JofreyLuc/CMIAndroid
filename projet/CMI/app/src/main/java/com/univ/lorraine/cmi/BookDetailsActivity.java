package com.univ.lorraine.cmi;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.univ.lorraine.cmi.R;
import com.univ.lorraine.cmi.database.model.Livre;

import java.io.File;

/**
 * Activité affichant les détails d'un livre de la bibliothèque ou d'un résultat de recherche.
 */
public class BookDetailsActivity extends AppCompatActivity {

    // Livre dont on veut afficher les détails
    private Livre livre;
    // Vue de la couverture du livre
    private ImageView cover;
    // Vue des détails du live
    private TextView details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        //Récupération du livre via les extras de l'intent
        Bundle b = getIntent().getBundleExtra("bundle");
        livre = b.getParcelable("livre");

        // Initialisation des vues
        cover = (ImageView) findViewById(R.id.details_cover);
        details = (TextView) findViewById(R.id.details_tags);
        details.setMovementMethod(new ScrollingMovementMethod());

        // Chargement de l'image de couverture
        if (Utilities.hasACover(getApplicationContext(), livre)) {
            Picasso.with(getApplicationContext()).load(new File(Utilities.getBookCoverPath(getApplicationContext(), livre))).into(cover);
        } else {
            Picasso.with(getApplicationContext()).load(R.mipmap.defaultbook).into(cover);
        }

        // Création du texte des détails
        details.setText(processText());
    }

    /**
     * Transforme les données du livre en texte de détails
     * @return Détails en String
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
}
