package com.example.meowapp.Main;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
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
        fetchLanguagePreference(); // Fetch language preference from API

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.tab_lang) {
                    showLanguagePopup();
                    return true;
                } else if (itemId == R.id.tab_heart) {
                    showPointPopup("Point", "298484");
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

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", userId);
                    editor.putInt("score", score);
                    editor.putInt("streak", streaks);
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

    private void showPointPopup(String titleText, String messageText) {
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.item_popup_point, null);

        TextView title = popupView.findViewById(R.id.title);
        TextView message = popupView.findViewById(R.id.message);

        title.setText(titleText);
        message.setText(messageText);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.showAsDropDown(bottomNavigationView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });
    }
}
