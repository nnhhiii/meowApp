package com.example.meowapp.Main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.adapter.LanguagePreferenceAdapter;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.LanguagePreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsCourseActivity extends AppCompatActivity {
    private LanguagePreferenceAdapter adapter;
    private ListView listView;
    private FloatingActionButton btnAdd;
    private ImageButton btnBack;
    private String userId;
    private List<Pair<String, LanguagePreference>> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_course);
        listView = findViewById(R.id.listView);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsCourseCreateActivity.class);
            startActivity(intent);
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        loadData();
    }
    private void loadData() {
        FirebaseApiService.apiService.getAllLanguagePreferenceByUserId("\"user_id\"", "\"" + userId + "\"").enqueue(new Callback<Map<String, LanguagePreference>>() {
            @Override
            public void onResponse(Call<Map<String, LanguagePreference>> call, Response<Map<String, LanguagePreference>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, LanguagePreference> LanguagePreferenceMap = response.body();
                    list.clear();

                    // Chuyển đổi từ Map<String, LanguagePreference> thành List<Pair<String, LanguagePreference>>
                    for (Map.Entry<String, LanguagePreference> entry : LanguagePreferenceMap.entrySet()) {
                        Pair<String, LanguagePreference> pair = new Pair<>(entry.getKey(), entry.getValue());
                        list.add(pair);
                    }
                    adapter = new LanguagePreferenceAdapter(SettingsCourseActivity.this, list);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(SettingsCourseActivity.this, "Failed to get info", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Map<String, LanguagePreference>> call, Throwable t) {
                Log.e("SettingCourse", "Error fetching languages", t);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}