package com.example.meowapp.questionType;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;

import com.example.meowapp.model.Question;

import java.util.ArrayList;
import java.util.Map;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlankActivity extends AppCompatActivity {
    private List<String> questionIds = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private Map<String, Question> questionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blank);
        loadData();
    }
    private void loadData() {
        FirebaseApiService.apiService.getQuestionsByLessonId("\"lesson_id\"", "1").enqueue(new Callback<Map<String, Question>>() {
            @Override
            public void onResponse(Call<Map<String, Question>> call, Response<Map<String, Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionMap = response.body();

                    // Kiểm tra nếu `Map` có chứa dữ liệu
                    if (!questionMap.isEmpty()) {
                        // Lưu tất cả questionId vào danh sách
                        questionIds.addAll(questionMap.keySet());

                        // Tải câu hỏi đầu tiên
                        loadFirstFragment();
                    } else {
                        Toast.makeText(BlankActivity.this, "No questions available", Toast.LENGTH_SHORT).show();
                    }

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

    private void loadFirstFragment() {
        if (currentQuestionIndex < questionIds.size()) {
            String questionId = questionIds.get(currentQuestionIndex); // Lấy ID
            Question currentQuestion = questionMap.get(questionId); // Lấy câu hỏi từ questionMap

            if (currentQuestion != null) {
                Bundle bundle = new Bundle();
                bundle.putString("questionId", questionId);

                Fragment fragment;
                switch (currentQuestion.getQuestion_type()) {
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
                        .commit();
            }
        }
    }

    public void onQuestionCompleted() {
        currentQuestionIndex++;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);

        loadNextFragment(transaction); // Tải câu hỏi mới
    }
    private void loadNextFragment(FragmentTransaction transaction) {
        if (currentQuestionIndex < questionIds.size()) {
            String questionId = questionIds.get(currentQuestionIndex); // Lấy ID
            Question currentQuestion = questionMap.get(questionId); // Lấy câu hỏi từ questionMap

            if (currentQuestion != null) {
                Bundle bundle = new Bundle();
                bundle.putString("questionId", questionId);

                Fragment fragment;
                switch (currentQuestion.getQuestion_type()) {
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
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }




}