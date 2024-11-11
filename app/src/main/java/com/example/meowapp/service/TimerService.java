package com.example.meowapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.User;
import com.example.meowapp.questionType.BlankActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimerService extends Service {
    private CountDownTimer countDownTimer;
    private int hearts = 2;
    private String userId = "2";
    private int duration;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        duration = intent.getIntExtra("duration", 0);
        startCountDownTimer();

        return START_STICKY;
    }

    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Cập nhật UI hoặc gửi broadcast
            }

            @Override
            public void onFinish() {
                // Thông báo đã hoàn thành
                if (hearts < 5) {
                    hearts++;
                    updateUserHeart();
                }
                // Khởi động lại bộ đếm ngược để chạy liên tục
                startCountDownTimer();
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateUserHeart(){
        Map<String, Object> field = new HashMap<>();
        field.put("hearts", hearts);
        FirebaseApiService.apiService.updateUserField(userId, field).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Xử lý khi cập nhật thành công
                } else {
                    Toast.makeText(TimerService.this, "Không lấy được tim", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(TimerService.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
