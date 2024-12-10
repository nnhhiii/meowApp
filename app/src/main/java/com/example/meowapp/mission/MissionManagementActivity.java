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
    private Spinner spRequire, spType;
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
        spRequire = findViewById(R.id.sp_require);
        spType = findViewById(R.id.sp_type);

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMissions();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Thiết lập spinner
        setupSpinners();
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

    private void setupSpinners() {
        // Thiết lập spinner yêu cầu
        ArrayAdapter<CharSequence> requireAdapter = ArrayAdapter.createFromResource(
                this, R.array.require_options, android.R.layout.simple_spinner_item);
        requireAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRequire.setAdapter(requireAdapter);

        // Thiết lập spinner loại
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.type_options, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        // Lọc theo yêu cầu và loại
        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterMissions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spRequire.setOnItemSelectedListener(filterListener);
        spType.setOnItemSelectedListener(filterListener);
    }

    private void filterMissions() {
        String searchText = etSearch.getText().toString().toLowerCase();
        String selectedRequire = spRequire.getSelectedItem().toString();
        String selectedType = spType.getSelectedItem().toString();

        filteredMissionList.clear();
        for (Pair<String, Mission> pair : missionList) {
            Mission mission = pair.second;
            boolean matchesSearch = mission.getMissionName().toLowerCase().contains(searchText);
            boolean matchesRequire = selectedRequire.equals("Tất cả") || mission.getRewardType().equals(selectedRequire);
            boolean matchesType = selectedType.equals("Tất cả") || mission.getRewardType().equals(selectedType);

            if (matchesSearch && matchesRequire && matchesType) {
                filteredMissionList.add(pair);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
