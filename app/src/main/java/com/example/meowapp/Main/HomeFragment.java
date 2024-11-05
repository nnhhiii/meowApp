package com.example.meowapp.Main;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;

import com.example.meowapp.model.LanguagePreference;
import com.example.meowapp.questionType.BlankActivity;
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

public class HomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private ButtonAdapter buttonAdapter;
    private List<String> lessonIds,lessonNames;
    private List<Language> languages; // Danh sách ngôn ngữ
    private Map<String, String> languageIdMap = new HashMap<>(); // Lưu trữ mối quan hệ giữa ngôn ngữ và languageId
    private String userId, languageId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        bottomNavigationView = view.findViewById(R.id.bottomNavView);
        recyclerView = view.findViewById(R.id.recyclerView);

        lessonIds = new ArrayList<>();
        lessonNames = new ArrayList<>();
        languages = new ArrayList<>(); // Khởi tạo danh sách ngôn ngữ
        buttonAdapter = new ButtonAdapter(lessonNames,lessonIds, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(buttonAdapter);


        fetchLanguagePreference(); // Lấy danh sách ngôn ngữ từ API


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.tab_lang) {
                    showLanguagePopup();
                    return true;
                } else if (itemId == R.id.tab_point) {
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
                    for (Map.Entry<String, Lesson> entry : response.body().entrySet()) {
                        String lessonName = entry.getValue().getLesson_name();
                        // Sử dụng biểu thức chính quy để lấy số từ tên bài học
                        String lessonNumber = lessonName.replaceAll("[^0-9]", "");
                        lessonIds.add(entry.getKey());
                        lessonNames.add(lessonNumber);
                    }
                    buttonAdapter.notifyDataSetChanged(); // Cập nhật adapter
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
        userId = "1";
        FirebaseApiService.apiService.getAllLanguagePreferenceByUserId("\"user_id\"", "\"" + userId + "\"").enqueue(new Callback<Map<String, LanguagePreference>>() {
            @Override
            public void onResponse(Call<Map<String, LanguagePreference>> call, Response<Map<String, LanguagePreference>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Map.Entry<String, LanguagePreference> entry : response.body().entrySet()) {
                        languageId = entry.getValue().getLanguage_id();
                        fetchLanguageById(languageId);
                        fetchLessonsByLanguageId(languageId);
                    }

                }else{
                    Toast.makeText(getContext(), "No language preference available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, LanguagePreference>> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching languages", t);
            }
        });
    }

    private void fetchLanguageById(String languageId) {
        FirebaseApiService.apiService.getLanguageById(languageId).enqueue(new Callback<Language>() {
            @Override
            public void onResponse(Call<Language> call, Response<Language> response) {
                if (response.isSuccessful() && response.body() != null) {
                    languages.add(response.body());
                    languageIdMap.put(response.body().getLanguage_name(), languageId); // Lưu trữ languageId
                }
            }
            @Override
            public void onFailure(Call<Language> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching language by ID: " + languageId, t);
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
            // Tạo LinearLayout dọc để chứa cả ImageButton và TextView
            LinearLayout languageLayout = new LinearLayout(getContext());
            languageLayout.setOrientation(LinearLayout.VERTICAL);
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    300
            );
            params.setMargins(16, 0, 0, 0);
            languageLayout.setLayoutParams(params);

            // Tạo ImageButton để hiển thị ảnh ngôn ngữ
            ImageButton languageButton = new ImageButton(getContext());
            languageButton.setLayoutParams(new ViewGroup.MarginLayoutParams(250, 200));
            languageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Picasso.get()
                    .load(language.getLanguage_image())
                    .into(languageButton);

            languageButton.setOnClickListener(v -> popupWindow.dismiss());

            // Tạo TextView để hiển thị tên ngôn ngữ
            TextView languageName = new TextView(getContext());
            languageName.setText(language.getLanguage_name());
            languageName.setGravity(Gravity.CENTER);
            languageName.setTextSize(16);
            languageName.setPadding(0, 8, 0, 0);
            languageName.setMaxLines(2); // Giới hạn tối đa 2 dòng
            languageName.setEllipsize(TextUtils.TruncateAt.END); // Thêm dấu "..." nếu quá dài
            languageName.setWidth(200); // Giới hạn chiều rộng của TextView để đảm bảo xuống dòng


            // Thêm ImageButton và TextView vào LinearLayout
            languageLayout.addView(languageButton);
            languageLayout.addView(languageName);

            // Thêm LinearLayout vào buttonContainer
            buttonContainer.addView(languageLayout);

            languageButton.setOnClickListener(v -> {
                popupWindow.dismiss(); // Đóng popup khi nhấn
                String languageId = languageIdMap.get(language.getLanguage_name());
                fetchLessonsByLanguageId(languageId); // Gọi hàm để lấy bài học
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
