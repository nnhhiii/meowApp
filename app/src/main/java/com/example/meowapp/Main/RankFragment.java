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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_rank, container, false);
        listView = view.findViewById(R.id.viewRank);

        leaderboardList = new ArrayList<>();
        apiService = FirebaseApiService.apiService;
        firebaseAuth = FirebaseAuth.getInstance();

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

                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String userId = firebaseUser.getUid(); // Lấy ID từ Firebase
                        loadCurrentUserInfo(view, userId);
                    } else {
                        Toast.makeText(getContext(), "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("RankFragment", "Lỗi khi tải người dùng: " + response.message());
                    Toast.makeText(getContext(), "Lỗi khi tải dữ liệu bảng xếp hạng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, User>> call, Throwable t) {
                Log.e("RankFragment", "Lỗi khi tải người dùng: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu bảng xếp hạng", Toast.LENGTH_SHORT).show();
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
        userScore.setText(user.getScore() + " điểm");

        if (user.getAvatar() != null) {
            Picasso.get().load(user.getAvatar()).into(userImage);
        } else {
            userImage.setImageResource(R.drawable.ic_account);
        }
    }

    private void loadCurrentUserInfo(View rootView, String userId) {
        apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User currentUser = response.body();

                    TextView userName = rootView.findViewById(R.id.tvUserNamex);
                    TextView userScore = rootView.findViewById(R.id.tvScorex);
                    ImageView userImage = rootView.findViewById(R.id.imageView_userx);
                    TextView userRank = rootView.findViewById(R.id.tvrankx);

                    userName.setText(currentUser.getUsername());
                    userScore.setText(currentUser.getScore() + " điểm");

                    if (currentUser.getAvatar() != null) {
                        Picasso.get().load(currentUser.getAvatar()).into(userImage);
                    } else {
                        userImage.setImageResource(R.drawable.ic_account);
                    }

                    // Tìm hạng của người dùng hiện tại trong leaderboard
                    int rank = leaderboardList.indexOf(currentUser) + 1;
                    userRank.setText(String.valueOf(rank));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("RankFragment", "Lỗi khi tải thông tin người dùng: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi khi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
