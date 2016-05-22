package com.univ.lorraine.cmi.synchronize.callContainer.bibliotheque;

import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.synchronize.callContainer.CallContainer;

import retrofit2.Call;

/**
 * Created by alexis on 22/05/2016.
 */
public abstract class AbstractBibliothequeCall<R> extends CallContainer<Bibliotheque, R> {

    public final static String dataType = "BIBLIOTHEQUE";

    public AbstractBibliothequeCall(Call<R> c, Bibliotheque o) {
        super(c, o);
    }

    public String getDataType() {
        return dataType;
    }

}
