package com.univ.lorraine.cmi.retrofit;

import com.univ.lorraine.cmi.database.model.Annotation;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.database.model.Utilisateur;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface Retrofit pour l'API REST CallMeIshmael.
 */
public interface CallMeIshmaelService {

    // Utilisateur

    @GET("users/{idUser}")
    Call<Utilisateur> getUtilisateur(@Path("idUser") Long idUtilisateur);

    @PUT("users")
    Call<ResponseBody> updateUtilisateur(@Body Utilisateur utilisateur);

    // Livre

    @GET("books/{idBook}")
    Call<Livre> getLivre(@Path("idBook") Long idLivre);

    // Recherche de livres

    @GET("books")
    Call<List<Livre>> searchLivre(@Query("titre") String titre, @Query("author") String auteur, @Query("language") String langue, @Query("start-limit") Integer startLimit, @Query("end-limit") Integer endLimit);

    // Bibliotheque

    @GET("users/{idUser}/library")
    Call<List<Bibliotheque>> getBibliotheques(@Path("idUser") Long idUtilisateur);

    @GET("users/{idUser}/library/{idLibrary}")
    Call<List<Bibliotheque>> getBibliotheque(@Path("idUser") Long idUtilisateur, @Path("idLibrary") Long idBibliotheque);

    @POST("users/{idUser}/library")
    Call<Bibliotheque> createBibliotheque(@Path("idUser") Long idUtilisateur, @Body Bibliotheque bibliotheque);

    @PUT("users/{idUser}/library")
    Call<ResponseBody> updateBibliotheque(@Path("idUser") Long idUtilisateur, @Body Bibliotheque bibliotheque);

    @DELETE("users/{idUser}/library/{idLibrary}")
    Call<ResponseBody> deleteBibliotheque(@Path("idUser") Long idUtilisateur, @Path("idLibrary") Long idBibliotheque);

    // Notes

    @GET("users/{idUser}/library/{idLibrary}/notes")
    Call<List<Annotation>> getAnnotations(@Path("idUser") Long idUtilisateur, @Path("idLibrary") Long idBibliotheque);

    @GET("users/{idUser}/library/{idLibrary}/notes/{idNote}")
    Call<Annotation> getAnnotation(@Path("idUser") Long idUtilisateur, @Path("idLibrary") Long idBibliotheque, @Path("idNote") Long idAnnotation);

    @POST("users/{idUser}/library/{idLibrary}/notes")
    Call<Annotation> createAnnotation(@Path("idUser") Long idUtilisateur, @Path("idLibrary") Long idBibliotheque, @Body Annotation annotation);

    @PUT("users/{idUser}/library/{idLibrary}/notes")
    Call<ResponseBody> updateAnnotation(@Path("idUser") Long idUtilisateur, @Path("idLibrary") Long idBibliotheque, @Body Annotation annotation);

    @DELETE("users/{idUser}/library/{idLibrary}/notes/{idNote}")
    Call<ResponseBody> deleteAnnotation(@Path("idUser") Long idUtilisateur, @Path("idLibrary") Long idBibliotheque, @Path("idNote") Long idAnnotation);

    // Top 10

    @GET("top10")
    Call<List<Livre>> getTop10();
}
