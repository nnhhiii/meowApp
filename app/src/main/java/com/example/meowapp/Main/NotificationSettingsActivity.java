package com.example.meowapp.Main;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Notification;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationSettingsActivity extends AppCompatActivity {
    private TextView tvNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_noti);

        ImageButton btnBack = findViewById(R.id.btnBack);
        tvNotification = findViewById(R.id.tvNotification);

        // Xử lý nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Hiển thị danh sách thông báo từ API
        displayNotifications();
    }

    private void displayNotifications() {
        // Gọi API để lấy danh sách thông báo
        FirebaseApiService.apiService.getNotifications().enqueue(new Callback<Map<String, Notification>>() {
            @Override
            public void onResponse(Call<Map<String, Notification>> call, Response<Map<String, Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Notification> notifications = response.body();
                    if (notifications.isEmpty()) {
                        tvNotification.setText("Bạn không có thông báo mới.");
                    } else {
                        StringBuilder builder = new StringBuilder();
                        // Duyệt qua các thông báo trong Map
                        for (Notification notification : notifications.values()) {
                            builder.append("• ").append(notification.getMessage()).append("\n");
                        }
                        tvNotification.setText(builder.toString());
                    }
                } else {
                    tvNotification.setText("Không thể tải thông báo.");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Notification>> call, Throwable t) {
                tvNotification.setText("Lỗi kết nối!");
            }
        });
    }

}
