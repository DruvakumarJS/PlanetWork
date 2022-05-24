package com.netiapps.planetwork.network_retrofit;

import com.google.gson.JsonObject;
import com.netiapps.planetwork.model.PieChartDAO;
import com.netiapps.planetwork.mvvm.UserTaskDAO;
import com.netiapps.planetwork.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitApi {

   // @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: */*"})
    @POST("api/chartdata")
    Call<JsonObject> getchartdata(@HeaderMap Map<String, String> headers, @Body HashMap<String, Object> listDetails);

    @POST("api/notifications")
    Call<JsonObject> getnotification(@HeaderMap HashMap<String, String> mHeaders, @Body HashMap<String, Object> listDetails);

    @Multipart
    //@POST("api/update_task_details")
    @POST("http://127.0.0.1::8080/planetwork/upload_images.php")
    Call<JsonObject> uploadNewsFeedImages(@HeaderMap HashMap<String, String> mHeaders,
                                          @Part List<MultipartBody.Part> list,
                                          @Part("user_id") String userId,
                                          @Part("job_id") String st_jobId,
                                          @Part("status") String st_task_status,
                                          @Part("description") String description,
                                          @Part("visits") String no_of_visits,
                                          @Part("assigned_jiobid") String jobassignid);

    @POST("api/jobs")
    Call<UserTaskDAO> gettaskfromserver(@HeaderMap HashMap<String, String> mHeaders,@Body HashMap<String, Object> listDetails);

    @POST("api/job-status-update")
    Call<JsonObject> updatetaskstatus(@HeaderMap HashMap<String, String> mHeaders, @Body HashMap<String, Object> listDetails);

    @POST("api/update-fcm-token")
    Call<JsonObject> updatefcmToken(@HeaderMap HashMap<String, String> mHeaders,@Body HashMap<String, Object> listDetails);

    @POST("api/attendance")
    Call<JsonObject> attendence(@HeaderMap HashMap<String, String> mHeaders, @Body HashMap<String, Object> listDetails);

    @POST("api/attendance-report")
    Call<JsonObject> attendencereport(@HeaderMap HashMap<String, String> mHeaders, @Body HashMap<String, Object> listDetails);

    @POST("api/get-user-path")
    Call<JsonObject> get_user_path(@HeaderMap HashMap<String, String> mHeaders,  @Body HashMap<String, Object> listDetails);
}
