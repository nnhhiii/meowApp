package com.example.meowapp.auth;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.questionType.Start;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin, btnLogUp, btnForget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_login);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogUp = findViewById(R.id.btnLogup);
        btnForget = findViewById(R.id.btnForget);
        btnForget.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgetPasswordActivity.class);
            startActivity(intent);
        });
        btnLogUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, BlankActivity.class);
            startActivity(intent);
        });
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, Start.class);
            startActivity(intent);
        });
    }
}