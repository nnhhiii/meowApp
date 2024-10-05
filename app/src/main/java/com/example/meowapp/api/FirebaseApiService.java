package com.example.meowapp.api;

import com.example.meowapp.model.Question;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FirebaseApiService {
    String BASE_URL = "https://appngonngu-default-rtdb.firebaseio.com/";

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    FirebaseApiService apiService = retrofit.create(FirebaseApiService.class);
    @GET("questions.json")
    Call<Map<String, Question>> getQuestionsByLessonId(
            @Query("orderBy") String orderBy,
            @Query("equalTo") int lessonId
    );
    @GET("questions/{id}.json")
    Call<Question> getQuestionById(@Path("id") String id);
}
