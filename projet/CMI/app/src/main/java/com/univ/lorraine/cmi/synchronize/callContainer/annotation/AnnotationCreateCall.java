package com.univ.lorraine.cmi.synchronize.callContainer.annotation;

import com.univ.lorraine.cmi.database.model.Annotation;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by alexis on 22/05/2016.
 */
public class AnnotationCreateCall extends AbstractAnnotationCall<Annotation> {

    public final static String type = dataType + "_CREATE_" + extensionName;

    public AnnotationCreateCall(Call<Annotation> c, Annotation o) {
        super(c, o);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void beforeExecuteCall(Call<Annotation> call) {

    }

    @Override
    public void afterExecuteCall(Response<Annotation> response) {
        // Update cette annotation dans la base de donnée pour enregistrer l'id serveur
    }

    @Override
    public void onCallFailed(Call<Annotation> call) {

    }
}
