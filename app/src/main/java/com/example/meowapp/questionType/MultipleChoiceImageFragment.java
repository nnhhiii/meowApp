package com.example.meowapp.questionType;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Question;
import com.squareup.picasso.Picasso;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MultipleChoiceImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MultipleChoiceImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MultipleChoiceImageFragment newInstance(String param1, String param2) {
        MultipleChoiceImageFragment fragment = new MultipleChoiceImageFragment();
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
        return view;
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
}