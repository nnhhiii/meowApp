package com.example.meowapp.Main;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meowapp.R;
import com.example.meowapp.questionType.ResultBottomSheet;
import com.squareup.picasso.Picasso;

public class MultipleChoiceImageFragmentNew extends Fragment {

    private TextView optionA, optionB, optionC, optionD, questionTv;
    private String correctAnswer, selectedAnswer;
    private CardView cardViewA, cardViewB, cardViewC, cardViewD;
    private Button submitButton;
    private ImageView imageA, imageB, imageC, imageD;

    public static MultipleChoiceImageFragmentNew newInstance(String[] imageOptions, String[] answerOptions, String correctAnswer, String questionText) {
        MultipleChoiceImageFragmentNew fragment = new MultipleChoiceImageFragmentNew();
        Bundle args = new Bundle();
        args.putStringArray("imageOptions", imageOptions);
        args.putStringArray("answerOptions", answerOptions);
        args.putString("correctAnswer", correctAnswer);
        args.putString("question_text", questionText);
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

        Bundle args = getArguments();
        if (args != null) {
            String[] imageOptions = args.getStringArray("imageOptions");
            String[] answerOptions = args.getStringArray("answerOptions");
            correctAnswer = args.getString("correctAnswer");
            String questionText = args.getString("question_text");

            questionTv.setText(questionText);
            displayQuestion(imageOptions, answerOptions);
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

        return view;
    }

    private void displayQuestion(String[] imageOptions, String[] answerOptions) {
        String questionText = getArguments().getString("question_text");
        questionTv.setText(questionText);
        Picasso.get().load(imageOptions[0]).into(imageA);
        Picasso.get().load(imageOptions[1]).into(imageB);
        Picasso.get().load(imageOptions[2]).into(imageC);
        Picasso.get().load(imageOptions[3]).into(imageD);

        optionA.setText(answerOptions[0]);
        optionB.setText(answerOptions[1]);
        optionC.setText(answerOptions[2]);
        optionD.setText(answerOptions[3]);
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
}
