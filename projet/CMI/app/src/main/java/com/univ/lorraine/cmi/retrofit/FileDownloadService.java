package com.univ.lorraine.cmi.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Interface retrofit permettant de télécharger un fichier depuis n'importe quelle URL.
 */
public interface FileDownloadService {

    @GET
    Call<ResponseBody> downloadFileWithDynamicUrl(@Url String fileUrl);
}
