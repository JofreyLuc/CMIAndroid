package com.univ.lorraine.cmi.retrofit.jsonAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.univ.lorraine.cmi.Utilities;
import com.univ.lorraine.cmi.database.model.Evaluation;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.database.model.Utilisateur;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alexis on 25/05/2016.
 */
public class EvaluationJsonAdapter implements JsonSerializer<Evaluation>, JsonDeserializer<Evaluation> {

    @Override
    public JsonElement serialize(Evaluation src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject json = new JsonObject();
        json.addProperty(Evaluation.ID_SERVEUR_JSON_NAME, src.getIdEvaluation());
        json.addProperty(Evaluation.UTILISATEUR_JSON_NAME, src.getUtilisateur().getIdUtilisateur());
        json.addProperty(Evaluation.LIVRE_JSON_NAME, src.getLivre().getIdServeur());
        json.addProperty(Evaluation.COMMENTAIRE_JSON_NAME, src.getCommentaire());
        json.addProperty(Evaluation.NOTE_JSON_NAME, src.getNote());
        json.addProperty(Evaluation.DATE_MODIFICATION_JSON_NAME, new SimpleDateFormat(CallMeIshmaelServiceProvider.DATE_FORMAT).format(src.getDateModification()));
        return json;
    }

    @Override
    public Evaluation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jobject = (JsonObject) json;

        Long idServeur = jobject.get(Evaluation.ID_SERVEUR_JSON_NAME).getAsLong();
        Long idLivre = jobject.get(Evaluation.LIVRE_JSON_NAME).getAsLong();
        String commentaire = jobject.get(Evaluation.COMMENTAIRE_JSON_NAME).getAsString();
        double note = jobject.get(Evaluation.NOTE_JSON_NAME).getAsDouble();
        Date dateModif = null;
        try {
            dateModif = new SimpleDateFormat(CallMeIshmaelServiceProvider.DATE_FORMAT).parse(jobject.get(Evaluation.DATE_MODIFICATION_JSON_NAME).getAsString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setIdEvaluation(idServeur);
        Long idUtilisateur;
        String pseudo = "";
        boolean possibiliteSuivi = false;
        // Si on reçoit l'utilisateur sous forme de nested object
        if (jobject.has(Evaluation.UTILISATEUR_JSON_OBJECT)) {
            JsonObject utilisateurJson = (JsonObject) jobject.getAsJsonObject().get(Evaluation.UTILISATEUR_JSON_OBJECT);
            idUtilisateur = utilisateurJson.get(Utilisateur.ID_UTILISATEUR_JSON_NAME).getAsLong();
            pseudo = utilisateurJson.get(Utilisateur.PSEUDO_JSON_NAME).getAsString();
            possibiliteSuivi = utilisateurJson.get(Utilisateur.POSSIBILITE_SUIVI_JSON_NAME).getAsBoolean();
        }
        else
            idUtilisateur = jobject.get(Utilisateur.ID_UTILISATEUR_JSON_NAME).getAsLong();

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUtilisateur(idUtilisateur);
        utilisateur.setPseudo(pseudo);
        utilisateur.setPossibiliteSuivi(possibiliteSuivi);
        evaluation.setUtilisateur(utilisateur);

        Livre livre = new Livre();
        livre.setIdServeur(idLivre);
        evaluation.setLivre(livre);

        evaluation.setCommentaire(commentaire);
        evaluation.setNote(note);
        evaluation.setDateModification(dateModif);

        return evaluation;
    }

}
