package com.example.meowapp.Main;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.speech.tts.TextToSpeech;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.meowapp.R;
import com.example.meowapp.questionType.BlankActivity;

import java.util.Locale;


public class MultipleChoiceTextFragmentNew extends Fragment {

    private TextView optionA, optionB, optionC, optionD, questionTv;
    private String correctAnswer, selectedAnswer;
    private CardView cardViewA, cardViewB, cardViewC, cardViewD;
    private Button submitButton;
    private TextToSpeech tts;
    private ImageButton playButton;

    public static MultipleChoiceTextFragmentNew newInstance(String questionText, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        MultipleChoiceTextFragmentNew fragment = new MultipleChoiceTextFragmentNew();
        Bundle args = new Bundle();
        args.putString("question_text", questionText);
        args.putString("option_a", optionA);
        args.putString("option_b", optionB);
        args.putString("option_c", optionC);
        args.putString("option_d", optionD);
        args.putString("correct_answer", correctAnswer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_type_multiple_choice, container, false);

        questionTv = view.findViewById(R.id.question);
        cardViewA = view.findViewById(R.id.cardView1);
        cardViewB = view.findViewById(R.id.cardView2);
        cardViewC = view.findViewById(R.id.cardView3);
        cardViewD = view.findViewById(R.id.cardView4);
        optionA = view.findViewById(R.id.option_a);
        optionB = view.findViewById(R.id.option_b);
        optionC = view.findViewById(R.id.option_c);
        optionD = view.findViewById(R.id.option_d);
        submitButton = view.findViewById(R.id.btnSubmit);

        Bundle args = getArguments();
        if (args != null) {
            questionTv.setText(args.getString("question_text"));
            optionA.setText(args.getString("option_a"));
            optionB.setText(args.getString("option_b"));
            optionC.setText(args.getString("option_c"));
            optionD.setText(args.getString("option_d"));
            correctAnswer = args.getString("correct_answer");
        }

        cardViewA.setOnClickListener(v -> {
            setBackground(cardViewA);
            selectedAnswer = optionA.getText().toString();
        });
        cardViewB.setOnClickListener(v -> {
            setBackground(cardViewB);
            selectedAnswer = optionB.getText().toString();
        });
        cardViewC.setOnClickListener(v -> {
            setBackground(cardViewC);
            selectedAnswer = optionC.getText().toString();
        });
        cardViewD.setOnClickListener(v -> {
            setBackground(cardViewD);
            selectedAnswer = optionD.getText().toString();
        });

        submitButton.setOnClickListener(v -> {
            if (selectedAnswer != null) {
                boolean isCorrect = checkAnswer(selectedAnswer);
                ResultBottomSheetNew bottomSheet = new ResultBottomSheetNew(isCorrect, correctAnswer);
                bottomSheet.show(getParentFragmentManager(), "ResultBottomSheet");
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn đáp án!", Toast.LENGTH_SHORT).show();
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


        return view;
    }

    private void setBackground(CardView selectedCardView) {
        selectedCardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pink3));

        for (CardView cardView : new CardView[]{cardViewA, cardViewB, cardViewC, cardViewD}) {
            if (cardView != selectedCardView) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));
            }
        }
    }

    private boolean checkAnswer(String selectedAnswer) {
        return selectedAnswer.equals(correctAnswer);
    }
    private void handleTextToSpeech(String text) {
        if (tts != null && !tts.isSpeaking()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Toast.makeText(getContext(), "Không hỗ trợ Text To Speech hiện tại", Toast.LENGTH_SHORT).show();
        }
    }
}
