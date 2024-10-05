package com.example.meowapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meowapp.R;

public class EditUserActivity extends AppCompatActivity {

    private EditText edtName, edtUsername, edtEmail, edtPassword;
    private Button btnSave;
    private ImageButton btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String username = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");

        if (name != null) {
            edtName.setText(name);
        }
        if (username != null) {
            edtUsername.setText(username);
        }
        if (email != null) {
            edtEmail.setText(email);
        }

        btnCancel.setOnClickListener(v -> {
            finish();
        });

        btnSave.setOnClickListener(v -> {
            String newName = edtName.getText().toString();
            String newUsername = edtUsername.getText().toString();
            String newEmail = edtEmail.getText().toString();
            String newPassword = edtPassword.getText().toString();

            if (newName.isEmpty() || newUsername.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(EditUserActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(EditUserActivity.this, "Đã lưu thông tin người dùng!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
