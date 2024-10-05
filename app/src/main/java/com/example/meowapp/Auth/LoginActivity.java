package com.example.meowapp.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.meowapp.MainActivity;
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