package com.example.meowapp.Level;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
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

public class LevelEditActivity extends AppCompatActivity {
    private EditText etLevelName;
    private Spinner spLanguage;
    private ImageButton btnBack;
    private Button btnSave;
    private String levelId, languageId;
    private Map<String, String> languageMap = new HashMap<>();
    private Level level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_edit);

        etLevelName = findViewById(R.id.et_level_name);
        spLanguage = findViewById(R.id.sp_language);
        btnSave = findViewById(R.id.btn_save_changes);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveToFirebase());

        loadData();
    }

    private void loadData() {
        levelId = getIntent().getStringExtra("levelId");
        FirebaseApiService.apiService.getLevelById(levelId).enqueue(new Callback<Level>() {
            @Override
            public void onResponse(Call<Level> call, Response<Level> response) {
                if (response.isSuccessful() && response.body() != null) {
                    level = response.body();
                    etLevelName.setText(level.getLevel_name());
                    languageId = level.getLanguage_id();
                    loadLanguageNameById(languageId);
                } else {
                    Toast.makeText(LevelEditActivity.this, "Không thể tải thông tin cấp độ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Level> call, Throwable t) {
                Toast.makeText(LevelEditActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadLanguageNameById(String languageId) {
        FirebaseApiService.apiService.getAllLanguage().enqueue(new Callback<Map<String, Language>>() {
            @Override
            public void onResponse(Call<Map<String, Language>> call, Response<Map<String, Language>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Language> languageMapResponse = response.body();
                    List<String> languageNames = new ArrayList<>();
                    int selectedIndex = -1;
                    int index = 0;
                    // Lấy tất cả tên ngôn ngữ và lưu vào languageMap
                    for (Map.Entry<String, Language> entry : languageMapResponse.entrySet()) {
                        String languageName = entry.getValue().getLanguage_name();
                        languageNames.add(languageName);

                        // Lưu cặp languageName và languageId vào languageMap
                        languageMap.put(languageName, entry.getKey());

                        if (entry.getKey().equals(languageId)) {
                            selectedIndex = index;  // Lưu lại index của ngôn ngữ đã chọn
                        }
                        index++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(LevelEditActivity.this,
                            android.R.layout.simple_spinner_item, languageNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spLanguage.setAdapter(adapter);
                    // Đặt Spinner vào vị trí ngôn ngữ tương ứng
                    if (selectedIndex != -1) {
                        spLanguage.setSelection(selectedIndex);
                    }
                } else {
                    Toast.makeText(LevelEditActivity.this, "Không thể tải thông tin ngôn ngữ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Language>> call, Throwable t) {
                Toast.makeText(LevelEditActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToFirebase(){
        String levelName = etLevelName.getText().toString().trim();
        String selectedLanguageName = spLanguage.getSelectedItem().toString();
        String selectedLanguageId = languageMap.get(selectedLanguageName);

        // Kiểm tra thông tin đầu vào
        if (TextUtils.isEmpty(levelName)) {
            Toast.makeText(this, "Vui lòng nhập tên cấp độ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(selectedLanguageName)) {
            Toast.makeText(this, "Vui lòng chọn ngôn ngữ", Toast.LENGTH_SHORT).show();
            return;
        }

        level.setLevel_name(levelName);
        level.setLanguage_id(selectedLanguageId);

        // Lấy thời gian hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        level.setUpdated_at(currentTime);

        // Lưu cấp độ vào Firebase
        FirebaseApiService.apiService.updateLevel(levelId, level).enqueue(new Callback<Level>() {
            @Override
            public void onResponse(Call<Level> call, Response<Level> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LevelEditActivity.this, "Cập nhật cấp độ thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LevelEditActivity.this, "Cập nhật cấp độ thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Level> call, Throwable t) {
                Toast.makeText(LevelEditActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
