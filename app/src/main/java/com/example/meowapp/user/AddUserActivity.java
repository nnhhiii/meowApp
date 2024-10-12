package com.example.meowapp.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;

public class AddUserActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextEmail;
    private Button buttonAdd;
    private ImageButton buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);

        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        buttonAdd = findViewById(R.id.btnSave);
        buttonCancel = findViewById(R.id.btnCancel);

        buttonAdd.setOnClickListener(v -> addUser());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void addUser() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Thêm người dùng thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
