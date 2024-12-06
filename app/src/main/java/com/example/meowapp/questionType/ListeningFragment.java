package com.example.meowapp.questionType;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Question;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListeningFragment extends Fragment {
    private Button submitButton;
    private FlexboxLayout wordContainer, answerContainer;
    private String correctAnswer, orderWord;
    private ArrayList<String> selectedWords = new ArrayList<>();
    private ImageView playButton;
    private TextToSpeech tts;
    private Question question;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_type_listening, container, false);

        wordContainer = view.findViewById(R.id.wordContainer);
        answerContainer = view.findViewById(R.id.answerContainer);

        tts = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Default language, can be changed later based on detection
                tts.setLanguage(Locale.US);
            }
        });

        playButton = view.findViewById(R.id.btnVolume);
        playButton.setOnClickListener(v -> {
            if (question != null) {
                BlankActivity activity = (BlankActivity) getActivity();
                activity.handleTextToSpeech(question.getQuestion_text());
            } else {
                Toast.makeText(getContext(), "Câu hỏi không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        submitButton = view.findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(v -> {
            // Ghép các từ lại thành câu hoàn chỉnh
            StringBuilder selectedSentence = new StringBuilder();
            for (String word : selectedWords) {
                selectedSentence.append(word).append(" ");
            }
            String userAnswer = selectedSentence.toString().trim();
            boolean isCorrect = checkAnswer(userAnswer);

            ResultBottomSheet bottomSheet = new ResultBottomSheet(isCorrect, correctAnswer);
            bottomSheet.show(getParentFragmentManager(), "ResultBottomSheet");
        });
        loadData();
        return view;
    }
    private boolean checkAnswer(String userAnswer) {
        return userAnswer.equals(correctAnswer);
    }

    private void loadData(){
        // Nhận Bundle
        String questionId = getArguments().getString("questionId");
        FirebaseApiService.apiService.getQuestionById(questionId).enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question = response.body();
                    correctAnswer = question.getCorrect_answer();
                    orderWord = question.getOrder_words();

                    String[] words = orderWord.split(", ");

                    for (String word : words) {
                        TextView wordView = new TextView(getContext());
                        wordView.setText(word);
                        wordView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        wordView.setSingleLine(true);
                        wordView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.word_background));

                        // Đặt margin cho TextView
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(16, 16, 16, 16);
                        wordView.setLayoutParams(params);

                        wordView.setOnClickListener(v -> {
                            selectedWords.add(word);

                            // Animation bien mat
                            wordView.animate()
                                    .alpha(0f)
                                    .setDuration(300)
                                    .withEndAction(() -> {
                                        wordContainer.removeView(wordView); // Xóa từ khỏi wordContainer

                                        // Thêm từ vào answerContainer
                                        TextView answerView = new TextView(getContext());
                                        answerView.setText(word);
                                        answerView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                        answerView.setSingleLine(true);
                                        answerView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.word_background));

                                        // Đặt margin cho TextView mới trong answerContainer
                                        LinearLayout.LayoutParams answerParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        answerParams.setMargins(16, 16, 16, 16);
                                        answerView.setLayoutParams(answerParams);

                                        // Animation hien thi
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

                                            // Animation bien mat
                                            wordView.setAlpha(0f);
                                            wordView.animate()
                                                    .alpha(1f)
                                                    .setDuration(300)
                                                    .start();
                                            selectedWords.remove(word); // Xóa từ khỏi danh sách đã chọn
                                        });
                                    })
                                    .start();
                        });

                        wordContainer.addView(wordView);
                    }
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
}