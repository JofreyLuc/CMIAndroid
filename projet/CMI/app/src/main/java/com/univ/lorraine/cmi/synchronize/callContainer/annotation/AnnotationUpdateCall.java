package com.univ.lorraine.cmi.synchronize.callContainer.annotation;

import com.univ.lorraine.cmi.database.model.Annotation;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by alexis on 22/05/2016.
 */
public class AnnotationUpdateCall extends AbstractAnnotationCall<ResponseBody> {

    public final static String type = dataType + "_UPDATE_" + extensionName;

    public AnnotationUpdateCall(){}

    public AnnotationUpdateCall(Long idU, Annotation o) {
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
    protected Response<ResponseBody> executeCall(CallMeIshmaelService service) throws IOException, RuntimeException {
        Annotation annot = (Annotation) getObjectData();
        return service.updateAnnotation(idUser, annot.getBibliotheque().getIdServeur(), annot)
                .execute();
    }

    @Override
    protected void afterExecuteCall(Response<ResponseBody> response) {

    }

}
