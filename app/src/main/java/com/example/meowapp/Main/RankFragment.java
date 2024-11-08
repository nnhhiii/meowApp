package com.example.meowapp.Main;

import android.os.Bundle;
import android.util.Log;
import com.squareup.picasso.Picasso;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.meowapp.R;
import com.example.meowapp.model.User;
import com.example.meowapp.adapter.LeaderboardAdapter;
import com.example.meowapp.api.FirebaseApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankFragment extends Fragment {

    private ListView listView;
    private LeaderboardAdapter leaderboardAdapter;
    private List<User> leaderboardList;
    private FirebaseApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_rank, container, false);
        listView = view.findViewById(R.id.viewRank);

        leaderboardList = new ArrayList<>();
        apiService = FirebaseApiService.apiService;

        loadLeaderboardData(view);
        return view;
    }

    private void loadLeaderboardData(View view) {
        apiService.getAllUsers().enqueue(new Callback<Map<String, User>>() {
            @Override
            public void onResponse(Call<Map<String, User>> call, Response<Map<String, User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    leaderboardList.clear();

                    for (Map.Entry<String, User> entry : response.body().entrySet()) {
                        User user = entry.getValue();
                        leaderboardList.add(user);
                    }

                    Collections.sort(leaderboardList, new Comparator<User>() {
                        @Override
                        public int compare(User u1, User u2) {
                            return Integer.compare(u2.getScore(), u1.getScore());
                        }
                    });

                    setTopThreeUsers(view);
                    List<User> remainingUsers = leaderboardList.subList(Math.min(3, leaderboardList.size()), leaderboardList.size());
                    leaderboardAdapter = new LeaderboardAdapter(getContext(), remainingUsers, 4);
                    listView.setAdapter(leaderboardAdapter);
                    leaderboardAdapter.notifyDataSetChanged();
                } else {
                    Log.e("RankFragment", "Failed to load users: " + response.message());
                    Toast.makeText(getContext(), "Failed to load leaderboard data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, User>> call, Throwable t) {
                Log.e("RankFragment", "Failed to load users: " + t.getMessage());
                Toast.makeText(getContext(), "Failed to load leaderboard data", Toast.LENGTH_SHORT).show();
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

    private void setTopUserView(View rootView, int layoutId, User user) {
        View layout = rootView.findViewById(layoutId);

        TextView userName = layout.findViewById(layoutId == R.id.top1 ? R.id.tvUserName1 :
                (layoutId == R.id.top2 ? R.id.tvUserName2 : R.id.tvUserName3));
        TextView userScore = layout.findViewById(layoutId == R.id.top1 ? R.id.tvScore1 :
                (layoutId == R.id.top2 ? R.id.tvScore2 : R.id.tvScore3));
        ImageView userImage = layout.findViewById(layoutId == R.id.top1 ? R.id.imageView_user1 :
                (layoutId == R.id.top2 ? R.id.imageView_user2 : R.id.imageView_user3));

        userName.setText(user.getUsername());
        userScore.setText(user.getScore() + " points");

        if (user.getAvatar() != null) {
            Picasso.get().load(user.getAvatar()).into(userImage);
        } else {
            userImage.setImageResource(R.drawable.ic_account);
        }
    }
}
