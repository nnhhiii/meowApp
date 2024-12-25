package com.example.meowapp.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordActivity extends AppCompatActivity {

    // Khai báo và ánh xạ giao diện
    private TextInputEditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private Button btnChangePassword;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_changepassword);

        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(v -> finish());

        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPassword = edtCurrentPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Kiểm tra đầu vào
        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Không để trống thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới và xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra tính hợp lệ của mật khẩu mới
        if (!isValidPassword(newPassword)) {
            Toast.makeText(this, "Mật khẩu phải tối thiểu 8 ký tự, có chữ hoa, chữ thường, số, và ký tự đặc biệt!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null && user.getEmail() != null) {
            // Xác thực lại người dùng và đổi mật khẩu
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword); //Xác thực lại người dùng bằng EmailAuthProvider.getCredential(email, currentPassword) để đảm bảo rằng người dùng đã được xác thực trước khi đổi mật khẩu.
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.e("ChangePassword", "Error updating password: " + updateTask.getException().getMessage());
                            Toast.makeText(this, "Lỗi: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e("ChangePassword", "Re-authentication failed: " + task.getException().getMessage());
                    Toast.makeText(this, "Mật khẩu hiện tại không chính xác!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Kiểm tra xem mật khẩu mới có hợp lệ hay không bằng regex
    private boolean isValidPassword(String password) {
        String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(PASSWORD_PATTERN);
    }
}
