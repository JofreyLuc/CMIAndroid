package com.univ.lorraine.cmi.synchronize.callContainer.annotation;

import com.univ.lorraine.cmi.database.model.Annotation;
import com.univ.lorraine.cmi.synchronize.callContainer.CallContainer;

import retrofit2.Call;

/**
 * Created by alexis on 22/05/2016.
 */
public abstract class AbstractAnnotationCall<R> extends CallContainer<Annotation, R> {

    public final static String dataType = "ANNOTATION";

    public AbstractAnnotationCall(){ }

    public AbstractAnnotationCall(Long idU, Annotation o) {
        super(idU, o);
    }

    public String getDataType() {
        return dataType;
    }

}
