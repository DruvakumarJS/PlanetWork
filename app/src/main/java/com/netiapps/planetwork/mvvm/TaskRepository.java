package com.netiapps.planetwork.mvvm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.netiapps.planetwork.network_retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskRepository extends ViewModel {
    private MutableLiveData<UserTaskDAO> tasklist;
    private SavedStateHandle savedStateHandle;

    public TaskRepository(SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
    }

    //ViewModel
    public MutableLiveData<UserTaskDAO> getalltasks(HashMap<String, String> mHeaders, HashMap<String, Object> listDetails) {

        if (tasklist == null) {
            tasklist = new MutableLiveData<>();
            loadtasklist(mHeaders,listDetails);
        }
        return tasklist;
    }

    private void loadtasklist(HashMap<String, String> mHeaders, HashMap<String, Object> listDetails) {
        Call<UserTaskDAO> call = RetrofitClient.getInstance().getMyApi().gettaskfromserver(mHeaders,listDetails);
        call.enqueue(new Callback<UserTaskDAO>() {
            @Override
            public void onResponse(Call<UserTaskDAO> call, Response<UserTaskDAO> response) {
                tasklist.setValue(response.body());
            }

            @Override
            public void onFailure(Call<UserTaskDAO> call, Throwable t) {
                tasklist.setValue(null);
            }
        });
    }


    //Model
   /* private void loadtasklist(HashMap<String, Object> listDetails) {


        Call<UserTaskDAO> call = RetrofitClient.getInstance().getMyApi().gettaskfromserver(listDetails);
        call.enqueue(new Callback<UserTaskDAO>() {
            @Override
            public void onResponse(Call<UserTaskDAO> call, Response<UserTaskDAO> response) {
                tasklist.setValue(response.body());
            }

            @Override
            public void onFailure(Call<UserTaskDAO> call, Throwable t) {
                tasklist.setValue(null);
            }
        });

    }*/
}
