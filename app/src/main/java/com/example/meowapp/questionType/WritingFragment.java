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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WritingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WritingFragment extends Fragment {
    private TextView questionTv;
    private EditText et_answer;
    private Button submitButton;
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

    public WritingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment writting.
     */
    // TODO: Rename and change types and number of parameters
    public static WritingFragment newInstance(String param1, String param2) {
        WritingFragment fragment = new WritingFragment();
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
        View view = inflater.inflate(R.layout.fragment_question_type_writing, container, false);

        questionTv = view.findViewById(R.id.question);
        et_answer = view.findViewById(R.id.answer);
        submitButton = view.findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(v -> {
            String answer = et_answer.getText().toString();
            if(answer!=null) {
                boolean isCorrect = checkAnswer(answer);
                ResultBottomSheet bottomSheet = new ResultBottomSheet(isCorrect, correct_answer);
                bottomSheet.show(getParentFragmentManager(), "ResultBottomSheet");
            }else{
                Toast.makeText(getContext(), "Vui lòng nhập câu trả lời!", Toast.LENGTH_SHORT).show();
            }
        });
        loadData();

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