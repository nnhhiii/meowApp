package com.example.meowapp.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private String language_id;

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


        btnSubmit.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            String selectedLevelId = sharedPreferences.getString("selected_level_id", null);

            if (selectedLevelId != null) {
                args.putString("level_id", selectedLevelId);
                processingSubmitOnClick();
            } else {
                Toast.makeText(requireContext(), "Vui lòng chọn cấp độ!", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void loadLevelSelections() {
        language_id = args.getString("language_id");
        FirebaseApiService.apiService.getAllLevelByLanguageId("\"language_id\"", "\"" + language_id + "\"")
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


    private void navigationToMainActivity() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
    }

    private void processingSubmitOnClick() {
        progressBar.setVisibility(View.VISIBLE);
        selectLevelForm.setVisibility(View.GONE);

        processingRegisterAccount(args, this::navigationToMainActivity);
    }

    private void processingRegisterAccount(Bundle args, Runnable onSuccessCallback) {
        String email = args.getString("email");
        String password = args.getString("password");
        String username = args.getString("username", "unknown");
        String language_id = args.getString("language_id");
        String level_id = args.getString("level_id");

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Log.e(TAG, "Can't get email or password from bundle");
            Toast.makeText(requireContext(), "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Create account with Firebase Authentication successfully!");
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        if (currentUser == null) {
                            Log.e(TAG, "Can't obtain FirebaseUser object");
                            Toast.makeText(requireContext(), "Lỗi hệ thống đăng ký!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        processingUserData(currentUser.getUid(), username, language_id, level_id, onSuccessCallback);
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthException) {
                            String errorCode = ((FirebaseAuthException) exception).getErrorCode();
                            mappingErrorCode(errorCode);
                        } else {
                            Log.e(TAG, "Create account with Firebase Authentication fail!", exception);
                        }
                    }
                });
    }

    private void mappingErrorCode(String errorCode) {
        switch (errorCode) {
            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(requireContext(), "Email đã được đăng ký!", Toast.LENGTH_SHORT).show();
                return;
            case "ERROR_INVALID_EMAIL":
                Toast.makeText(requireContext(), "Email không đúng định dạng!", Toast.LENGTH_SHORT).show();
                return;
            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(requireContext(), "Mật khẩu quá yếu!", Toast.LENGTH_SHORT).show();
                return;
            default:
                Log.e(TAG, "FireBase Error Code: " + errorCode);
        }
    }

    private void processingUserData(String uid, String username, String language_id, String level_id, Runnable onSuccessCallback) {
        String defaultAvatar = "https://firebasestorage.googleapis.com/v0/b/appngonngu.appspot.com/o/default_user.jpg?alt=media&token=9861418d-58db-42c0-837e-8f2fe758f981";
        User user = new User();
        user.setEmail(firebaseAuth.getCurrentUser().getEmail());
        user.setUsername(username);
        user.setAvatar(defaultAvatar);
        user.setRole("user");

        // Lấy thời gian hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        user.setCreated_at(currentTime);
        user.setUpdated_at(currentTime);

        usersReference.child(uid).setValue(user).addOnCompleteListener(saveUserDataTask -> {
            if (saveUserDataTask.isSuccessful()) {
                Log.i(TAG, "Create user data successfully!");
                LanguagePreference preference = new LanguagePreference(language_id, uid, level_id, 0);
                String uniqueKey = languagePreferenceReference.push().getKey();
                languagePreferenceReference.child(uniqueKey).setValue(preference).addOnCompleteListener(saveLanguageReferenceTask -> {
                            if (saveLanguageReferenceTask.isSuccessful()) {
                                Log.i(TAG, "Save language reference successfully!");
                                Toast.makeText(requireContext(), "Đăng kí tài khoản thành công!", Toast.LENGTH_SHORT).show();
                                if (onSuccessCallback != null) {
                                    onSuccessCallback.run();
                                } else {
                                    Log.e(TAG, "Not Found onSuccessCallback");
                                    Toast.makeText(requireContext(), "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStackImmediate();
                                }
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

}
