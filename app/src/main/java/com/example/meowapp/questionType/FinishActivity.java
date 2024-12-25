package com.example.meowapp.questionType;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meowapp.Level.LevelEditActivity;
import com.example.meowapp.Main.HomeFragment;
import com.example.meowapp.Main.MainActivity;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.LanguagePreference;
import com.example.meowapp.model.Lesson;
import com.example.meowapp.model.Level;
import com.example.meowapp.model.User;
import com.example.meowapp.model.UserProgress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinishActivity extends AppCompatActivity {
    private TextView tvScore, tvPercent;
    private Button btnFinish;
    private int scoreLesson, scoreUser, streaks, previousStreaks, percentScore, languageScore, eightyLessons, perfectLessons, lessons;
    private String userId, lessonId, languageId, currentDateTime, progressId;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question_type_finish);
        tvScore = findViewById(R.id.tvScore);
        tvPercent = findViewById(R.id.tvPercent);
        btnFinish = findViewById(R.id.btnFinish);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDateTime = sdf.format(new Date());

        percentScore = getIntent().getIntExtra("PERCENT_SCORE", 0);
        tvPercent.setText(percentScore + "%");

        lessonId = getIntent().getStringExtra("LESSON_ID");
        if(lessonId != null){
            loadData();
        }else {
            scoreLesson = 5;
            tvScore.setText("5");
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        languageId = sharedPreferences.getString("languageId", null);
        languageScore = sharedPreferences.getInt("languageScore", 0);
        previousStreaks = sharedPreferences.getInt("streaks", 0);
        streaks = sharedPreferences.getInt("streaks", 0);
        scoreUser = sharedPreferences.getInt("score", 0);
        perfectLessons = sharedPreferences.getInt("perfectLessons", 0);
        eightyLessons = sharedPreferences.getInt("eightyLessons", 0);
        lessons = sharedPreferences.getInt("lessons", 0);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();


        btnFinish.setOnClickListener(v -> {
            updateUserScore();
            updateLanguageScore();
        });
    }
    private void loadData() {
        FirebaseApiService.apiService.getLessonById(lessonId).enqueue(new Callback<Lesson>() {
            @Override
            public void onResponse(Call<Lesson> call, Response<Lesson> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Lesson lesson = response.body();
                    tvScore.setText(String.valueOf(lesson.getLesson_score()));
                    scoreLesson = lesson.getLesson_score();
                } else {
                    Toast.makeText(FinishActivity.this, "Không lấy được điểm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Lesson> call, Throwable t) {
                Toast.makeText(FinishActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUserScore(){
        int newScore;
        newScore = scoreUser + (int) (scoreLesson * (percentScore / 100.0f));
        Map<String, Object> field = new HashMap<>();
        field.put("score", newScore);
        field.put("lessons", ++lessons);

        boolean shouldUpdateEightyLessons = percentScore >= 80;
        boolean shouldUpdatePerfectLessons = percentScore == 100;

        if (shouldUpdateEightyLessons) {
            field.put("eightyLessons", ++eightyLessons);
        }

        if (shouldUpdatePerfectLessons) {
            field.put("perfectLessons", ++perfectLessons);
        }
        FirebaseApiService.apiService.updateUserField(userId, field).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("score", newScore);
                    editor.apply();

                    Toast.makeText(FinishActivity.this, "Điểm đã được cập nhật: " + newScore, Toast.LENGTH_SHORT).show();
                    if(lessonId != null){
                        checkUserProgress();
                    }else {
                        goToMainActivity();
                    }
                } else {
                    Toast.makeText(FinishActivity.this, "Không cập nhật được điểm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(FinishActivity.this, "Lỗi update user: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateLanguageScore(){
        int newScore;
        newScore = languageScore + (int) (scoreLesson * (percentScore / 100.0f));
        Map<String, Object> field = new HashMap<>();
        field.put("language_score", newScore);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("language_preferences");
        ref.orderByChild("user_id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Duyệt qua các entry và tìm entry với đúng language_id
                for (DataSnapshot languageSnapshot : snapshot.getChildren()) {
                    String langId = languageSnapshot.child("language_id").getValue(String.class);
                    if (langId != null && langId.equals(languageId)) {
                        // Cập nhật language_score mới
                        languageSnapshot.getRef().child("language_score").setValue(newScore)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(FinishActivity.this, "Điểm ngôn ngữ đã được cập nhật: " + newScore, Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(FinishActivity.this, "Lỗi cập nhật điểm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(FinishActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserProgress() {
        FirebaseApiService.apiService.getAllUserProgressByUserId("\"user_id\"", "\"" + userId + "\"")
                .enqueue(new Callback<Map<String, UserProgress>>() {
                    @Override
                    public void onResponse(Call<Map<String, UserProgress>> call, Response<Map<String, UserProgress>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean hasLearnedLesson = false;
                            long earliestCompletedAt = Long.MAX_VALUE;  // Khởi tạo với giá trị lớn nhất để tìm thời gian nhỏ nhất trong ngày hôm qua

                            // Lấy thời gian bắt đầu của ngày hôm qua (00:00:00)
                            long yesterdayStartTime = getYesterdayStartTime();

                            // Kiểm tra xem lessonId đã tồn tại trong danh sách
                            for (Map.Entry<String, UserProgress> entry : response.body().entrySet()) {
                                UserProgress progress = entry.getValue();
                                progressId = entry.getKey();
                                if (lessonId.equals(progress.getLesson_id())) {
                                    hasLearnedLesson = true;
                                }

                                // Chuyển đổi completed_at thành timestamp
                                long completedAt = convertToTimestamp(progress.getCompleted_at());

                                // Kiểm tra xem completedAt có thuộc về ngày hôm qua không
                                if (completedAt >= yesterdayStartTime && completedAt < yesterdayStartTime + 24 * 60 * 60 * 1000) {
                                    if (completedAt < earliestCompletedAt) {
                                        earliestCompletedAt = completedAt;
                                    }
                                }
                            }

                            // Kiểm tra nếu earliestCompletedAt là hợp lệ và đã qua 24 giờ nhưng dưới 48 giờ
                            if (earliestCompletedAt > 0) {
                                long currentTime = System.currentTimeMillis();
                                long timeDifference = currentTime - earliestCompletedAt;

                                // Nếu thời gian hoàn thành sớm nhất trong ngày hôm qua và đã qua 24h nhưng dưới 48h
                                if (timeDifference >= 24 * 60 * 60 * 1000 && timeDifference < 48 * 60 * 60 * 1000) {
                                    streaks++;  // Tăng streak
                                    updateStreak(streaks);
                                } else {
                                    streaks = 1;  // Reset streak
                                    updateStreak(streaks);
                                }
                            }

                            if (!hasLearnedLesson) {
                                addUserProgress(userId, lessonId);
                            } else {
                                updateUserProgress();
                            }

                        } else {
                            addUserProgress(userId, lessonId);
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, UserProgress>> call, Throwable t) {
                        Toast.makeText(FinishActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Chuyển đổi giá trị completed_at (String) thành timestamp
    public long convertToTimestamp(String completed_at) {
        try {
            Date date = sdf.parse(completed_at);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
    private long getYesterdayStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);  // Lùi lại 1 ngày
        calendar.set(Calendar.HOUR_OF_DAY, 0);  // Đặt giờ thành 00:00
        calendar.set(Calendar.MINUTE, 0);  // Đặt phút thành 00
        calendar.set(Calendar.SECOND, 0);  // Đặt giây thành 00
        calendar.set(Calendar.MILLISECOND, 0);  // Đặt mili giây thành 00
        return calendar.getTimeInMillis();
    }



    private void updateStreak(Integer streaks){
        Map<String, Object> field = new HashMap<>();
        field.put("streaks", streaks);
        FirebaseApiService.apiService.updateUserField(userId, field).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("streaks", streaks);
                    editor.apply();
                }else {
                    Toast.makeText(FinishActivity.this, "Không update được streaks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(FinishActivity.this, "Lỗi update streak: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserProgress(String userId, String lessonId) {
        UserProgress newUserProgress = new UserProgress();
        newUserProgress.setUser_id(userId);
        newUserProgress.setLesson_id(lessonId);
        newUserProgress.setCompleted_at(currentDateTime);
        FirebaseApiService.apiService.addUserProgress(newUserProgress).enqueue(new Callback<UserProgress>() {
            @Override
            public void onResponse(Call<UserProgress> call, Response<UserProgress> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FinishActivity.this, "Tiến trình học đã được cập nhật", Toast.LENGTH_SHORT).show();
                    if (streaks > previousStreaks) {
                        Intent intent = new Intent(FinishActivity.this, StreakActivity.class);
                        intent.putExtra("STREAK",streaks);
                        startActivity(intent);
                    } else {
                        goToMainActivity();
                    }
                } else {
                    Toast.makeText(FinishActivity.this, "Không thể cập nhật tiến trình học", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProgress> call, Throwable t) {
                Toast.makeText(FinishActivity.this, "Lỗi update userProgress: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUserProgress(){
        Map<String, Object> field = new HashMap<>();
        field.put("completed_at", currentDateTime);
        FirebaseApiService.apiService.updateFieldUserProgress(progressId, field).enqueue(new Callback<UserProgress>() {
            @Override
            public void onResponse(Call<UserProgress> call, Response<UserProgress> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (streaks > previousStreaks) {
                        Intent intent = new Intent(FinishActivity.this, StreakActivity.class);
                        intent.putExtra("STREAK",streaks);
                        startActivity(intent);
                    } else {
                        goToMainActivity();
                    }
                } else {
                    Toast.makeText(FinishActivity.this, "Không cập nhật được thời gian", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProgress> call, Throwable t) {
                Toast.makeText(FinishActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void goToMainActivity() {
        Intent intent = new Intent(FinishActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}