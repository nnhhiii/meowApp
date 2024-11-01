package com.example.meowapp.questionType;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Question;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderWordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderWordFragment extends Fragment {
    private TextView questionTv;
    private Button submitButton;
    private FlexboxLayout wordContainer, answerContainer;
    private String correctAnswer, orderWord;
    private ArrayList<String> selectedWords = new ArrayList<>();


    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderWordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment orderWord.
     */
    public static OrderWordFragment newInstance(String param1, String param2) {
        OrderWordFragment fragment = new OrderWordFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question_type_order_word, container, false);

        wordContainer = view.findViewById(R.id.wordContainer);
        questionTv = view.findViewById(R.id.question);
        answerContainer = view.findViewById(R.id.answerContainer);

        submitButton = view.findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(v -> {
            // Ghép các từ lại thành câu hoàn chỉnh
            StringBuilder selectedSentence = new StringBuilder();
            for (String word : selectedWords) {
                selectedSentence.append(word).append(" ");
            }

            // Xóa khoảng trắng cuối cùng
            String userAnswer = selectedSentence.toString().trim();

            // Kiểm tra xem câu của người dùng có khớp với đáp án đúng không
            boolean isCorrect = checkAnswer(userAnswer);

            // Hiển thị kết quả
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
                    Question question = response.body();
                    questionTv.setText(question.getQuestion_text());
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
