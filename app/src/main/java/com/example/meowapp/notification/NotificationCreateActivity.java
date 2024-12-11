package com.example.meowapp.notification;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseAuthHelper;
import com.example.meowapp.api.NotificationApiService;
import com.example.meowapp.model.FirebaseNotificationRequest;
import com.example.meowapp.model.FirebaseResponse;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationCreateActivity extends AppCompatActivity {
    private EditText etTitle, etMessage;
    private Button btnSendNotification;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_notification_create);
        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnSendNotification = findViewById(R.id.btnSendNotification);
        btnSendNotification.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            String message = etMessage.getText().toString();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(message)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ tiêu đề và nội dung", Toast.LENGTH_SHORT).show();
            } else {
                // Gửi thông báo
                sendNotificationToServer(title, message);
            }
        });
    }

    private void sendNotificationToServer(String title, String message) {
        // Lấy OAuth 2.0 Access Token
        FirebaseAuthHelper.getAccessToken(accessToken -> {
            if (accessToken != null) {
                Log.d("FCM", "Access Token: " + accessToken);
                String authHeader = "Bearer " + accessToken;

                // Tạo yêu cầu thông báo cho chủ đề "all_devices"
                FirebaseNotificationRequest request = new FirebaseNotificationRequest("all_devices", title, message);

                NotificationApiService.apiService.sendNotification(authHeader, request).enqueue(new Callback<FirebaseResponse>() {
                    @Override
                    public void onResponse(Call<FirebaseResponse> call, Response<FirebaseResponse> response) {
                        if (response.isSuccessful()) {
                            Log.d("FCM", "Thông báo đã gửi thành công: " + response.body().getName());
                            saveNotificationToDatabase(title, message);
                        } else {
                            Log.e("FCM", "Gửi thông báo thất bại: " + response.message());
                            if (response.errorBody() != null) {
                                try {
                                    String errorBody = response.errorBody().string();
                                    Log.e("FCM", "Chi tiết lỗi: " + errorBody);
                                } catch (IOException e) {
                                    Log.e("FCM", "Không thể đọc lỗi từ response", e);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FirebaseResponse> call, Throwable t) {
                        Log.e("FCM", "Lỗi khi gửi thông báo: ", t);
                    }
                });
            } else {
                Log.e("FCM", "Không thể lấy access token");
            }
        });
    }

    private void saveNotificationToDatabase(String title, String message) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("notifications");

        String notificationId = database.push().getKey();
        if (notificationId != null) {
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("title", title);
            notificationData.put("message", message);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(new Date());

            notificationData.put("createdAt", currentTime);

            database.child(notificationId).setValue(notificationData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FCM", "Thông báo đã được lưu vào database");
                        Toast.makeText(this, "Đã gửi thông báo", Toast.LENGTH_SHORT).show(); // Chỉ hiển thị khi thành công
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FCM", "Lỗi khi lưu thông báo vào database", e);
                        Toast.makeText(this, "Có lỗi xảy ra khi gửi thông báo", Toast.LENGTH_SHORT).show(); // Thông báo lỗi khi thất bại
                    });
        }
    }



}