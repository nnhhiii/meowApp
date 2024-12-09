package com.example.meowapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.meowapp.Main.MainActivity;
import com.example.meowapp.R;
import com.example.meowapp.adapter.SelectLevelAdapter;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.LanguagePreference;
import com.example.meowapp.model.Level;
import com.example.meowapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectLevelFragment extends Fragment {

    private static final String TAG = SelectLevelFragment.class.getSimpleName();

    private ProgressBar progressBar;
    private RelativeLayout selectLevelForm;
    private Button btnSubmit;
    private ListView listView;
    private SelectLevelAdapter adapter;
    private Bundle args;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersReference;
    private DatabaseReference languagePreferenceReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_level, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof BlankActivity) {
            ((BlankActivity) getActivity()).updateProgressBar(100);
        }

        progressBar = view.findViewById(R.id.progressBar);
        selectLevelForm = view.findViewById(R.id.selectLevelForm);
        listView = view.findViewById(R.id.linearLayout);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        firebaseAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference("users");
        languagePreferenceReference = FirebaseDatabase.getInstance().getReference("language_preferences");

        getReceivingBundle();

        loadLevelSelections();
        listView.setOnItemClickListener((parent, view1, position, id) -> processingSelectedItem(position));
        btnSubmit.setOnClickListener(v -> processingSubmitOnClick());
    }

    private void getReceivingBundle() {
        args = getArguments();
        if (args == null) {
            Log.e(TAG, "Can't get arguments");
            Toast.makeText(
                    requireContext(),
                    "Lỗi hệ thống, vui lòng thử lại sau!",
                    Toast.LENGTH_SHORT
            ).show();
            getParentFragmentManager().popBackStack();
        }
    }

    private void processingSelectedItem(int position) {
        Map.Entry<String, Level> selectedEntry = adapter.getItem(position);
        if (!TextUtils.isEmpty(selectedEntry.getKey())) {
            args.putString("level_id", selectedEntry.getKey());
        } else {
            Log.e(TAG, "Can't get selected level id");
            Toast.makeText(
                    requireContext(),
                    "Lỗi hệ thống, vui lòng thử lại sau!",
                    Toast.LENGTH_SHORT
            ).show();
            btnSubmit.setEnabled(false);
        }
    }

    private void processingSubmitOnClick() {
        progressBar.setVisibility(View.VISIBLE);
        selectLevelForm.setVisibility(View.GONE);

        processingRegisterAccount(args);

        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void processingRegisterAccount(Bundle args) {
        String email = args.getString("email");
        String password = args.getString("password");
        String username = args.getString("username", "unknown");
        String language_id = args.getString("language_id");
        String level_id = args.getString("level_id");
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Create account with Firebase Authentication successfully!");
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            if (currentUser != null) {
                                processingUserData(currentUser.getUid(), username, language_id, level_id);
                            } else {
                                Log.e(TAG, "Can't obtain FirebaseUser object");
                                Toast.makeText(requireContext(), "Lỗi hệ thống đăng ký!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthException) {
                                String errorCode = ((FirebaseAuthException) exception).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        Toast.makeText(requireContext(), "Email đã được đăng ký!", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "ERROR_INVALID_EMAIL":
                                        Toast.makeText(requireContext(), "Email không đúng định dạng!", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "ERROR_WEAK_PASSWORD":
                                        Toast.makeText(requireContext(), "Mật khẩu quá yếu!", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Log.e(TAG, "FireBase Error Code: " + errorCode);
                                        break;
                                }
                            } else {
                                Log.e(TAG, "Create account with Firebase Authentication fail!", exception);
                            }
                        }
                    });
        } else {
            Log.e(TAG, "Can't get email and password from bundle");
            Toast.makeText(requireContext(), "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
        }
    }

    private void processingUserData(String uid, String username, String language_id, String level_id) {
        User user = new User();
        user.setEmail(firebaseAuth.getCurrentUser().getEmail());
        user.setUsername(username);
        user.setRole("user");
        user.setLanguage_id(language_id);
        user.setCreated_at("");
        user.setUpdated_at("");

        usersReference.child(uid).setValue(user).addOnCompleteListener(saveUserDataTask -> {
            if (saveUserDataTask.isSuccessful()) {
                Log.i(TAG, "Create user data successfully!");
                LanguagePreference preference = new LanguagePreference(language_id, uid, level_id, 0);
                languagePreferenceReference.child("test").setValue(preference)
                        .addOnCompleteListener(saveLanguageReferenceTask -> {
                            if (saveLanguageReferenceTask.isSuccessful()) {
                                Log.i(TAG, "Save language reference successfully!");
                                Toast.makeText(requireContext(), "Đăng kí tài khoản thành công!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "Save language reference fail!", saveLanguageReferenceTask.getException());
                                Toast.makeText(requireContext(), "Đăng kí tài khoản thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Log.e(TAG, "Create user data fail!", saveUserDataTask.getException());
                Toast.makeText(requireContext(), "Đăng kí tài khoản thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLevelSelections() {
        String languageId = "1";
        FirebaseApiService.apiService.getAllLevelByLanguageId("\"language_id\"", "\"" + languageId + "\"")
                .enqueue(new Callback<Map<String, Level>>() {
                    @Override
                    public void onResponse(Call<Map<String, Level>> call, Response<Map<String, Level>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Map<String, Level> levelMap = response.body();
                            List<Map.Entry<String, Level>> entryList = new ArrayList<>(levelMap.entrySet());
                            adapter = new SelectLevelAdapter(requireContext(), entryList);
                            listView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), "Failed to get info", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Level>> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("error:", t.getMessage(), t);
                    }
                });
    }
}