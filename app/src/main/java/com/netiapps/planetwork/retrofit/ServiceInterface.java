package com.netiapps.planetwork.retrofit;

import com.netiapps.planetwork.model.ApiModel;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.Keys;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServiceInterface {

    @Multipart
    @POST(ApiConstants.ENDPOINT+"upload_images.php")
    Call<ApiModel> uploadtaskDetailswithImages(@Part List<MultipartBody.Part> files,
                                        @Part("user_id") String userId,
                                        @Part("job_id") String st_jobId,
                                        @Part("status") String st_task_status,
                                        @Part("description") String description,
                                        @Part("visits") String no_of_visits,
                                        @Part("assigned_jobid") String jobassignid);

    @Multipart
    @POST(ApiConstants.ENDPOINT+"upload_images.php")
    Call<ApiModel> uploadTaskdetails(@Part("user_id") String userId,
                                     @Part("job_id") String st_jobId,
                                     @Part("status") String st_task_status,
                                     @Part("description") String description,
                                     @Part("visits") String no_of_visits,
                                     @Part("assigned_jobid") String jobassignid);
}
