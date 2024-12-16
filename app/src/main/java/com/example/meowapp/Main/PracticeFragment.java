package com.example.meowapp.Main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Question;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PracticeFragment extends Fragment {

    private FirebaseApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_practice, container, false);
        apiService = FirebaseApiService.apiService;

        RelativeLayout btnTracNghiemHinh = rootView.findViewById(R.id.btnTracNghiemHinh);

        btnTracNghiemHinh.setOnClickListener(v -> loadMultipleChoiceImageFragment());

        return rootView;
    }

    private void loadMultipleChoiceImageFragment() {
        if (apiService != null) {
            // Gọi API lấy danh sách câu hỏi theo type
            apiService.getQuestionsByType("\"question_type\"", "\"3\"").enqueue(new Callback<Map<String, Question>>() {
                @Override
                public void onResponse(Call<Map<String, Question>> call, Response<Map<String, Question>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        // Duyệt qua Map để lấy câu hỏi đầu tiên
                        Map<String, Question> questionMap = response.body();
                        Question question = questionMap.values().iterator().next(); // Lấy phần tử đầu tiên từ Map

                        // Chuẩn bị dữ liệu cho fragment
                        String[] imageOptions = {
                                question.getImage_option_a(),
                                question.getImage_option_b(),
                                question.getImage_option_c(),
                                question.getImage_option_d()
                        };
                        String[] answerOptions = {
                                question.getOption_a(),
                                question.getOption_b(),
                                question.getOption_c(),
                                question.getOption_d()
                        };
                        String correctAnswer = question.getCorrect_answer();
                        String questionText = question.getQuestion_text();

                        Log.d("PracticeFragment", "Loaded Question: " + question.getQuestion_text());

                        // Chuyển sang fragment MultipleChoiceImageFragmentNew
                        replaceFragment(MultipleChoiceImageFragmentNew.newInstance(imageOptions, answerOptions, correctAnswer, questionText));
                    } else {
                        Log.e("PracticeFragment", "No questions available or API failed.");
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Question>> call, Throwable t) {
                    Log.e("PracticeFragment", "Error loading questions", t);
                }
            });
        } else {
            Log.e("PracticeFragment", "FirebaseApiService is null");
        }
    }

    private void replaceFragment(Fragment fragment) {
        // Thực hiện transaction để thay đổi fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
