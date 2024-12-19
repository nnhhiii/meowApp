package com.example.meowapp.api;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.LanguagePreference;
import com.example.meowapp.model.Lesson;
import com.example.meowapp.model.Level;
import com.example.meowapp.model.Mission;
import com.example.meowapp.model.Notification;
import com.example.meowapp.model.Question;
import com.example.meowapp.model.Lesson;
import com.example.meowapp.model.QuestionType;
import com.example.meowapp.model.User;
import com.example.meowapp.model.UserProgress;
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
import retrofit2.http.PATCH;
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
    @GET("users.json")
    Call<Map<String, User>> getUserByEmail(
            @Query("orderBy") String orderBy,
            @Query("equalTo") String email
    );
    @POST("users.json")
    Call<User> addUser(@Body User user);
    @PUT("users/{id}.json")
    Call<User> updateUser(@Path("id") String userId, @Body User user);
    @PATCH("users/{id}.json")
    Call<User> updateUserField(@Path("id") String userId, @Body Map<String, Object> field);
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


    @GET("language_preferences.json")
    Call<Map<String, LanguagePreference>> getAllLanguagePreferenceByUserId(
            @Query("orderBy") String orderBy,
            @Query("equalTo") String userId
    );
    @POST("language_preferences.json")
    Call<LanguagePreference> addLanguagePreference(@Body LanguagePreference language);
    @PATCH("language_preferences/{id}.json")
    Call<LanguagePreference> updateLanguageScore(@Path("id") String userId, @Body Map<String, Object> scoreField);
    @DELETE("language_preferences/{id}.json")
    Call<LanguagePreference> deleteLanguagePreference(@Path("id") String id);


    @GET("levels.json")
    Call<Map<String, Level>> getAllLevel();
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


    @GET("lessons.json")
    Call<Map<String, Lesson>> getAllLessons();
    @GET("lessons.json")
    Call<Map<String, Lesson>> getAllLessonByLanguageId(
            @Query("orderBy") String orderBy,
            @Query("equalTo") String languageId
    );
    @GET("lessons.json")
    Call<Map<String, Lesson>> getAllLessonByLevelId(
            @Query("orderBy") String orderBy,
            @Query("equalTo") String levelId
    );
    @GET("lessons/{id}.json")
    Call<Lesson> getLessonById(@Path("id") String id);
    @POST("lessons.json")
    Call<Lesson> addLesson(@Body Lesson lesson);
    @PUT("lessons/{id}.json")
    Call<Lesson> updateLesson(@Path("id") String id, @Body Lesson lesson);
    @DELETE("lessons/{id}.json")
    Call<Lesson> deleteLesson(@Path("id") String id);



    @GET("question_type.json")
    Call<Map<String, QuestionType>> getAllQuestionType();
    @GET("questions.json")
    Call<Map<String, Question>> getQuestionsByLessonId(
            @Query("orderBy") String orderBy,
            @Query("equalTo") String lessonId
    );
    @GET("questions.json")
    Call<Map<String, Question>> getQuestionsByType(
            @Query("orderBy") String orderBy,
            @Query("equalTo") String questionType
    );
    @GET("questions/{id}.json")
    Call<Question> getQuestionById(@Path("id") String id);
    @POST("questions.json")
    Call<Question> addQuestion(@Body Question question);
    @PUT("questions/{id}.json")
    Call<Question> updateQuestion(@Path("id") String id, @Body Question question);
    @DELETE("questions/{id}.json")
    Call<Question> deleteQuestion(@Path("id") String id);



    @GET("user_progress.json")
    Call<Map<String, UserProgress>> getAllUserProgressByUserId(
            @Query("orderBy") String orderBy,
            @Query("equalTo") String userId
    );
    @POST("user_progress.json")
    Call<UserProgress> addUserProgress(@Body UserProgress userProgress);

    @PATCH("user_progress/{id}.json")
    Call<UserProgress> updateFieldUserProgress(@Path("id") String id, @Body Map<String, Object> field);



    @GET("missions.json")
    Call<Map<String, Mission>> getAllMission();
    @GET("missions/{id}.json")
    Call<Mission> getMissionById(@Path("id") String id);
    @POST("missions.json")
    Call<Mission> addMission(@Body Mission mission);
    @PUT("missions/{id}.json")
    Call<Mission> updateMission(@Path("id") String id, @Body Mission mission);
    @DELETE("missions/{id}.json")
    Call<Mission> deleteMission(@Path("id") String id);


    @GET("notifications.json")
    Call<Map<String, Notification>> getAllNotifications();
    @DELETE("notifications/{id}.json")
    Call<Notification> deleteNotification(@Path("id") String id);




}
