package com.univ.lorraine.cmi.synchronize.callContainer;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by alexis on 20/05/2016.
 */
public abstract class CallContainer<O, R> {

    protected static final String extensionName = "CALL";

    private Call call;

    private O objectData;

    public CallContainer(Call<R> c, O o) {
        call = c.clone();
        objectData = o;
    }

    public abstract String getDataType();

    public abstract String getType();

    public final O getObjectData() {
        return objectData;
    }

    public final Response<R> execute() {
        beforeExecuteCall(call);
        Response<R> response = null;
        try {
            response = call.execute();
            afterExecuteCall(response);
        } catch (IOException e) {
            e.printStackTrace();
            onCallFailed(call);
        } catch (RuntimeException e) {
            e.printStackTrace();
            onCallFailed(call);
        }
        return response;
    }

    public abstract void beforeExecuteCall(Call<R> call);

    public abstract void afterExecuteCall(Response<R> response);

    public abstract void onCallFailed(Call<R> call);
}
