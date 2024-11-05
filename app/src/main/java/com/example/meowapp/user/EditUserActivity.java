package com.example.meowapp.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {
    private EditText etName, etEmail;
    private Button btnImage, btnSave;
    private ImageButton btnCancel;
    private ImageView imageView;
    private Uri imgUri;
    private String userId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_edit); // Đảm bảo layout này tồn tại

        etName = findViewById(R.id.username);
        etEmail = findViewById(R.id.email);
        imageView = findViewById(R.id.image);

        btnCancel = findViewById(R.id.btnBack);
        btnCancel.setOnClickListener(v -> finish());

        btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResult.launch(intent);
        });

        userId = getIntent().getStringExtra("userId");

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim(); // Lấy email từ trường
            if (!TextUtils.isEmpty(newName) && !TextUtils.isEmpty(newEmail)) {
                user.setUsername(newName); // Đặt tên cho người dùng
                user.setEmail(newEmail); // Đặt email cho người dùng
                // Cập nhật thời gian
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                user.setUpdated_at(currentTime); // Đặt thời gian cập nhật
                if (imgUri != null) {
                    uploadImageToFirebaseStorage(imgUri); // Tải ảnh lên Firebase
                } else {
                    saveToFireBase(userId, user); // Lưu thông tin người dùng
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        loadData();
    }

    private void loadData() {
        FirebaseApiService.apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    etName.setText(user.getUsername());
                    etEmail.setText(user.getEmail()); // Hiển thị email trong EditText
                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Picasso.get().load(user.getAvatar()).into(imageView);
                    }
                } else {
                    Toast.makeText(EditUserActivity.this, "Không tải được dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Toast.makeText(EditUserActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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
                        saveToFireBase(userId, user); // Lưu thông tin người dùng
                        Toast.makeText(EditUserActivity.this, "Upload thành công", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditUserActivity.this, "Upload thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToFireBase(String userId, User user) {
        FirebaseApiService.apiService.updateUser(userId, user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditUserActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditUserActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Toast.makeText(EditUserActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
