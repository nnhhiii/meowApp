package com.example.meowapp.Main;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.speech.tts.TextToSpeech;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meowapp.R;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Locale;

public class ListeningFragmentNew extends Fragment {
    private String correctAnswer;
    private String questionText;
    private String orderWords;
    private Button submitButton;
    private FlexboxLayout wordContainer, answerContainer;
    private ArrayList<String> selectedWords = new ArrayList<>();
    private ImageView playButton;
    private TextToSpeech tts;

    public static ListeningFragmentNew newInstance(String correctAnswer, String questionText, String orderWords) {
        ListeningFragmentNew fragment = new ListeningFragmentNew();
        Bundle args = new Bundle();
        args.putString("correctAnswer", correctAnswer);
        args.putString("questionText", questionText);
        args.putString("orderWords", orderWords);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            correctAnswer = getArguments().getString("correctAnswer");
            questionText = getArguments().getString("questionText");
            orderWords = getArguments().getString("orderWords");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_type_listening, container, false);

        wordContainer = view.findViewById(R.id.wordContainer);
        answerContainer = view.findViewById(R.id.answerContainer);

        tts = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        playButton = view.findViewById(R.id.btnVolume);
        playButton.setOnClickListener(v -> {
            handleTextToSpeech(questionText);
        });

        submitButton = view.findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(v -> {
            StringBuilder selectedSentence = new StringBuilder();
            for (String word : selectedWords) {
                selectedSentence.append(word).append(" ");
            }
            String userAnswer = selectedSentence.toString().trim();
            boolean isCorrect = checkAnswer(userAnswer);
            ResultBottomSheetNew bottomSheet = new ResultBottomSheetNew(isCorrect, correctAnswer);
            bottomSheet.show(getParentFragmentManager(), "ResultBottomSheet");
        });

        if (orderWords != null && !orderWords.isEmpty()) {
            loadWords(orderWords.split(", "));
        }

        return view;
    }

    private boolean checkAnswer(String userAnswer) {
        return userAnswer.equals(correctAnswer);
    }

    private void loadWords(String[] words) {
        for (String word : words) {
            TextView wordView = new TextView(getContext());
            wordView.setText(word);
            wordView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            wordView.setSingleLine(true);
            wordView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.word_background));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(16, 16, 16, 16);
            wordView.setLayoutParams(params);

            wordView.setOnClickListener(v -> {
                selectedWords.add(word);
                wordView.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction(() -> {
                            wordContainer.removeView(wordView);
                            TextView answerView = new TextView(getContext());
                            answerView.setText(word);
                            answerView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                            answerView.setSingleLine(true);
                            answerView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.word_background));

                            LinearLayout.LayoutParams answerParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            answerParams.setMargins(16, 16, 16, 16);
                            answerView.setLayoutParams(answerParams);

                            answerView.setAlpha(0f);
                            answerContainer.addView(answerView);
                            answerView.animate()
                                    .alpha(1f)
                                    .setDuration(300)
                                    .start();

                            answerView.setOnClickListener(aView -> {
                                answerContainer.removeView(answerView);
                                wordContainer.addView(wordView);
                                wordView.setEnabled(true);

                                wordView.setAlpha(0f);
                                wordView.animate()
                                        .alpha(1f)
                                        .setDuration(300)
                                        .start();
                                selectedWords.remove(word);
                            });
                        })
                        .start();
            });

            wordContainer.addView(wordView);
        }
    }

    private void handleTextToSpeech(String text) {
        if (tts != null && !tts.isSpeaking()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Toast.makeText(getContext(), "Không hỗ trợ Text To Speech hiện tại", Toast.LENGTH_SHORT).show();
        }
    }
}
