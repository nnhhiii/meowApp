package com.example.meowapp.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment; // Sử dụng Fragment

import com.example.meowapp.R;
import com.example.meowapp.auth.LoginActivity;
import com.example.meowapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserFragment extends Fragment {
    private LinearLayout navigationMenu;
    private LinearLayout userInfoLayout;
    private ImageButton btnSettings;
    private ImageButton btnBack;
    private Button btnEditProfile, btnNotificationSettings, btnCourseSettings, btnLogout;
    private TextView tvUserName, tvUserUsername, tvCoursePoints, tvUserCourses;
    private ImageView imgAvatar;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating layout cho fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Ánh xạ các thành phần trong layout
        navigationMenu = view.findViewById(R.id.navigationMenu);
        userInfoLayout = view.findViewById(R.id.infoLayout);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnBack = view.findViewById(R.id.btnBack);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnNotificationSettings = view.findViewById(R.id.btnNotificationSettings);
        btnCourseSettings = view.findViewById(R.id.btnCourseSettings);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Các TextView để hiển thị thông tin người dùng
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserUsername = view.findViewById(R.id.tvUserUsername);
        tvCoursePoints = view.findViewById(R.id.tvCoursePoints);
        tvUserCourses = view.findViewById(R.id.tvUserCourses);
        imgAvatar = view.findViewById(R.id.imgAvatar);

        // Lấy dữ liệu người dùng từ Firebase (hoặc nguồn khác)
        fetchUserDataFromFirebase();

        // Ẩn navigation menu ban đầu
        navigationMenu.setVisibility(View.GONE);

        // Lắng nghe sự kiện nút Settings để hiển thị/ẩn menu cài đặt
        btnSettings.setOnClickListener(v -> {
            if (navigationMenu.getVisibility() == View.GONE) {
                navigationMenu.setVisibility(View.VISIBLE);
                userInfoLayout.setVisibility(View.GONE);
            } else {
                navigationMenu.setVisibility(View.GONE);
                userInfoLayout.setVisibility(View.VISIBLE);
            }
        });

        // Nút Back
        btnBack.setOnClickListener(v -> getActivity().onBackPressed());

        // Các nút cài đặt khác
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        btnNotificationSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationSettingsActivity.class);
            startActivity(intent);
        });

        btnCourseSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CourseSettingsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }

    // Phương thức lấy dữ liệu người dùng từ Firebase
    private void fetchUserDataFromFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Lấy ID người dùng hiện tại

        // Lấy dữ liệu từ node "users"
        database.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lấy dữ liệu và ánh xạ vào đối tượng User
                user = dataSnapshot.getValue(User.class);

                // Sau khi lấy dữ liệu xong, cập nhật giao diện
                updateUserInterface();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }

    // Phương thức cập nhật giao diện sau khi lấy được dữ liệu người dùng
    private void updateUserInterface() {
        if (user != null) {
            tvUserName.setText(user.getUsername());
            tvUserUsername.setText(user.getEmail()); // Hiển thị email trong trường username
            tvCoursePoints.setText("Course Points: " + user.getScore());
            tvUserCourses.setText("Courses: " + user.getLessons());  // Hiển thị số lượng bài học

            // Nếu avatar là URL, dùng Picasso hoặc Glide để tải ảnh
            Picasso.get().load(user.getAvatar()).into(imgAvatar);  // Dùng Picasso để tải avatar
        }
    }
}
