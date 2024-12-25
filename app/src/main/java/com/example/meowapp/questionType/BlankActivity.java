package com.example.meowapp.questionType;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.meowapp.Main.MainActivity;
import com.example.meowapp.R;
import com.example.meowapp.adapter.QuestionAdapter;
import com.example.meowapp.api.FirebaseApiService;

import com.example.meowapp.auth.LoginActivity;
import com.example.meowapp.lesson.LessonEditActivity;
import com.example.meowapp.model.Question;
import com.example.meowapp.model.QuestionType;
import com.example.meowapp.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlankActivity extends AppCompatActivity {
    public List<String> questionIds = new ArrayList<>();
    private List<Pair<String, QuestionType>> questionTypeList = new ArrayList<>();
    public int currentQuestionIndex = 0;
    public Map<String, Question> questionMap;
    private String lessonId, userId, questionType;
    private Map<String, QuestionType> questionTypeMap;
    private TextToSpeech tts;
    public int correctAnswers = 0;  // Số câu đúng
    public int inCorrectAnswers = 0;  // Số câu đúng
    private int totalQuestions = 0;  // Tổng số câu hỏi
    private int percentScore;
    public int hearts, diamonds;
    private ProgressBar progressBar;
    public TextView tvHeart;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blank);
        tvHeart = findViewById(R.id.tvHeart);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận thoát")
                    .setMessage("Bạn có chắc chắn muốn thoát không?")
                    .setPositiveButton("OK", (dialog, which) -> {
                        finish();
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> {
                        dialog.dismiss(); // Đóng Dialog
                    })
                    .show();
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();


        lessonId = getIntent().getStringExtra("LESSON_ID");
        questionType = getIntent().getStringExtra("QUESTION_TYPE");

        if(lessonId != null && questionType == null){
            loadQuestionsByLessonId();
            Log.e("HomeFragment", "load lessonId");
        }else if(lessonId == null && questionType != null) {
            loadQuestionsByQuestionType();
            Log.e("HomeFragment", "load questionType");
        }
        fetchUserById();
    }
    private void fetchUserById() {
        FirebaseApiService.apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    diamonds = user.getDiamonds();
                    hearts = user.getHearts();
                    tvHeart.setText(String.valueOf(hearts));
                } else {
                    Toast.makeText(BlankActivity.this, "No user available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching languages", t);
            }
        });
    }
    private void loadQuestionsByLessonId() {
        FirebaseApiService.apiService.getQuestionsByLessonId("\"lesson_id\"", "\"" + lessonId + "\"")
                .enqueue(new Callback<Map<String, Question>>() {
                    @Override
                    public void onResponse(Call<Map<String, Question>> call, Response<Map<String, Question>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            questionMap = response.body();
                            questionIds.addAll(questionMap.keySet());
                            totalQuestions = questionIds.size();

                            getAllQuestionType();
                        } else {
                            Toast.makeText(BlankActivity.this, "Failed to get info", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Question>> call, Throwable t) {
                        Toast.makeText(BlankActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("error:", t.getMessage(), t);
                    }
                });
    }
    private void loadQuestionsByQuestionType() {
        FirebaseApiService.apiService.getQuestionsByType("\"question_type\"", "\"" + questionType + "\"").enqueue(new Callback<Map<String, Question>>() {
            @Override
            public void onResponse(Call<Map<String, Question>> call, Response<Map<String, Question>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    questionMap = response.body();
                    questionIds.addAll(questionMap.keySet());
                    totalQuestions = questionIds.size();

                    getAllQuestionType();
                } else {
                    Log.e("PracticeFragment", "No questions available or API failed.");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Question>> call, Throwable t) {
                Log.e("PracticeFragment", "Error loading questions", t);
            }
        });
    }
    private void getAllQuestionType() {
        questionTypeList.clear();
        FirebaseApiService.apiService.getAllQuestionType().enqueue(new Callback<Map<String, QuestionType>>() {
            @Override
            public void onResponse(Call<Map<String, QuestionType>> call, Response<Map<String, QuestionType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionTypeMap = response.body();
                    loadFragment(currentQuestionIndex);
                } else {
                    Toast.makeText(BlankActivity.this, "Không thể tải thông tin ngôn ngữ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, QuestionType>> call, Throwable t) {
                Toast.makeText(BlankActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFragment(int index) {
        if (index < questionIds.size()) {
            String questionId = questionIds.get(index); // Lấy ID
            Bundle bundle = new Bundle();
            bundle.putString("questionId", questionId);

            Fragment fragment;
            String questionTypeKey = questionMap.get(questionId).getQuestion_type();

            handleTextToSpeech(Objects.requireNonNull(questionMap.get(questionId)).getQuestion_text());

            switch (questionTypeMap.get(questionTypeKey).getQuestion_type_name()) {
                case "order_words":
                    fragment = new OrderWordFragment();
                    break;
                case "listening":
                    fragment = new ListeningFragment();
                    break;
                case "speaking":
                    fragment = new SpeakingFragment();
                    break;
                case "writing":
                    fragment = new WritingFragment();
                    break;
                case "multiple_choice":
                    fragment = new MultipleChoiceFragment();
                    break;
                case "multiple_choice_image":
                    fragment = new MultipleChoiceImageFragment();
                    break;
                default:
                    Toast.makeText(BlankActivity.this, "Unknown question type", Toast.LENGTH_SHORT).show();
                    return;
            }
            fragment.setArguments(bundle); // Gán Bundle cho fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            percentScore = (int) (((totalQuestions - inCorrectAnswers) / (float) totalQuestions) * 100);

            Intent intent = new Intent(this, FinishActivity.class);
            intent.putExtra("LESSON_ID", lessonId);
            intent.putExtra("PERCENT_SCORE", percentScore);
            startActivity(intent);
            finish();
        }
    }
    public void onQuestionCompleted() {
        currentQuestionIndex++;
        loadFragment(currentQuestionIndex);
    }

    public void handleTextToSpeech(String textToSpeak) {
        if (tts == null) {
            tts = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    setTextToSpeechLanguage(textToSpeak);
                    tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    Toast.makeText(BlankActivity.this, "Error initializing TTS", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            setTextToSpeechLanguage(textToSpeak);
            tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void setTextToSpeechLanguage(String text) {
        String languageCode = getDetectedLanguage(text);
        Locale locale;

        switch (languageCode) {
            case "vi":
                locale = new Locale("vi", "VN");
                break;
            case "fr":
                locale = Locale.FRENCH;
                break;
            case "es":
                locale = new Locale("es", "ES");
                break;
            default:
                locale = Locale.US;
                break;
        }

        int result = tts.isLanguageAvailable(locale);
        if (result == TextToSpeech.LANG_AVAILABLE || result == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
            tts.setLanguage(locale);
        } else {
            tts.setLanguage(Locale.US);  // Ngôn ngữ mặc định là tiếng Anh nếu ngôn ngữ không hỗ trợ
        }
    }

    public String getDetectedLanguage(String text) {
        if (text.matches(".*[àáảãạâầấẩẫậăằắẳẵặđèéẻẽẹêềếểễệìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵ].*")) {
            return "vi"; // Vietnamese
        } else if (text.matches(".*[áéíóúñÑüÜ].*")) {
            return "es"; // Spanish
        } else if (text.matches(".*[àâæçéèêëîïôùûüÿñ].*")) {
            return "fr"; // French
        }
        return "en"; // Default to English
    }


    public void updateProgressBar() {
        // Tính phần trăm tiến độ dựa trên số câu đúng và tổng số câu
        if (totalQuestions > 0) {
            int progress = (int) ((correctAnswers / (float) totalQuestions) * 100);

            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progress);
            animation.setDuration(500); // thời gian chạy animation (1000 milliseconds = 1 giây)
            animation.start(); // bắt đầu animation
        }
    }
    public void updateUserHeart(){
        Map<String, Object> field = new HashMap<>();
        field.put("hearts", hearts);
        if(hearts <= 0){
            showHeartDepletedDialog();
        }
        FirebaseApiService.apiService.updateUserField(userId, field).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvHeart.setText(String.valueOf(hearts));
                } else {
                    Toast.makeText(BlankActivity.this, "Không lấy được tim", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(BlankActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showHeartDepletedDialog() {
        // Tạo dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_heart_depleted, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tvDiamond = dialogView.findViewById(R.id.tvDiamond);
        RelativeLayout refillButton = dialogView.findViewById(R.id.layoutBtnRefill);
        RelativeLayout cancelButton = dialogView.findViewById(R.id.layoutBtnNoTks);

        tvDiamond.setText(String.valueOf(diamonds));

        refillButton.setOnClickListener(v -> {
            if (diamonds >= 450) {
                diamonds -= 450;
                hearts = 5;
                updateUserHeart();
                updateUserDiamonds(diamonds);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Bạn không đủ đá", Toast.LENGTH_SHORT).show();
            }
        });
        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }
    public void updateUserDiamonds(Integer diamonds){
        Map<String, Object> field = new HashMap<>();
        field.put("diamonds", diamonds);
        FirebaseApiService.apiService.updateUserField(userId, field).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    Toast.makeText(BlankActivity.this, "Không lấy được đá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(BlankActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}