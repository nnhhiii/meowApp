package com.example.meowapp.Main;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.adapter.NotificationAdapter;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Notification;
import com.example.meowapp.notification.NotificationManagementActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsNotificationActivity extends AppCompatActivity {
    private TextView tvNotification;
    private ImageButton btnBack;
    private List<Pair<String, Notification>> list = new ArrayList<>();
    private ListView listView;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_noti);
        listView = findViewById(R.id.listView);
        tvNotification = findViewById(R.id.tvNoti);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        displayNotifications();
    }

    private void displayNotifications() {
        FirebaseApiService.apiService.getAllNotifications().enqueue(new Callback<Map<String, Notification>>() {
            @Override
            public void onResponse(Call<Map<String, Notification>> call, Response<Map<String, Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Notification> notificationMap = response.body();
                    list.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới

                    if (notificationMap.isEmpty()) {
                        tvNotification.setVisibility(View.VISIBLE);
                        tvNotification.setText("Bạn không có thông báo mới.");
                        listView.setVisibility(View.GONE);
                    } else {
                        tvNotification.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);

                        // Chuyển đổi từ Map<String, Notification> thành List<Pair<String, Notification>>
                        for (Map.Entry<String, Notification> entry : notificationMap.entrySet()) {
                            Pair<String, Notification> pair = new Pair<>(entry.getKey(), entry.getValue());
                            list.add(pair);
                        }

                        adapter = new NotificationAdapter(SettingsNotificationActivity.this, list);
                        listView.setAdapter(adapter);
                    }
                } else {
                    tvNotification.setVisibility(View.VISIBLE);
                    tvNotification.setText("Không thể tải thông báo.");
                    listView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Notification>> call, Throwable t) {
                tvNotification.setVisibility(View.VISIBLE);
                tvNotification.setText("Lỗi kết nối!");
                listView.setVisibility(View.GONE);
            }
        });
    }


}
