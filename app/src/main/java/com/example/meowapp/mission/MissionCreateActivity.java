package com.example.meowapp.mission;

import android.annotation.SuppressLint;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissionCreateActivity extends AppCompatActivity {
    private EditText etMissionName, etRequire, etType;
    private Spinner spRequire, spType;
    private ImageButton btnBack;
    private Button btnSave;

    // Danh sách các lựa chọn cho Spinner
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

        // Ánh xạ view
        etMissionName = findViewById(R.id.et_mission_name);
        etRequire = findViewById(R.id.et_require);
        etType = findViewById(R.id.et_type);
        spRequire = findViewById(R.id.sp_require);
        spType = findViewById(R.id.sp_type);

        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btn_save_changes);

        // Đổ dữ liệu vào Spinner spRequire
        ArrayAdapter<String> requireAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, requireOptions);
        requireAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRequire.setAdapter(requireAdapter);

        // Đổ dữ liệu vào Spinner spType
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeOptions);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        // Xử lý khi nhấn nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý khi nhấn nút Save
        btnSave.setOnClickListener(v -> saveMission());
    }

    private void saveMission() {
        // Lấy lựa chọn từ Spinner spRequire
        String selectedRequire = spRequire.getSelectedItem().toString();
        String requireAttribute = "";

        // Chuyển đổi lựa chọn thành thuộc tính tương ứng
        switch (selectedRequire) {
            case "Điểm":
                requireAttribute = "requiredScore";
                break;
            case "Bài học xuất sắc":
                requireAttribute = "requiredPerfectLessons";
                break;
            case "Bài học đúng trên 80%":
                requireAttribute = "requiredEightyLessons";
                break;
            case "Bài học":
                requireAttribute = "requiredLessons";
                break;
            case "Chuỗi":
                requireAttribute = "requiredStreaks";
                break;
        }

        // Lấy lựa chọn từ Spinner spType
        String selectedType = spType.getSelectedItem().toString();
        String rewardType = "";

        // Chuyển đổi lựa chọn thành rewardType tương ứng
        switch (selectedType) {
            case "Đá":
                rewardType = "diamond";
                break;
            case "Tim":
                rewardType = "heart";
                break;
        }

        // Lấy tên nhiệm vụ từ EditText
        String missionName = etMissionName.getText().toString().trim();

        // Hiển thị kết quả để kiểm tra
        Toast.makeText(this,
                "Mission Name: " + missionName + "\nRequire: " + requireAttribute + "\nReward Type: " + rewardType,
                Toast.LENGTH_SHORT).show();

        // TODO: Thêm logic lưu dữ liệu vào database hoặc xử lý khác nếu cần
    }
}
