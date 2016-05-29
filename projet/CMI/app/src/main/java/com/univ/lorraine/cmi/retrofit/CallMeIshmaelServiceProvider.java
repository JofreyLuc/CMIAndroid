package com.univ.lorraine.cmi.retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.univ.lorraine.cmi.CredentialsUtilities;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Evaluation;
import com.univ.lorraine.cmi.retrofit.jsonAdapter.BibliothequeJsonAdapter;
import com.univ.lorraine.cmi.retrofit.jsonAdapter.BooleanJsonAdapter;
import com.univ.lorraine.cmi.retrofit.jsonAdapter.EvaluationJsonAdapter;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classe permettant d'obtenir un singleton de CallMeIshmaelService.
 */
public class CallMeIshmaelServiceProvider {

    public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    // URL à modifier pour les tests
    private static final String API_URL = AdresseLocale.adresse;

    private static CallMeIshmaelService service;

    private static GsonConverterFactory gsonConverterFactory = createGsonConverterFactory();

    private static HttpLoggingInterceptor httpLoggingInterceptor = createHttpLoggingInterceptor();

    private static Interceptor tokenRefreshInterceptor = createTokenRefreshInterceptor();

    private static GsonConverterFactory createGsonConverterFactory() {
        // Configuration du parser json
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setLenient()
                .setDateFormat(DATE_FORMAT)
                .registerTypeAdapter(Bibliotheque.class, new BibliothequeJsonAdapter())
                .registerTypeAdapter(Evaluation.class, new EvaluationJsonAdapter())
                .registerTypeAdapter(boolean.class, new BooleanJsonAdapter())
                .create();

        return GsonConverterFactory.create(gson);
    }

    private static HttpLoggingInterceptor createHttpLoggingInterceptor() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logInterceptor;
    }

    private static Interceptor createAuthInterceptor(final String token) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("Auth", token).build();
                return chain.proceed(newRequest);
            }
        };
    }

    private static Interceptor createTokenRefreshInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                // Essaie la requête
                Response response = chain.proceed(request);
                // Si le token a expiré
                if (CredentialsUtilities.isTokenExpired(response.code())) {
                    Log.d("LOLLLLLLLLLLLLLLL", "tokenRefresh");
                    // On en récupère un nouveau
                    String token = CredentialsUtilities.refreshToken();
                    if (token != null) {
                        Log.e("token", ""+token);
                        CallMeIshmaelServiceProvider.setHeaderAuth(token);
                        // Crée une nouvelle requête en ajoutant le token au header
                        Request newRequest = request.newBuilder().addHeader("Auth", token).build();
                        // On réessaie la requête
                        response = chain.proceed(newRequest);
                    }
                }
                // On passe la réponse
                return response;
            }
        };
    }

    private static OkHttpClient createClient(Interceptor... interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        for (Interceptor interceptor : interceptors)
            builder.interceptors().add(interceptor);
        return builder.build();
    }

    public static void setHeaderAuth(final String token) {
        // Création de l'AuthInterceptor qui va ajouter le token Auth dans le header de chaque call
        Interceptor authorizationInterceptor = createAuthInterceptor(token);

        // Création du client
        OkHttpClient client = createClient(httpLoggingInterceptor,
                authorizationInterceptor,
                tokenRefreshInterceptor);

        // Création de l'instance retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(gsonConverterFactory)  // On récupère le parser Gson crée au lancement
                .client(client)
                .build();
        service = retrofit.create(CallMeIshmaelService.class);
    }

    public static void unsetHeaderAuth() {
        // On crée à nouveau le service retrofit sans l'interceptor sur le client
        service = null;
        getService();
    }

    public static synchronized CallMeIshmaelService getService() {
        if (service == null) {
            // Création du client avec seulement le logInterceptor et le tokenRefresh
            OkHttpClient client = createClient(httpLoggingInterceptor, tokenRefreshInterceptor);

            // Création de l'instance retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(gsonConverterFactory)  // On récupère le parser Gson crée au lancement
                    .client(client)
                    .build();
            service = retrofit.create(CallMeIshmaelService.class);
        }
        return service;
    }

}
