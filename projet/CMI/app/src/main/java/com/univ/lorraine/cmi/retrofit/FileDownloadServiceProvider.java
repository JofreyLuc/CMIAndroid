package com.univ.lorraine.cmi.retrofit;

import com.univ.lorraine.cmi.AdresseLocale;

import retrofit2.Retrofit;

/**
 * Classe permettant d'obtenir un singleton de FileDownloadService.
 */
public class FileDownloadServiceProvider {

    // Url inutile pour FileDownloadService
    private static final String API_URL = AdresseLocale.adresse;

    private static FileDownloadService service;

    public static synchronized FileDownloadService getService() {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .build();

            service = retrofit.create(FileDownloadService.class);
        }
        return service;
    }

}
