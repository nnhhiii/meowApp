package com.example.meowapp.lesson;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.Level.LevelEditActivity;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.Lesson;
import com.example.meowapp.model.Level;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonEditActivity extends AppCompatActivity {
    private EditText et_lesson_name;
    private Spinner sp_level;
    private Button btn_save_changes;
    private ImageButton btnBack;
    private Map<String, String> levelMap = new HashMap<>();
    private String levelId, lessonId, languageId;
    private Lesson lesson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lesson_edit);

        sp_level = findViewById(R.id.sp_level);
        et_lesson_name = findViewById(R.id.et_lesson_name);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btn_save_changes = findViewById(R.id.btn_save_changes);
        btn_save_changes.setOnClickListener(v -> saveToFirebase());

        loadData();
    }
    private void loadData() {
        lessonId = getIntent().getStringExtra("lessonId");
        FirebaseApiService.apiService.getLessonById(lessonId).enqueue(new Callback<Lesson>() {
            @Override
            public void onResponse(Call<Lesson> call, Response<Lesson> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lesson = response.body();
                    et_lesson_name.setText(lesson.getLesson_name());
                    levelId = lesson.getLevel_id();
                    languageId = lesson.getLanguage_id();
                    loadLevelNameById(levelId);
                } else {
                    Toast.makeText(LessonEditActivity.this, "Không thể tải thông tin cấp độ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Lesson> call, Throwable t) {
                Toast.makeText(LessonEditActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadLevelNameById(String languageId) {
        FirebaseApiService.apiService.getAllLevelByLanguageId("\"language_id\"","\"" + languageId + "\"").enqueue(new Callback<Map<String, Level>>() {
            @Override
            public void onResponse(Call<Map<String, Level>> call, Response<Map<String, Level>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Level> languageMapResponse = response.body();
                    List<String> levelNames = new ArrayList<>();
                    int selectedIndex = -1;
                    int index = 0;

                    // Lấy tất cả tên ngôn ngữ và lưu vào languageMap
                    for (Map.Entry<String, Level> entry : languageMapResponse.entrySet()) {
                        String levelName = entry.getValue().getLevel_name();
                        levelNames.add(levelName);

                        // Lưu cặp languageName và languageId vào languageMap
                        levelMap.put(levelName, entry.getKey());

                        if (entry.getKey().equals(levelId)) {
                            selectedIndex = index;  // Lưu lại index của ngôn ngữ đã chọn
                        }
                        index++;
                    }

                    // Tạo Adapter cho Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(LessonEditActivity.this, android.R.layout.simple_spinner_item, levelNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Gán Adapter cho Spinner
                    sp_level.setAdapter(adapter);

                    // Đặt Spinner vào vị trí ngôn ngữ tương ứng
                    if (selectedIndex != -1) {
                        sp_level.setSelection(selectedIndex);
                    }
                } else {
                    Toast.makeText(LessonEditActivity.this, "Không thể tải thông tin ngôn ngữ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Level>> call, Throwable t) {
                Toast.makeText(LessonEditActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveToFirebase() {
        String levelName = sp_level.getSelectedItem().toString();
        String selectedLevelName = sp_level.getSelectedItem().toString();
        String selectedLevelId = levelMap.get(selectedLevelName);
        String lessonName = et_lesson_name.getText().toString().trim();

        // Kiểm tra thông tin đầu vào
        if (TextUtils.isEmpty(levelName)) {
            Toast.makeText(this, "Vui lòng chọn cấp độ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(lessonName)) {
            Toast.makeText(this, "Vui lòng nhập tên bài học", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Lesson mới
        Lesson lesson = new Lesson();
        lesson.setLesson_name(lessonName);
        lesson.setLevel_id(selectedLevelId); // Nếu có trường ID cấp độ

        // Lấy thời gian hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        lesson.setCreated_at(currentTime);
        lesson.setUpdated_at(currentTime);

        // Lưu bài học vào Firebase
        FirebaseApiService.apiService.addLesson(lesson).enqueue(new Callback<Lesson>() {
            @Override
            public void onResponse(Call<Lesson> call, Response<Lesson> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LessonEditActivity.this, "Chỉnh sửa bài học thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LessonEditActivity.this, "Chỉnh sửa bài học thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Lesson> call, Throwable t) {
                Toast.makeText(LessonEditActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}