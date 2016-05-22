package com.univ.lorraine.cmi.synchronize.callContainer.bibliotheque;

import com.univ.lorraine.cmi.database.model.Bibliotheque;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by alexis on 22/05/2016.
 */
public class BibliothequeUpdateCall extends AbstractBibliothequeCall<ResponseBody> {

    public final static String type = dataType + "_UPDATE_" + extensionName;

    public BibliothequeUpdateCall(Call<ResponseBody> c, Bibliotheque o) {
        super(c, o);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void beforeExecuteCall(Call<ResponseBody> call) {

    }

    @Override
    public void afterExecuteCall(Response<ResponseBody> response) {

    }

    @Override
    public void onCallFailed(Call<ResponseBody> call) {

    }
}
