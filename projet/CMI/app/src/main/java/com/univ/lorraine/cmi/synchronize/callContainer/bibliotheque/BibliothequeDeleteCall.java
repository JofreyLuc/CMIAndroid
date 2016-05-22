package com.univ.lorraine.cmi.synchronize.callContainer.bibliotheque;

import com.univ.lorraine.cmi.database.model.Bibliotheque;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by alexis on 22/05/2016.
 */
public class BibliothequeDeleteCall extends AbstractBibliothequeCall<ResponseBody> {

    public final static String type = dataType + "_DELETE_" + extensionName;

    public BibliothequeDeleteCall(Call<ResponseBody> c, Bibliotheque o) {
        super(c, o);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void beforeExecuteCall(Call call) {

    }

    @Override
    public void afterExecuteCall(Response response) {

    }

    @Override
    public void onCallFailed(Call call) {

    }
}
