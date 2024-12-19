package com.example.meowapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.User;
import com.example.meowapp.questionType.BlankActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class TimerService extends Service {
    private CountDownTimer countDownTimer;
    private int hearts;
    private String userId;
    private int duration;
    private long remainingTime; // Lưu trữ thời gian còn lại
    private Handler handler = new Handler(); // Handler để kiểm tra định kỳ

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            duration = intent.getIntExtra("duration", 0);
        } else {
            Log.e("TimerService", "Intent is null in onStartCommand");
            duration = 0;
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        getUserHeart();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getUserHeart() {
        FirebaseApiService.apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    hearts = user.getHearts();
                    Log.d("TimerService", "Hearts: " + hearts);

                    // Kiểm tra xem có cần bắt đầu bộ đếm ngược hay không
                    if (hearts < 5) {
                        if (countDownTimer == null) {  // Đảm bảo bộ đếm ngược không bị khởi động lại khi đang chạy
                            startCountDownTimer();
                        }
                    } else {
                        resetCountDownTimer();
                    }

                    // Tiếp tục kiểm tra định kỳ mỗi 5 giây
                    handler.removeCallbacksAndMessages(null); // Hủy các tác vụ trước đó
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("TimerService", "Checking hearts...");
                            getUserHeart();  // Tiếp tục kiểm tra tim
                            handler.postDelayed(this, 10000);  // Lặp lại kiểm tra mỗi 10 giây
                        }
                    }, 10000); // Bắt đầu kiểm tra sau 5 giây

                } else {
                    Toast.makeText(TimerService.this, "Không lấy được tim", Toast.LENGTH_SHORT).show();
                    Log.e("TimerService", "Failed to get user heart, response unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(TimerService.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TimerService", "API call failed: " + t.getMessage());
            }
        });
    }

    private void startCountDownTimer() {
        long startTime = (remainingTime > 0) ? remainingTime : duration;

        countDownTimer = new CountDownTimer(startTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished; // Cập nhật thời gian còn lại
                sendCountdownUpdateBroadcast(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                // Thông báo khi hoàn thành
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

    private void resetCountDownTimer() {
        // Dừng bộ đếm ngược nếu có
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;  // Đảm bảo không còn tham chiếu đến bộ đếm ngược cũ
        }
        // Reset lại thời gian còn lại về 0
        remainingTime = 0;
        sendCountdownUpdateBroadcast(remainingTime);
        Log.d("TimerService", "Timer reset to 0 because hearts >= 5");
    }



    private void updateUserHeart() {
        Map<String, Object> field = new HashMap<>();
        field.put("hearts", hearts);
        FirebaseApiService.apiService.updateUserField(userId, field).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sendHeartUpdateBroadcast(); // Gửi broadcast
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

    private void sendHeartUpdateBroadcast() {
        Intent intent = new Intent("com.example.UPDATE_HEART");
        intent.putExtra("heartCount", hearts);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d("TimerService", "Đã gửi broadcast heart");
    }

    private void sendCountdownUpdateBroadcast(long millisUntilFinished) {
        Intent intent = new Intent("com.example.UPDATE_COUNTDOWN");
        intent.putExtra("countdown", millisUntilFinished);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d("TimerService", "Đã gửi broadcast time");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        handler.removeCallbacksAndMessages(null); // Hủy các công việc trong Handler khi service bị huỷ
    }
}
