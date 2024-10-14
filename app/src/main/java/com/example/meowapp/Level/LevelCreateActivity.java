package com.example.meowapp.Level;

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

public class LevelCreateActivity extends AppCompatActivity {
    private EditText etLevelName;
    private Spinner spLanguage;
    private ImageButton btnBack;
    private Button btnSave;
    private Map<String, String> languageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_add);

        etLevelName = findViewById(R.id.et_level_name);
        spLanguage = findViewById(R.id.sp_language);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnSave = findViewById(R.id.btn_save_changes);
        btnSave.setOnClickListener(v -> saveToFirebase());

        loadDataToSpinner();
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(LevelCreateActivity.this, android.R.layout.simple_spinner_item, languageNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Gán Adapter cho Spinner
                    spLanguage.setAdapter(adapter);
                } else {
                    Toast.makeText(LevelCreateActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Language>> call, Throwable t) {
                Toast.makeText(LevelCreateActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

        // Tạo đối tượng Level mới
        Level level = new Level();
        level.setLevel_name(levelName);
        level.setLanguage_id(selectedLanguageId);

        // Lấy thời gian hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        level.setCreated_at(currentTime);
        level.setUpdated_at(currentTime);

        // Lưu cấp độ vào Firebase
        FirebaseApiService.apiService.addLevel(level).enqueue(new Callback<Level>() {
            @Override
            public void onResponse(Call<Level> call, Response<Level> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LevelCreateActivity.this, "Thêm cấp độ thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LevelCreateActivity.this, "Thêm cấp độ thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Level> call, Throwable t) {
                Toast.makeText(LevelCreateActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
