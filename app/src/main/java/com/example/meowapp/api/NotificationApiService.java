package com.example.meowapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.meowapp.model.FirebaseNotificationRequest;
import com.example.meowapp.model.FirebaseResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

public interface NotificationApiService {
    String BASE_URL = "https://fcm.googleapis.com/";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    NotificationApiService apiService = retrofit.create(NotificationApiService.class);

    @POST("v1/projects/appngonngu/messages:send")
    Call<FirebaseResponse> sendNotification(
            @Header("Authorization") String authHeader,
            @Body FirebaseNotificationRequest request);
}
