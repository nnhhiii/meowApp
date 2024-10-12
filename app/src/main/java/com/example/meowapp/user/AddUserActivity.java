package com.example.meowapp.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_add); // Đảm bảo layout này tồn tại

        etName = findViewById(R.id.username);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        btnImage = findViewById(R.id.btnImage);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        imageView = findViewById(R.id.staffImage);

        btnCancel.setOnClickListener(v -> finish());

        btnImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResult.launch(intent);
        });
        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            String newPassword = etPassword.getText().toString().trim();
            if (!TextUtils.isEmpty(newName) && !TextUtils.isEmpty(newEmail) && !TextUtils.isEmpty(newPassword)) {
                user.setName(newName);
                user.setEmail(newEmail);
                user.setPassword(newPassword);

                // Lấy thời gian hiện tại
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentTime = sdf.format(new Date()); // Định dạng thời gian hiện tại

                user.setCreatedAt(currentTime); // Đặt thời gian tạo
                user.setUpdatedAt(currentTime); // Đặt thời gian cập nhật

                if (imgUri != null) {
                    uploadImageToFirebaseStorage(imgUri); // Tải ảnh lên Firebase
                } else {
                    saveToFireBase(user); // Lưu thông tin người dùng nếu không có ảnh
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
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
                        user.setProfileImage(imageUrl); // Đặt đường dẫn ảnh cho người dùng
                        saveToFireBase(user); // Lưu thông tin người dùng
                        Toast.makeText(AddUserActivity.this, "Upload thành công", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddUserActivity.this, "Upload thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToFireBase(User user) {
        Log.d("AddUserActivity", "Bắt đầu lưu người dùng vào Firebase");
        FirebaseApiService.apiService.addUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
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
