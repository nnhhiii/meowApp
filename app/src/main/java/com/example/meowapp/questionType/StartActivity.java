package com.example.meowapp.questionType;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;

public class StartActivity extends AppCompatActivity {
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_type_start);
        btn = findViewById(R.id.btnStart);
        String lessonId = getIntent().getStringExtra("LESSON_ID");
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(this, BlankActivity.class);
            intent.putExtra("LESSON_ID", lessonId);
            startActivity(intent);
            finish();
        });

    }
}