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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private void loadFragmentWithQuestions(String questionType, QuestionsCallback callback) {
        if (apiService != null) {
            apiService.getQuestionsByType("\"question_type\"", "\"" + questionType + "\"").enqueue(new Callback<Map<String, Question>>() {
                @Override
                public void onResponse(Call<Map<String, Question>> call, Response<Map<String, Question>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Map<String, Question> questionsMap = response.body();
                        List<Question> questionsList = questionsMap.values().stream().collect(Collectors.toList());
                        callback.onQuestionsLoaded(questionsList);
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

    private void loadMultipleChoiceImageFragment(List<Question> questions) {
        if (questions != null && !questions.isEmpty()) {
            Question question = questions.get(0);

            String[] imageOptions = {question.getImage_option_a(), question.getImage_option_b(),
                    question.getImage_option_c(), question.getImage_option_d()};

            // Creating a list of answer options
            String[] answerOptions = {question.getOption_a(), question.getOption_b(),
                    question.getOption_c(), question.getOption_d()};

            String correctAnswer = question.getCorrect_answer();
            String questionText = question.getQuestion_text();

            // Passing the list of questions to the fragment
            replaceFragment(MultipleChoiceImageFragmentNew.newInstance(questions));
        }
    }


    private void loadMultipleChoiceTextFragment(List<Question> questions) {
        Question question = questions.get(0);
        String questionText = question.getQuestion_text();
        String optionA = question.getOption_a();
        String optionB = question.getOption_b();
        String optionC = question.getOption_c();
        String optionD = question.getOption_d();
        String correctAnswer = question.getCorrect_answer();

        replaceFragment(MultipleChoiceTextFragmentNew.newInstance(questions));
    }

    private void loadListeningFragment(List<Question> questions) {
        Question question = questions.get(0);
        String correctAnswer = question.getCorrect_answer();
        String questionText = question.getQuestion_text();
        String orderWords = question.getOrder_words();

        replaceFragment(ListeningFragmentNew.newInstance(questions));
    }

    private void loadSpeakingFragment(List<Question> questions) {
        replaceFragment(SpeakingFragmentNew.newInstance(questions));
    }

    private void loadWritingFragment(List<Question> questions) {
        Question question = questions.get(0);
        String questionText = question.getQuestion_text();
        String correctAnswer = question.getCorrect_answer();

        replaceFragment(WritingFragmentNew.newInstance(questions));
    }

//    private void loadArrangeWordsFragment(List<Question> questions) {
//        Question question = questions.get(0);
//        String questionText = question.getQuestion_text();
//        String[] wordsToArrange = question.getOrder_words().split(" ");
//
//        replaceFragment(Arrange.newInstance(questionText, wordsToArrange));
//    }

    @FunctionalInterface
    private interface QuestionsCallback {
        void onQuestionsLoaded(List<Question> questions);
    }
}
