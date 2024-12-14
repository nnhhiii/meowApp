package com.example.meowapp.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AdminProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar, btnBack;
    private EditText edtName, edtEmail, edtPassword;
    private Button btnEditProfile, btnSaveProfile, btnChangeAvatar;
    private LinearLayout profileSection, editProfileSection;

    private DatabaseReference dbRef;
    private StorageReference storageRef;
    private Uri imgUri;
    private String adminId = "admin"; // ID cố định hoặc lấy từ logic của bạn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Ánh xạ view
        imgAvatar = findViewById(R.id.imgAvatar);
        btnBack = findViewById(R.id.btnBack);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnChangeAvatar = findViewById(R.id.btnImage);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        profileSection = findViewById(R.id.profileInfoSection);
        editProfileSection = findViewById(R.id.editProfileSection);

        // Firebase references
        dbRef = FirebaseDatabase.getInstance().getReference("users").child(adminId);
        storageRef = FirebaseStorage.getInstance().getReference("avatars");

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút chỉnh sửa thông tin
        btnEditProfile.setOnClickListener(v -> {
            editProfileSection.setVisibility(View.VISIBLE);
            profileSection.setVisibility(View.GONE);
        });

        // Nút chọn ảnh đại diện
        btnChangeAvatar.setOnClickListener(v -> selectImage());

        // Nút lưu thông tin
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        // Tải dữ liệu người dùng
        loadUserData();
    }

    private void loadUserData() {
        dbRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();
                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String avatar = snapshot.child("avatar").getValue(String.class);

                edtName.setText(name);
                edtEmail.setText(email);

                if (!TextUtils.isEmpty(avatar)) {
                    Picasso.get().load(avatar).into(imgAvatar);
                }
            } else {
                Toast.makeText(this, "Không tải được dữ liệu!", Toast.LENGTH_SHORT).show();
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
                    imgAvatar.setImageURI(imgUri);
                } else {
                    Toast.makeText(this, "Không chọn được ảnh!", Toast.LENGTH_SHORT).show();
                }
            });

    private void saveProfile() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imgUri != null) {
            uploadImageToFirebase(name, email, password);
        } else {
            updateUserData(name, email, password, null);
        }
    }

    private void uploadImageToFirebase(String name, String email, String password) {
        String fileName = "avatar_" + System.currentTimeMillis() + ".jpg";
        StorageReference avatarRef = storageRef.child(fileName);

        avatarRef.putFile(imgUri).addOnSuccessListener(taskSnapshot ->
                avatarRef.getDownloadUrl().addOnSuccessListener(uri ->
                        updateUserData(name, email, password, uri.toString())
                )
        ).addOnFailureListener(e ->
                Toast.makeText(this, "Tải ảnh lên thất bại!", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateUserData(String name, String email, String password, String avatarUrl) {
        dbRef.child("name").setValue(name);
        dbRef.child("email").setValue(email);

        if (!TextUtils.isEmpty(password)) {
            dbRef.child("password").setValue(password);
        }

        if (avatarUrl != null) {
            dbRef.child("avatar").setValue(avatarUrl);
        }

        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
