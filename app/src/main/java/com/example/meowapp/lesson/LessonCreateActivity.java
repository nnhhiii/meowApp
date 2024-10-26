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

public class LessonCreateActivity extends AppCompatActivity {
    private EditText et_lesson_name;
    private Spinner sp_Language, sp_level;
    private ImageButton btnBack;
    private Button btn_save_changes;
    private Map<String, String> languageMap = new HashMap<>();
    private Map<String, String> levelMap = new HashMap<>(); // Map để lưu cấp độ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lesson_add);

        sp_level = findViewById(R.id.sp_level);
        et_lesson_name = findViewById(R.id.et_lesson_name);
        sp_Language = findViewById(R.id.sp_language);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btn_save_changes = findViewById(R.id.btn_save_changes);
        btn_save_changes.setOnClickListener(v -> saveToFirebase());

        loadDataToSpinner();

        // Thiết lập listener cho Spinner ngôn ngữ
        sp_Language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguageName = sp_Language.getSelectedItem().toString();
                String selectedLanguageId = languageMap.get(selectedLanguageName);
                loadLevelsByLanguageId(selectedLanguageId); // Gọi phương thức để tải cấp độ
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì khi không có lựa chọn
            }
        });
    }

    private void loadDataToSpinner() {
        FirebaseApiService.apiService.getAllLanguage().enqueue(new Callback<Map<String, Language>>() {
            @Override
            public void onResponse(Call<Map<String, Language>> call, Response<Map<String, Language>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Language> responseMap = response.body();

                    // Tạo danh sách các tên ngôn ngữ và lưu language_id tương ứng
                    List<String> languageNames = new ArrayList<>();
                    for (Map.Entry<String, Language> entry : responseMap.entrySet()) {
                        languageMap.put(entry.getValue().getLanguage_name(), entry.getKey()); // Lưu cặp tên-ngôn ngữ với id
                        languageNames.add(entry.getValue().getLanguage_name()); // Chỉ lấy tên ngôn ngữ
                    }

                    // Tạo Adapter cho Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(LessonCreateActivity.this, android.R.layout.simple_spinner_item, languageNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Gán Adapter cho Spinner
                    sp_Language.setAdapter(adapter);
                } else {
                    Toast.makeText(LessonCreateActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Language>> call, Throwable t) {
                Toast.makeText(LessonCreateActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLevelsByLanguageId(String languageId) {
        FirebaseApiService.apiService.getAllLevelByLanguageId("\"language_id\"", "\"" + languageId + "\"").enqueue(new Callback<Map<String, Level>>() {
            @Override
            public void onResponse(Call<Map<String, Level>> call, Response<Map<String, Level>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Level> responseMap = response.body();
                    List<String> levelNames = new ArrayList<>();

                    // Tạo danh sách các tên cấp độ và lưu level_id tương ứng
                    levelMap.clear(); // Xóa dữ liệu cũ
                    for (Map.Entry<String, Level> entry : responseMap.entrySet()) {
                        levelMap.put(entry.getValue().getLevel_name(), entry.getKey()); // Lưu cặp tên-cấp độ với id
                        levelNames.add(entry.getValue().getLevel_name()); // Chỉ lấy tên cấp độ
                    }

                    // Tạo Adapter cho Spinner cấp độ
                    ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(LessonCreateActivity.this, android.R.layout.simple_spinner_item, levelNames);
                    levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Gán Adapter cho Spinner cấp độ
                    sp_level.setAdapter(levelAdapter);
                } else {
                    Toast.makeText(LessonCreateActivity.this, "Failed to get levels data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Level>> call, Throwable t) {
                Toast.makeText(LessonCreateActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToFirebase() {
        String levelName = sp_level.getSelectedItem().toString();
        String selectedLanguageName = sp_Language.getSelectedItem().toString();
        String selectedLanguageId = languageMap.get(selectedLanguageName);
        String selectedLevelId = levelMap.get(levelName); // Lấy ID cấp độ tương ứng
        String lessonName = et_lesson_name.getText().toString().trim();

        // Kiểm tra thông tin đầu vào
        if (TextUtils.isEmpty(levelName)) {
            Toast.makeText(this, "Vui lòng chọn cấp độ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(selectedLanguageName)) {
            Toast.makeText(this, "Vui lòng chọn ngôn ngữ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(lessonName)) {
            Toast.makeText(this, "Vui lòng nhập tên bài học", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Lesson mới
        Lesson lesson = new Lesson();
        lesson.setLesson_name(lessonName);
        lesson.setLanguage_id(selectedLanguageId);
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
                    Toast.makeText(LessonCreateActivity.this, "Thêm bài học thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LessonCreateActivity.this, "Thêm bài học thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Lesson> call, Throwable t) {
                Toast.makeText(LessonCreateActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
