package com.univ.lorraine.cmi.synchronize.callContainer;

import com.google.gson.annotations.Expose;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by alexis on 20/05/2016.
 */
public abstract class CallContainer<O, R> {

    protected static final String extensionName = "CALL";

    @Expose
    protected Long idUser;

    @Expose
    protected O objectData;

    public CallContainer() {}

    public CallContainer(Long idU, O o) {
        idUser = idU;
        objectData = o;
    }

    public abstract String getDataType();

    public abstract String getType();

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public O getObjectData() {
        return objectData;
    }

    public void setObjectData(O objectData) {
        this.objectData = objectData;
    }

    /**
     * Lance dans l'ordre :
     * - beforeExecuteCall()
     * - executeCall()
     *(- onCallFailed en cas d'Exception)
     * - afterExecuteCall(Response)
     */
    public final void execute(CallMeIshmaelService service) {
        Response<R> response = null;
        beforeExecuteCall();
        try {
        response = executeCall(service);
        } catch (IOException e) {
            e.printStackTrace();
            onCallFailed();
        } catch (RuntimeException e) {
            e.printStackTrace();
            onCallFailed();
        }
        afterExecuteCall(response);
    }

    /**
     * Exécuté juste avant executeCall dans execute.
     */
    protected abstract void beforeExecuteCall();

    /**
     * Méthode à complèter avec la construction du Call et son exécution dans execute.
     * Ne pas catch les exceptions liés à Call.execute.
     *
     * @param service Interface Retrofit de l'API.
     */
    protected abstract Response<R> executeCall(CallMeIshmaelService service) throws IOException, RuntimeException;

    /**
     * Exécuté juste après executeCall dans execute.
     * @param response
     */
    protected abstract void afterExecuteCall(Response<R> response);

    /**
     * Exécuté si une exception survient (levé par executeCall) dans execute.
     */
    protected abstract void onCallFailed();

}
