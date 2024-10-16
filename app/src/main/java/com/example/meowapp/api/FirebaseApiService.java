package com.example.meowapp.api;

import com.example.meowapp.model.Language;
import com.example.meowapp.model.Level;
import com.example.meowapp.model.Question;
import com.example.meowapp.model.Lesson;
import com.example.meowapp.model.User;
import com.example.meowapp.model.Leaderboard;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
    @GET("users.json")
    Call<Map<String, User>> getAllUsers();
    @GET("users/{id}.json")
    Call<User> getUserById(@Path("id") String userId);
    @POST("users.json")
    Call<User> addUser(@Body User user);
    @PUT("users/{id}.json")
    Call<User> updateUser(@Path("id") String userId, @Body User user);
    @DELETE("users/{id}.json")
    Call<Void> deleteUser(@Path("id") String userId);

    @GET("languages.json")
    Call<Map<String, Language>> getAllLanguage();
    @GET("languages/{id}.json")
    Call<Language> getLanguageById(@Path("id") String id);
    @POST("languages.json")
    Call<Language> addLanguage(@Body Language language);
    @PUT("languages/{id}.json")
    Call<Language> updateLanguage(@Path("id") String id, @Body Language language);
    @DELETE("languages/{id}.json")
    Call<Language> deleteLanguage(@Path("id") String id);

    @GET("levels.json")
    Call<Map<String, Level>> getAllLevelByLanguageId(
            @Query("orderBy") String orderBy,
            @Query("equalTo") String languageId
    );
    @GET("levels/{id}.json")
    Call<Level> getLevelById(@Path("id") String id);
    @POST("levels.json")
    Call<Level> addLevel(@Body Level level);
    @PUT("levels/{id}.json")
    Call<Level> updateLevel(@Path("id") String id, @Body Level level);
    @DELETE("levels/{id}.json")
    Call<Level> deleteLevel(@Path("id") String id);

    @GET("questions.json")
    Call<Map<String, Question>> getQuestionsByLessonId(
            @Query("orderBy") String orderBy,
            @Query("equalTo") String lessonId
    );
    @GET("questions/{id}.json")
    Call<Question> getQuestionById(@Path("id") String id);

    @GET("leaderboard.json")
    Call<Map<String, Leaderboard>> getAllLeaderboard();
    @GET("lessons.json")
    Call<Map<String, Lesson>> getAllLessons();


}
