package com.example.meowapp.lesson;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Question;
import com.example.meowapp.model.QuestionType;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionCreateActivity extends AppCompatActivity {
    private EditText optionA, optionB, optionC, optionD, questionText, correctAnswer, orderWords;
    private ImageView imageA, imageB, imageC, imageD;
    private Spinner spQuestionType;
    private Button btnImageA, btnImageB, btnImageC, btnImageD, btnSave;
    private ImageButton btnBack;
    private Uri imgUriA, imgUriB, imgUriC, imgUriD;
    private LinearLayout layoutOrderWords, layoutOptionA, layoutOptionB, layoutOptionC, layoutOptionD,
            layoutImageA, layoutImageB, layoutImageC, layoutImageD;
    private Map<String, String> questionTypeMap = new HashMap<>();
    private Question question = new Question();
    private String lessonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lesson_question_add);

        spQuestionType = findViewById(R.id.spQuestionType);
        questionText = findViewById(R.id.etQuestion);
        correctAnswer = findViewById(R.id.etAnswer);

        layoutOrderWords = findViewById(R.id.layoutOrderWords);
        layoutImageA = findViewById(R.id.layoutImageA);
        layoutImageB = findViewById(R.id.layoutImageB);
        layoutImageC = findViewById(R.id.layoutImageC);
        layoutImageD = findViewById(R.id.layoutImageD);
        layoutOptionA = findViewById(R.id.layoutOptionA);
        layoutOptionB = findViewById(R.id.layoutOptionB);
        layoutOptionC = findViewById(R.id.layoutOptionC);
        layoutOptionD = findViewById(R.id.layoutOptionD);

        orderWords = findViewById(R.id.etOrderWords);
        imageA = findViewById(R.id.imageA);
        imageB = findViewById(R.id.imageB);
        imageC = findViewById(R.id.imageC);
        imageD = findViewById(R.id.imageD);
        optionA = findViewById(R.id.etOptionA);
        optionB = findViewById(R.id.etOptionB);
        optionC = findViewById(R.id.etOptionC);
        optionD = findViewById(R.id.etOptionD);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnImageA = findViewById(R.id.btnImageA);
        btnImageA.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultA.launch(intent);
        });
        btnImageB = findViewById(R.id.btnImageB);
        btnImageB.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultB.launch(intent);
        });
        btnImageC = findViewById(R.id.btnImageC);
        btnImageC.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultC.launch(intent);
        });
        btnImageD = findViewById(R.id.btnImageD);
        btnImageD.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultD.launch(intent);
        });

        lessonId = getIntent().getStringExtra("lessonId");

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            checkNull();
        });

        loadDataToSpinner();
    }
    private void loadDataToSpinner(){
        FirebaseApiService.apiService.getAllQuestionType().enqueue(new Callback<Map<String, QuestionType>>() {
            @Override
            public void onResponse(Call<Map<String, QuestionType>> call, Response<Map<String, QuestionType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, QuestionType> responseMap = response.body();
                    List<String> questionTypeNames = new ArrayList<>();
                    for (Map.Entry<String, QuestionType> entry : responseMap.entrySet()) {
                        questionTypeMap.put(entry.getValue().getQuestion_type_name(), entry.getKey()); // Lưu cặp tên-ngôn ngữ với id
                        questionTypeNames.add(entry.getValue().getQuestion_type_name()); // Chỉ lấy tên
                    }

                    // Tạo Adapter cho Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(QuestionCreateActivity.this, android.R.layout.simple_spinner_item, questionTypeNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Gán Adapter cho Spinner
                    spQuestionType.setAdapter(adapter);

                    spQuestionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            layoutOrderWords.setVisibility(View.GONE);
                            layoutOptionA.setVisibility(View.GONE);
                            layoutOptionB.setVisibility(View.GONE);
                            layoutOptionC.setVisibility(View.GONE);
                            layoutOptionD.setVisibility(View.GONE);
                            layoutImageA.setVisibility(View.GONE);
                            layoutImageB.setVisibility(View.GONE);
                            layoutImageC.setVisibility(View.GONE);
                            layoutImageD.setVisibility(View.GONE);

                            switch (questionTypeNames.get(position)) {
                                case "order_words":
                                    layoutOrderWords.setVisibility(View.VISIBLE);
                                    break;
                                case "multiple_choice":
                                    layoutOptionA.setVisibility(View.VISIBLE);
                                    layoutOptionB.setVisibility(View.VISIBLE);
                                    layoutOptionC.setVisibility(View.VISIBLE);
                                    layoutOptionD.setVisibility(View.VISIBLE);
                                    break;
                                case "multiple_choice_image":
                                    layoutOptionA.setVisibility(View.VISIBLE);
                                    layoutOptionB.setVisibility(View.VISIBLE);
                                    layoutOptionC.setVisibility(View.VISIBLE);
                                    layoutOptionD.setVisibility(View.VISIBLE);
                                    layoutImageA.setVisibility(View.VISIBLE);
                                    layoutImageB.setVisibility(View.VISIBLE);
                                    layoutImageC.setVisibility(View.VISIBLE);
                                    layoutImageD.setVisibility(View.VISIBLE);
                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });

                } else {
                    Toast.makeText(QuestionCreateActivity.this, "Không thể tải thông tin ngôn ngữ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, QuestionType>> call, Throwable t) {
                Toast.makeText(QuestionCreateActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkNull(){
        String questionTv = questionText.getText().toString().trim();
        String newCorrectAnswer = correctAnswer.getText().toString().trim();
        String newOrderWords = orderWords.getText().toString().trim();
        String newOptionA = optionA.getText().toString().trim();
        String newOptionB = optionB.getText().toString().trim();
        String newOptionC = optionC.getText().toString().trim();
        String newOptionD = optionD.getText().toString().trim();
        String selectedQuestionName = spQuestionType.getSelectedItem().toString();
        String selectedQuestionId = questionTypeMap.get(selectedQuestionName);

        // Kiểm tra thông tin đầu vào
        if (TextUtils.isEmpty(questionTv)) {
            Toast.makeText(this, "Vui lòng nhập câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newCorrectAnswer)) {
            Toast.makeText(this, "Vui lòng nhập đáp án", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (selectedQuestionName) {
            case "order_words":
                if (TextUtils.isEmpty(newOrderWords)) {
                    Toast.makeText(this, "Vui lòng nhập các từ theo thứ tự", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    question.setOrder_words(newOrderWords);
                }
                break;
            case "multiple_choice":
                if (TextUtils.isEmpty(newOptionA) || TextUtils.isEmpty(newOptionB) ||
                        TextUtils.isEmpty(newOptionC) || TextUtils.isEmpty(newOptionD)) {
                    Toast.makeText(this, "Vui lòng nhập tất cả các đáp án", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    question.setOption_a(newOptionA);
                    question.setOption_b(newOptionB);
                    question.setOption_c(newOptionC);
                    question.setOption_d(newOptionD);
                }
                break;
            case "multiple_choice_image":
                if(TextUtils.isEmpty(newOptionA) || TextUtils.isEmpty(newOptionB) ||
                        TextUtils.isEmpty(newOptionC) || TextUtils.isEmpty(newOptionD) ||
                        imgUriA == null || imgUriB == null||imgUriC == null||imgUriD == null){
                    Toast.makeText(this, "Vui lòng nhập đủ dữ liệu và các ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    question.setOption_a(newOptionA);
                    question.setOption_b(newOptionB);
                    question.setOption_c(newOptionC);
                    question.setOption_d(newOptionD);
                    uploadImagesToFirebaseStorage(imgUriA, imgUriB, imgUriC, imgUriD);
                }
                break;
            default:
                break;
        }
        question.setQuestion_text(questionTv);
        question.setCorrect_answer(newCorrectAnswer);
        question.setQuestion_type(selectedQuestionId);
        question.setLesson_id(lessonId);

        // Lấy thời gian hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        question.setCreated_at(currentTime);
        question.setUpdated_at(currentTime);

        if (!selectedQuestionName.equals("multiple_choice_image")) {
            saveToFirebase(question);
        }
    }
    private void uploadImagesToFirebaseStorage(Uri imgUriA, Uri imgUriB, Uri imgUriC, Uri imgUriD) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        Map<String, Uri> imageUris = new HashMap<>();
        imageUris.put("optionA", imgUriA);
        imageUris.put("optionB", imgUriB);
        imageUris.put("optionC", imgUriC);
        imageUris.put("optionD", imgUriD);

        Map<String, String> imageUrls = new HashMap<>();

        for (Map.Entry<String, Uri> entry : imageUris.entrySet()) {
            String option = entry.getKey();
            Uri imageUri = entry.getValue();

            if (imageUri != null) {
                // Đặt tên file và tạo thư mục trong Firebase Storage
                StorageReference imageRef = storageReference.child("questions/" + option + "_" + System.currentTimeMillis() + ".jpg");

                // Upload từng file
                imageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                imageUrls.put(option, imageUrl);

                                // Kiểm tra nếu tất cả ảnh đã được tải lên
                                if (imageUrls.size() == imageUris.size()) {
                                    question.setImage_option_a(imageUrls.get("optionA"));
                                    question.setImage_option_b(imageUrls.get("optionB"));
                                    question.setImage_option_c(imageUrls.get("optionC"));
                                    question.setImage_option_d(imageUrls.get("optionD"));

                                    saveToFirebase(question); // Lưu câu hỏi vào Firebase sau khi tất cả ảnh đã được tải lên
                                    Toast.makeText(QuestionCreateActivity.this, "Tải lên tất cả ảnh thành công!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(QuestionCreateActivity.this, "Tải lên thất bại cho " + option, Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }

    private void saveToFirebase(Question question){
        FirebaseApiService.apiService.addQuestion(question).enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(QuestionCreateActivity.this, "Thêm câu hỏi thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(QuestionCreateActivity.this, "Thêm câu hỏi thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Toast.makeText(QuestionCreateActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private final ActivityResultLauncher<Intent> activityResultA = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    imgUriA = result.getData().getData();
                    imageA.setImageURI(imgUriA);
                }
            });
    private final ActivityResultLauncher<Intent> activityResultB = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    imgUriB = result.getData().getData();
                    imageB.setImageURI(imgUriB);
                }
            });
    private final ActivityResultLauncher<Intent> activityResultC = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    imgUriC = result.getData().getData();
                    imageC.setImageURI(imgUriC);
                }
            });
    private final ActivityResultLauncher<Intent> activityResultD = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    imgUriD = result.getData().getData();
                    imageD.setImageURI(imgUriD);
                }
            });
}