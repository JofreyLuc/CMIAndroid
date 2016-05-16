package com.univ.lorraine.cmi.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by alexis on 16/05/2016.
 */
public interface FileDownloadService {

    @GET
    Call<ResponseBody> downloadFileWithDynamicUrl(@Url String fileUrl);
}
