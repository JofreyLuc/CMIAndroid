package com.univ.lorraine.cmi.retrofit;

import com.univ.lorraine.cmi.AdresseLocale;

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
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(CallMeIshmaelService.class);
        }
        return service;
    }
}
