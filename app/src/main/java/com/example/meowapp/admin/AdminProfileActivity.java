package com.example.meowapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.Main.SettingsEditProfileActivity;
import com.example.meowapp.R;
import com.example.meowapp.auth.ChangePasswordActivity;
import com.example.meowapp.language.LanguageManagementActivity;
import com.example.meowapp.lesson.LessonManagementActivity;
import com.example.meowapp.mission.MissionManagementActivity;
import com.example.meowapp.model.User;
import com.example.meowapp.user.UserManagementActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AdminProfileActivity extends AppCompatActivity {

    private TextView tvAdminName, tvAdminEmail, tvTotalCourses, tvTotalLessons;
    private Button btnEditProfile, btnChangePassword;
    private ImageButton btnBack;
    private ImageView imgAvatar;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Ánh xạ view
        tvAdminName = findViewById(R.id.tvName);
        tvAdminEmail = findViewById(R.id.tvEmail);
        tvTotalCourses = findViewById(R.id.tvTotalCourses);
        tvTotalLessons = findViewById(R.id.tvTotalLessons);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnEditProfile = findViewById(R.id.btnEdit);
        btnBack = findViewById(R.id.btnBack);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnBack.setOnClickListener(v -> finish());
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProfileActivity.this, SettingsEditProfileActivity.class);
            startActivity(intent);
        });
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        loadData();
    }

    private void loadData() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("users").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    tvAdminName.setText(user.getUsername());
                    tvAdminEmail.setText(user.getEmail());

                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Picasso.get().load(user.getAvatar()).into(imgAvatar);
                    }
                } else {
                    Toast.makeText(AdminProfileActivity.this, "Không tìm thấy thông tin admin", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AdminProfileActivity.this, "Lỗi khi lấy thông tin admin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
