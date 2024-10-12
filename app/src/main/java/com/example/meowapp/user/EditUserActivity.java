package com.example.meowapp.user;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;

public class EditUserActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextEmail;
    private Button buttonSave;
    private ImageButton buttonCancel;

    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        buttonSave = findViewById(R.id.btnSave);
        buttonCancel = findViewById(R.id.btnCancel);

        editTextUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isChanged = true;
            }
        });

        editTextEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isChanged = true;
            }
        });

        buttonSave.setOnClickListener(v -> saveUser());
        buttonCancel.setOnClickListener(v -> showConfirmationDialog());
    }

    private void saveUser() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Chỉnh sửa thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showConfirmationDialog() {
        if (!isChanged) {
            finish();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn chắc chắn muốn thoát mà không lưu thay đổi?")
                .setPositiveButton("Có", (dialog, which) -> finish())
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        showConfirmationDialog();
        super.onBackPressed();
    }
}
