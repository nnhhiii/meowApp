package com.example.meowapp.Main;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.meowapp.R;
import com.example.meowapp.adapter.ButtonAdapter;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.auth.ChangePasswordActivity;
import com.example.meowapp.auth.LoginActivity;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.LanguagePreference;
import com.example.meowapp.model.Lesson;
import com.example.meowapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    private LinearLayout navigationMenu, userInfoLayout, courseLayout;
    private ImageButton btnSettings, btnBack;
    private Button btnEditProfile, btnNotificationSettings, btnCourseSettings, btnLogout, btnChangePassword;
    private TextView tvUserName, tvEmail, tvCoursePoints, tvUserCourses;
    private ImageView imgAvatar;
    private Map<String, Pair<String, Integer>> languageIdMap = new HashMap<>();
    private List<Language> languages = new ArrayList<>();
    private User user;
    private String userId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_user, container, false);

        // Ánh xạ các thành phần trong layout
        initializeUI(view);

        // Lấy dữ liệu ngôn ngữ từ SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        String languageId = sharedPreferences.getString("languageId", null);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        if (languageId != null) {
            // Gọi API để lấy thông tin ngôn ngữ và hiển thị hình cờ
            fetchLanguageById(languageId);
        }
        fetchUserDataFromFirebase();

        return view;
    }

    private void initializeUI(View view) {
        navigationMenu = view.findViewById(R.id.navigationMenu);
        userInfoLayout = view.findViewById(R.id.infoLayout);
        courseLayout = view.findViewById(R.id.courseLayout); // Đảm bảo ánh xạ đúng
        btnSettings = view.findViewById(R.id.btnSettings);
        btnBack = view.findViewById(R.id.btnBack);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnNotificationSettings = view.findViewById(R.id.btnNotificationSettings);
        btnCourseSettings = view.findViewById(R.id.btnCourseSettings);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvCoursePoints = view.findViewById(R.id.tvCoursePoints);
        tvUserCourses = view.findViewById(R.id.tvUserCourses);
        imgAvatar = view.findViewById(R.id.imgAvatar);

        // Ẩn navigation menu ban đầu
        navigationMenu.setVisibility(View.GONE);

        // Tương tác với các nút
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
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });
    }

    private void fetchUserDataFromFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user = dataSnapshot.getValue(User.class);
                    Log.d("UserFragment", "Data: " + dataSnapshot.getValue());
                    updateUserInterface();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInterface() {
        if (user != null) {
            tvUserName.setText(user.getUsername());
            tvEmail.setText(user.getEmail());
            tvCoursePoints.setText("Điểm: " + user.getScore());

            // Hiển thị tất cả ngôn ngữ người dùng đã học
            StringBuilder languagesText = new StringBuilder("Ngôn ngữ học: ");
            for (Language language : languages) {
                languagesText.append(language.getLanguage_name()).append(", ");
            }
            if (languagesText.length() > 0) {
                languagesText.setLength(languagesText.length() - 2);
            }
            tvUserCourses.setText(languagesText.toString());

            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Picasso.get().load(user.getAvatar()).into(imgAvatar);
            }

            if (user.getLanguage_name() != null && !user.getLanguage_name().isEmpty()) {
                String[] languageIds = user.getLanguage_name().split(",");
                for (String languageId : languageIds) {
                    fetchLanguageById(languageId);
                }
            }
        }
    }



    private void fetchLanguageById(String languageId) {
        FirebaseApiService.apiService.getLanguageById(languageId).enqueue(new Callback<Language>() {
            @Override
            public void onResponse(Call<Language> call, Response<Language> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Language language = response.body();
                    addLanguageFlag(language);
                    languages.add(language);
                    languageIdMap.put(language.getLanguage_name(), new Pair<>(languageId, 0)); // Use default score 0 for now
                }
            }

            @Override
            public void onFailure(Call<Language> call, Throwable t) {
                Log.e("UserFragment", "Error fetching language by ID: " + languageId, t);
            }
        });
    }

    private void addLanguageFlag(Language language) {
        ImageView languageFlagImage = new ImageView(getContext());
        String flagImageUrl = language.getLanguage_image();

        if (flagImageUrl != null && !flagImageUrl.isEmpty()) {
            Picasso.get().load(flagImageUrl).into(languageFlagImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    courseLayout.addView(languageFlagImage);
                }

                @Override
                public void onError(Exception e) {
                    languageFlagImage.setImageResource(R.drawable.ic_launcher_background);
                    courseLayout.addView(languageFlagImage);
                }
            });
        } else {
            languageFlagImage.setImageResource(R.drawable.ic_launcher_background);
            courseLayout.addView(languageFlagImage);
        }

        languageFlagImage.setPadding(10, 10, 10, 10);

        // Thiết lập layout cho các cờ
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        languageFlagImage.setLayoutParams(layoutParams);
    }

}
