package com.example.meowapp.mission;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
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

public class MissionEditActivity extends AppCompatActivity {

    private EditText etMissionName, etRequire, etType;
    private Spinner spRequire, spType;
    private ImageButton btnBack;
    private Button btnSave;
    private Mission mission;
    private String missionId;

    private String[] requireOptions = {
            "Điểm",
            "Bài học xuất sắc",
            "Bài học đúng trên 80%",
            "Bài học",
            "Chuỗi"
    };

    private String[] typeOptions = {
            "diamond",
            "heart"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_edit);

        // Ánh xạ các view
        etMissionName = findViewById(R.id.et_mission_name);
        etRequire = findViewById(R.id.et_require);
        etType = findViewById(R.id.et_type);
        spRequire = findViewById(R.id.sp_require);
        spType = findViewById(R.id.sp_type);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnSave = findViewById(R.id.btn_save_changes);
        btnSave.setOnClickListener(v -> saveMissionData());

        // Thiết lập Spinner
        setupSpinners();

        // Tải dữ liệu nhiệm vụ
        loadData();
    }

    private void setupSpinners() {
        // Thiết lập Spinner spRequire
        ArrayAdapter<String> requireAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, requireOptions);
        requireAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRequire.setAdapter(requireAdapter);

        // Thiết lập Spinner spType
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeOptions);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);
    }

    private void loadData() {
        // Lấy missionId từ Intent
        missionId = getIntent().getStringExtra("missionId");

        if (!TextUtils.isEmpty(missionId)) {
            FirebaseApiService.apiService.getMissionById(missionId).enqueue(new Callback<Mission>() {
                @Override
                public void onResponse(Call<Mission> call, Response<Mission> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        mission = response.body();

                        // Thiết lập giá trị cho các view
                        etMissionName.setText(mission.getMissionName());
                        etType.setText(String.valueOf(mission.getRewardAmount()));

                        // Đặt giá trị rewardType vào spType
                        setSpinnerValue(spType, mission.getRewardType());

                        // Lấy yêu cầu đầu tiên > 0 và thiết lập vào spRequire, etRequire
                        Pair<String, String> requirePair = getFirstNonZeroRequire(mission);
                        etRequire.setText(requirePair.second); // Giá trị số
                        setSpinnerValue(spRequire, requirePair.first); // Giá trị mô tả
                    } else {
                        Toast.makeText(MissionEditActivity.this, "Không thể tải nhiệm vụ", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Mission> call, Throwable t) {
                    Toast.makeText(MissionEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "ID nhiệm vụ không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMissionData() {
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
        // Đảm bảo sự tương thích giữa giá trị trong Spinner và giá trị rewardType
        switch (selectedType) {
            case "diamond":
                rewardType = "diamond";
                break;
            case "heart":
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
        FirebaseApiService.apiService.updateMission(missionId, mission).enqueue(new Callback<Mission>() {
            @Override
            public void onResponse(Call<Mission> call, Response<Mission> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MissionEditActivity.this, "Cập nhật nhiệm vụ thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MissionEditActivity.this, "Cập nhật nhiệm vụ thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Mission> call, Throwable t) {
                Toast.makeText(MissionEditActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(value); // Tìm vị trí của giá trị trong Spinner
        if (position >= 0) {
            spinner.setSelection(position); // Đặt vị trí cho Spinner
        }
    }

    private Pair<String, String> getFirstNonZeroRequire(Mission mission) {
        if (mission.getRequiredScore() > 0) return new Pair<>("Điểm", String.valueOf(mission.getRequiredScore()));
        if (mission.getRequiredPerfectLessons() > 0)
            return new Pair<>("Bài học xuất sắc", String.valueOf(mission.getRequiredPerfectLessons()));
        if (mission.getRequiredEightyLessons() > 0)
            return new Pair<>("Bài học đúng trên 80%", String.valueOf(mission.getRequiredEightyLessons()));
        if (mission.getRequiredLessons() > 0)
            return new Pair<>("Bài học", String.valueOf(mission.getRequiredLessons()));
        if (mission.getRequiredStreaks() > 0)
            return new Pair<>("Chuỗi", String.valueOf(mission.getRequiredStreaks()));
        return new Pair<>("Điểm", "0"); // Mặc định
    }
}
