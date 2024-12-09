package com.example.meowapp.Main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.meowapp.R;
import com.example.meowapp.auth.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class UserFragment extends Fragment {

    private static final String TAG = UserFragment.class.getSimpleName();

    private MaterialButton btnLogout;
    private FirebaseAuth firebaseAuth;

    public UserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLogout = view.findViewById(R.id.btnLogout);

        firebaseAuth = FirebaseAuth.getInstance();

        btnLogout.setOnClickListener(v -> processingLogoutOnClick());
    }

    private void processingLogoutOnClick() {
        firebaseAuth.signOut();
        try {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setAction(Intent.ACTION_MAIN);
            startActivity(intent);
            requireActivity().finish();
        } catch (IllegalStateException exception) {
            Log.e(TAG, "Get error while trying to navigate to login", exception);
        }
    }
}