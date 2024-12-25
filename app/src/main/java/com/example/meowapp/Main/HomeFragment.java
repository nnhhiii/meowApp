package com.example.meowapp.Main;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import com.example.meowapp.model.LanguagePreference;
import com.example.meowapp.model.Level;
import com.example.meowapp.model.User;
import com.example.meowapp.model.UserProgress;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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

import org.apache.commons.lang3.tuple.Triple;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class HomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private ButtonAdapter buttonAdapter;
    private List<String> lessonIds, lessonNames;
    private List<Language> languages;
    private Map<String, Pair<String, LanguagePreference>> languageIdMap = new HashMap<>();
    private String userId, languageId, levelId, languagePrefId;
    private int language_score, score, streaks, hearts, eightyLessons, lessons, perfectLessons, diamonds;
    private LinearLayout heartContainer;
    private TextView heartText, countDownText, diamondText;
    private boolean isReceiverRegistered = false;
    private RelativeLayout refillButton;

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(buttonAdapter);

        bottomNavigationView.setItemIconTintList(null);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        fetchUserById();
        fetchLanguagePreference();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        languageId = sharedPreferences.getString("languageId", null);
        levelId = sharedPreferences.getString("levelId", null);
        languagePrefId = sharedPreferences.getString("languagePrefId", null);
        if(languageId != null && levelId != null && languagePrefId != null){
            fetchLessonsByLanguageAndLevelId(languageId, levelId, languagePrefId);
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.tab_lang) {
                showLanguagePopup();
                return true;
            } else if (itemId == R.id.tab_gem) {
                showDiamondsPopup();
                return true;
            } else if (itemId == R.id.tab_streak) {
                showStreakPopup();
                return true;
            } else if (itemId == R.id.tab_heart) {
                showHeartPopup();
                return true;
            }

            return false;
        });

        return view;
    }
    private void fetchUserById() {
        FirebaseApiService.apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    score = user.getScore();
                    streaks = user.getStreaks();
                    diamonds = user.getDiamonds();
                    hearts = user.getHearts();
                    perfectLessons = user.getPerfectLessons();
                    eightyLessons = user.getEightyLessons();
                    lessons = user.getLessons();

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
    private void fetchLanguagePreference() {
        FirebaseApiService.apiService.getAllLanguagePreferenceByUserId("\"user_id\"", "\"" + userId + "\"")
                .enqueue(new Callback<Map<String, LanguagePreference>>() {
                    @Override
                    public void onResponse(Call<Map<String, LanguagePreference>> call, Response<Map<String, LanguagePreference>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            for (Map.Entry<String, LanguagePreference> entry : response.body().entrySet()) {
                                LanguagePreference languagePreference = entry.getValue();
                                String languagePrefId = entry.getKey();
                                String languageId = entry.getValue().getLanguage_id();

                                fetchLanguageById(languageId, languagePreference, languagePrefId);
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
    private void fetchLanguageById(String languageId, LanguagePreference languagePreference, String languagePrefId) {
        FirebaseApiService.apiService.getLanguageById(languageId).enqueue(new Callback<Language>() {
            @Override
            public void onResponse(Call<Language> call, Response<Language> response) {
                if (response.isSuccessful() && response.body() != null) {
                    languages.add(response.body());
                    languageIdMap.put(response.body().getLanguage_name(), new Pair<>(languagePrefId, languagePreference));
                }
            }
            @Override
            public void onFailure(Call<Language> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching language by ID: " + languageId, t);
            }
        });
    }
    private void fetchLessonsByLanguageAndLevelId(String languageId, String levelId, String languagePrefId) {
        FirebaseApiService.apiService.getAllLessonByLanguageId("\"language_id\"", "\"" + languageId + "\"").enqueue(new Callback<Map<String, Lesson>>() {
            @Override
            public void onResponse(Call<Map<String, Lesson>> call, Response<Map<String, Lesson>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lessonIds.clear();
                    lessonNames.clear();
                    List<Map.Entry<String, Lesson>> entries = new ArrayList<>(response.body().entrySet());

                    // Lọc thêm theo level_id
//                    List<Map.Entry<String, Lesson>> filteredEntries = new ArrayList<>();
//                    for (Map.Entry<String, Lesson> entry : entries) {
//                        if (entry.getValue().getLevel_id().equals(levelId)) {
//                            filteredEntries.add(entry);
//                        }
//                    }

                    // Sắp xếp theo thứ tự bài học
                    Collections.sort(entries, (entry1, entry2) -> {
                        try {
                            int number1 = Integer.parseInt(entry1.getValue().getLesson_name().replaceAll("[^0-9]", ""));
                            int number2 = Integer.parseInt(entry2.getValue().getLesson_name().replaceAll("[^0-9]", ""));
                            return Integer.compare(number1, number2);
                        } catch (NumberFormatException e) {
                            Log.e("LessonSorting", "Invalid lesson name format: " + e.getMessage());
                            return 0; // Trường hợp không thể so sánh
                        }
                    });

                    // Thêm bài học vào danh sách hiển thị
                    for (Map.Entry<String, Lesson> entry : entries) {
                        String lessonName = entry.getValue().getLesson_name();
                        String lessonNumber = lessonName.replaceAll("[^0-9]", "");
                        lessonIds.add(entry.getKey());
                        lessonNames.add(lessonNumber);
                    }

                    fetchUserProgress(languageId, levelId, languagePrefId, lessonIds);
                    Log.d("HomeFragment", "Lessons: " + lessonIds);
                } else {
                    Log.e("HomeFragment", "No lessons found for language_id and level_id.");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Lesson>> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching lessons", t);
            }
        });
    }
    private void fetchUserProgress(String languageId, String levelId, String languagePrefId, List<String> lessonIds) {
        FirebaseApiService.apiService.getAllUserProgressByUserId("\"user_id\"", "\"" + userId + "\"")
                .enqueue(new Callback<Map<String, UserProgress>>() {
                    @Override
                    public void onResponse(Call<Map<String, UserProgress>> call, Response<Map<String, UserProgress>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Map<String, UserProgress> userProgressMap = response.body();
                            Set<String> completedLessons = new HashSet<>();

                            if (userProgressMap.isEmpty()) {
                                // Nếu không có tiến độ người dùng, thêm bài học đầu tiên vào adapter
                                completedLessons.add("1");
                            } else {
                                // Nếu có dữ liệu tiến độ người dùng, tiếp tục xử lý như bình thường
                                for (UserProgress progress : userProgressMap.values()) {
                                    // Kiểm tra xem lesson_id có tồn tại không
                                    String lessonId = progress.getLesson_id();
                                    if (lessonId != null && !lessonId.isEmpty()) {
                                        Log.d("HomeFragment", "completed lesson ID: " + lessonId);
                                        completedLessons.add(lessonId);

                                        // Thêm các bài học đã hoàn thành vào danh sách
                                        for (int i = 1; i <= Integer.parseInt(lessonId) + 1; i++) {
                                            completedLessons.add(String.valueOf(i));
                                        }
                                    } else {
                                        Log.d("HomeFragment", "Bỏ qua mục không có lesson_id");
                                    }
                                }
                            }
                            checkAndUpdateUserLevel(languageId, levelId, completedLessons, languagePrefId, lessonIds);
                            buttonAdapter.updateLessonStatus(completedLessons);
                        } else {
                            Log.e("HomeFragment", "No user progress found.");
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, UserProgress>> call, Throwable t) {
                        Log.e("HomeFragment", "Error fetching user progress", t);
                    }
                });
    }
    private void checkAndUpdateUserLevel(String languageId, String currentLevelId, Set<String> completedLessons, String languagePrefId, List<String> lessonIds) {
        // Kiểm tra danh sách bài học đã hoàn thành
        if (completedLessons.containsAll(lessonIds)) {
            FirebaseApiService.apiService.getAllLevelByLanguageId("\"language_id\"", "\"" + languageId + "\"")
                    .enqueue(new Callback<Map<String, Level>>() {
                        @Override
                        public void onResponse(Call<Map<String, Level>> call, Response<Map<String, Level>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Map<String, Level> levels = response.body();

                                String currentLevelKey = null;
                                String nextLevelKey = null;

                                for (Map.Entry<String, Level> entry : levels.entrySet()) {
                                    if (entry.getKey().equals(currentLevelId)) {
                                        currentLevelKey = entry.getKey();
                                        String currentLevelName = entry.getValue().getLevel_name();

                                        // Tìm cấp độ tiếp theo dựa trên tên cấp độ hiện tại
                                        for (Map.Entry<String, Level> levelEntry : levels.entrySet()) {
                                            if ("Cơ bản".equalsIgnoreCase(currentLevelName) &&
                                                    "Trung cấp".equalsIgnoreCase(levelEntry.getValue().getLevel_name())) {
                                                nextLevelKey = levelEntry.getKey();
                                                break;
                                            } else if ("Trung cấp".equalsIgnoreCase(currentLevelName) &&
                                                    "Nâng cao".equalsIgnoreCase(levelEntry.getValue().getLevel_name())) {
                                                nextLevelKey = levelEntry.getKey();
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }

                                if (currentLevelKey != null && nextLevelKey != null) {
                                    Map<String, Object> field = new HashMap<>();
                                    field.put("level_id", nextLevelKey);

                                    FirebaseApiService.apiService.updateLanguagePreference(languagePrefId, field)
                                            .enqueue(new Callback<LanguagePreference>() {
                                                @Override
                                                public void onResponse(Call<LanguagePreference> call, Response<LanguagePreference> response) {
                                                    if (response.isSuccessful()) {
                                                        Log.d("HomeFragment", "Cấp độ đã được cập nhật thành công.");
                                                    } else {
                                                        Log.e("HomeFragment", "Không thể cập nhật cấp độ.");
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<LanguagePreference> call, Throwable t) {
                                                    Log.e("HomeFragment", "Lỗi khi cập nhật cấp độ.", t);
                                                }
                                            });
                                } else {
                                    Toast.makeText(getContext(), "Hết bài học! Bạn đã hoàn thành hết ngôn ngữ ! Chờ Meow cập nhật thêm bài học nhé", Toast.LENGTH_LONG).show();
                                    Log.d("HomeFragment", "Không tìm thấy cấp độ tiếp theo.");
                                }
                            } else {
                                Log.e("HomeFragment", "Không thể lấy danh sách cấp độ từ language_id.");
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Level>> call, Throwable t) {
                            Log.e("HomeFragment", "Lỗi khi lấy danh sách cấp độ.", t);
                        }
                    });
        } else {
            Log.d("HomeFragment", "Người dùng chưa hoàn thành tất cả bài học.");
        }
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
                Pair<String, LanguagePreference> pair= languageIdMap.get(language.getLanguage_name());
                String languageId = pair.second.getLanguage_id();
                String levelId = pair.second.getLevel_id();
                int languageScore = pair.second.getLanguage_score();
                String languagePrefId = pair.first;
                fetchLessonsByLanguageAndLevelId(languageId, levelId, languagePrefId); // Gọi hàm để lấy bài học

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("languageId", languageId);
                editor.putString("levelId", levelId);
                editor.putString("languagePrefId", languagePrefId);
                editor.putInt("languageScore", languageScore);
                editor.apply();
            });
        }
    }

    private void showHeartPopup() {
        fetchUserById();
        // Inflate layout popup
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.item_popup_heart, null);

        diamondText = popupView.findViewById(R.id.tvDiamond);
        heartText = popupView.findViewById(R.id.heart_text);
        heartContainer = popupView.findViewById(R.id.heartContainer);
        countDownText = popupView.findViewById(R.id.tvCountDown);
        refillButton = popupView.findViewById(R.id.layoutBtnRefill);

        diamondText.setText(String.valueOf(diamonds));
        heartText.setText("Tim: " + hearts);
        updateHeartDisplay(hearts);

        if(hearts >= 5){
            refillButton.setVisibility(View.GONE);
        }
        refillButton.setOnClickListener(v -> {
            if (diamonds >= 450) {
                diamonds -= 450;
                updateUserHeart();
                updateUserDiamonds(diamonds);
            } else {
                Toast.makeText(getContext(), "Bạn không đủ đá", Toast.LENGTH_SHORT).show();
            }
        });

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
                Log.d(TAG, "Broadcast nhận: " + intent.getAction());
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
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(heartAndCountdownReceiver, filter);
        isReceiverRegistered = true;
        Log.d(TAG, "Receiver đã đăng kí");

        // Hủy receiver khi dialog bị đóng
        dialog.setOnDismissListener(dialogInterface -> {
            try {
                if (isReceiverRegistered) {
                    LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(heartAndCountdownReceiver);
                    isReceiverRegistered = false; // Đánh dấu receiver đã bị hủy
                    Log.d(TAG, "Receiver hủy đăng kí");
                }
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Receiver not registered or already unregistered", e);
            }
        });
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void showDiamondsPopup() {
        fetchUserById();
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.item_popup_gem, null);

        TextView diamondText = popupView.findViewById(R.id.gem_count);
        diamondText.setText(String.valueOf(diamonds));

        // Tạo và hiển thị AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(popupView);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    private void showStreakPopup() {
        fetchUserById(); // Lấy thông tin streak của người dùng (giả sử `streaks` đã được cập nhật)

        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.item_popup_streak, null);
        TextView streakTitle = popupView.findViewById(R.id.streak_title);
        streakTitle.setText("Chuỗi " + streaks + " ngày"); // Hiển thị số ngày streak

        // Xác định ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK); // Lấy ngày trong tuần (1 = Chủ nhật, 2 = Thứ 2, ...)

        // Mảng lưu trạng thái streak
        boolean[] streakDays = new boolean[7]; // Mặc định false
        int maxStreaks = today == Calendar.SUNDAY ? 6 : today ; // Số ngày streak tối đa có thể hiển thị

        int actualStreaks = Math.min(streaks, maxStreaks); // Cắt streak nếu tràn
        for (int i = 0; i < actualStreaks; i++) {
            int dayIndex = (today - 2 - i + 7) % 7; // Tính toán thứ (0 = Thứ 2, 6 = CN)
            streakDays[dayIndex] = true;
        }

        // Cập nhật giao diện các vòng tròn
        for (int i = 0; i < 7; i++) {
            String dayId = "day_t" + i; // day_t1, day_t2, ...
            int resId = requireContext().getResources().getIdentifier(dayId, "id", requireContext().getPackageName());

            if (resId != 0) {
                TextView dayView = popupView.findViewById(resId);
                if (dayView != null) {
                    // Đặt màu cam nếu là ngày streak
                    if (streakDays[i]) {
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
        countDownText.setText(timeFormatted);
    }
    public void updateUserHeart(){
        Map<String, Object> field = new HashMap<>();
        field.put("hearts", 5);
        FirebaseApiService.apiService.updateUserField(userId, field).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateHeartDisplay(5);
                    refillButton.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "Không lấy được tim", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updateUserDiamonds(Integer diamonds){
        Map<String, Object> field = new HashMap<>();
        field.put("diamonds", diamonds);
        FirebaseApiService.apiService.updateUserField(userId, field).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    diamondText.setText(String.valueOf(diamonds));
                }else{
                    Toast.makeText(getContext(), "Không lấy được đá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
