package com.example.meowapp.Main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meowapp.R;
import com.example.meowapp.model.Leaderboard;
import com.example.meowapp.model.User; // Import model User
import com.example.meowapp.Adapter.LeaderboardAdapter;
import com.example.meowapp.api.FirebaseApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankFragment extends Fragment {

    private RecyclerView recyclerView;
    private LeaderboardAdapter leaderboardAdapter;
    private List<Leaderboard> leaderboardList;
    private FirebaseApiService apiService; // API service for Firebase

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_rank, container, false);
        recyclerView = view.findViewById(R.id.viewRank);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        leaderboardList = new ArrayList<>();
        apiService = FirebaseApiService.apiService; // Initialize API service

        loadLeaderboardData(view); // Load leaderboard data

        return view;
    }

    private void loadLeaderboardData(View view) {
        apiService.getAllLeaderboard().enqueue(new Callback<Map<String, Leaderboard>>() {
            @Override
            public void onResponse(Call<Map<String, Leaderboard>> call, Response<Map<String, Leaderboard>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    leaderboardList.clear(); // Clear current leaderboard list
                    for (Map.Entry<String, Leaderboard> entry : response.body().entrySet()) {
                        leaderboardList.add(entry.getValue());
                    }
                    loadUserData(view); // Load user data
                } else {
                    Log.e("RankFragment", "Error: " + response.message());
                    Toast.makeText(getContext(), "Failed to load leaderboard data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Leaderboard>> call, Throwable t) {
                Log.e("RankFragment", "Failure: " + t.getMessage());
                Toast.makeText(getContext(), "Failed to load leaderboard data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData(View view) {
        apiService.getAllUsers().enqueue(new Callback<Map<String, User>>() {
            @Override
            public void onResponse(Call<Map<String, User>> call, Response<Map<String, User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RankFragment", "User data response: " + response.body().toString());

                    Map<Integer, String> userMap = new HashMap<>(); // Create a map for quick access
                    for (Map.Entry<String, User> entry : response.body().entrySet()) {
                        userMap.put(entry.getValue().getUserId(), entry.getValue().getUsername());
                    }

                    for (Leaderboard leaderboard : leaderboardList) {
                        String userName = userMap.get(leaderboard.getUserId());
                        leaderboard.setUserName(userName); // Set the user name
                        Log.d("RankFragment", "Setting UserName for User ID: " + leaderboard.getUserId() + " - Name: " + userName);
                    }

                    setTopThreeUsers(view); // Set top three users
                    List<Leaderboard> remainingUsers = leaderboardList.subList(Math.min(3, leaderboardList.size()), leaderboardList.size());
                    leaderboardAdapter = new LeaderboardAdapter(getContext(), remainingUsers);
                    recyclerView.setAdapter(leaderboardAdapter); // Set the adapter
                } else {
                    Log.e("RankFragment", "Error loading user data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, User>> call, Throwable t) {
                Log.e("RankFragment", "Failure: " + t.getMessage());
                Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTopThreeUsers(View rootView) {
        if (leaderboardList.size() >= 1) {
            setTopUserView(rootView, R.id.top1, leaderboardList.get(0));
        }
        if (leaderboardList.size() >= 2) {
            setTopUserView(rootView, R.id.top2, leaderboardList.get(1));
        }
        if (leaderboardList.size() >= 3) {
            setTopUserView(rootView, R.id.top3, leaderboardList.get(2));
        }
    }

    private void setTopUserView(View rootView, int layoutId, Leaderboard user) {
        View layout = rootView.findViewById(layoutId);
        ImageView userImage;
        TextView userName;
        TextView userScore;

        // Update IDs based on layoutId
        if (layoutId == R.id.top1) {
            userImage = layout.findViewById(R.id.imageView_user1);
            userName = layout.findViewById(R.id.tvUserName1);
            userScore = layout.findViewById(R.id.tvScore1);
        } else if (layoutId == R.id.top2) {
            userImage = layout.findViewById(R.id.imageView_user2);
            userName = layout.findViewById(R.id.tvUserName2);
            userScore = layout.findViewById(R.id.tvScore2);
        } else { // R.id.top3
            userImage = layout.findViewById(R.id.imageView_user3);
            userName = layout.findViewById(R.id.tvUserName3);
            userScore = layout.findViewById(R.id.tvScore3);
        }

        userName.setText(user.getUserName());
        userScore.setText(String.valueOf(user.getTotalScore()));
    }
}
