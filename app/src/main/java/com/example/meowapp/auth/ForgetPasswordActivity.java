package com.example.meowapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {
    private Button btnResetPassword;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_forgotpassword);

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);

        btnResetPassword = findViewById(R.id.btnSendEmail);
        emailEditText = findViewById(R.id.etEmail);

        btnResetPassword.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ email", Toast.LENGTH_SHORT).show();
                return;
            }

            checkEmailExistence(email);
        });
    }

    // Kiểm tra xem email có tồn tại không
    private void checkEmailExistence(String email) {
        FirebaseApiService.apiService.getUserByEmail("\"email\"", "\"" + email + "\"").enqueue(new Callback<Map<String, User>>() {
            @Override
            public void onResponse(Call<Map<String, User>> call, Response<Map<String, User>> response) {
                if (response.isSuccessful()) {
                    Map<String, User> users = response.body();
                    if (users != null && !users.isEmpty()) {
                        sendPasswordResetEmail(email);
                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, User>> call, Throwable t) {
                Log.e("LogUpFragment", "Error", t);
            }
        });
    }

    public void sendPasswordResetEmail(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email) // Yêu cầu Firebase gửi email đặt lại mật khẩu.
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        finish();
                        Toast.makeText(this, "Email đặt lại mật khẩu đã được gửi!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}