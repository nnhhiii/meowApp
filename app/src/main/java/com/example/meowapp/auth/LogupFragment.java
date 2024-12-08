package com.example.meowapp.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.meowapp.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogupFragment extends Fragment {

    private Button btnLogUp;
    private TextInputEditText etUsername;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_auth_logup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof BlankActivity) {
            ((BlankActivity) getActivity()).updateProgressBar(65);
        }

        btnLogUp = view.findViewById(R.id.btnLogup);
        etUsername = view.findViewById(R.id.edtUsername);
        etEmail = view.findViewById(R.id.edtEmail);
        etPassword = view.findViewById(R.id.edtPassword);

        btnLogUp.setOnClickListener(v -> logUpOnClickHandle());
    }

    private boolean isValidInputs(String username, String email, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(requireContext(), "Không để trống thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Vui lòng kiểm tra lại email!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(requireContext(), "Mật khẩu không hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void logUpOnClickHandle() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!isValidInputs(username, email, password)) {
            return;
        }

        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("email", email);
        args.putString("password", password);

        transactionHandle(args);
    }

    private boolean isValidPassword(final String password) {
        // Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character
        final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void transactionHandle(Bundle args) {
        Fragment fragment = new SelectLanguageFragment();
        fragment.setArguments(args);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
        );
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}