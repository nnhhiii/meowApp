package com.example.meowapp.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.LanguagePreference;
import com.example.meowapp.model.Level;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsCourseCreateActivity extends AppCompatActivity {
    private String userId;
    private Spinner spLanguage, spLevel;
    private ImageButton btnBack;
    private Button btnSave;
    private Map<String, String> languageMap = new HashMap<>();
    private Map<String, String> levelMap = new HashMap<>(); // Map để lưu cấp độ
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings_course_create);
        spLevel = findViewById(R.id.spLevel);
        spLanguage = findViewById(R.id.spLanguage);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveToFirebase());

        loadDataToSpinner();
        spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguageName = spLanguage.getSelectedItem().toString();
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
        // Gọi API lấy danh sách ngôn ngữ đã học
        FirebaseApiService.apiService.getAllLanguagePreferenceByUserId("\"user_id\"", "\"" + userId + "\"").enqueue(new Callback<Map<String, LanguagePreference>>() {
            @Override
            public void onResponse(Call<Map<String, LanguagePreference>> call, Response<Map<String, LanguagePreference>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, LanguagePreference> preferencesMap = response.body();
                    List<String> learnedLanguageIds = new ArrayList<>();

                    // Lấy danh sách language_id đã học
                    for (LanguagePreference preference : preferencesMap.values()) {
                        learnedLanguageIds.add(preference.getLanguage_id());
                    }

                    // Gọi API lấy danh sách tất cả ngôn ngữ
                    FirebaseApiService.apiService.getAllLanguage().enqueue(new Callback<Map<String, Language>>() {
                        @Override
                        public void onResponse(Call<Map<String, Language>> call, Response<Map<String, Language>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Map<String, Language> responseMap = response.body();

                                List<String> languageNames = new ArrayList<>();
                                languageMap.clear();

                                // Lọc ra các ngôn ngữ chưa học
                                for (Map.Entry<String, Language> entry : responseMap.entrySet()) {
                                    String languageId = entry.getKey();
                                    String languageName = entry.getValue().getLanguage_name();

                                    if (!learnedLanguageIds.contains(languageId)) {
                                        languageMap.put(languageName, languageId);
                                        languageNames.add(languageName);
                                    }
                                }

                                // Tạo Adapter cho Spinner
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(SettingsCourseCreateActivity.this,
                                        android.R.layout.simple_spinner_item, languageNames);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                // Gán Adapter cho Spinner
                                spLanguage.setAdapter(adapter);
                            } else {
                                Toast.makeText(SettingsCourseCreateActivity.this, "Failed to get language data", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Language>> call, Throwable t) {
                            Toast.makeText(SettingsCourseCreateActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(SettingsCourseCreateActivity.this, "Failed to get language preferences", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, LanguagePreference>> call, Throwable t) {
                Toast.makeText(SettingsCourseCreateActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadLevelsByLanguageId(String languageId) {
        FirebaseApiService.apiService.getAllLevelByLanguageId("\"language_id\"", "\"" + languageId + "\"")
                .enqueue(new Callback<Map<String, Level>>() {
                    @Override
                    public void onResponse(Call<Map<String, Level>> call, Response<Map<String, Level>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Map<String, Level> responseMap = response.body();
                            List<String> levelNames = new ArrayList<>();

                            // Tạo danh sách các tên cấp độ và lưu level_id tương ứng
                            for (Map.Entry<String, Level> entry : responseMap.entrySet()) {
                                levelMap.put(entry.getValue().getLevel_name(), entry.getKey()); // Lưu cặp tên-cấp độ với id
                                levelNames.add(entry.getValue().getLevel_name()); // Chỉ lấy tên cấp độ
                            }

                            // Tạo Adapter cho Spinner cấp độ
                            ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(SettingsCourseCreateActivity.this,
                                    android.R.layout.simple_spinner_item, levelNames);
                            levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Gán Adapter cho Spinner cấp độ
                            spLevel.setAdapter(levelAdapter);
                        } else {
                            Toast.makeText(SettingsCourseCreateActivity.this, "Failed to get levels data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Level>> call, Throwable t) {
                        Toast.makeText(SettingsCourseCreateActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveToFirebase() {
        String selectedLanguageId = languageMap.get(spLanguage.getSelectedItem().toString());
        String selectedLevelId = levelMap.get(spLevel.getSelectedItem().toString());

        // Tạo đối tượng 
        LanguagePreference languagePreference = new LanguagePreference();
        languagePreference.setLanguage_id(selectedLanguageId);
        languagePreference.setLevel_id(selectedLevelId);
        languagePreference.setLanguage_score(0);
        languagePreference.setUser_id(userId);
        FirebaseApiService.apiService.addLanguagePreference(languagePreference).enqueue(new Callback<LanguagePreference>() {
            @Override
            public void onResponse(Call<LanguagePreference> call, Response<LanguagePreference> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SettingsCourseCreateActivity.this, "Thêm ngôn ngữ thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SettingsCourseCreateActivity.this, "Thêm ngôn ngữ thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LanguagePreference> call, Throwable t) {
                Toast.makeText(SettingsCourseCreateActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}