package com.example.meowapp.auth;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.meowapp.R;
import com.example.meowapp.adapter.SelectLanguageAdapter;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectLanguageFragment extends Fragment {

    private static final String TAG = SelectLanguageFragment.class.getSimpleName();
    private int selectedPosition = -1;
    private Bundle args;
    private Button btnNext;
    private ListView listView;
    private SelectLanguageAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_language, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof BlankActivity) {
            ((BlankActivity) getActivity()).updateProgressBar(90);
        }

        btnNext = view.findViewById(R.id.btnNext);
        listView = view.findViewById(R.id.listView);

        args = getArguments();
        if (args == null) {
            Log.e(TAG, "Can't get arguments");
            Toast.makeText(
                    requireContext(),
                    "Lỗi hệ thống, vui lòng thử lại sau!",
                    Toast.LENGTH_SHORT
            ).show();
            getParentFragmentManager().popBackStack();
            return;
        }
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xóa hết dữ liệu
        editor.apply();

        loadLanguageSelections();
        btnNext.setOnClickListener(v -> {
            String selectedLanguageId = sharedPreferences.getString("selected_language_id", null);

            if (selectedLanguageId != null) {
                args.putString("language_id", selectedLanguageId);
                processingNextOnClick();
            } else {
                Toast.makeText(requireContext(), "Vui lòng chọn ngôn ngữ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLanguageSelections() {
        FirebaseApiService.apiService.getAllLanguage().enqueue(new Callback<Map<String, Language>>() {
            @Override
            public void onResponse(Call<Map<String, Language>> call, Response<Map<String, Language>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Language> languageMap = response.body();
                    List<Map.Entry<String, Language>> entryList = new ArrayList<>(languageMap.entrySet());
                    adapter = new SelectLanguageAdapter(requireContext(), entryList);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy dữ liệu ngôn ngữ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Language>> call, Throwable t) {
                Log.e(TAG, "Can't get data from server!", t);
            }
        });
    }

    private void processingNextOnClick() {
        Fragment fragment = new SelectLevelFragment();
        fragment.setArguments(args);

        try {
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
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}