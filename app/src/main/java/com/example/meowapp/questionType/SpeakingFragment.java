package com.example.meowapp.questionType;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Question;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpeakingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeakingFragment extends Fragment {
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private Handler handler = new Handler();
    private boolean isListening = false;
    private static final int RECORDING_TIMEOUT = 10000; // 5 giây

    private RelativeLayout btnMicro;
    private TextView questionTv;

    private String correct_answer;
    private ImageButton playButton;
    private TextToSpeech tts;
    private Question question;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SpeakingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpeakingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpeakingFragment newInstance(String param1, String param2) {
        SpeakingFragment fragment = new SpeakingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_type_speaking, container, false);

        // Khởi tạo các view
        questionTv = view.findViewById(R.id.question);
        playButton = view.findViewById(R.id.btnVolume);
        btnMicro = view.findViewById(R.id.btnMicro); // Nút thu âm

        // Khởi tạo TextToSpeech
        tts = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        // Xử lý TextToSpeech phát câu hỏi
        playButton.setOnClickListener(v -> {
            if (question != null) {
                tts.speak(question.getQuestion_text(), TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Toast.makeText(getContext(), "Câu hỏi không hợp lệ", Toast.LENGTH_SHORT).show();
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
                    ResultBottomSheet bottomSheet = new ResultBottomSheet(isCorrect, correct_answer);
                    bottomSheet.show(getParentFragmentManager(), "ResultBottomSheet");
                }
                stopListening();
            }

            @Override
            public void onError(int error) {
                String errorMessage = "Có lỗi xảy ra: ";

                switch (error) {
                    case SpeechRecognizer.ERROR_CLIENT:
                        errorMessage += "Lỗi phía client.";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        errorMessage += "Lỗi mạng.";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        errorMessage += "Lỗi timeout mạng.";
                        break;
                    case SpeechRecognizer.ERROR_AUDIO:
                        errorMessage += "Lỗi âm thanh.";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        errorMessage += "Lỗi phía server.";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        errorMessage += "Không có giọng nói nào được nhận diện.";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        errorMessage += "Không có quyền truy cập mic.";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        errorMessage += "Không có kết quả phù hợp.";
                        break;
                    default:
                        errorMessage += "Lỗi không xác định.";
                        break;
                }

                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
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

        // Load dữ liệu câu hỏi
        loadData();

        return view;
    }

    private void startListening() {
        isListening = true;
        speechRecognizer.startListening(recognizerIntent);

        // Dừng tự động sau 5 giây nếu người dùng không nhấn nút
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
        if (correct_answer == null || answer == null) return false;
        return answer.trim().equalsIgnoreCase(correct_answer.trim());
    }


    private void loadData(){
        // Nhận Bundle
        String questionId = getArguments().getString("questionId");
        FirebaseApiService.apiService.getQuestionById(questionId).enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question = response.body();
                    questionTv.setText(question.getQuestion_text());
                    correct_answer = question.getCorrect_answer();
                } else {
                    Toast.makeText(getContext(), "Failed to get info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error:", t.getMessage(), t);
            }
        });
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