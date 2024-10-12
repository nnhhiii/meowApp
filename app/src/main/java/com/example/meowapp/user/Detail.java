package com.example.meowapp.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;

public class Detail extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_detail);

            int userId = getIntent().getIntExtra("userId", -1);
        }
}