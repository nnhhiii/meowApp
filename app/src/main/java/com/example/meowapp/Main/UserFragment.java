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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.meowapp.R;
import com.example.meowapp.auth.ChangePasswordActivity;
import com.example.meowapp.auth.LoginActivity;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.LanguagePreference;
import com.example.meowapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UserFragment extends Fragment {
    private LinearLayout navigationMenu;
    private LinearLayout userInfoLayout;
    private LinearLayout courseLayout;
    private ImageButton btnSettings;
    private ImageButton btnBack;
    private Button btnEditProfile, btnNotificationSettings, btnCourseSettings, btnLogout, btnChangePassword;
    private TextView tvUserName, tvEmail, tvCoursePoints, tvUserCourses;
    private ImageView imgAvatar;
    private User user;

    private Map<String, Integer> languageFlags;

    private void setupLanguageFlags() {
        languageFlags = new HashMap<>();
        languageFlags.put("en", R.drawable.ic_flagofusa);
        languageFlags.put("es", R.drawable.ic_flagofspain);
        languageFlags.put("vi", R.drawable.ic_flagvn);
        // Thêm các ngôn ngữ khác
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating layout cho fragment
        View view = inflater.inflate(R.layout.fragment_main_user, container, false);

        // Ánh xạ các thành phần trong layout
        navigationMenu = view.findViewById(R.id.navigationMenu);
        userInfoLayout = view.findViewById(R.id.infoLayout);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnBack = view.findViewById(R.id.btnBack);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnNotificationSettings = view.findViewById(R.id.btnNotificationSettings);
        btnCourseSettings = view.findViewById(R.id.btnCourseSettings);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        courseLayout = view.findViewById(R.id.courseLayout);

        // Các TextView để hiển thị thông tin người dùng
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvCoursePoints = view.findViewById(R.id.tvCoursePoints);
        tvUserCourses = view.findViewById(R.id.tvUserCourses);
        imgAvatar = view.findViewById(R.id.imgAvatar);

        // Lấy dữ liệu người dùng từ Firebase
        fetchUserDataFromFirebase();
        setupLanguageFlags(); // thiết lập ánh xạ cờ

        // Ẩn navigation menu ban đầu
        navigationMenu.setVisibility(View.GONE);

        btnSettings.setOnClickListener(v -> {
            btnSettings.setVisibility(View.GONE);

            if (navigationMenu.getVisibility() == View.GONE) {
                navigationMenu.setTranslationX(navigationMenu.getWidth());
                navigationMenu.setVisibility(View.VISIBLE);
                navigationMenu.animate().translationX(0).setDuration(300).start();
                userInfoLayout.setVisibility(View.GONE);
            } else {
                navigationMenu.animate().translationX(navigationMenu.getWidth()).setDuration(300)
                        .withEndAction(() -> navigationMenu.setVisibility(View.GONE)).start();
                userInfoLayout.setVisibility(View.VISIBLE);
            }
        });

        btnBack.setOnClickListener(v -> {
            // Hiển thị lại nút Settings
            btnSettings.setVisibility(View.VISIBLE);

            // Ẩn menu điều hướng
            navigationMenu.setVisibility(View.GONE);

            // Hiển thị lại layout thông tin người dùng
            userInfoLayout.setVisibility(View.VISIBLE);
        });


        // Các nút cài đặt khác
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsEditProfileActivity.class);
            intent.putExtra("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
        });


        btnNotificationSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsNotificationActivity.class);
            startActivity(intent);
        });

        btnCourseSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsCourseActivity.class);
            startActivity(intent);
        });
        btnLogout.setOnClickListener(v -> {
            // Đăng xuất khỏi Firebase
            FirebaseAuth.getInstance().signOut();

            // Chuyển hướng đến LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        btnChangePassword.setOnClickListener(v -> {
            // Chuyển đến màn hình Đổi mật khẩu
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });
        return view;
    }

    // Phương thức lấy dữ liệu người dùng từ Firebase
    private void fetchUserDataFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // Nếu không có người dùng đăng nhập, bạn có thể thông báo và thoát fragment
            Toast.makeText(getContext(), "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();  // Lấy ID người dùng hiện tại
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        // Lấy dữ liệu từ node "users"
        database.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Kiểm tra dữ liệu người dùng có tồn tại không
                if (dataSnapshot.exists()) {
                    // Lấy dữ liệu và ánh xạ vào đối tượng User
                    user = dataSnapshot.getValue(User.class);
                    // Sau khi lấy dữ liệu xong, cập nhật giao diện
                    updateUserInterface();
                } else {
                    // Xử lý nếu không có dữ liệu
                    tvUserName.setText("User not found");
                    tvEmail.setText("N/A");
                    tvCoursePoints.setText("N/A");
                    tvUserCourses.setText("N/A");
                    imgAvatar.setImageResource(R.drawable.user_avatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Thông báo lỗi khi việc lấy dữ liệu gặp sự cố
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức cập nhật giao diện sau khi lấy được dữ liệu người dùng
    private void updateUserInterface() {
        if (user != null) {
            tvUserName.setText(user.getUsername());
            tvEmail.setText(user.getEmail());
            tvCoursePoints.setText("Điểm: " + user.getScore());

            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Picasso.get().load(user.getAvatar()).into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.drawable.user_avatar);
            }

            courseLayout.removeAllViews();
            if (user.getLanguage_id() != null && !user.getLanguage_id().isEmpty()) {
                String[] languageIds = user.getLanguage_id().split(",");
                for (String languageId : languageIds) {
                    Integer flagResource = languageFlags.get(languageId.trim());
                    if (flagResource != null) {
                        ImageButton flagButton = new ImageButton(getContext());
                        flagButton.setImageResource(flagResource);
                        flagButton.setBackground(null);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(16, 16, 16, 16);
                        flagButton.setLayoutParams(layoutParams);

                        courseLayout.addView(flagButton);
                    }
                }
            }
        }
    }
}
