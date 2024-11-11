package com.example.meowapp.questionType;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meowapp.Main.MainActivity;
import com.example.meowapp.R;

public class StreakActivity extends AppCompatActivity {
    private TextView tvStreak;
    private int streak;
    private Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question_type_streak);

        tvStreak = findViewById(R.id.tvStreak);
        btnNext = findViewById(R.id.btnNext);
        streak = getIntent().getIntExtra("STREAK",0);
        tvStreak.setText(String.valueOf(streak));

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(StreakActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}