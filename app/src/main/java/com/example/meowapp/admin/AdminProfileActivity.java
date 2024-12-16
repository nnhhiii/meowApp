package com.example.meowapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.language.LanguageManagementActivity;
import com.example.meowapp.lesson.LessonManagementActivity;
import com.example.meowapp.mission.MissionManagementActivity;
import com.example.meowapp.user.UserManagementActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminProfileActivity extends AppCompatActivity {

    private TextView tvAdminName, tvAdminEmail, tvTotalUsers, tvTotalCourses, tvTotalLessons;
    private Button btnEditProfile, btnLogout;
    private ImageButton btnBack;
    private LinearLayout usersSection, coursesSection, lessonsSection;

    private DatabaseReference dbRef;
    private String adminId = "admin"; // ID cố định hoặc lấy từ logic của bạn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Ánh xạ view
        tvAdminName = findViewById(R.id.tvName);
        tvAdminEmail = findViewById(R.id.tvEmail);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalCourses = findViewById(R.id.tvTotalCourses);
        tvTotalLessons = findViewById(R.id.tvTotalLessons);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        // Firebase reference
        dbRef = FirebaseDatabase.getInstance().getReference();

        btnBack.setOnClickListener(v -> finish());

        getAdminInfo();
        // Lấy dữ liệu từ Firebase
        getAdminStats();

        usersSection.setOnClickListener(v -> navigateToUserManagement());

        coursesSection.setOnClickListener(v -> navigateToLanguageManagement());

        lessonsSection.setOnClickListener(v -> navigateToLessonManagement());

        btnEditProfile.setOnClickListener(v -> editProfile());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void getAdminInfo() {
        dbRef.child("admins").child(adminId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    String adminName = snapshot.child("name").getValue(String.class);
                    String adminEmail = snapshot.child("email").getValue(String.class);

                    // Cập nhật thông tin lên giao diện
                    tvAdminName.setText(adminName != null ? adminName : "Chưa có tên");
                    tvAdminEmail.setText(adminEmail != null ? adminEmail : "Chưa có email");
                } else {
                    Toast.makeText(AdminProfileActivity.this, "Không tìm thấy thông tin admin", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AdminProfileActivity.this, "Lỗi khi lấy thông tin admin", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getAdminStats() {
        // Lấy thông tin về số lượng người dùng, khóa học và bài học từ Firebase
        dbRef.child("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                int userCount = (int) snapshot.getChildrenCount();
                tvTotalUsers.setText(String.valueOf(userCount));
            } else {
                Toast.makeText(AdminProfileActivity.this, "Lỗi khi lấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
            }
        });

        dbRef.child("languages").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                int courseCount = (int) snapshot.getChildrenCount(); // Số lượng khóa học
                tvTotalCourses.setText(String.valueOf(courseCount));  // Hiển thị tổng số khóa học
            } else {
                Toast.makeText(AdminProfileActivity.this, "Lỗi khi lấy dữ liệu khóa học", Toast.LENGTH_SHORT).show();
            }
        });


        dbRef.child("lessons").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                int lessonCount = (int) snapshot.getChildrenCount();
                tvTotalLessons.setText(String.valueOf(lessonCount));
            } else {
                Toast.makeText(AdminProfileActivity.this, "Lỗi khi lấy dữ liệu bài học", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToUserManagement() {
        Intent intent = new Intent(AdminProfileActivity.this, UserManagementActivity.class);
        startActivity(intent);
    }

    private void navigateToLanguageManagement() {
        Intent intent = new Intent(AdminProfileActivity.this, LanguageManagementActivity.class);
        startActivity(intent);
    }

    private void navigateToLessonManagement() {
        Intent intent = new Intent(AdminProfileActivity.this, LessonManagementActivity.class);
        startActivity(intent);
    }

    private void editProfile() {
        Intent intent = new Intent(AdminProfileActivity.this, AdminEditProfileActivity.class);
        startActivity(intent);
    }
    private void logout() {
        Toast.makeText(AdminProfileActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        finish();
    }
}
