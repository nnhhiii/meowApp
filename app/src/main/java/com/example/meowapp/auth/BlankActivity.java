package com.example.meowapp.auth;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;

public class BlankActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blank2);
        progressBar = findViewById(R.id.progressBar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new LogupFragment())
                    .commit();
        }
    }
    public void updateProgressBar(int progress) {
        // Tạo ObjectAnimator để thực hiện animation cho ProgressBar
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progress);
        animation.setDuration(500); // thời gian chạy animation (1000 milliseconds = 1 giây)
        animation.start(); // bắt đầu animation
    }
}