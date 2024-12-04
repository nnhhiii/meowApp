package com.example.meowapp.Main;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.meowapp.R;
import com.example.meowapp.adapter.RewardAdapter;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.LanguagePreference;
import com.example.meowapp.model.Mission;
import com.example.meowapp.model.User;
import com.example.meowapp.model.UserProgress;
import com.example.meowapp.questionType.FinishActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardFragment extends Fragment {
    private int score, lessons, perfectLessons, eightyLessons, streaks, hearts, diamonds;
    private RewardAdapter rewardAdapter;
    private ListView listView;
    private String userId;
    private List<String> userMissionIdList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_award, container, false);
        listView = view.findViewById(R.id.listVieww);

        fetchUserProgress()
;        return view;
    }
    private void fetchUserProgress() {
        FirebaseApiService.apiService.getAllUserProgressByUserId("\"user_id\"", "\"" + userId + "\"").enqueue(new Callback<Map<String, UserProgress>>() {
            @Override
            public void onResponse(Call<Map<String, UserProgress>> call, Response<Map<String, UserProgress>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Response", "Data received: " + response.body().toString()); // In toàn bộ dữ liệu nhận được
                    for (Map.Entry<String, UserProgress> entry : response.body().entrySet()) {
                        String missionId = entry.getValue().getMission_id();
                        if (missionId != null && !missionId.isEmpty()) {
                            userMissionIdList.add(missionId);
                        } else {
                            Log.d("RewardFragment", "Mission ID is missing or empty for entry: " + entry.getKey());
                        }
                    }
                    fetchUserById();
                } else {
                    Log.d("RewardFragment", "Failed to fetch user progress: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, UserProgress>> call, Throwable throwable) {

            }
        });
    }
    private void fetchUserById() {
        FirebaseApiService.apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    score = user.getScore();
                    streaks = user.getStreaks();
                    lessons = user.getLessons();
                    hearts = user.getHearts();
                    diamonds = user.getDiamonds();
                    eightyLessons = user.getEightyLessons();
                    perfectLessons = user.getPerfectLessons();
                    loadMission();
                } else {
                    Toast.makeText(getContext(), "No user available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching languages", t);
            }
        });
    }


    private void loadMission() {
        FirebaseApiService.apiService.getAllMission().enqueue(new Callback<Map<String, Mission>>() {
            @Override
            public void onResponse(Call<Map<String, Mission>> call, Response<Map<String, Mission>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pair<String, Mission>> missions = new ArrayList<>();
                    for (Map.Entry<String, Mission> entry : response.body().entrySet()) {
                        Pair<String, Mission> missionPair = new Pair<>(entry.getKey(), entry.getValue());
                        missions.add(missionPair);
                    }
                    rewardAdapter = new RewardAdapter(
                            getContext(),
                            missions,
                            userId,
                            score,
                            lessons,
                            perfectLessons,
                            eightyLessons,
                            streaks,
                            hearts,
                            diamonds,
                            userMissionIdList
                    );
                    listView.setAdapter(rewardAdapter);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Mission>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to get info: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}