package com.example.meowapp.Main;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.meowapp.R;
import com.example.meowapp.model.Question;

import java.util.ArrayList;
import java.util.Locale;
import java.util.List;

public class WritingFragmentNew extends Fragment {
    private TextView questionTv;
    private EditText et_answer;
    private Button submitButton;
    private String correct_answer;
    private ImageButton playButton;
    private TextToSpeech tts;
    private Question question;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;

    private static final String ARG_QUESTION_TEXT = "questionText";
    private static final String ARG_CORRECT_ANSWER = "correctAnswer";

    public WritingFragmentNew() {
        // Required empty public constructor
    }

    public static WritingFragmentNew newInstance(List<Question> questions) {
        WritingFragmentNew fragment = new WritingFragmentNew();
        Bundle args = new Bundle();
        args.putSerializable("questions", (ArrayList<Question>) questions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questions = (List<Question>) getArguments().getSerializable("questions");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_type_writing, container, false);

        questionTv = view.findViewById(R.id.question);
        et_answer = view.findViewById(R.id.answer);
        submitButton = view.findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(v -> {
            String answer = et_answer.getText().toString();
            if (!answer.isEmpty()) {
                boolean isCorrect = checkAnswer(answer);
                ResultBottomSheetNew bottomSheet = new ResultBottomSheetNew(isCorrect, correct_answer);
                bottomSheet.show(getParentFragmentManager(), "ResultBottomSheet");
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập câu trả lời!", Toast.LENGTH_SHORT).show();
            }
        });

        tts = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        playButton = view.findViewById(R.id.btnVolume);
        playButton.setOnClickListener(v -> {
            handleTextToSpeech(questionTv.getText().toString());
        });

        displayQuestion(); // Initialize the first question

        return view;
    }

    private boolean checkAnswer(String selected) {
        if (selected.equals(correct_answer)) {
            correctAnswersCount++;
            return true;
        }
        return false;
    }

    private void handleTextToSpeech(String text) {
        if (tts != null && !tts.isSpeaking()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Toast.makeText(getContext(), "Không hỗ trợ Text To Speech hiện tại", Toast.LENGTH_SHORT).show();
        }
    }


    private void displayQuestion() {
        if (questions != null && currentQuestionIndex < questions.size()) {
            question = questions.get(currentQuestionIndex);
            questionTv.setText(question.getQuestion_text());
            correct_answer = question.getCorrect_answer();
        } else {
            // Handle the case where there are no more questions
            Toast.makeText(getContext(), "Đã hoàn thành bài tập!", Toast.LENGTH_SHORT).show();
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new PracticeFragment())
                        .commit();
            }
        }
    }

    public void displayNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size())
        {
            displayQuestion();
        }  else {
            PracticeFinishFragment practiceFinishFragment = new PracticeFinishFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("correctAnswersCount", correctAnswersCount);
            bundle.putInt("totalQuestions", questions.size());
            practiceFinishFragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, practiceFinishFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
