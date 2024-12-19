package com.example.meowapp.questionType;

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
public class WritingFragment extends Fragment {
    private TextView questionTv;
    private EditText et_answer;
    private Button submitButton;
    private String correct_answer;
    private ImageButton playButton;
    private Question question;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question_type_writing, container, false);

        questionTv = view.findViewById(R.id.question);
        et_answer = view.findViewById(R.id.answer);
        submitButton = view.findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(v -> {
            String answer = et_answer.getText().toString();
            if(!answer.isEmpty()) {
                boolean isCorrect = checkAnswer(answer);
                ResultBottomSheet bottomSheet = new ResultBottomSheet(isCorrect, correct_answer);
                bottomSheet.show(getParentFragmentManager(), "ResultBottomSheet");
            }else{
                Toast.makeText(getContext(), "Vui lòng nhập câu trả lời!", Toast.LENGTH_SHORT).show();
            }
        });
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
        return view;
    }

    private boolean checkAnswer(String answer) {
        return answer.equals(correct_answer);
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
}