package com.example.meowapp.questionType;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Question;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultipleChoiceImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultipleChoiceImageFragment extends Fragment {
    private TextView optionA, optionB, optionC, optionD, questionTv;
    private String correct_answer, selectedAnswer;
    private CardView cardViewA, cardViewB, cardViewC, cardViewD;
    private Button submitButton;
    private ImageView imageA, imageB, imageC, imageD;
    private ImageButton playButton;
    private Question question;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question_type_multiple_choice_image, container, false);

        questionTv = view.findViewById(R.id.question);
        cardViewA = view.findViewById(R.id.cardView1);
        cardViewB = view.findViewById(R.id.cardView2);
        cardViewC = view.findViewById(R.id.cardView3);
        cardViewD = view.findViewById(R.id.cardView4);
        imageA = view.findViewById(R.id.image_1);
        imageB = view.findViewById(R.id.image_2);
        imageC = view.findViewById(R.id.image_3);
        imageD = view.findViewById(R.id.image_4);
        optionA = view.findViewById(R.id.option_a);
        optionB = view.findViewById(R.id.option_b);
        optionC = view.findViewById(R.id.option_c);
        optionD = view.findViewById(R.id.option_d);
        submitButton = view.findViewById(R.id.btnSubmit);

        loadData();

        playButton = view.findViewById(R.id.btnVolume);
        playButton.setOnClickListener(v -> {
            if (question != null) {
                BlankActivity activity = (BlankActivity) getActivity();
                activity.handleTextToSpeech(question.getQuestion_text());
            } else {
                Toast.makeText(getContext(), "Câu hỏi không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

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
            if(selectedAnswer!=null) {
                boolean isCorrect = checkAnswer(selectedAnswer);
                ResultBottomSheet bottomSheet = new ResultBottomSheet(isCorrect, correct_answer);
                bottomSheet.show(getParentFragmentManager(), "ResultBottomSheet");
            }else{
                Toast.makeText(getContext(), "Vui lòng chọn đáp án!", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
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
                    optionA.setText(question.getOption_a());
                    optionB.setText(question.getOption_b());
                    optionC.setText(question.getOption_c());
                    optionD.setText(question.getOption_d());
                    correct_answer = question.getCorrect_answer();

                    if (question.getImage_option_a() != null && !question.getImage_option_a().isEmpty()
                            && question.getImage_option_b() != null && !question.getImage_option_b().isEmpty()
                            && question.getImage_option_c() != null && !question.getImage_option_c().isEmpty()
                            && question.getImage_option_d() != null && !question.getImage_option_d().isEmpty()) {
                        Picasso.get().load(question.getImage_option_a()).into(imageA);
                        Picasso.get().load(question.getImage_option_b()).into(imageB);
                        Picasso.get().load(question.getImage_option_c()).into(imageC);
                        Picasso.get().load(question.getImage_option_d()).into(imageD);
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
    private void setBackground(CardView selectedCardView) {
        selectedCardView.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.pink3));

        for (CardView cardView : new CardView[]{cardViewA, cardViewB, cardViewC, cardViewD}) {
            if (cardView != selectedCardView) {
                cardView.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), android.R.color.white));
            }
        }
    }
    private boolean checkAnswer(String selectedAnswer) {
        return selectedAnswer.equals(correct_answer);
    }
}