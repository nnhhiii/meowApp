package com.example.meowapp.Main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.meowapp.R;
import com.example.meowapp.model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpeakingFragmentNew extends Fragment {

    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private Handler handler = new Handler();
    private boolean isListening = false;
    private static final int RECORDING_TIMEOUT = 10000; // 10 giây

    private RelativeLayout btnMicro;
    private TextView questionTv;
    private String correctAnswer;
    private TextToSpeech tts;
    private ArrayList<Question> questions;
    private int currentQuestionIndex = 0;

    // Phương thức newInstance để nhận dữ liệu từ Fragment khác
    public static SpeakingFragmentNew newInstance(List<Question> questions) {
        SpeakingFragmentNew fragment = new SpeakingFragmentNew();
        Bundle args = new Bundle();
        args.putSerializable("questions", (ArrayList<Question>) questions);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_type_speaking, container, false);

        // Khởi tạo các view
        questionTv = view.findViewById(R.id.question);
        btnMicro = view.findViewById(R.id.btnMicro); // Nút thu âm

        // Nhận dữ liệu câu hỏi từ Bundle
        if (getArguments() != null) {
            questions = (ArrayList<Question>) getArguments().getSerializable("questions");
            if (questions != null && !questions.isEmpty()) {
                currentQuestionIndex = 0;
                loadQuestion(questions.get(currentQuestionIndex));
            }
        }

        // Khởi tạo TextToSpeech
        tts = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        // Khởi tạo SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(getContext(), "Bắt đầu nói...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String spokenText = matches.get(0);
                    Log.d("SpeechResult", "Kết quả nhận diện: " + spokenText);
                    boolean isCorrect = checkAnswer(spokenText);
                    ResultBottomSheetNew bottomSheet = new ResultBottomSheetNew(isCorrect, correctAnswer);
                    bottomSheet.show(getParentFragmentManager(), "ResultBottomSheet");
                }
                stopListening();
            }

            @Override
            public void onError(int error) {
                handleSpeechError(error);
                stopListening();
            }

            @Override public void onEndOfSpeech() {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEvent(int eventType, Bundle params) {}
            @Override public void onRmsChanged(float rmsdB) {}
        });

        // Xử lý nút thu âm
        btnMicro.setOnClickListener(v -> {
            if (isListening) {
                stopListening();
            } else {
                startListening();
            }
        });

        return view;
    }

    private void loadQuestion(Question question) {
        questionTv.setText(question.getQuestion_text());
        correctAnswer = question.getCorrect_answer();
    }

    private void startListening() {
        isListening = true;
        speechRecognizer.startListening(recognizerIntent);

        // Dừng tự động sau 10 giây nếu người dùng không nhấn nút
        handler.postDelayed(this::stopListening, RECORDING_TIMEOUT);
    }

    private void stopListening() {
        if (isListening) {
            isListening = false;
            speechRecognizer.stopListening();
            handler.removeCallbacksAndMessages(null); // Hủy timeout
        }
    }

    private boolean checkAnswer(String answer) {
        if (correctAnswer == null || answer == null) return false;
        return answer.trim().equalsIgnoreCase(correctAnswer.trim());
    }

    private void handleSpeechError(int error) {
        String errorMessage = "Có lỗi xảy ra: ";
        switch (error) {
            case SpeechRecognizer.ERROR_CLIENT:
                errorMessage += "Lỗi phía client.";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorMessage += "Lỗi mạng.";
                break;
            case SpeechRecognizer.ERROR_AUDIO:
                errorMessage += "Lỗi âm thanh.";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorMessage += "Không có kết quả phù hợp.";
                break;
            default:
                errorMessage += "Lỗi không xác định.";
                break;
        }
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void displayNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            loadQuestion(questions.get(currentQuestionIndex));
        } else {
            Toast.makeText(getContext(), "Đã hoàn thành bài tập!", Toast.LENGTH_SHORT).show();
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new PracticeFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (tts != null) {
            tts.shutdown();
        }
    }
}
