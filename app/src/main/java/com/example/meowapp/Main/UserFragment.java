package com.example.meowapp.Main;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.auth.ChangePasswordActivity;
import com.example.meowapp.auth.LoginActivity;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.LanguagePreference;
import com.example.meowapp.model.Level;
import com.example.meowapp.model.User;
import com.example.meowapp.model.UserProgress;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    private LinearLayout navigationMenu, userInfoLayout, courseLayout;
    private ImageButton btnSettings, btnBack;
    private Button btnEditProfile, btnNotificationSettings, btnCourseSettings, btnLogout, btnChangePassword;
    private TextView tvUserName, tvEmail, tvCoursePoints, tvTotalCourses, tvTotalLessons;
    private ImageView imgAvatar;
    Map<String, Triple<Language, Integer, String>> languageMap = new HashMap<>();
    private User user;
    private String userId;
    private int languagesLoaded, expectedSize, lessonsCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_user, container, false);

        // Ánh xạ các thành phần trong layout
        initializeUI(view);
        
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        fetchLanguagePreference();

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
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvTotalLessons = view.findViewById(R.id.tvTotalLessons);
        tvTotalCourses = view.findViewById(R.id.tvTotalCourses);

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
    private void fetchLanguagePreference() {
        FirebaseApiService.apiService.getAllLanguagePreferenceByUserId("\"user_id\"", "\"" + userId + "\"")
                .enqueue(new Callback<Map<String, LanguagePreference>>() {
                    @Override
                    public void onResponse(Call<Map<String, LanguagePreference>> call, Response<Map<String, LanguagePreference>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            expectedSize = response.body().size();
                            for (Map.Entry<String, LanguagePreference> entry : response.body().entrySet()) {
                                String languageId = entry.getValue().getLanguage_id();
                                String levelId = entry.getValue().getLevel_id();
                                int languageScore = entry.getValue().getLanguage_score();

                                fetchLanguageById(languageId, levelId, languageScore);
                            }
                        } else {
                            Toast.makeText(getContext(), "No language preference available", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, LanguagePreference>> call, Throwable t) {
                        Toast.makeText(getContext(), "Error fetching language preferences", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchLanguageById(String languageId, String levelId, int languageScore) {
        FirebaseApiService.apiService.getLanguageById(languageId).enqueue(new Callback<Language>() {
            @Override
            public void onResponse(Call<Language> call, Response<Language> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Language language = response.body();
                    fetchLevelById(levelId, languageId, language, languageScore);
                }
            }

            @Override
            public void onFailure(Call<Language> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching language by ID: " + languageId, t);
            }
        });
    }

    private void fetchLevelById(String levelId, String languageId, Language language, int languageScore) {
        FirebaseApiService.apiService.getLevelById(levelId).enqueue(new Callback<Level>() {
            @Override
            public void onResponse(Call<Level> call, Response<Level> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Level level = response.body();
                    languageMap.put(languageId, Triple.of(language, languageScore, level.getLevel_name()));

                    languagesLoaded++;

                    // Kiểm tra nếu tất cả dữ liệu đã tải xong
                    if (languagesLoaded == expectedSize) {
                        fetchUserProgressByUserId();
                    }
                }
            }

            @Override
            public void onFailure(Call<Level> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching level", t);
            }
        });
    }
    private void fetchUserProgressByUserId() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("user_progress");
        database.orderByChild("user_id").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<UserProgress> userProgressList = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserProgress userProgress = snapshot.getValue(UserProgress.class);
                            // Kiểm tra xem lesson_id có tồn tại không
                            String lessonId = userProgress.getLesson_id();
                            if (lessonId != null && !lessonId.isEmpty()) {
                                userProgressList.add(userProgress);
                            } else {
                                Log.e("UserFragment", "Bỏ qua mục không có lesson_id");
                            }
                        }
                        lessonsCount = userProgressList.size();
                        fetchUser();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FirebaseError", "Error: " + databaseError.getMessage());
                    }
                });
    }

    private void fetchUser() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user = dataSnapshot.getValue(User.class);
                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Picasso.get().load(user.getAvatar()).into(imgAvatar);
                    }
                    tvUserName.setText(user.getUsername());
                    tvEmail.setText(user.getEmail());
                    tvCoursePoints.setText("Điểm: " + user.getScore());
                    tvTotalCourses.setText(String.valueOf(expectedSize));
                    tvTotalLessons.setText(String.valueOf(lessonsCount));

                    // Xóa nội dung cũ trước khi thêm mới
                    courseLayout.removeAllViews();

                    for (Map.Entry<String, Triple<Language, Integer, String>> entry : languageMap.entrySet()) {
                        Language language = entry.getValue().getLeft();
                        int languageScore = entry.getValue().getMiddle();
                        String levelName = entry.getValue().getRight();

                        LinearLayout languageLayout = new LinearLayout(getContext());
                        languageLayout.setOrientation(LinearLayout.VERTICAL);
                        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(16, 0, 0, 0);
                        languageLayout.setLayoutParams(params);

                        TextView languageName = new TextView(getContext());
                        languageName.setText(language.getLanguage_name());
                        languageName.setGravity(Gravity.CENTER);
                        languageName.setTextSize(16);
                        languageName.setMaxLines(1);
                        languageName.setWidth(200);

                        ImageView languageButton = new ImageView(getContext());
                        languageButton.setLayoutParams(new ViewGroup.MarginLayoutParams(250, 200));
                        languageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                        Glide.with(getContext())
                                .load(language.getLanguage_image())
                                .transform(new RoundedCorners(30)) // Bo tròn góc với bán kính 30px
                                .into(languageButton);

                        TextView levelNameTv = new TextView(getContext());
                        levelNameTv.setText(levelName);
                        levelNameTv.setGravity(Gravity.CENTER);
                        levelNameTv.setTextSize(16);
                        levelNameTv.setMaxLines(2);
                        levelNameTv.setTextColor(ContextCompat.getColor(getContext(), R.color.light_gray));
                        levelNameTv.setEllipsize(TextUtils.TruncateAt.END);
                        levelNameTv.setWidth(200);

                        TextView languageScoreTv = new TextView(getContext());
                        languageScoreTv.setText(languageScore + "đ");
                        languageScoreTv.setGravity(Gravity.CENTER);
                        languageScoreTv.setTextSize(16);
                        languageScoreTv.setPadding(0, 8, 0, 0);
                        languageScoreTv.setMaxLines(2);
//                        languageScoreTv.setTextColor(Color.parseColor("#777777"));
                        levelNameTv.setTextColor(ContextCompat.getColor(getContext(), R.color.pink1));
                        languageScoreTv.setEllipsize(TextUtils.TruncateAt.END);
                        languageScoreTv.setWidth(200);

                        languageLayout.addView(languageName);
                        languageLayout.addView(languageButton);
                        languageLayout.addView(levelNameTv);
                        languageLayout.addView(languageScoreTv);
                        courseLayout.addView(languageLayout);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
