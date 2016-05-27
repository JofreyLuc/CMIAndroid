package com.univ.lorraine.cmi.synchronize.callContainer.annotation;

import com.univ.lorraine.cmi.database.model.Annotation;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by alexis on 22/05/2016.
 */
public class AnnotationCreateCall extends AbstractAnnotationCall<Annotation> {

    public final static String type = dataType + "_CREATE_" + extensionName;

    public AnnotationCreateCall(){}

    public AnnotationCreateCall(Long idU, Annotation o) {
        super(idU, o);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    protected void beforeExecuteCall() {

    }

    @Override
    protected Response<Annotation> executeCall(CallMeIshmaelService service) throws IOException, RuntimeException {
        Annotation annot = (Annotation) getObjectData();
        return service.createAnnotation(idUser, annot.getBibliotheque().getIdServeur(), annot)
                .execute();
    }

    @Override
    protected void afterExecuteCall(Response<Annotation> response) {
        // Mise à jour de l'idServeur de l'annotation dans la bdd locale
        //TODO
    }

}
