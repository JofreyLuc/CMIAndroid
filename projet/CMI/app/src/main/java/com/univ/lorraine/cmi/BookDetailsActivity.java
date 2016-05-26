package com.univ.lorraine.cmi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.squareup.picasso.Picasso;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Evaluation;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.database.model.Utilisateur;
import com.univ.lorraine.cmi.reader.ReaderActivity;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelService;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    // PopUp pour enter un commentaire
    private Dialog rateDialog;
    // Bouton pour ouvrir la PopUp commentaire
    private FloatingActionButton writeComment;
    // Rating Bar
    private RatingBar ratingBar;
    // TextView cliquable pour envoyer le commentaire
    private TextView envoyer;
// Commentaire
    private EditText comment;

    private RecyclerView evalsView;
    private List<Evaluation> evaluations;

    private Evaluation evaluationPerso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        // Récupération du livre via les extras de l'intent
        Bundle bundle = getIntent().getBundleExtra("bundle");
        livre = bundle.getParcelable("livre");
        boolean demande_evaluation = bundle.getBoolean("evaluer");

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

        final boolean isInBdd = BookUtilities.isInBdd(livre, getHelper());

        // Si livre déjà téléchargé
        if (isInBdd){
            boutonAjout.setEnabled(false);
            boutonAjout.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            boutonAjout.setText(R.string.button_add_inactive);
            boutonLecture.setText(R.string.button_readNow_alt);
        }

        final Activity activity = this;

        boutonAjout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookUtilities.ajouterLivreBibliotheque(activity, livre, getHelper());
                finish();
            }
        });

        boutonLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInBdd) BookUtilities.lancerLecture(activity, livre, getHelper());
                else BookUtilities.ajouterLivreBibliothequeEtLire(activity, livre, getHelper());
            }
        });

        writeComment = (FloatingActionButton) findViewById(R.id.fab);
        // Si le livre est importé localement, on ne peut pas le noter et on affiche pas les commentaires/notes
        if (livre.estImporteLocalement()) {
            writeComment.setVisibility(View.GONE);
        } else {
            if (writeComment != null) {
                writeComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        demanderAEvaluer();
                    }
                });
            }
        }
        rateDialog = new Dialog(BookDetailsActivity.this);
        rateDialog.setContentView(R.layout.rate_layout);
        rateDialog.setCancelable(true);
        ratingBar = (RatingBar) rateDialog.findViewById(R.id.dialog_ratingbar);
        TextView dialogTitle = (TextView) rateDialog.findViewById(R.id.rate_dialog_title);
        dialogTitle.setText(livre.getTitre());
        envoyer = (TextView) rateDialog.findViewById(R.id.rate_dialog_submit);
        comment = (EditText) rateDialog.findViewById(R.id.edit_commentaire);
        evaluations = new ArrayList<>();
        setEvaluations();

        if (demande_evaluation)
            demanderAEvaluer();

        evalsView = (RecyclerView) findViewById(R.id.evals_recyclerView);
        evalsView.setAdapter(new EvalRecyclerAdapter(getApplicationContext(), evaluations));
        evalsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // On rafraîchit la note
        setNoteLivre();
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

    private void demanderAEvaluer() {
        // Si il y a déjà un commentaire de cet utilisateur
        if (evaluationPerso != null) {
            ratingBar.setRating((float) evaluationPerso.getNote());
            comment.setText(evaluationPerso.getCommentaire());
        }
        envoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (evaluationPerso == null)
                    envoyerEvaluationLivre((double) ratingBar.getRating(), comment.getText().toString());
                else
                    modifierEvaluationLivre((double) ratingBar.getRating(), comment.getText().toString());
                rateDialog.dismiss();
            }
        });
        rateDialog.show();
    }

    public void modifierEvaluationLivre(double rating, String comment) {
        //TODO idUser
        Long idUser = (long) 1;
        evaluationPerso.setNote(rating);
        evaluationPerso.setCommentaire(comment);
        evaluationPerso.setDateModification(new Date());
        CallMeIshmaelServiceProvider
                .getService()
                .updateEvaluation(idUser, livre.getIdServeur(), evaluationPerso.getIdEvaluation(), evaluationPerso)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        // Erreur dans la réponse
                        if (Utilities.isErrorCode(response.code()))
                            onFailure(call, new IOException());
                        Toast.makeText(BookDetailsActivity.this, "Votre évaluation a bien été modifiée", Toast.LENGTH_SHORT).show();
                        // On rafraîchit la note
                        setNoteLivre();
                        // et les évaluations
                        setEvaluations();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("ERR", "", t);
                        Toast.makeText(BookDetailsActivity.this, "Erreur lors de l'envoi de l'évaluation au serveur", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void envoyerEvaluationLivre(double rating, String comment) {
        //TODO idUser
        Long idUser = (long) 1;
        Evaluation evaluation = new Evaluation(idUser, livre, rating, comment);
        // On envoie l'évaluation au serveur
        CallMeIshmaelServiceProvider
                .getService()
                .createEvaluation(idUser, livre.getIdServeur(), evaluation)
                .enqueue(new Callback<Evaluation>() {
                    @Override
                    public void onResponse(Call<Evaluation> call, Response<Evaluation> response) {
                        // Erreur dans la réponse
                        if (Utilities.isErrorCode(response.code()))
                            onFailure(call, new IOException());

                        Evaluation evaluation = response.body();
                        if (evaluation == null)
                            onFailure(call, new IOException());

                        Toast.makeText(BookDetailsActivity.this, "Votre évaluation a bien été enregistrée", Toast.LENGTH_SHORT).show();
                        // On rafraîchit la note
                        setNoteLivre();
                        // et les évaluations
                        setEvaluations();
                    }

                    @Override
                    public void onFailure(Call<Evaluation> call, Throwable t) {
                        Log.e("ERR", "", t);
                        Toast.makeText(BookDetailsActivity.this, "Erreur lors de l'envoi de l'évaluation au serveur", Toast.LENGTH_SHORT).show();
                    }
                });
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

        if (livre.getGenre() != null && !livre.getGenre().equals("")) sb.append("Genre : " + livre.getGenre() + '\n');

        if (livre.getDateParution() != null && !livre.getDateParution().equals("")) sb.append("Date de parution : " + livre.getDateParution() + '\n');

        if (livre.getLangue() != null && !livre.getLangue().equals("")) sb.append("Langue : " + livre.getLangue() + '\n');

        if (livre.getResume() != null && !livre.getResume().equals("")) sb.append("Résumé : " + livre.getResume() + '\n');

        return sb.toString();
    }

    private void setNoteLivre() {
        CallMeIshmaelServiceProvider
                .getService()
                .getLivre(livre.getIdServeur())
                .enqueue(new Callback<Livre>() {
                    @Override
                    public void onResponse(Call<Livre> call, Response<Livre> response) {
                        // Erreur dans la réponse
                        if (Utilities.isErrorCode(response.code()))
                            onFailure(call, new IOException());

                        Livre l = response.body();
                        if (l == null)
                            onFailure(call, new IOException());

                        livre = l;
                        rafraichirAffichageNote();
                    }

                    @Override
                    public void onFailure(Call<Livre> call, Throwable t) {
                        Log.e("EXCEVALS", "", t);
                        rafraichirAffichageNote();
                    }
                });
    }

    private void rafraichirAffichageNote() {
        // On rafraîchit la note et le nombre d'évaluations
        float moyenne = (float) Math.round(livre.getNoteMoyenne() * 10) / 10;
        ((RatingBar) findViewById(R.id.rating_bar)).setRating(moyenne);
        ((TextView) findViewById(R.id.note)).setText(moyenne+"");
        ((TextView) findViewById(R.id.nb_evals)).setText("("+livre.getNombreEvaluations()+" évaluations)");
        Log.e("LIVRE", livre.toString());
    }

    private void setEvaluations(){
        //TODO récupérer idUser
        final Long idUser = (long) 1;
        final CallMeIshmaelService cmiService = CallMeIshmaelServiceProvider.getService();

        Call<List<Evaluation>> call = cmiService.getEvaluations(livre.getIdServeur(), true);
        call.enqueue(new Callback<List<Evaluation>>() {
            @Override
            public void onResponse(Call<List<Evaluation>> call, Response<List<Evaluation>> response) {
                if (response.body() != null) {
                    evaluations = response.body();
                    // On regarde si le commentaire de l'utilisateur se trouve parmi les commentaires
                    boolean match = false;
                    Iterator<Evaluation> it = evaluations.iterator();
                    while (it.hasNext() && !match) {
                        Evaluation eval = it.next();
                        if (eval.getUtilisateur().getIdUtilisateur().equals(idUser)) {
                            evaluationPerso = eval;
                            it.remove();
                            match = true;
                        }
                    }
                    // Si l'utilisateur a déjà mis un commentaire
                    if (evaluationPerso != null) {
                        findViewById(R.id.evaluer_text).setVisibility(View.GONE);
                        View evalPersoView = findViewById(R.id.eval_perso);
                        evalPersoView.setVisibility(View.VISIBLE);
                        evalPersoView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                demanderAEvaluer();
                            }
                        });
                        ((TextView) evalPersoView.findViewById(R.id.eval_rater))
                                .setText("Mon évaluation");
                        ((RatingBar) evalPersoView.findViewById(R.id.eval_rating_bar))
                                .setRating((float) evaluationPerso.getNote());
                        ((TextView) evalPersoView.findViewById(R.id.eval_eval))
                                .setText(evaluationPerso.getCommentaire());

                        // On met à jour le dialogue
                        ratingBar.setRating((float) evaluationPerso.getNote());
                        comment.setText(evaluationPerso.getCommentaire());
                    }
                    else {
                        findViewById(R.id.eval_perso).setVisibility(View.GONE);
                        findViewById(R.id.evaluer_text).setVisibility(View.VISIBLE);
                        findViewById(R.id.evaluer_text).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                demanderAEvaluer();
                            }
                        });
                    }

                    evalsView.setAdapter(new EvalRecyclerAdapter(getApplicationContext(), evaluations));
                }
            }

            @Override
            public void onFailure(Call<List<Evaluation>> call, Throwable t) {
                Log.e("EXCEVALS", "", t);
            }
        });
    }
}
