package com.univ.lorraine.cmi.synchronize;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.univ.lorraine.cmi.Utilities;
import com.univ.lorraine.cmi.database.model.Annotation;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.synchronize.callContainer.CallContainer;
import com.univ.lorraine.cmi.synchronize.callContainer.CallContainerClassAdapter;
import com.univ.lorraine.cmi.synchronize.callContainer.annotation.AbstractAnnotationCall;
import com.univ.lorraine.cmi.synchronize.callContainer.annotation.AnnotationCreateCall;
import com.univ.lorraine.cmi.synchronize.callContainer.annotation.AnnotationDeleteCall;
import com.univ.lorraine.cmi.synchronize.callContainer.annotation.AnnotationUpdateCall;
import com.univ.lorraine.cmi.synchronize.callContainer.bibliotheque.BibliothequeDeleteCall;
import com.univ.lorraine.cmi.synchronize.callContainer.bibliotheque.BibliothequeUpdateCall;

import java.util.Iterator;

/**
 * Created by alexis on 22/05/2016.
 */
public class CallContainerQueue extends AbstractCallContainerQueue {

    private final static String SHARED_PREF_KEY = "callcontainerqueue";

    private Gson gson;

    private static CallContainerQueue ourInstance = new CallContainerQueue();

    public static CallContainerQueue getInstance() {
        return ourInstance;
    }

    private CallContainerQueue() {
        gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(CallContainer.class, new CallContainerClassAdapter())
                .create();
    }

    /**
     * Méthode appelée pendant enqueue juste avant d'ajouter l'élément à la file.
     *
     * @param callToAdd L'élément qui va être ajouté.
     */
    @Override
    protected void beforeEnqueue(CallContainer callToAdd) {
        CallContainer currentCall;
        Iterator<CallContainer> it;
        Bibliotheque biblio;
        Annotation annot;
        switch (callToAdd.getType()) {
            // Bibliothèque
            case BibliothequeUpdateCall.type :
                biblio = (Bibliotheque) callToAdd.getObjectData();
                // On enlève les updateBiblio liés au même livre
                it = iterator();
                while (it.hasNext()) {
                    currentCall = it.next();
                    if (currentCall.getType() == BibliothequeUpdateCall.type
                            && biblio.getIdBibliotheque().equals(
                            ((Bibliotheque) currentCall.getObjectData()).getIdBibliotheque())) {
                        it.remove();
                    }
                }
                break;

            case BibliothequeDeleteCall.type :
                biblio = (Bibliotheque) callToAdd.getObjectData();
                // On enlève les updateBiblio liés au même livre ainsi que les opérations sur les annotations
                it = iterator();
                while (it.hasNext()) {
                    currentCall = it.next();
                    if ( (currentCall.getType() == BibliothequeUpdateCall.type                                 // UdateBiblio
                            && biblio.getIdBibliotheque().equals(                                       // même
                            ((Bibliotheque) currentCall.getObjectData()).getIdBibliotheque()))                  // idBiblio

                            || (currentCall.getDataType() == AbstractAnnotationCall.dataType                   // Annotation
                            && biblio.getIdBibliotheque().equals(                                       // même
                            ((Annotation) currentCall.getObjectData()).getBibliotheque().getIdBibliotheque()))  // idBiblio
                            )
                        it.remove();
                }
                break;

            // Annotation
            case AnnotationCreateCall.type :
                break;
            case AnnotationUpdateCall.type :
                annot = (Annotation) callToAdd.getObjectData();
                // On enlève les updateAnnot liés à la même annotation
                it = iterator();
                while (it.hasNext()) {
                    currentCall = it.next();
                    if (currentCall.getType() == AnnotationUpdateCall.type
                            && annot.getIdAnnotation().equals(
                            ((Annotation) currentCall.getObjectData()).getIdAnnotation()))
                        it.remove();
                }
                break;

            case AnnotationDeleteCall.type :
                annot = (Annotation) callToAdd.getObjectData();
                // On enlève les opérations touchant la même annotation
                it = iterator();
                while (it.hasNext()) {
                    currentCall = it.next();
                    if (currentCall.getDataType() == AbstractAnnotationCall.dataType
                        && annot.getIdAnnotation().equals(
                            ((Annotation) currentCall.getObjectData()).getIdAnnotation()))
                        it.remove();
                }
                break;
        }
    }

    /**
     * Méthode appelée pendant enqueue juste après avoir ajouter l'élément à la file.
     *
     * @param callContainer L'élément qui a été ajouté.
     */
    @Override
    protected void afterEnqueue(CallContainer callContainer) {

    }

    public void save(SharedPreferences preferences) {
        String json = gson.toJson(this);
        preferences.edit().putString(SHARED_PREF_KEY, json).apply();
    }

    public void load(SharedPreferences preferences) {
        String json = preferences.getString(SHARED_PREF_KEY, "");
        if (!json.equals(""))
            ourInstance = gson.fromJson(json, this.getClass());
    }
}
