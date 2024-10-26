package com.example.meowapp.lesson;

import android.annotation.SuppressLint;
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

import com.example.meowapp.adapter.LessonManagementAdapter;
import com.example.meowapp.adapter.SelectLevelAdapter;
import com.example.meowapp.lesson.LessonCreateActivity;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.Lesson;
import com.example.meowapp.model.Level;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonManagementActivity extends AppCompatActivity {
    private LessonManagementAdapter adapter;
    private ListView listView;
    private FloatingActionButton btnAdd;
    private Spinner spLanguage, spLevel;
    private ImageButton btnBack;
    private EditText etSearch;
    private String language_id;
    private List<Pair<String, Lesson>> LessonList = new ArrayList<>();
    private List<Pair<String, Lesson>> filteredLessonList = new ArrayList<>();
    private final Map<String, String> languageMap = new HashMap<>();
    private final Map<String, String> levelMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_management);

        listView = findViewById(R.id.listView);
        etSearch = findViewById(R.id.etSearch);
        btnBack = findViewById(R.id.back_btn);
        btnAdd = findViewById(R.id.add_button);
        spLanguage = findViewById(R.id.sp_language);
        spLevel = findViewById(R.id.sp_level);

        btnBack.setOnClickListener(v -> finish());
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, LessonCreateActivity.class);
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
                filterLessonList(s.toString()); // Lọc danh sách theo từ khóa
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì
            }
        });
        loadDataToSpinnerLanguage();
    }
    private void loadDataToSpinnerLanguage() {
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(LessonManagementActivity.this, android.R.layout.simple_spinner_item, languageNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Gán Adapter cho Spinner
                    spLanguage.setAdapter(adapter);

                    // Thiết lập OnItemSelectedListener cho Spinner
                    spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedLanguage = languageMap.get(spLanguage.getSelectedItem().toString()); // Lấy language_id từ languageMap
                            loadData();
                            loadDataToSpinnerLevel(selectedLanguage); // Gọi hàm tải Level với language_id
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Không cần thực hiện hành động nào khi không có lựa chọn
                        }
                    });
                } else {
                    Toast.makeText(LessonManagementActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Language>> call, Throwable t) {
                Toast.makeText(LessonManagementActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataToSpinnerLevel(String language_id) {
        FirebaseApiService.apiService.getAllLevelByLanguageId("\"language_id\"","\"" + language_id + "\"").enqueue(new Callback<Map<String, Level>>() {
            @Override
            public void onResponse(Call<Map<String, Level>> call, Response<Map<String, Level>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Level> responseMap = response.body();

                    // Tạo danh sách các tên ngôn ngữ và lưu language_id tương ứng
                    List<String> levelNames = new ArrayList<>();
                    for (Map.Entry<String, Level> entry : responseMap.entrySet()) {
                        levelMap.put(entry.getValue().getLevel_name(), entry.getKey()); // Lưu cặp tên-key
                        levelNames.add(entry.getValue().getLevel_name()); // Chỉ lấy tên
                    }

                    // Tạo Adapter cho Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(LessonManagementActivity.this, android.R.layout.simple_spinner_item, levelNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Gán Adapter cho Spinner
                    spLevel.setAdapter(adapter);

                    // Thiết lập OnItemSelectedListener cho Spinner
                    spLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    Toast.makeText(LessonManagementActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Level>> call, Throwable t) {
                Toast.makeText(LessonManagementActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        if (spLanguage.getSelectedItem() == null) {
            // Gọi API lấy tất cả bài học
            FirebaseApiService.apiService.getAllLesson().enqueue(new Callback<Map<String, Lesson>>() {
                @Override
                public void onResponse(Call<Map<String, Lesson>> call, Response<Map<String, Lesson>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Lesson> lessonMap = response.body();
                        LessonList.clear();
                        filteredLessonList.clear();

                        // Gọi API lấy tất cả ngôn ngữ và cấp độ
                        getAllLanguagesAndLevels(lessonMap);
                    } else {
                        Toast.makeText(LessonManagementActivity.this, "Không thể tải dữ liệu bài học", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Lesson>> call, Throwable t) {
                    Toast.makeText(LessonManagementActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("LessonManagement", "Lỗi: ", t);
                }
            });
        } else {
            String selectedLanguageName = spLanguage.getSelectedItem().toString();
            String selectedLanguageId = "\"" + languageMap.get(selectedLanguageName) + "\"";

            FirebaseApiService.apiService.getAllLessonByLanguageId("\"language_id\"", selectedLanguageId).enqueue(new Callback<Map<String, Lesson>>() {
                @Override
                public void onResponse(Call<Map<String, Lesson>> call, Response<Map<String, Lesson>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Lesson> lessonMap = response.body();
                        LessonList.clear();
                        filteredLessonList.clear();

                        // Gọi API lấy tất cả ngôn ngữ và cấp độ
                        getAllLanguagesAndLevels(lessonMap);
                    } else {
                        Toast.makeText(LessonManagementActivity.this, "Không thể tải dữ liệu bài học", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Lesson>> call, Throwable t) {
                    Toast.makeText(LessonManagementActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("LessonManagement", "Lỗi: ", t);
                }
            });
        }
    }


    private void getAllLanguagesAndLevels(Map<String, Lesson> lessonMap) {
        // Lấy danh sách ngôn ngữ
        FirebaseApiService.apiService.getAllLanguage().enqueue(new Callback<Map<String, Language>>() {
            @Override
            public void onResponse(Call<Map<String, Language>> call, Response<Map<String, Language>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Language> languageMap = response.body();

                    // Lấy danh sách cấp độ
                    FirebaseApiService.apiService.getAllLevel().enqueue(new Callback<Map<String, Level>>() {
                        @Override
                        public void onResponse(Call<Map<String, Level>> call, Response<Map<String, Level>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Map<String, Level> levelMap = response.body();

                                // Xử lý danh sách bài học để thêm tên ngôn ngữ và cấp độ
                                for (Map.Entry<String, Lesson> entry : lessonMap.entrySet()) {
                                    Lesson lesson = entry.getValue();
                                    String lessonId = entry.getKey();

                                    // Lấy tên ngôn ngữ tương ứng
                                    String languageId = lesson.getLanguage_id();
                                    String languageName = getLanguageNameById(languageId, languageMap);

                                    // Lấy tên cấp độ tương ứng
                                    String levelId = lesson.getLevel_id();
                                    String levelName = getLevelNameById(levelId, levelMap);

                                    // Cập nhật danh sách bài học
                                    lesson.setLanguage_id(languageId);
                                    lesson.setLevel_id(levelId);

                                    // Thêm bài học vào danh sách
                                    filteredLessonList.add(lesson);
                                }

                                // Cập nhật Adapter
                                adapter = new LessonManagementAdapter(LessonManagementActivity.this, filteredLessonList);
                                listView.setAdapter(adapter);
                            } else {
                                Toast.makeText(LessonManagementActivity.this, "Không thể tải dữ liệu cấp độ", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Level>> call, Throwable t) {
                            Toast.makeText(LessonManagementActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("LessonManagement", "Lỗi: ", t);
                        }
                    });
                } else {
                    Toast.makeText(LessonManagementActivity.this, "Không thể tải dữ liệu ngôn ngữ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Language>> call, Throwable t) {
                Toast.makeText(LessonManagementActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LessonManagement", "Lỗi: ", t);
            }
        });
    }
    private String getLanguageNameById(String id, Map<String, Language> languageMap) {
        for (Map.Entry<String, Language> entry : languageMap.entrySet()) {
            if (entry.getKey().equals(id)) {
                return entry.getValue().getLanguage_name();
            }
        }
        return "Unknown Language"; // Trả về tên mặc định nếu không tìm thấy
    }

    private String getLevelNameById(String id, Map<String, Level> levelMap) {
        for (Map.Entry<String, Level> entry : levelMap.entrySet()) {
            if (entry.getKey().equals(id)) {
                return entry.getValue().getLevel_name();
            }
        }
        return "Unknown Level"; // Trả về tên mặc định nếu không tìm thấy
    }



    // Lọc danh sách cấp độ theo từ khóa
    private void filterLessonList(String keyword) {
        filteredLessonList.clear();
        if (keyword.isEmpty()) {
            filteredLessonList.addAll(LessonList); // Hiển thị tất cả cấp độ nếu từ khóa trống
        } else {
            for (Pair<String, Lesson> pair : LessonList) {
                Lesson Lesson = pair.second;
                if (Lesson.getLesson_name().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredLessonList.add(pair); // Thêm cấp độ khớp từ khóa vào danh sách lọc
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
