package com.example.meowapp.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.User;
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

public class AddUserActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private Button btnSave, btnImage;
    private ImageButton btnCancel;
    private Uri imgUri;
    private ImageView imageView;
    private User user = new User();
    private RadioGroup roleGroup;
    private RadioButton rbAdmin, rbStudent;
    private Spinner spLanguage;
    private TextView scoreTextView;
    private final Map<String, String> languageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_add); // Đảm bảo layout này tồn tại

        etName = findViewById(R.id.username);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        btnImage = findViewById(R.id.btnImage);
        btnCancel = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        imageView = findViewById(R.id.image);
        roleGroup = findViewById(R.id.radioGroup);
        rbAdmin = findViewById(R.id.radioButtonAdmin);
        rbStudent = findViewById(R.id.radioButtonStudent);
        spLanguage = findViewById(R.id.spLanguage);
        scoreTextView = findViewById(R.id.scoreTextView);


        btnCancel.setOnClickListener(v -> finish());

        btnImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResult.launch(intent);
        });

        roleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonAdmin) {
                spLanguage.setVisibility(View.VISIBLE);
                scoreTextView.setVisibility(View.GONE);
                loadDataToSpinner();
            } else {
                spLanguage.setVisibility(View.GONE);
                scoreTextView.setVisibility(View.VISIBLE);
                scoreTextView.setText("Điểm số: " + 0);
            }
        });
        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            String newPassword = etPassword.getText().toString().trim();
            if (!TextUtils.isEmpty(newName) && !TextUtils.isEmpty(newEmail) && !TextUtils.isEmpty(newPassword)) {
                user.setUsername(newName);
                user.setEmail(newEmail);
                user.setPassword(newPassword);

                // Lấy thời gian hiện tại
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentTime = sdf.format(new Date()); // Định dạng thời gian hiện tại

                user.setCreated_at(currentTime); // Đặt thời gian tạo
                user.setUpdated_at(currentTime); // Đặt thời gian cập nhật

                if (imgUri != null) {
                    uploadImageToFirebaseStorage(imgUri); // Tải ảnh lên Firebase
                } else {
                    saveToFireBase(user); // Lưu thông tin người dùng nếu không có ảnh
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            }

            if (rbAdmin.isChecked()) {
                user.setRole("Admin");
                String selectedLanguage = spLanguage.getSelectedItem().toString();
                user.setLanguage_id(languageMap.get(selectedLanguage));
            } else if (rbStudent.isChecked()) {
                user.setRole("Student");
                user.setScore(0);
                scoreTextView.setText("Điểm số: " + 0);
            }
        });

    }

    private final ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    imgUri = result.getData().getData();
                    imageView.setImageURI(imgUri); // Hiển thị ảnh đã chọn
                }
            });

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference imageRef = storageReference.child("users/" + System.currentTimeMillis() + ".jpg");

        // Upload file
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy đường dẫn của ảnh vừa upload
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        user.setAvatar(imageUrl); // Đặt đường dẫn ảnh cho người dùng
                        saveToFireBase(user); // Lưu thông tin người dùng
                        Toast.makeText(AddUserActivity.this, "Upload thành công", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddUserActivity.this, "Upload thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadDataToSpinner() {
        FirebaseApiService.apiService.getAllLanguage().enqueue(new Callback<Map<String, Language>>() {
            @Override
            public void onResponse(Call<Map<String, Language>> call, Response<Map<String, Language>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Language> languages = response.body();
                    List<String> languageNames = new ArrayList<>();
                    int selectedIndex = -1;
                    int index = 0;

                    for (Map.Entry<String, Language> entry : languages.entrySet()) {
                        String languageName = entry.getValue().getLanguage_name();
                        languageNames.add(languageName);
                        languageMap.put(languageName, entry.getKey());

                        if (entry.getKey().equals(user.getLanguage_id())) {
                            selectedIndex = index;
                        }
                        index++;
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddUserActivity.this, android.R.layout.simple_spinner_item, languageNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spLanguage.setAdapter(adapter);

                    if (selectedIndex != -1) {
                        spLanguage.setSelection(selectedIndex);
                    }
                } else {
                    Toast.makeText(AddUserActivity.this, "Không thể tải thông tin ngôn ngữ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Language>> call, Throwable throwable) {
                Toast.makeText(AddUserActivity.this, "Lỗi khi tải ngôn ngữ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToFireBase(User user) {
        Log.d("AddUserActivity", "Bắt đầu lưu người dùng vào Firebase");
        FirebaseApiService.apiService.addUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddUserActivity.this, "Thêm người dùng thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddUserActivity.this, "Thêm người dùng thất bại!", Toast.LENGTH_SHORT).show();
                    Log.d("AddUserActivity", "Thêm người dùng thất bại: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Toast.makeText(AddUserActivity.this, "Thêm người dùng thất bại!", Toast.LENGTH_SHORT).show();
                Log.d("AddUserActivity", "Lỗi khi thêm người dùng: " + throwable.getMessage());
            }
        });
    }
}
