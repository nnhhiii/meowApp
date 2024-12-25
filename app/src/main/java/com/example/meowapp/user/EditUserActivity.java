package com.example.meowapp.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private RadioGroup roleGroup;
    private RadioButton rbAdmin, rbStudent;
    private TextView scoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        etName = findViewById(R.id.username);
        etEmail = findViewById(R.id.email);
        imageView = findViewById(R.id.image);
        btnCancel = findViewById(R.id.btnBack);
        btnImage = findViewById(R.id.btnImage);
        btnSave = findViewById(R.id.btnSave);
        roleGroup = findViewById(R.id.radioGroup);
        rbAdmin = findViewById(R.id.radioButtonAdmin);
        rbStudent = findViewById(R.id.radioButtonStudent);
        scoreTextView = findViewById(R.id.scoreTextView);

        btnCancel.setOnClickListener(v -> finish());

        roleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonAdmin) {
                scoreTextView.setVisibility(View.GONE);
            } else {
                scoreTextView.setVisibility(View.VISIBLE);
            }
        });

        btnImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResult.launch(intent);
        });

        userId = getIntent().getStringExtra("userId");

        btnSave.setOnClickListener(v -> saveUserInfo());

        loadData();
    }

    private void loadData() {
        FirebaseApiService.apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    etName.setText(user.getUsername());
                    etEmail.setText(user.getEmail());
                    scoreTextView.setText(String.valueOf(user.getScore()));

                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Picasso.get().load(user.getAvatar()).into(imageView);
                    }

                    if (user.getRole().equals("admin")) {
                        rbAdmin.setChecked(true);
                        scoreTextView.setVisibility(View.GONE);
                    } else {
                        rbStudent.setChecked(true);
                        scoreTextView.setVisibility(View.VISIBLE);
                        scoreTextView.setText("Điểm số: " + user.getScore());
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
                if (result.getData() != null && result.getResultCode() == RESULT_OK) {
                    imgUri = result.getData().getData();
                    imageView.setImageURI(imgUri);
                }
            });

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference imageRef = storageReference.child("users/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    user.setAvatar(uri.toString());
                    saveToFireBase(userId, user);
                    Toast.makeText(EditUserActivity.this, "Upload thành công", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> Toast.makeText(EditUserActivity.this, "Upload thất bại", Toast.LENGTH_SHORT).show());
    }

    private void saveUserInfo() {
        String newName = etName.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newEmail)) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        user.setUsername(newName);
        user.setEmail(newEmail);
        user.setUpdated_at(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        int selectedRoleId = roleGroup.getCheckedRadioButtonId();
        if (selectedRoleId == R.id.radioButtonAdmin) {
            user.setRole("admin");
        } else {
            user.setRole("student");
        }

        if (imgUri != null) {
            uploadImageToFirebaseStorage(imgUri);
        } else {
            saveToFireBase(userId, user);
        }
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