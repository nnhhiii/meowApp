package com.example.meowapp.Main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsEditProfileActivity extends AppCompatActivity {
    private EditText etName, etEmail;
    private Button btnSave, btnChangeImage;
    private ImageView ivAvatar;
    private ImageView btnBack;
    private Uri imgUri;
    private User user;
    private String userId;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_edit_profile);

        etName = findViewById(R.id.edtName);
        etEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSave);
        btnChangeImage = findViewById(R.id.btnImage);
        ivAvatar = findViewById(R.id.imgAvatar);
        btnBack = findViewById(R.id.btnBack);
        edtPassword = findViewById(R.id.edtPassword);

        // Xử lý sự kiện nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Lấy userId từ Intent
        userId = getIntent().getStringExtra("userId");

        // Kiểm tra nếu userId không hợp lệ
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            finish();  // Quay lại nếu không có userId
            return;
        }

        // Chọn hình ảnh mới
        btnChangeImage.setOnClickListener(v -> selectImage());

        // Lưu thông tin người dùng khi nhấn nút lưu
        btnSave.setOnClickListener(v -> saveUserProfile());

        // Tải dữ liệu người dùng
        loadUserData();
    }

    private void loadUserData() {
        FirebaseApiService.apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    etName.setText(user.getUsername());
                    etEmail.setText(user.getEmail());

                    // Hiển thị ảnh đại diện nếu có
                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Picasso.get().load(user.getAvatar()).into(ivAvatar);
                    }
                } else {
                    Toast.makeText(SettingsEditProfileActivity.this, "Không tải được dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SettingsEditProfileActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updatePassword() {
        String newPassword = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật mật khẩu thông qua API
        user.setPassword(newPassword);  // Giả sử model User có thuộc tính password
        FirebaseApiService.apiService.updateUser(userId, user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SettingsEditProfileActivity.this, "Mật khẩu đã được cập nhật!", Toast.LENGTH_SHORT).show();
                    edtPassword.setText(""); // Xóa trường nhập mật khẩu sau khi cập nhật
                } else {
                    Toast.makeText(SettingsEditProfileActivity.this, "Cập nhật mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SettingsEditProfileActivity.this, "Lỗi mạng! Không thể cập nhật mật khẩu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePicker.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imagePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null && result.getResultCode() == RESULT_OK) {
                    imgUri = result.getData().getData();
                    ivAvatar.setImageURI(imgUri);
                } else {
                    Toast.makeText(SettingsEditProfileActivity.this, "Không chọn được ảnh!", Toast.LENGTH_SHORT).show();
                }
            });

    private void saveUserProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        user.setUsername(name);
        user.setEmail(email);
        user.setUpdated_at(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        // Kiểm tra nếu có ảnh mới để tải lên
        if (imgUri != null) {
            uploadImageToFirebase();
        } else {
            updateUserData();
        }
    }

    private void uploadImageToFirebase() {
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("users/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imgUri).addOnSuccessListener(taskSnapshot ->
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    user.setAvatar(uri.toString());
                    updateUserData();
                })
        ).addOnFailureListener(e ->
                Toast.makeText(SettingsEditProfileActivity.this, "Tải ảnh lên thất bại!", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateUserData() {
        FirebaseApiService.apiService.updateUser(userId, user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SettingsEditProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SettingsEditProfileActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SettingsEditProfileActivity.this, "Cập nhật thất bại! Lỗi mạng hoặc kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}