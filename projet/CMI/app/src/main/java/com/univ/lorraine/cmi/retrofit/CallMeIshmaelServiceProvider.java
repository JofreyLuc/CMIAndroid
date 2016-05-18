package com.univ.lorraine.cmi.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
            // Configuration du parser json
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            service = retrofit.create(CallMeIshmaelService.class);
        }
        return service;
    }
}
