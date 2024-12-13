package com.example.meowapp.mission;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.meowapp.R;
import com.example.meowapp.adapter.MissionManagementAdapter;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.Mission;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissionManagementActivity extends AppCompatActivity {
    private MissionManagementAdapter adapter;
    private ListView listView;
    private FloatingActionButton btnAdd;
    private EditText etSearch;
    private List<Pair<String, Mission>> missionList = new ArrayList<>();
    private List<Pair<String, Mission>> filteredMissionList = new ArrayList<>();
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_management);

        // Ánh xạ view
        listView = findViewById(R.id.listView);
        etSearch = findViewById(R.id.etSearch);
        btnBack = findViewById(R.id.back_btn);
        btnAdd = findViewById(R.id.add_button);

        // Gán sự kiện
        btnBack.setOnClickListener(v -> finish());
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, MissionCreateActivity.class);
            startActivity(intent);
        });

        // Load dữ liệu
        loadMissions();

        // Thiết lập tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filteredMissions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }

    private void loadMissions() {
        FirebaseApiService.apiService.getAllMission().enqueue(new Callback<Map<String, Mission>>() {
            @Override
            public void onResponse(Call<Map<String, Mission>> call, Response<Map<String, Mission>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    missionList.clear();
                    for (Map.Entry<String, Mission> entry : response.body().entrySet()) {
                        missionList.add(new Pair<>(entry.getKey(), entry.getValue()));
                    }
                    filteredMissionList = new ArrayList<>(missionList);
                    adapter = new MissionManagementAdapter(MissionManagementActivity.this, filteredMissionList);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(MissionManagementActivity.this, "Không thể tải danh sách nhiệm vụ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Mission>> call, Throwable t) {
                Toast.makeText(MissionManagementActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filteredMissions(String keyword) {
        filteredMissionList.clear();
        if (keyword.isEmpty()) {
            filteredMissionList.addAll(missionList); // Nếu không có từ khóa, hiển thị tất cả
        } else {
            for (Pair<String, Mission> pair : missionList) {
                Mission mission = pair.second; // Lấy đối tượng từ cặp Pair
                if (mission.getMissionName().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredMissionList.add(pair); // Thêm cặp Pair vào danh sách lọc
                }
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged(); // Cập nhật lại danh sách hiển thị
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadMissions(); // Tải lại dữ liệu khi quay lại màn hình
    }
}
