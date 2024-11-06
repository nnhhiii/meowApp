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
import com.example.meowapp.model.LessonInfo;
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
    private String selectedLanguage, selectedLevel;
    private List<LessonInfo> lessonList = new ArrayList<>();
    private List<LessonInfo> filteredLessonList = new ArrayList<>();
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
        loadData();
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
                            selectedLanguage = spLanguage.getSelectedItem().toString();
                            String selectedLanguageId = languageMap.get(spLanguage.getSelectedItem().toString());
                            filterLessons(); // Gọi phương thức lọc chung để áp dụng cả language và level
                            loadDataToSpinnerLevel(selectedLanguageId);
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
                            selectedLevel = spLevel.getSelectedItem().toString();
                            filterLessons();
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

    private void loadData(){
        FirebaseApiService.apiService.getAllLessons().enqueue(new Callback<Map<String, Lesson>>() {
            @Override
            public void onResponse(Call<Map<String, Lesson>> call, Response<Map<String, Lesson>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Lesson> lessonMap = response.body();
                    lessonList.clear();
                    filteredLessonList.clear();

                    // Lấy tất cả ngôn ngữ và cấp độ
                    getAllLanguagesAndLevels(lessonMap);
                } else {
                    Toast.makeText(LessonManagementActivity.this, "Không thể tải", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Lesson>> call, Throwable t) {
                Toast.makeText(LessonManagementActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LessonManagement", "Lỗi: ", t);
            }
        });
    }
    private void filterLessons() {
        filteredLessonList.clear();
        for (LessonInfo lessonInfo : lessonList) {
            boolean matchesLanguage = selectedLanguage == null || lessonInfo.getLanguageName().equals(selectedLanguage);
            boolean matchesLevel = selectedLevel == null || lessonInfo.getLevelName().equals(selectedLevel);

            if (matchesLanguage && matchesLevel) {
                filteredLessonList.add(lessonInfo); // Thêm vào danh sách lọc nếu cả language và level đều khớp
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged(); // Cập nhật lại giao diện danh sách
        }
    }


    private void getAllLanguagesAndLevels(Map<String, Lesson> lessonMap) {
        FirebaseApiService.apiService.getAllLanguage().enqueue(new Callback<Map<String, Language>>() {
            @Override
            public void onResponse(Call<Map<String, Language>> call, Response<Map<String, Language>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Language> languageMap = response.body();
                    FirebaseApiService.apiService.getAllLevel().enqueue(new Callback<Map<String, Level>>() {
                        @Override
                        public void onResponse(Call<Map<String, Level>> call, Response<Map<String, Level>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Map<String, Level> levelMap = response.body();
                                lessonList.clear();
                                filteredLessonList.clear();
                                // Lưu danh sách LessonInfo
                                List<LessonInfo> lessonInfoList = new ArrayList<>();

                                // Xử lý danh sách bài học để thêm tên ngôn ngữ và cấp độ
                                for (Map.Entry<String, Lesson> entry : lessonMap.entrySet()) {
                                    Lesson lesson = entry.getValue();
                                    String lessonId = entry.getKey();

                                    String languageId = lesson.getLanguage_id();
                                    String languageName = getLanguageNameById(languageId, languageMap);

                                    String levelId = lesson.getLevel_id();
                                    String levelName = getLevelNameById(levelId, levelMap);

                                    int score = lesson.getLesson_score();

                                    // Tạo một đối tượng LessonInfo chứa thông tin bài học
                                    LessonInfo lessonInfo = new LessonInfo(lessonId, lesson, languageName, levelName, score);
                                    lessonInfoList.add(lessonInfo);
                                }

                                lessonList.addAll(lessonInfoList); // Thêm dữ liệu vào lessonList
                                filteredLessonList.addAll(lessonList);

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
        Language language = languageMap.get(id);
        return language != null ? language.getLanguage_name() : "Unknown Language"; // Trả về tên mặc định nếu không tìm thấy
    }

    private String getLevelNameById(String id, Map<String, Level> levelMap) {
        Level level = levelMap.get(id);
        return level != null ? level.getLevel_name() : "Unknown Level"; // Trả về tên mặc định nếu không tìm thấy
    }

    // Lọc danh sách cấp độ theo từ khóa
    private void filterLessonList(String keyword) {
        filteredLessonList.clear();

        if (keyword.isEmpty()) {
            filteredLessonList.addAll(lessonList); // Hiển thị tất cả bài học nếu từ khóa trống
        } else {
            for (LessonInfo lessonInfo : lessonList) {
                Lesson lesson = lessonInfo.getLesson();
                String lessonName = lesson.getLesson_name().toLowerCase();
                String languageName = lessonInfo.getLanguageName().toLowerCase();
                String levelName = lessonInfo.getLevelName().toLowerCase();
                String lessonScore = String.valueOf(lessonInfo.getScore()).toLowerCase();

                // Kiểm tra nếu từ khóa có trong lessonName, languageName, levelName hoặc lessonScore
                if (lessonName.contains(keyword.toLowerCase()) ||
                        languageName.contains(keyword.toLowerCase()) ||
                        levelName.contains(keyword.toLowerCase()) ||
                        lessonScore.contains(keyword.toLowerCase())) {
                    filteredLessonList.add(lessonInfo); // Thêm bài học khớp từ khóa vào danh sách lọc
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

