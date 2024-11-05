package com.example.meowapp.questionType;

import android.content.Intent;
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
import com.example.meowapp.Main.MainActivity;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Lesson;
import com.example.meowapp.model.Level;
import com.example.meowapp.model.User;
import com.example.meowapp.model.UserProgress;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinishActivity extends AppCompatActivity {
    private TextView tvScore;
    private Button btnFinish;
    private int scoreLesson, scoreUser;
    private String userId, lessonId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question_type_finish);
        tvScore = findViewById(R.id.tvScore);
        btnFinish = findViewById(R.id.btnFinish);

        lessonId = getIntent().getStringExtra("LESSON_ID");
        userId = "2";

        loadData();
        btnFinish.setOnClickListener(v -> getUserScore());
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

    private void getUserScore(){
        FirebaseApiService.apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    scoreUser = user.getScore();
                    updateUserScore();
                } else {
                    Toast.makeText(FinishActivity.this, "Không lấy được điểm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(FinishActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUserScore(){
        int newScore;
        newScore = scoreUser + scoreLesson;
        Map<String, Object> scoreField = new HashMap<>();
        scoreField.put("score", newScore);

        FirebaseApiService.apiService.updateUserScore(userId, scoreField).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(FinishActivity.this, "Điểm đã được cập nhật: " + newScore, Toast.LENGTH_SHORT).show();
                    checkUserProgress();
                } else {
                    Toast.makeText(FinishActivity.this, "Không lấy được điểm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(FinishActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserProgress() {
        FirebaseApiService.apiService.getAllUserProgressByUserId("\"user_id\"", "\""+ userId +"\"").enqueue(new Callback<Map<String, UserProgress>>() {
            @Override
            public void onResponse(Call<Map<String, UserProgress>> call, Response<Map<String, UserProgress>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean hasLearnedLesson = false;

                    // Kiểm tra xem lessonId đã tồn tại trong danh sách user progress chưa
                    for (Map.Entry<String, UserProgress> entry : response.body().entrySet()) {
                        UserProgress progress = entry.getValue();
                        if (lessonId.equals(progress.getLesson_id())) {
                            hasLearnedLesson = true;
                            break;
                        }
                    }

                    // Nếu chưa học bài này thì gọi API addUserProgress
                    if (!hasLearnedLesson) {
                        addUserProgress(userId, lessonId);
                    }else {
                        goToMainActivity();
                        Log.d("FinishActivity", "User đã học bài này rồi.");
                    }
                } else {
                    // Nếu không có dữ liệu user progress nào, thêm luôn tiến trình học mới
                    addUserProgress(userId, lessonId);
                }
            }

            @Override
            public void onFailure(Call<Map<String, UserProgress>> call, Throwable t) {
                Toast.makeText(FinishActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Hàm để gọi API addUserProgress
    private void addUserProgress(String userId, String lessonId) {
        UserProgress newUserProgress = new UserProgress();
        newUserProgress.setUser_id(userId);
        newUserProgress.setLesson_id(lessonId);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        newUserProgress.setCompleted_at(currentTime);
        FirebaseApiService.apiService.addUserProgress(newUserProgress).enqueue(new Callback<UserProgress>() {
            @Override
            public void onResponse(Call<UserProgress> call, Response<UserProgress> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FinishActivity.this, "Tiến trình học đã được cập nhật", Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                } else {
                    Toast.makeText(FinishActivity.this, "Không thể cập nhật tiến trình học", Toast.LENGTH_SHORT).show();
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