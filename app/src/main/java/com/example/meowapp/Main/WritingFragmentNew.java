package com.example.meowapp.Main;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Question;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WritingFragmentNew extends Fragment {
    private TextView questionTv;
    private EditText et_answer;
    private Button submitButton;
    private String correct_answer;
    private ImageButton playButton;
    private TextToSpeech tts;
    private Question question;

    private static final String ARG_QUESTION_TEXT = "questionText";
    private static final String ARG_CORRECT_ANSWER = "correctAnswer";

    public WritingFragmentNew() {
        // Required empty public constructor
    }

    public static WritingFragmentNew newInstance(String questionText, String correctAnswer) {
        WritingFragmentNew fragment = new WritingFragmentNew();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION_TEXT, questionText);
        args.putString(ARG_CORRECT_ANSWER, correctAnswer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String questionText = getArguments().getString(ARG_QUESTION_TEXT);
            correct_answer = getArguments().getString(ARG_CORRECT_ANSWER);
            question = new Question(); // Create a new Question instance
            question.setQuestion_text(questionText);
            question.setCorrect_answer(correct_answer);
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
            if(answer != null && !answer.isEmpty()) {
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


        questionTv.setText(question.getQuestion_text());

        return view;
    }

    private boolean checkAnswer(String answer) {
        return answer.equals(correct_answer);
    }
    private void handleTextToSpeech(String text) {
        if (tts != null && !tts.isSpeaking()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Toast.makeText(getContext(), "Không hỗ trợ Text To Speech hiện tại", Toast.LENGTH_SHORT).show();
        }
    }
}
