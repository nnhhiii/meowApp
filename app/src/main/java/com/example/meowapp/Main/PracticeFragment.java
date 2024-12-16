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

        setupButtonClickListeners(rootView);

        return rootView;
    }

    private void setupButtonClickListeners(View rootView) {
        RelativeLayout btnTracNghiemHinh = rootView.findViewById(R.id.btnTracNghiemHinh);
        RelativeLayout btnTracNghiem = rootView.findViewById(R.id.btnTracNghiem);
        RelativeLayout btnNghe = rootView.findViewById(R.id.btnNghe);
        RelativeLayout btnNoi = rootView.findViewById(R.id.btnNoi);
        RelativeLayout btnViet = rootView.findViewById(R.id.btnViet);
        RelativeLayout btnSapXepChu = rootView.findViewById(R.id.btnSapXepChu);

        btnTracNghiemHinh.setOnClickListener(v -> loadFragmentWithQuestions("3", this::loadMultipleChoiceImageFragment));
        btnTracNghiem.setOnClickListener(v -> loadFragmentWithQuestions("2", this::loadMultipleChoiceTextFragment));
        btnNghe.setOnClickListener(v -> loadFragmentWithQuestions("4", this::loadListeningFragment));
        btnNoi.setOnClickListener(v -> loadFragmentWithQuestions("5", this::loadSpeakingFragment));
        btnViet.setOnClickListener(v -> loadFragmentWithQuestions("1", this::loadWritingFragment));
//        btnSapXepChu.setOnClickListener(v -> loadFragmentWithQuestions("l1", this::loadArrangeWordsFragment));
    }

    private void loadFragmentWithQuestions(String questionType, QuestionCallback callback) {
        if (apiService != null) {
            apiService.getQuestionsByType("\"question_type\"", "\"" + questionType + "\"").enqueue(new Callback<Map<String, Question>>() {
                @Override
                public void onResponse(Call<Map<String, Question>> call, Response<Map<String, Question>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Question question = response.body().values().iterator().next();
                        callback.onQuestionLoaded(question);
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
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadMultipleChoiceImageFragment(Question question) {
        String[] imageOptions = {question.getImage_option_a(), question.getImage_option_b(), question.getImage_option_c(), question.getImage_option_d()};
        String[] answerOptions = {question.getOption_a(), question.getOption_b(), question.getOption_c(), question.getOption_d()};
        String correctAnswer = question.getCorrect_answer();
        String questionText = question.getQuestion_text();

        replaceFragment(MultipleChoiceImageFragmentNew.newInstance(imageOptions, answerOptions, correctAnswer, questionText));
    }

    private void loadMultipleChoiceTextFragment(Question question) {
        String questionText = question.getQuestion_text();
        String optionA = question.getOption_a();
        String optionB = question.getOption_b();
        String optionC = question.getOption_c();
        String optionD = question.getOption_d();
        String correctAnswer = question.getCorrect_answer();

        replaceFragment(MultipleChoiceTextFragmentNew.newInstance(questionText, optionA, optionB, optionC, optionD, correctAnswer));
    }

    private void loadListeningFragment(Question question) {
        String correctAnswer = question.getCorrect_answer();
        String questionText = question.getQuestion_text();
        String orderWords = question.getOrder_words();

        replaceFragment(ListeningFragmentNew.newInstance(correctAnswer, questionText, orderWords));
    }

    private void loadSpeakingFragment(Question question) {
        replaceFragment(SpeakingFragmentNew.newInstance(question));
    }

    private void loadWritingFragment(Question question) {
        String questionText = question.getQuestion_text();
        String correctAnswer = question.getCorrect_answer();

        replaceFragment(WritingFragmentNew.newInstance(questionText, correctAnswer));
    }

//    private void loadArrangeWordsFragment(Question question) {
//        String correctAnswer = question.getCorrect_answer();
//        String questionText = question.getQuestion_text();
//        String orderWords = question.getOrder_words();
//
//        String[] words = orderWords.split(", ");
//        replaceFragment(ArrangeWordsFragment.newInstance(correctAnswer, questionText, words));
//    }


    @FunctionalInterface
    private interface QuestionCallback {
        void onQuestionLoaded(Question question);
    }
}
