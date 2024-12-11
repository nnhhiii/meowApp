package com.example.meowapp.Main;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.util.Log;
import com.example.meowapp.R;
import com.example.meowapp.model.Question;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.example.meowapp.api.FirebaseApiService;
import androidx.core.content.ContextCompat;
import com.example.meowapp.model.LanguagePreference;

public class MultipleChoiceImageFragmentNew extends Fragment {
    private TextView questionTv, optionA, optionB, optionC, optionD;
    private CardView cardViewA, cardViewB, cardViewC, cardViewD;
    private ImageView imageA, imageB, imageC, imageD;
    private String correct_answer, selectedAnswer;

    public MultipleChoiceImageFragmentNew() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        loadData();

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

        return view;
    }

    private void loadData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Lấy ID của người dùng hiện tại

        FirebaseApiService.apiService.getAllLanguagePreferenceByUserId("user_id", userId).enqueue(new Callback<Map<String, LanguagePreference>>() {
            @Override
            public void onResponse(Call<Map<String, LanguagePreference>> call, Response<Map<String, LanguagePreference>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, LanguagePreference> languagePreferenceMap = response.body();
                    String languageId = null;
                    String levelId = null;

                    // Lấy language_id và level_id từ dữ liệu LanguagePreference
                    if (languagePreferenceMap != null) {
                        LanguagePreference languagePreference = languagePreferenceMap.get("language_id"); // Chỉ lấy language_id thôi
                        if (languagePreference != null) {
                            languageId = languagePreference.getLanguage_id();
                            levelId = languagePreference.getLevel_id();
                        }
                    }

                    if (languageId != null && levelId != null) {
                        FirebaseApiService.apiService.getQuestionsByTypeAndLevelLanguage("3", levelId, languageId).enqueue(new Callback<List<Question>>() {
                            @Override
                            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Question question = response.body().get(0); // Chỉ lấy câu hỏi đầu tiên làm ví dụ
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
                                    Toast.makeText(getContext(), "Không thể lấy câu hỏi", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Question>> call, Throwable t) {
                                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Lỗi:", t.getMessage(), t);
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Không thể lấy thông tin về ngôn ngữ", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Không thể lấy thông tin về ngôn ngữ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, LanguagePreference>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Lỗi:", t.getMessage(), t);
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
}
