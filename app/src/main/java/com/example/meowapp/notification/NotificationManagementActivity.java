package com.example.meowapp.notification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meowapp.R;
import com.example.meowapp.adapter.NotificationAdapter;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Notification;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationManagementActivity extends AppCompatActivity {
    private NotificationAdapter adapter;
    private ListView listView;
    private FloatingActionButton btnAdd;
    private ImageButton btnBack;
    private List<Pair<String, Notification>> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_notification_management);
        listView = findViewById(R.id.listView);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationCreateActivity.class);
            startActivity(intent);
        });
        loadData();
    }
    private void loadData() {
        FirebaseApiService.apiService.getAllNotifications().enqueue(new Callback<Map<String, Notification>>() {
            @Override
            public void onResponse(Call<Map<String, Notification>> call, Response<Map<String, Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Notification> NotificationMap = response.body();
                    list.clear();
                    
                    // Chuyển đổi từ Map<String, Notification> thành List<Pair<String, Notification>>
                    for (Map.Entry<String, Notification> entry : NotificationMap.entrySet()) {
                        Pair<String, Notification> pair = new Pair<>(entry.getKey(), entry.getValue());
                        list.add(pair);
                    }
                    adapter = new NotificationAdapter(NotificationManagementActivity.this, list);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(NotificationManagementActivity.this, "Failed to get info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Notification>> call, Throwable t) {
                Toast.makeText(NotificationManagementActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error:", t.getMessage(), t);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}