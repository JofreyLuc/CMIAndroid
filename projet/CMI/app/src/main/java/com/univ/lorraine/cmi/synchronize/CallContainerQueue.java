package com.univ.lorraine.cmi.synchronize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.univ.lorraine.cmi.database.model.Annotation;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.synchronize.callContainer.CallContainer;
import com.univ.lorraine.cmi.synchronize.callContainer.CallContainerClassAdapter;

import java.util.Iterator;

/**
 * Created by alexis on 22/05/2016.
 */
public class CallContainerQueue extends AbstractCallContainerQueue {

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
     * @param callContainer L'élément qui va être ajouté.
     */
    @Override
    protected void beforeEnqueue(CallContainer callContainer) {
        CallContainer call;
        Iterator<CallContainer> it;
        Bibliotheque biblio;
        Annotation annot;
        /*switch (callContainer.getType()) {
            // Bibliothèque
            case BibliothequeUpdateCall.type :
                biblio = (Bibliotheque) callContainer.getObjectData();
                // On enlève les updateBiblio liés au même livre
                it = iterator();
                while (it.hasNext()) {
                    call = it.next();
                    if (call.getType() == BibliothequeUpdateCall.type
                            && biblio.getIdBibliotheque().equals(
                            ((Bibliotheque) call.getObjectData()).getIdBibliotheque()))
                        it.remove();
                }
                break;

            case BibliothequeDeleteCall.type :
                biblio = (Bibliotheque) callContainer.getObjectData();
                // On enlève les updateBiblio liés au même livre ainsi que les opérations sur les annotations
                it = iterator();
                while (it.hasNext()) {
                    call = it.next();
                    if ( (call.getType() == BibliothequeUpdateCall.type                                 // UdateBiblio
                            && biblio.getIdBibliotheque().equals(                                       // même
                            ((Bibliotheque) call.getObjectData()).getIdBibliotheque()))                  // idBiblio

                            || (call.getDataType() == AbstractAnnotationCall.dataType                   // Annotation
                            && biblio.getIdBibliotheque().equals(                                       // même
                            ((Annotation) call.getObjectData()).getBibliotheque().getIdBibliotheque()))  // idBiblio
                            )
                        it.remove();
                }
                break;

            // Annotation
            case AnnotationCreateCall.type :
                break;
            case AnnotationUpdateCall.type :
                annot = (Annotation) callContainer.getObjectData();
                // On enlève les updateAnnot liés à la même annotation
                it = iterator();
                while (it.hasNext()) {
                    call = it.next();
                    if (call.getType() == AnnotationUpdateCall.type
                            && annot.getIdAnnotation().equals(
                            ((Annotation) call.getObjectData()).getIdAnnotation()))
                        it.remove();
                }
                break;

            case AnnotationDeleteCall.type :
                annot = (Annotation) callContainer.getObjectData();
                // On enlève les opérations touchant la même annotation
                it = iterator();
                while (it.hasNext()) {
                    call = it.next();
                    if (call.getDataType() == AbstractAnnotationCall.dataType
                        && annot.getIdAnnotation().equals(
                            ((Annotation) call.getObjectData()).getIdAnnotation()))
                        it.remove();
                }
                break;
        }*/
    }

    /**
     * Méthode appelée pendant enqueue juste après avoir ajouter l'élément à la file.
     *
     * @param callContainer L'élément qui a été ajouté.
     */
    @Override
    protected void afterEnqueue(CallContainer callContainer) {

    }

    public String save() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(CallContainer.class, new CallContainerClassAdapter())
                .create();
        return gson.toJson(this);
    }

    public void load(String json) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(CallContainer.class, new CallContainerClassAdapter())
                .create();

        ourInstance = gson.fromJson(json, this.getClass());
    }
}
