package com.example.meowapp.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meowapp.Adapter.ButtonAdapter;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Lesson;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.graphics.drawable.ColorDrawable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Map;

public class HomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private ButtonAdapter buttonAdapter;
    private List<String> lessonIds;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        bottomNavigationView = view.findViewById(R.id.bottomNavView);
        recyclerView = view.findViewById(R.id.recyclerView);


        lessonIds = new ArrayList<>();
        buttonAdapter = new ButtonAdapter(lessonIds, getContext());
        recyclerView.setLayoutManager(new CurvedLayoutManager(getContext()));
        recyclerView.setAdapter(buttonAdapter);


        fetchLessons();

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
                        lessonIds.add(entry.getKey());
                    }
                    buttonAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Lesson>> call, Throwable t) {
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

        ImageButton buttonEnglish = popupView.findViewById(R.id.button_english);
        ImageButton buttonSpain = popupView.findViewById(R.id.button_spain);

        buttonEnglish.setOnClickListener(v -> popupWindow.dismiss());
        buttonSpain.setOnClickListener(v -> popupWindow.dismiss());
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
