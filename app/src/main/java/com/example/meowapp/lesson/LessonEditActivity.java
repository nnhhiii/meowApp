package com.example.meowapp.lesson;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meowapp.Level.LevelEditActivity;
import com.example.meowapp.R;
import com.example.meowapp.adapter.QuestionAdapter;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.Lesson;
import com.example.meowapp.model.Level;
import com.example.meowapp.model.Question;
import com.example.meowapp.model.QuestionType;
import com.example.meowapp.model.User;
import com.example.meowapp.user.UserAdapter;
import com.example.meowapp.user.UserManagementActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonEditActivity extends AppCompatActivity{
    private EditText etName, etScore;
    private Spinner spLevel;
    private Button btnSave, btnAdd, btnCancel;
    private ImageButton btnBack, btnEdit;
    private Map<String, String> levelMap = new HashMap<>();
    private List<Pair<String, Question>> questionList = new ArrayList<>();
    private List<Pair<String, QuestionType>> questionTypeList = new ArrayList<>();
    private String levelId, lessonId, languageId;
    private Lesson lesson;
    private RecyclerView recyclerView;
    private QuestionAdapter adapter;
    private RelativeLayout layoutBtnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lesson_edit);

        layoutBtnEdit = findViewById(R.id.layoutBtnEdit);
        spLevel = findViewById(R.id.sp_level);
        etName = findViewById(R.id.etLessonName);
        etScore = findViewById(R.id.etScore);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        lessonId = getIntent().getStringExtra("lessonId");

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuestionCreateActivity.class);
            intent.putExtra("lessonId", lessonId);
            startActivity(intent);
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> {
            btnEdit.setVisibility(View.GONE);
            layoutBtnEdit.setVisibility(View.VISIBLE);
        });

        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> {
            btnEdit.setVisibility(View.VISIBLE);
            layoutBtnEdit.setVisibility(View.GONE);
        });

        btnSave = findViewById(R.id.btnSaveLesson);
        btnSave.setOnClickListener(v -> saveToFirebase());

        loadData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QuestionAdapter.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            adapter.updateImageAtPosition(imageUri);
        }
    }


    private void loadData() {
        FirebaseApiService.apiService.getLessonById(lessonId).enqueue(new Callback<Lesson>() {
            @Override
            public void onResponse(Call<Lesson> call, Response<Lesson> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lesson = response.body();
                    etName.setText(lesson.getLesson_name());
                    etScore.setText(String.valueOf(lesson.getLesson_score()));
                    levelId = lesson.getLevel_id();
                    languageId = lesson.getLanguage_id();
                    loadLevelNameById(languageId, levelId);
                    loadQuestionsByLessonId(lessonId);
                } else {
                    Toast.makeText(LessonEditActivity.this, "Không thể tải thông tin cấp độ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Lesson> call, Throwable t) {
                Toast.makeText(LessonEditActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadLevelNameById(String languageId, String levelId) {
        FirebaseApiService.apiService.getAllLevelByLanguageId("\"language_id\"","\"" + languageId + "\"").enqueue(new Callback<Map<String, Level>>() {
            @Override
            public void onResponse(Call<Map<String, Level>> call, Response<Map<String, Level>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Level> levelMapResponse = response.body();
                    List<String> levelNames = new ArrayList<>();
                    int selectedIndex = -1;
                    int index = 0;

                    for (Map.Entry<String, Level> entry : levelMapResponse.entrySet()) {
                        String levelName = entry.getValue().getLevel_name();
                        levelNames.add(levelName);

                        // Lưu cặp languageName và languageId vào languageMap
                        levelMap.put(levelName, entry.getKey());

                        if (entry.getKey().equals(levelId)) {
                            selectedIndex = index;  // Lưu lại index của ngôn ngữ đã chọn
                        }
                        index++;
                    }

                    // Tạo Adapter cho Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(LessonEditActivity.this, android.R.layout.simple_spinner_item, levelNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Gán Adapter cho Spinner
                    spLevel.setAdapter(adapter);

                    // Đặt Spinner vào vị trí ngôn ngữ tương ứng
                    if (selectedIndex != -1) {
                        spLevel.setSelection(selectedIndex);
                    }
                } else {
                    Toast.makeText(LessonEditActivity.this, "Không thể tải thông tin ngôn ngữ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Level>> call, Throwable t) {
                Toast.makeText(LessonEditActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestionsByLessonId(String lessonId){
        questionList.clear();
        FirebaseApiService.apiService.getQuestionsByLessonId("\"lesson_id\"","\"" + lessonId + "\"").enqueue(new Callback<Map<String, Question>>() {
            @Override
            public void onResponse(Call<Map<String, Question>> call, Response<Map<String, Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Question> questionMap = response.body();
                    for (Map.Entry<String, Question> entry : questionMap.entrySet()) {
                        Pair<String, Question> pair = new Pair<>(entry.getKey(), entry.getValue());
                        questionList.add(pair);
                    }
                    getAllQuestionType();
                } else {
                    Toast.makeText(LessonEditActivity.this, "Không thể tải thông tin ngôn ngữ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Question>> call, Throwable t) {
                Toast.makeText(LessonEditActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllQuestionType(){
        questionTypeList.clear();
        FirebaseApiService.apiService.getAllQuestionType().enqueue(new Callback<Map<String, QuestionType>>() {
            @Override
            public void onResponse(Call<Map<String, QuestionType>> call, Response<Map<String, QuestionType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, QuestionType> typeMap = response.body();
                    for (Map.Entry<String, QuestionType> entry : typeMap.entrySet()) {
                        Pair<String, QuestionType> pair = new Pair<>(entry.getKey(), entry.getValue());
                        questionTypeList.add(pair);
                    }
                    adapter = new QuestionAdapter(recyclerView,LessonEditActivity.this, questionList, questionTypeList, lessonId);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(LessonEditActivity.this, "Không thể tải thông tin ngôn ngữ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, QuestionType>> call, Throwable t) {
                Toast.makeText(LessonEditActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveToFirebase() {
        String selectedLevelName = spLevel.getSelectedItem().toString();
        String selectedLevelId = levelMap.get(selectedLevelName);
        String lessonName = etName.getText().toString().trim();
        String lessonScore = etScore.getText().toString().trim();

        // Kiểm tra thông tin đầu vào
        if (TextUtils.isEmpty(selectedLevelName)) {
            Toast.makeText(this, "Vui lòng chọn cấp độ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(lessonName)) {
            Toast.makeText(this, "Vui lòng nhập tên bài học", Toast.LENGTH_SHORT).show();
            return;
        }

        lesson.setLesson_name(lessonName);
        lesson.setLesson_score(Integer.parseInt(lessonScore));
        lesson.setLevel_id(selectedLevelId);
        lesson.setLanguage_id(languageId);


        // Lấy thời gian hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        lesson.setCreated_at(currentTime);
        lesson.setUpdated_at(currentTime);

        // Lưu bài học vào Firebase
        FirebaseApiService.apiService.updateLesson(lessonId,lesson).enqueue(new Callback<Lesson>() {
            @Override
            public void onResponse(Call<Lesson> call, Response<Lesson> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LessonEditActivity.this, "Chỉnh sửa bài học thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LessonEditActivity.this, "Chỉnh sửa bài học thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Lesson> call, Throwable t) {
                Toast.makeText(LessonEditActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}