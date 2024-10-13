package com.example.meowapp.Level;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Level;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LevelEditActivity extends AppCompatActivity {
    private EditText etLevelName;
    private Spinner spLanguage;
    private Button btnSave;
    private String levelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_edit);

        etLevelName = findViewById(R.id.et_level_name);
        spLanguage = findViewById(R.id.sp_language);
        btnSave = findViewById(R.id.btn_save_changes);

        // Nhận levelId từ Intent
        if (getIntent() != null && getIntent().hasExtra("LEVEL_ID")) {
            levelId = getIntent().getStringExtra("LEVEL_ID");
            getLevelDetails(levelId);  // Lấy chi tiết cấp độ từ Firebase
        }

        btnSave.setOnClickListener(v -> {
            String levelName = etLevelName.getText().toString().trim();
            String selectedLanguage = spLanguage.getSelectedItem().toString();

            if (TextUtils.isEmpty(levelName)) {
                Toast.makeText(this, "Vui lòng nhập tên cấp độ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật cấp độ
            updateLevel(levelId, levelName, selectedLanguage);
        });
    }

    private void getLevelDetails(String levelId) {
        FirebaseApiService.apiService.getLevelById(levelId).enqueue(new Callback<Level>() {
            @Override
            public void onResponse(Call<Level> call, Response<Level> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Level level = response.body();
                    etLevelName.setText(level.getLevel_name());
                    // Đặt ngôn ngữ vào Spinner...
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

    private void updateLevel(String levelId, String levelName, String language) {
        Level updatedLevel = new Level();
        updatedLevel.setLevel_name(levelName);
//        updatedLevel.setLanguage(language);

        FirebaseApiService.apiService.updateLevel(levelId, updatedLevel).enqueue(new Callback<Level>() {
            @Override
            public void onResponse(Call<Level> call, Response<Level> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LevelEditActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LevelEditActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Level> call, Throwable t) {
                Toast.makeText(LevelEditActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
