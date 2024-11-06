package com.example.meowapp.questionType;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.meowapp.R;
import com.example.meowapp.adapter.QuestionAdapter;
import com.example.meowapp.api.FirebaseApiService;

import com.example.meowapp.lesson.LessonEditActivity;
import com.example.meowapp.model.Question;
import com.example.meowapp.model.QuestionType;

import java.util.ArrayList;
import java.util.Map;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlankActivity extends AppCompatActivity {
    private List<String> questionIds = new ArrayList<>();
    private List<Pair<String, QuestionType>> questionTypeList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private Map<String, Question> questionMap;
    private String lessonId;
    private Map<String, QuestionType> questionTypeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blank);
        lessonId = getIntent().getStringExtra("LESSON_ID");
        loadData();
    }

    private void loadData() {
        FirebaseApiService.apiService.getQuestionsByLessonId("\"lesson_id\"", "\"" + lessonId + "\"")
                .enqueue(new Callback<Map<String, Question>>() {
                    @Override
                    public void onResponse(Call<Map<String, Question>> call, Response<Map<String, Question>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            questionMap = response.body();
                            questionIds.addAll(questionMap.keySet());
                            getAllQuestionType();
                        } else {
                            Toast.makeText(BlankActivity.this, "Failed to get info", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Question>> call, Throwable t) {
                        Toast.makeText(BlankActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("error:", t.getMessage(), t);
                    }
                });
    }

    private void getAllQuestionType() {
        questionTypeList.clear();
        FirebaseApiService.apiService.getAllQuestionType().enqueue(new Callback<Map<String, QuestionType>>() {
            @Override
            public void onResponse(Call<Map<String, QuestionType>> call, Response<Map<String, QuestionType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionTypeMap = response.body();
                    for (Map.Entry<String, QuestionType> entry : questionTypeMap.entrySet()) {
                        Pair<String, QuestionType> pair = new Pair<>(entry.getKey(), entry.getValue());
                        questionTypeList.add(pair);
                    }
                    loadFragment(currentQuestionIndex);
                } else {
                    Toast.makeText(BlankActivity.this, "Không thể tải thông tin ngôn ngữ", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, QuestionType>> call, Throwable t) {
                Toast.makeText(BlankActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFragment(int index) {
        if (index < questionIds.size()) {
            String questionId = questionIds.get(index); // Lấy ID
            Question currentQuestion = questionMap.get(questionId); // Lấy câu hỏi từ questionMap

            if (currentQuestion != null) {
                Bundle bundle = new Bundle();
                bundle.putString("questionId", questionId);

                Fragment fragment = null;
                String questionTypeKey = currentQuestion.getQuestion_type(); // Lấy question type key từ câu hỏi
                QuestionType questionType = questionTypeMap.get(questionTypeKey); // Lấy QuestionType từ map

                // Tạo fragment dựa trên loại câu hỏi
                switch (questionType.getQuestion_type_name()) {
                    case "order_words":
                        fragment = new OrderWordFragment();
                        break;
                    case "writing":
                        fragment = new WritingFragment();
                        break;
                    case "multiple_choice":
                        fragment = new MultipleChoiceFragment();
                        break;
                    case "multiple_choice_image":
                        fragment = new MultipleChoiceImageFragment();
                        break;
                    default:
                        Toast.makeText(BlankActivity.this, "Unknown question type", Toast.LENGTH_SHORT).show();
                        return;
                }

                fragment.setArguments(bundle); // Gán Bundle cho fragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        } else {
            Intent intent = new Intent(this, FinishActivity.class);
            intent.putExtra("LESSON_ID", lessonId);
            startActivity(intent);
            finish();
        }
    }
    public void onQuestionCompleted() {
        currentQuestionIndex++;
        loadFragment(currentQuestionIndex);
    }
}