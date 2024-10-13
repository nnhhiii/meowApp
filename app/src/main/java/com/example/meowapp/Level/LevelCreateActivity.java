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

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LevelCreateActivity extends AppCompatActivity {
    private EditText etLevelName;
    private Spinner spLanguage;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_add);

        etLevelName = findViewById(R.id.et_level_name);
        spLanguage = findViewById(R.id.sp_language);
        btnSave = findViewById(R.id.btn_save_changes);

        btnSave.setOnClickListener(v -> {
            String levelName = etLevelName.getText().toString().trim();
            String selectedLanguage = spLanguage.getSelectedItem().toString();

            // Kiểm tra thông tin đầu vào
            if (TextUtils.isEmpty(levelName)) {
                Toast.makeText(this, "Vui lòng nhập tên cấp độ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(selectedLanguage)) {
                Toast.makeText(this, "Vui lòng chọn ngôn ngữ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng Level mới
            Level level = new Level();
            level.setLevel_name(levelName);
//            level.setLanguage(selectedLanguage);

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
        });
    }
}
