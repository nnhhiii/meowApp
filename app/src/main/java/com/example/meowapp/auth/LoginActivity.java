package com.example.meowapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.Main.MainActivity;
import com.example.meowapp.R;
import com.example.meowapp.admin.AdminActivity;
import com.example.meowapp.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private RelativeLayout loginForm;
    private Button btnLogin, btnLogUp, btnForget;
    private TextInputEditText txtEmail;
    private TextInputEditText txtPassword;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_login);

        progressBar = findViewById(R.id.progressBar);
        loginForm = findViewById(R.id.loginForm);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogUp = findViewById(R.id.btnLogup);
        btnForget = findViewById(R.id.btnForget);
        txtEmail = findViewById(R.id.edtEmail);
        txtPassword = findViewById(R.id.edtPassword);
        firebaseAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference("users");

        if (firebaseAuth.getCurrentUser() != null) {
            switchProgressBar(true);
            navigateToMainActivity();
        }

        btnForget.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgetPasswordActivity.class);
            startActivity(intent);
        });

        btnLogUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, BlankActivity.class);
            startActivity(intent);
            finish();
        });

        btnLogin.setOnClickListener(v -> processingLoginOnClick());
    }

    private void switchProgressBar(boolean visibility) {
        if (visibility) {
            progressBar.setVisibility(View.VISIBLE);
            loginForm.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginForm.setVisibility(View.VISIBLE);
        }
    }

    private boolean isValidInputs(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không phù hợp!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void processingLoginOnClick() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (!isValidInputs(email, password)) {
            return;
        }

        processingLoginWithFireBase(email, password);
    }

    private void processingLoginWithFireBase(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            } else {
                Exception exception = task.getException();
                if (exception instanceof FirebaseAuthException) {
                    String errorCode = ((FirebaseAuthException) exception).getErrorCode();
                    switch (errorCode) {
                        case "ERROR_USER_NOT_FOUND":
                            Toast.makeText(this, "Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
                            break;
                        case "ERROR_INVALID_CREDENTIAL":
                            Toast.makeText(this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Log.e(TAG, "Firebase Error Code: " + errorCode);
                    }
                } else {
                    Log.e(TAG, "Can't log in with Firebase Authentication", exception);
                }
            }
        });
    }

    private void navigateToMainActivity() {
        String currentUID = firebaseAuth.getCurrentUser().getUid();
        if (TextUtils.isEmpty(currentUID)) {
            switchProgressBar(false);
            Log.e(TAG, "Can't retrieve UID of current user");
            Toast.makeText(this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
            return;
        }

        usersReference.child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User currentUser = snapshot.getValue(User.class);
                    if (currentUser != null) {
                        String role = currentUser.getRole();

                        Intent intent = new Intent(LoginActivity.this, getDestinationClass(role));
                        intent.setAction(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                        finish();
                    } else {
                        switchProgressBar(false);
                        Log.e(TAG, "Fail to parse user data");
                        Toast.makeText(LoginActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    switchProgressBar(false);
                    Log.e(TAG, "User doesn't exists, can't find snapshot with UID");
                    Toast.makeText(LoginActivity.this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
                }
            }

            private Class<?> getDestinationClass(String role) {
                return role.equals("admin") ? AdminActivity.class : MainActivity.class;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                switchProgressBar(false);
                Log.d(TAG, "Firebase Error: ", error.toException());
                Toast.makeText(LoginActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}