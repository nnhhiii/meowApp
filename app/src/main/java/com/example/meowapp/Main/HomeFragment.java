package com.example.meowapp.Main;

import static android.content.Context.MODE_PRIVATE;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import java.util.Collections;
import java.util.Comparator;
import com.example.meowapp.model.LanguagePreference;
import com.example.meowapp.model.User;
import com.example.meowapp.questionType.BlankActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meowapp.adapter.ButtonAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.Lesson;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private ButtonAdapter buttonAdapter;
    private List<String> lessonIds, lessonNames;
    private List<Language> languages;
    private Map<String, Pair<String, Integer>> languageIdMap = new HashMap<>();
    private String userId, languageId;
    private int language_score;
    private FirebaseAuth firebaseAuth;
    private LinearLayout heartContainer;
    private TextView heartText, tvCountDown;;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        bottomNavigationView = view.findViewById(R.id.bottomNavView);
        recyclerView = view.findViewById(R.id.recyclerView);


        lessonIds = new ArrayList<>();
        lessonNames = new ArrayList<>();
        languages = new ArrayList<>();
        buttonAdapter = new ButtonAdapter(lessonNames, lessonIds, getContext());
        recyclerView.setLayoutManager(new CurvedLayoutManager(getContext()));
        recyclerView.setAdapter(buttonAdapter);
        bottomNavigationView.setItemIconTintList(null);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        fetchUserById();
        fetchLanguagePreference();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.tab_lang) {
                    showLanguagePopup();
                    return true;
                } else if (itemId == R.id.tab_gem) {
                    showDiamondsPopup("");
                    return true;
                } else if (itemId == R.id.tab_streak) {
                    // Lấy số ngày streak từ Firebase
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    userRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer streakDays = snapshot.child("streaks").getValue(Integer.class);
                            int streak = (streakDays != null) ? streakDays : 0;

                            showStreakPopup("Chuỗi ", streak);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("StreakPopup", "Failed to fetch user data: " + error.getMessage());
                        }
                    });

                    return true;


                } else if (itemId == R.id.tab_heart) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    userRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Integer hearts = dataSnapshot.child("hearts").getValue(Integer.class);

                            if (hearts != null) {

                                showHeartPopup("Tim", hearts);
                            } else {
                                // Nếu không có trái tim, hiển thị popup với giá trị mặc định là 0
                                showHeartPopup("Tim", 0);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Xử lý lỗi nếu có
                            Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu người dùng.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                }

                return false;
            }
        });

        return view;
    }

    private void fetchLessonsByLanguageId(String languageId) {
        FirebaseApiService.apiService.getAllLessonByLanguageId("\"language_id\"", "\"" + languageId + "\"").enqueue(new Callback<Map<String, Lesson>>() {
            @Override
            public void onResponse(Call<Map<String, Lesson>> call, Response<Map<String, Lesson>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lessonIds.clear();
                    lessonNames.clear();
                    List<Map.Entry<String, Lesson>> entries = new ArrayList<>(response.body().entrySet());

                    Collections.sort(entries, new Comparator<Map.Entry<String, Lesson>>() {
                        @Override
                        public int compare(Map.Entry<String, Lesson> entry1, Map.Entry<String, Lesson> entry2) {
                            String lessonName1 = entry1.getValue().getLesson_name();
                            String lessonName2 = entry2.getValue().getLesson_name();

                            int number1 = Integer.parseInt(lessonName1.replaceAll("[^0-9]", ""));
                            int number2 = Integer.parseInt(lessonName2.replaceAll("[^0-9]", ""));
                            return Integer.compare(number1, number2);
                        }
                    });

                    for (Map.Entry<String, Lesson> entry : entries) {
                        String lessonName = entry.getValue().getLesson_name();
                        String lessonNumber = lessonName.replaceAll("[^0-9]", "");
                        lessonIds.add(entry.getKey());
                        lessonNames.add(lessonNumber);
                    }
                    buttonAdapter.notifyDataSetChanged(); // Update adapter
                    Log.d("HomeFragment", "Lesson Numbers: " + lessonIds);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Lesson>> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching lessons", t);
            }
        });
    }

    private void fetchLanguagePreference() {
        FirebaseApiService.apiService.getAllLanguagePreferenceByUserId("\"user_id\"", "\"" + userId + "\"").enqueue(new Callback<Map<String, LanguagePreference>>() {
            @Override
            public void onResponse(Call<Map<String, LanguagePreference>> call, Response<Map<String, LanguagePreference>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Map.Entry<String, LanguagePreference> entry : response.body().entrySet()) {
                        languageId = entry.getValue().getLanguage_id();
                        language_score = entry.getValue().getLanguage_score();

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("languageId", languageId);
                        editor.putInt("languagePreferenceScore", language_score);
                        editor.apply();

                        fetchLanguageById(languageId, language_score);
                        fetchLessonsByLanguageId(languageId);
                    }
                } else {
                    Toast.makeText(getContext(), "No language preference available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, LanguagePreference>> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching languages", t);
            }
        });
    }

    private void fetchLanguageById(String languageId, int languagePreferenceScore) {
        FirebaseApiService.apiService.getLanguageById(languageId).enqueue(new Callback<Language>() {
            @Override
            public void onResponse(Call<Language> call, Response<Language> response) {
                if (response.isSuccessful() && response.body() != null) {
                    languages.add(response.body());
                    languageIdMap.put(response.body().getLanguage_name(), new Pair<>(languageId, languagePreferenceScore));
                }
            }

            @Override
            public void onFailure(Call<Language> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching language by ID: " + languageId, t);
            }
        });
    }

    private void fetchUserById() {
        FirebaseApiService.apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    int score = user.getScore();
                    int streaks = user.getStreaks();
                    int diamonds = user.getDiamonds();
                    int hearts = user.getHearts();
                    int perfectLessons = user.getPerfectLessons();
                    int eightyLessons = user.getEightyLessons();
                    int lessons = user.getLessons();

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", userId);
                    editor.putInt("score", score);
                    editor.putInt("streak", streaks);
                    editor.putInt("eightyLessons", eightyLessons);
                    editor.putInt("perfectLessons", perfectLessons);
                    editor.putInt("lessons", lessons);
                    editor.apply();
                } else {
                    Toast.makeText(getContext(), "No user available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching languages", t);
            }
        });
    }

    private void showLanguagePopup() {
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.item_popup_lang, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.showAsDropDown(bottomNavigationView);

        ViewGroup buttonContainer = popupView.findViewById(R.id.buttonContainer);

        for (Language language : languages) {
            LinearLayout languageLayout = new LinearLayout(getContext());
            languageLayout.setOrientation(LinearLayout.VERTICAL);
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    300
            );
            params.setMargins(16, 0, 0, 0);
            languageLayout.setLayoutParams(params);

            ImageButton languageButton = new ImageButton(getContext());
            languageButton.setLayoutParams(new ViewGroup.MarginLayoutParams(250, 200));
            languageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Picasso.get().load(language.getLanguage_image()).into(languageButton);

            TextView languageName = new TextView(getContext());
            languageName.setText(language.getLanguage_name());
            languageName.setGravity(Gravity.CENTER);
            languageName.setTextSize(16);
            languageName.setPadding(0, 8, 0, 0);
            languageName.setMaxLines(2);
            languageName.setEllipsize(TextUtils.TruncateAt.END);
            languageName.setWidth(200);

            languageLayout.addView(languageButton);
            languageLayout.addView(languageName);
            buttonContainer.addView(languageLayout);

            languageButton.setOnClickListener(v -> {
                popupWindow.dismiss();
                Pair<String, Integer> pair = languageIdMap.get(language.getLanguage_name());
                String languageId = pair.first;
                int language_score = pair.second;
                fetchLessonsByLanguageId(languageId); // Gọi hàm để lấy bài học

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("languageId", languageId);
                editor.putInt("languagePreferenceScore", language_score);
                editor.apply();
            });
        }
    }

    private void showHeartPopup(String title, int heartCount) {
        // Inflate layout popup
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.item_popup_heart, null);

        // Tham chiếu các thành phần UI
        heartText = popupView.findViewById(R.id.heart_text);
        heartContainer = popupView.findViewById(R.id.heartContainer);
        tvCountDown = popupView.findViewById(R.id.tvCountDown);
        TextView tvDiamond = popupView.findViewById(R.id.tvDiamond);

        heartText.setText(title + ": " + heartCount);
        updateHeartDisplay(heartCount);

        // Tạo và hiển thị AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(popupView);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        // Đăng ký BroadcastReceiver khi popup được hiển thị
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.UPDATE_HEART");
        filter.addAction("com.example.UPDATE_COUNTDOWN");

        BroadcastReceiver heartAndCountdownReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Kiểm tra Action để phân biệt các loại broadcast
                if (intent.getAction().equals("com.example.UPDATE_HEART")) {
                    int updatedHeartCount = intent.getIntExtra("heartCount", 0);
                    updateHeartDisplay(updatedHeartCount);
                } else if (intent.getAction().equals("com.example.UPDATE_COUNTDOWN")) {
                    long millisUntilFinished = intent.getLongExtra("countdown", 0);
                    updateCountdownDisplay(millisUntilFinished);
                }
            }
        };

        // Đăng ký receiver
        requireContext().registerReceiver(heartAndCountdownReceiver, filter);

        // Hủy receiver khi dialog bị đóng
        dialog.setOnDismissListener(dialogInterface -> requireContext().unregisterReceiver(heartAndCountdownReceiver));
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // Hàm hiển thị popup Diamonds (Diamonds Popup)
    private void showDiamondsPopup(String title) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Integer diamonds = dataSnapshot.child("diamonds").getValue(Integer.class);
                int count = (diamonds != null) ? diamonds : 0;

                showPopup(title, count); // Hiển thị popup
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Lỗi khi lấy dữ liệu " + title, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showPopup(String title, int count) {
        // Sử dụng context phù hợp (Fragment hoặc Activity)
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Layout tùy chỉnh cho popup, có thể thay đổi theo nhu cầu
        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.item_popup_gem, null);

        // Cập nhật tiêu đề của popup
        TextView titleView = popupView.findViewById(R.id.gem_count);
        titleView.setText(title);


        // Xây dựng và hiển thị AlertDialog
        builder.setView(popupView);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showStreakPopup(String title, int streakDays) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Inflate layout popup
                View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.item_popup_streak, null);

                // Cập nhật tiêu đề của popup với số ngày streak
                TextView streakTitle = popupView.findViewById(R.id.streak_title);
                streakTitle.setText("Chuỗi" + streakDays + " ngày "); // Hiển thị số ngày streak cùng với title

                // Cập nhật các vòng tròn streak
                for (int i = 1; i <= 10; i++) { // Giả sử có tối đa 10 ngày streak
                    String dayId = "day_t" + i; // day_t1, day_t2, ...
                    int resId = requireContext().getResources().getIdentifier(dayId, "id", requireContext().getPackageName());

                    if (resId != 0) {
                        TextView dayView = popupView.findViewById(resId);
                        if (dayView != null) {
                            // Nếu ngày streak nhỏ hơn hoặc bằng streakDays, đổi màu nền
                            if (i <= streakDays) {
                                dayView.setBackgroundResource(R.drawable.circle_background_orange);
                            } else {
                                dayView.setBackgroundResource(R.drawable.circle_background);
                            }
                        }
                    }
                }

                // Hiển thị popup
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setView(popupView);
                AlertDialog dialog = builder.create();
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StreakPopup", "Failed to fetch user data: " + error.getMessage());
            }
        });
    }

    // Cập nhật hiển thị trái tim
    private void updateHeartDisplay(int heartCount) {
        if (heartContainer != null) {
            heartContainer.removeAllViews(); // Xóa các trái tim cũ
            for (int i = 0; i < heartCount; i++) {
                ImageView heart = new ImageView(getContext());
                heart.setImageResource(R.drawable.heart);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(24), dpToPx(24));
                params.setMargins(8, 0, 8, 0);
                heart.setLayoutParams(params);
                heartContainer.addView(heart);
            }
            heartText.setText("Tim: " + heartCount);
        }
    }

    // Cập nhật hiển thị thời gian đếm ngược
    private void updateCountdownDisplay(long millisUntilFinished) {
        int seconds = (int) (millisUntilFinished / 1000) % 60;
        int minutes = (int) (millisUntilFinished / (1000 * 60)) % 60;

        // Chỉ hiển thị phút và giây
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvCountDown.setText(timeFormatted);
    }
}
