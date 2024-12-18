package com.example.meowapp.Main;

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
import com.example.meowapp.model.Question;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MultipleChoiceImageFragmentNew extends Fragment {

    private TextView optionA, optionB, optionC, optionD, questionTv;
    private String correctAnswer, selectedAnswer;
    private CardView cardViewA, cardViewB, cardViewC, cardViewD;
    private Button submitButton;
    private TextToSpeech tts;
    private ImageButton playButton;
    private ImageView imageA, imageB, imageC, imageD;
    private List<Question> questions;
    private int currentQuestionIndex = 0;

    // Sửa lại hàm mớiInstance để truyền danh sách câu hỏi từ gói practice
    public static MultipleChoiceImageFragmentNew newInstance(List<Question> questions) {
        MultipleChoiceImageFragmentNew fragment = new MultipleChoiceImageFragmentNew();
        Bundle args = new Bundle();
        Log.d("MultipleChoiceImageFragmentNew", "Received list of questions: " + questions.size());
        args.putSerializable("questions", (ArrayList<Question>) questions); // Gửi danh sách câu hỏi qua Bundle
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        // Nhận dữ liệu từ Bundle
        Bundle args = getArguments();
        if (args != null) {
            questions = (List<Question>) args.getSerializable("questions");
            displayQuestion(); // Hiển thị câu hỏi từ danh sách
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

    private void displayQuestion() {
        if (questions != null && currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex); // Lấy câu hỏi hiện tại
            String[] imageOptions = {question.getImage_option_a(), question.getImage_option_b(), question.getImage_option_c(), question.getImage_option_d()};
            String[] answerOptions = {question.getOption_a(), question.getOption_b(), question.getOption_c(), question.getOption_d()};
            correctAnswer = question.getCorrect_answer();
            questionTv.setText(question.getQuestion_text());

            Picasso.get().load(imageOptions[0]).into(imageA);
            Picasso.get().load(imageOptions[1]).into(imageB);
            Picasso.get().load(imageOptions[2]).into(imageC);
            Picasso.get().load(imageOptions[3]).into(imageD);

            optionA.setText(answerOptions[0]);
            optionB.setText(answerOptions[1]);
            optionC.setText(answerOptions[2]);
            optionD.setText(answerOptions[3]);
        } else {
            // Khi không còn câu hỏi nào nữa, xử lý kết thúc bài thi
            Toast.makeText(getContext(), "Đã hoàn thành bài tập!", Toast.LENGTH_SHORT).show();
            // Optionally, navigate to a summary or another fragment
        }
    }

    private void setBackground(CardView selectedCardView) {
        selectedCardView.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.pink3));

        for (CardView cardView : new CardView[]{cardViewA, cardViewB, cardViewC, cardViewD}) {
            if (cardView != selectedCardView) {
                cardView.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), android.R.color.white));
            }
        }
    }

    private boolean checkAnswer(String selected) {
        return selected.equals(correctAnswer);
    }

    private void handleTextToSpeech(String text) {
        if (tts != null && !tts.isSpeaking()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Toast.makeText(getContext(), "Không hỗ trợ Text To Speech hiện tại", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            displayQuestion();
        } else {
            // Khi không còn câu hỏi nào nữa, xử lý kết thúc bài tập và quay về PracticeFragment
            Toast.makeText(getContext(), "Đã hoàn thành bài tập!", Toast.LENGTH_SHORT).show();
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new PracticeFragment())
                        .commit();
            }
        }
    }



}
