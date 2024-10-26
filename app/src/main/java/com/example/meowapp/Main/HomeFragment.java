package com.example.meowapp.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import com.squareup.picasso.Picasso;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
<<<<<<< HEAD
import com.example.meowapp.adapter.ButtonAdapter;
=======
import com.example.meowapp.Adapter.ButtonAdapter;
>>>>>>> 7d8f6b6614c6d07a776d772e2160e2667fe34a64
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.Lesson;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private ButtonAdapter buttonAdapter;
    private List<String> lessonIds;
    private List<Language> languages; // Danh sách ngôn ngữ

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        bottomNavigationView = view.findViewById(R.id.bottomNavView);
        recyclerView = view.findViewById(R.id.recyclerView);

        lessonIds = new ArrayList<>();
        languages = new ArrayList<>(); // Khởi tạo danh sách ngôn ngữ
        buttonAdapter = new ButtonAdapter(lessonIds, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(buttonAdapter);

        fetchLessons();
        fetchLanguages(); // Lấy danh sách ngôn ngữ từ API

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

    private void fetchLessons() {
        FirebaseApiService.apiService.getAllLessons().enqueue(new Callback<Map<String, Lesson>>() {
            @Override
            public void onResponse(Call<Map<String, Lesson>> call, Response<Map<String, Lesson>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lessonIds.clear();
                    for (Map.Entry<String, Lesson> entry : response.body().entrySet()) {
                        String lessonName = entry.getValue().getLesson_name();
                        // Sử dụng biểu thức chính quy để lấy số từ tên bài học
                        String lessonNumber = lessonName.replaceAll("[^0-9]", "");
                        lessonIds.add(lessonNumber);
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

    private void fetchLanguages() {
        FirebaseApiService.apiService.getAllLanguage().enqueue(new Callback<Map<String, Language>>() {
            @Override
            public void onResponse(Call<Map<String, Language>> call, Response<Map<String, Language>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    languages.clear();
                    for (Map.Entry<String, Language> entry : response.body().entrySet()) {
                        languages.add(entry.getValue()); // Thêm ngôn ngữ vào danh sách
                    }
                    Log.d("HomeFragment", "Languages: " + languages);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Language>> call, Throwable t) {
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

        ViewGroup buttonContainer = popupView.findViewById(R.id.buttonContainer); // Chỉnh sửa để tìm kiếm buttonContainer


        for (Language language : languages) {
            ImageButton languageButton = new ImageButton(getContext());


            Picasso.get()
                    .load(language.getLanguage_image())
                    .into(languageButton);

            languageButton.setOnClickListener(v -> {
                popupWindow.dismiss();

            });

            // Thêm button vào buttonContainer
            buttonContainer.addView(languageButton);
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
