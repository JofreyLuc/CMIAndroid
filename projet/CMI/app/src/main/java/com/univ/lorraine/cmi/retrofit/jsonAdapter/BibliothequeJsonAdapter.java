package com.univ.lorraine.cmi.retrofit.jsonAdapter;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alexis on 25/05/2016.
 */
public class BibliothequeJsonAdapter implements JsonSerializer<Bibliotheque>, JsonDeserializer<Bibliotheque> {

    @Override
    public JsonElement serialize(Bibliotheque src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject json = new JsonObject();
        json.addProperty(Bibliotheque.ID_SERVEUR_JSON_NAME, src.getIdServeur());
        json.addProperty(Bibliotheque.LIVRE_JSON_NAME, src.getLivre().getIdServeur());
        json.addProperty(Bibliotheque.POSITION_LECTURE_JSON_NAME, src.getPositionLecture());
        json.addProperty(Bibliotheque.DATE_MODIFICATION_JSON_NAME, new SimpleDateFormat(CallMeIshmaelServiceProvider.DATE_FORMAT).format(src.getDateModification()));
        return json;
    }

    @Override
    public Bibliotheque deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jobject = (JsonObject) json;

        Long idServeur = jobject.get(Bibliotheque.ID_SERVEUR_JSON_NAME).getAsLong();

        Long idServeurLivre = jobject.get(Bibliotheque.LIVRE_JSON_NAME).getAsLong();
        double positionLecture = jobject.get(Bibliotheque.POSITION_LECTURE_JSON_NAME).getAsDouble();
        Date dateModif = null;
        try {
            Log.d("date", jobject.get(Bibliotheque.DATE_MODIFICATION_JSON_NAME).getAsString());
            dateModif = new SimpleDateFormat(CallMeIshmaelServiceProvider.DATE_FORMAT).parse(jobject.get(Bibliotheque.DATE_MODIFICATION_JSON_NAME).getAsString());
        } catch (ParseException e) {

        }
        Bibliotheque bibliotheque = new Bibliotheque();
        bibliotheque.setIdServeur(idServeur);
        Livre livre = new Livre();
        livre.setIdServeur(idServeurLivre);
        bibliotheque.setLivre(livre);
        bibliotheque.setPositionLecture(positionLecture);
        bibliotheque.setDateModification(dateModif);
        return bibliotheque;
    }
}
