package com.example.meowapp.Level;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.adapter.LevelManagementAdapter;
import com.example.meowapp.Level.LevelCreateActivity;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Level;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LevelManagementActivity extends AppCompatActivity {
    private LevelManagementAdapter adapter;
    private ListView listView;
    private FloatingActionButton btnAdd;
    private ImageButton btnBack;
    private EditText etSearch;
    private List<Pair<String, Level>> levelList = new ArrayList<>();
    private List<Pair<String, Level>> filteredLevelList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_managerment);

        listView = findViewById(R.id.listView);
        etSearch = findViewById(R.id.etSearch);
        btnBack = findViewById(R.id.back_btn);
        btnAdd = findViewById(R.id.add_button);

        // Quay lại màn hình trước đó
        btnBack.setOnClickListener(v -> finish());

        // Mở activity tạo cấp độ mới
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, LevelCreateActivity.class);
            startActivity(intent);
        });

        // Lắng nghe khi có thay đổi trong ô tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần làm gì
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterLevelList(s.toString()); // Lọc danh sách theo từ khóa
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì
            }
        });

        loadLevelData(); // Tải dữ liệu cấp độ từ Firebase
    }

    // Tải tất cả cấp độ từ Firebase
    private void loadLevelData() {
        FirebaseApiService.apiService.getAllLevel().enqueue(new Callback<Map<String, Level>>() {
            @Override
            public void onResponse(Call<Map<String, Level>> call, Response<Map<String, Level>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Level> levelMap = response.body();
                    // Xóa dữ liệu cũ
                    levelList.clear();
                    filteredLevelList.clear();
                    // Chuyển đổi từ Map<String, Level> sang List<Pair<String, Level>>
                    for (Map.Entry<String, Level> entry : levelMap.entrySet()) {
                        Pair<String, Level> pair = new Pair<>(entry.getKey(), entry.getValue());
                        levelList.add(pair);
                        filteredLevelList.add(pair); // Hiển thị toàn bộ danh sách ban đầu
                    }

                    // Cập nhật Adapter
                    adapter = new LevelManagementAdapter(LevelManagementActivity.this, filteredLevelList);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(LevelManagementActivity.this, "Không thể tải dữ liệu cấp độ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Level>> call, Throwable t) {
                Toast.makeText(LevelManagementActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LevelManagement", "Lỗi: ", t);
            }
        });
    }

    // Lọc danh sách cấp độ theo từ khóa
    private void filterLevelList(String keyword) {
        filteredLevelList.clear();
        if (keyword.isEmpty()) {
            filteredLevelList.addAll(levelList); // Hiển thị tất cả cấp độ nếu từ khóa trống
        } else {
            for (Pair<String, Level> pair : levelList) {
                Level level = pair.second;
                if (level.getLevel_name().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredLevelList.add(pair); // Thêm cấp độ khớp từ khóa vào danh sách lọc
                }
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged(); // Cập nhật lại giao diện danh sách
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLevelData(); // Tải lại dữ liệu khi quay lại màn hình
    }
}
