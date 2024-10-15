package com.example.meowapp.language;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.Adapter.LanguageManagementAdapter;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguageManagementActivity extends AppCompatActivity {
    private LanguageManagementAdapter adapter;
    private ListView listView;
    private FloatingActionButton btnAdd;
    private ImageButton btnBack;
    private EditText etSearch;
    private List<Pair<String, Language>> languagesList = new ArrayList<>();
    private List<Pair<String, Language>> filteredLanguagesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lang_managerment);
        listView = findViewById(R.id.listView);
        etSearch = findViewById(R.id.etSearch);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, LanguageCreateActivity.class);
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filteredLanguagesList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
        loadData();
    }

    private void loadData() {
        FirebaseApiService.apiService.getAllLanguage().enqueue(new Callback<Map<String, Language>>() {
            @Override
            public void onResponse(Call<Map<String, Language>> call, Response<Map<String, Language>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Language> languageMap = response.body();

                    // Xóa dữ liệu cũ trong danh sách chính và danh sách lọc
                    languagesList.clear();
                    filteredLanguagesList.clear();

                    // Chuyển đổi từ Map<String, Language> thành List<Pair<String, Language>>
                    for (Map.Entry<String, Language> entry : languageMap.entrySet()) {
                        Pair<String, Language> pair = new Pair<>(entry.getKey(), entry.getValue());
                        languagesList.add(pair);
                        filteredLanguagesList.add(pair); // Bắt đầu với toàn bộ danh sách
                    }

                    // Truyền danh sách lọc cho Adapter
                    adapter = new LanguageManagementAdapter(LanguageManagementActivity.this, filteredLanguagesList);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(LanguageManagementActivity.this, "Failed to get info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Language>> call, Throwable t) {
                Toast.makeText(LanguageManagementActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error:", t.getMessage(), t);
            }
        });
    }

    private void filteredLanguagesList(String keyword) {
        filteredLanguagesList.clear();
        if (keyword.isEmpty()) {
            filteredLanguagesList.addAll(languagesList); // Nếu không có từ khóa, hiển thị tất cả
        } else {
            for (Pair<String, Language> pair : languagesList) {
                Language language = pair.second; // Lấy đối tượng Language từ cặp Pair
                if (language.getLanguage_name().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredLanguagesList.add(pair); // Thêm cặp Pair vào danh sách lọc
                }
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged(); // Cập nhật lại danh sách hiển thị
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

}