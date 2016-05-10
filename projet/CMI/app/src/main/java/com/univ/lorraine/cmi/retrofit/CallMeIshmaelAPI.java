package com.univ.lorraine.cmi.retrofit;

import retrofit2.http.GET;

/**
 * Interface de l'API REST pour Retrofit
 */
public interface CallMeIshmaelAPI {
    // Exemple
    //@GET("/livre/{id}")
    //Call<Livre> getLivre(@Path("id") String id);
    // @Path("id") String id signifie que {id} dans l'url sera remplacé par l'id dans la méthode
}
