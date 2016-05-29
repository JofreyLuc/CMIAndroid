package com.univ.lorraine.cmi.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Evaluation;
import com.univ.lorraine.cmi.retrofit.jsonAdapter.BibliothequeJsonAdapter;
import com.univ.lorraine.cmi.retrofit.jsonAdapter.BooleanJsonAdapter;
import com.univ.lorraine.cmi.retrofit.jsonAdapter.EvaluationJsonAdapter;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

    public static void setHeaderAuthorization(final String token) {
        // Define the interceptor, add authentication headers
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("Authorization", token).build();
                return chain.proceed(newRequest);
            }
        };

        // Add the interceptor to OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);

        // Ajout du logInterceptor
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.interceptors().add(logInterceptor);

        OkHttpClient client = builder.build();

        // Configuration du parser json
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setLenient()
                .setDateFormat(DATE_FORMAT)
                .registerTypeAdapter(Bibliotheque.class, new BibliothequeJsonAdapter())
                .registerTypeAdapter(Evaluation.class, new EvaluationJsonAdapter())
                .registerTypeAdapter(boolean.class, new BooleanJsonAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        service = retrofit.create(CallMeIshmaelService.class);
    }

    public static void unsetHeaderAuthorization(){
        // Configuration du parser json
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setLenient()
                .setDateFormat(DATE_FORMAT)
                .registerTypeAdapter(Bibliotheque.class, new BibliothequeJsonAdapter())
                .registerTypeAdapter(Evaluation.class, new EvaluationJsonAdapter())
                .registerTypeAdapter(boolean.class, new BooleanJsonAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        service = retrofit.create(CallMeIshmaelService.class);
    }

    public static synchronized CallMeIshmaelService getService() {
        if (service == null) {

            // Ajout du logInterceptor
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            // Configuration du parser json
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setLenient()
                    .registerTypeAdapter(Bibliotheque.class, new BibliothequeJsonAdapter())
                    .registerTypeAdapter(Evaluation.class, new EvaluationJsonAdapter())
                    .registerTypeAdapter(boolean.class, new BooleanJsonAdapter())
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


}
