package com.example.meowapp.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;

public class AddUserActivity extends AppCompatActivity {

    private ImageButton btnCancel;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);

        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {

            Toast.makeText(this, "User added!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
