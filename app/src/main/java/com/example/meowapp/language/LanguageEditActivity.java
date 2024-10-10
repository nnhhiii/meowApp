package com.example.meowapp.language;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguageEditActivity extends AppCompatActivity {
    private EditText etName;
    private Button btnImage, btnSave;
    private ImageButton btnBack;
    private ImageView imageView;
    private Language language;
    private Uri imgUri;
    private String languageId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lang_edit);
        etName = findViewById(R.id.et_lang);
        imageView = findViewById(R.id.image);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResult.launch(intent);
        });

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            if (!newName.isEmpty() && imgUri != null) {
                language.setLanguage_name(newName);

                // Lấy thời gian hiện tại
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentTime = sdf.format(new Date()); // Định dạng thời gian hiện tại

                language.setUpdated_at(currentTime);
                uploadImageToFirebaseStorage(imgUri);
            }else {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });
        loadData();
    }

    private void loadData() {
        languageId = getIntent().getStringExtra("languageId");
        FirebaseApiService.apiService.getLanguageById(languageId).enqueue(new Callback<Language>() {
            @Override
            public void onResponse(Call<Language> call, Response<Language> response) {
                if (response.isSuccessful()) {
                    language = response.body();
                    etName.setText(language.getLanguage_name());
                    if(language.getLanguage_image()!=null && !language.getLanguage_image().isEmpty()){
                        Picasso.get().load(language.getLanguage_image()).into(imageView);
                    }

                } else {
                    Toast.makeText(LanguageEditActivity.this, "Ko load dc!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Language> call, Throwable throwable) {
                Toast.makeText(LanguageEditActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    imgUri = result.getData().getData();
                    imageView.setImageURI(imgUri);
                }
            });

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        // Tạo tham chiếu tới Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        // Đặt tên file và tạo thư mục trong Firebase Storage
        StorageReference imageRef = storageReference.child("languages/" + System.currentTimeMillis() + ".jpg");

        // Upload file
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy đường dẫn của ảnh vừa upload
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        language.setLanguage_image(imageUrl);
                        saveToFireBase(languageId, language);
                        Toast.makeText(LanguageEditActivity.this, "Upload thành công", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi upload thất bại
                    Toast.makeText(LanguageEditActivity.this, "Upload thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToFireBase(String languageId, Language language) {
        FirebaseApiService.apiService.updateLanguage(languageId, language).enqueue(new Callback<Language>() {
            @Override
            public void onResponse(Call<Language> call, Response<Language> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LanguageEditActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LanguageEditActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Language> call, Throwable throwable) {
                Toast.makeText(LanguageEditActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}