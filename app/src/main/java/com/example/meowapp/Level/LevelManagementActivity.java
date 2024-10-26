package com.example.meowapp.Level;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

<<<<<<< HEAD
import com.example.meowapp.adapter.LevelManagementAdapter;
=======
import com.example.meowapp.Adapter.LevelManagementAdapter;
import com.example.meowapp.Level.LevelCreateActivity;
>>>>>>> 7d8f6b6614c6d07a776d772e2160e2667fe34a64
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.Level;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LevelManagementActivity extends AppCompatActivity {
    private LevelManagementAdapter adapter;
    private ListView listView;
    private FloatingActionButton btnAdd;
    private Spinner spLanguage;
    private ImageButton btnBack;
    private EditText etSearch;
    private List<Pair<String, Level>> levelList = new ArrayList<>();
    private List<Pair<String, Level>> filteredLevelList = new ArrayList<>();
    private final Map<String, String> languageMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_management);

        listView = findViewById(R.id.listView);
        etSearch = findViewById(R.id.etSearch);
        btnBack = findViewById(R.id.back_btn);
        btnAdd = findViewById(R.id.add_button);
        spLanguage = findViewById(R.id.sp_language);

        btnBack.setOnClickListener(v -> finish());
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(LevelManagementActivity.this, android.R.layout.simple_spinner_item, languageNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Gán Adapter cho Spinner
                    spLanguage.setAdapter(adapter);

                    // Thiết lập OnItemSelectedListener cho Spinner
                    spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            loadData();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Không cần thực hiện hành động nào khi không có lựa chọn
                        }
                    });
                } else {
                    Toast.makeText(LevelManagementActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Language>> call, Throwable t) {
                Toast.makeText(LevelManagementActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        if (spLanguage.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn một ngôn ngữ", Toast.LENGTH_SHORT).show();
            return; // Trả về nếu không có ngôn ngữ được chọn
        }
        String selectedLanguageName = spLanguage.getSelectedItem().toString();
        String selectedLanguageId = "\"" + languageMap.get(selectedLanguageName) + "\""; // Thêm dấu ngoặc kép

        // Gọi API để lấy cấp độ theo language_id
        FirebaseApiService.apiService.getAllLevelByLanguageId("\"language_id\"", selectedLanguageId).enqueue(new Callback<Map<String, Level>>() {
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
                    adapter = new LevelManagementAdapter(LevelManagementActivity.this, filteredLevelList, selectedLanguageName);
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
        loadData(); // Tải lại dữ liệu khi quay lại màn hình
    }
}
