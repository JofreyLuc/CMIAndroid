package com.univ.lorraine.cmi.retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classe permettant d'obtenir un singleton de CallMeIshmaelService.
 */
public class CallMeIshmaelServiceProvider {

    // URL à modifier pour les tests
    private static final String API_URL = AdresseLocale.adresse;

    private static CallMeIshmaelService service;

    public static synchronized CallMeIshmaelService getService() {
        if (service == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            // Configuration du parser json
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setLenient()
                    .registerTypeAdapter(Bibliotheque.class, new BibliothequeSerializer())
                    .registerTypeAdapter(Bibliotheque.class, new BibliothequeDeserializer())
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
            service = retrofit.create(CallMeIshmaelService.class);
        }
        return service;
    }

    private static class BibliothequeSerializer implements JsonSerializer<Bibliotheque> {

        @Override
        public JsonElement serialize(Bibliotheque src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonObject json = new JsonObject();
            json.addProperty(Bibliotheque.ID_SERVEUR_JSON_NAME, src.getIdServeur());
            json.addProperty(Bibliotheque.LIVRE_JSON_NAME, src.getLivre().getIdServeur());
            json.addProperty(Bibliotheque.POSITION_LECTURE_JSON_NAME, src.getPositionLecture());
            json.addProperty(Bibliotheque.DATE_MODIFICATION_JSON_NAME, src.getDateModification().toString());
            return json;
        }
    }

    private static class BibliothequeDeserializer implements JsonDeserializer<Bibliotheque> {

        @Override
        public Bibliotheque deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jobject = (JsonObject) json;
            JsonElement elem;

            /*Long idServeur;
            elem = jobject.get(Bibliotheque.ID_SERVEUR_JSON_NAME);
            Log.d("LOLLLLLLLLLLLLL", "jsonelement == null : "+ (elem == null));

            if (elem != null)
                idServeur = elem.getAsLong();
            else
                idServeur = null;*/

            Long idServeur = jobject.get(Bibliotheque.ID_SERVEUR_JSON_NAME).getAsLong();
            Log.d("LOLLLLLLLLL", "idServeur = "+idServeur);

            Long idServeurLivre = jobject.get(Bibliotheque.LIVRE_JSON_NAME).getAsLong();
            double positionLecture = jobject.get(Bibliotheque.POSITION_LECTURE_JSON_NAME).getAsDouble();
            Date dateModif = null;
            try {
                Log.d("date", jobject.get(Bibliotheque.DATE_MODIFICATION_JSON_NAME).getAsString());
                dateModif = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(jobject.get(Bibliotheque.DATE_MODIFICATION_JSON_NAME).getAsString());
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
}
