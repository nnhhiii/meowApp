package com.example.meowapp.mission;

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
import com.example.meowapp.model.Mission;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissionCreateActivity extends AppCompatActivity {
    private EditText etMissionName, etRequire, etType;
    private Spinner spRequire, spType;
    private ImageButton btnBack;
    private Button btnSave;

    private String[] requireOptions = {
            "Điểm",
            "Bài học xuất sắc",
            "Bài học đúng trên 80%",
            "Bài học",
            "Chuỗi"
    };

    private String[] typeOptions = {
            "Đá",
            "Tim"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_add);

        etMissionName = findViewById(R.id.et_mission_name);
        etRequire = findViewById(R.id.et_require);
        etType = findViewById(R.id.et_type);
        spRequire = findViewById(R.id.sp_require);
        spType = findViewById(R.id.sp_type);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btn_save_changes);

        ArrayAdapter<String> requireAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, requireOptions);
        requireAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRequire.setAdapter(requireAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeOptions);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveMission());
    }

    private void saveMission() {
        String missionName = etMissionName.getText().toString().trim();
        String selectedRequire = spRequire.getSelectedItem().toString();
        String selectedType = spType.getSelectedItem().toString();
        String requireValue = etRequire.getText().toString().trim(); // Giá trị người dùng nhập
        String typeValue = etType.getText().toString().trim();

        if (TextUtils.isEmpty(missionName)) {
            Toast.makeText(this, "Vui lòng nhập tên nhiệm vụ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(requireValue)) {
            Toast.makeText(this, "Vui lòng nhập giá trị yêu cầu", Toast.LENGTH_SHORT).show();
            return;
        }

        String rewardType = "";
        switch (selectedType) {
            case "Đá":
                rewardType = "diamond";
                break;
            case "Tim":
                rewardType = "heart";
                break;
        }

        Mission mission = new Mission();
        mission.setMissionName(missionName);
        mission.setRewardType(rewardType);
        mission.setRewardAmount(Integer.parseInt(typeValue));

        // Gán giá trị cho thuộc tính tương ứng dựa trên lựa chọn và giá trị nhập
        try {
            int intValue = Integer.parseInt(requireValue); // Chuyển đổi giá trị nhập sang số nguyên
            switch (selectedRequire) {
                case "Điểm":
                    mission.setRequiredScore(intValue);
                    break;
                case "Bài học xuất sắc":
                    mission.setRequiredPerfectLessons(intValue);
                    break;
                case "Bài học đúng trên 80%":
                    mission.setRequiredEightyLessons(intValue);
                    break;
                case "Bài học":
                    mission.setRequiredLessons(intValue);
                    break;
                case "Chuỗi":
                    mission.setRequiredStreaks(intValue);
                    break;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá trị yêu cầu phải là số nguyên", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thêm thời gian hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());
        mission.setCreated_at(currentTime);
        mission.setUpdated_at(currentTime);

        // Gọi API để lưu vào Firebase
        FirebaseApiService.apiService.addMission(mission).enqueue(new Callback<Mission>() {
            @Override
            public void onResponse(Call<Mission> call, Response<Mission> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MissionCreateActivity.this, "Thêm nhiệm vụ thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MissionCreateActivity.this, "Thêm nhiệm vụ thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Mission> call, Throwable t) {
                Toast.makeText(MissionCreateActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
