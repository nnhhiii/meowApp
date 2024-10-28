package com.example.meowapp.Main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import java.util.Collections;
import java.util.Comparator;

import com.squareup.picasso.Picasso;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.meowapp.R;
import com.example.meowapp.model.Leaderboard;
import com.example.meowapp.model.User;
import com.example.meowapp.adapter.LeaderboardAdapter;
import com.example.meowapp.api.FirebaseApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankFragment extends Fragment {

    private ListView listView;
    private LeaderboardAdapter leaderboardAdapter;
    private List<Leaderboard> leaderboardList;
    private FirebaseApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_rank, container, false);
        listView = view.findViewById(R.id.viewRank);

        leaderboardList = new ArrayList<>();
        apiService = FirebaseApiService.apiService;

        // Tải dữ liệu bảng xếp hạng
        loadLeaderboardData(view);

        return view;
    }

    private void loadLeaderboardData(View view) {
        // Gọi API để lấy toàn bộ danh sách leaderboard
        apiService.getAllLeaderboard().enqueue(new Callback<Map<String, Leaderboard>>() {
            @Override
            public void onResponse(Call<Map<String, Leaderboard>> call, Response<Map<String, Leaderboard>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    leaderboardList.clear();
                    Log.d("RankFragment", "Leaderboard response body: " + response.body().toString());

                    for (Map.Entry<String, Leaderboard> entry : response.body().entrySet()) {
                        Leaderboard leaderboard = entry.getValue();
                        // Lưu user_id từ JSON vào trường userId trong mô hình
                        leaderboard.setUserId(entry.getValue().getUserId()); // Ánh xạ đúng userId từ JSON

                        Log.d("RankFragment", "Leaderboard entry: " + leaderboard.toString());
                        leaderboardList.add(leaderboard);
                    }

                    if (leaderboardList.isEmpty()) {
                        Log.d("RankFragment", "Leaderboard list is empty.");
                    } else {
                        Log.d("RankFragment", "Loaded leaderboard data size: " + leaderboardList.size());
                    }

                    loadAllUsers(view);
                } else {
                    Log.e("RankFragment", "Failed to get leaderboard: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Leaderboard>> call, Throwable t) {
                Log.e("RankFragment", "Failure: " + t.getMessage());
                Toast.makeText(getContext(), "Failed to load leaderboard data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadAllUsers(View view) {
        apiService.getAllUsers().enqueue(new Callback<Map<String, User>>() {
            @Override
            public void onResponse(Call<Map<String, User>> call, Response<Map<String, User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, User> usersMap = response.body();  // Lưu tất cả người dùng vào map với user_id làm key

                    // Duyệt qua danh sách leaderboard và ánh xạ user_id với tên người dùng
                    for (Leaderboard leaderboard : leaderboardList) {
                        String userId = leaderboard.getUserId();
                        Log.d("RankFragment", "Processing leaderboard with userId: " + userId); // Log userId
                        // Kiểm tra nếu user_id không null và có trong danh sách người dùng
                        if (userId != null && usersMap.containsKey(userId)) {
                            User user = usersMap.get(userId);
                            leaderboard.setUserName(user.getUsername());  // Đặt tên người dùng cho leaderboard
                            Log.d("RankFragment", "Found username: " + user.getUsername() + " for userId: " + userId); // Log tên người dùng
                        } else {
                            leaderboard.setUserName("Unknown"); // Nếu không tìm thấy tên người dùng, đặt là "Unknown"
                            Log.d("RankFragment", "No user found for userId: " + userId); // Log trường hợp không tìm thấy
                        }
                    }

                    // Sắp xếp leaderboardList theo điểm số từ cao xuống thấp
                    Collections.sort(leaderboardList, new Comparator<Leaderboard>() {
                        @Override
                        public int compare(Leaderboard l1, Leaderboard l2) {
                            return Integer.compare(l2.getTotalScore(), l1.getTotalScore()); // So sánh điểm số
                        }
                    });

                    // Sau khi load xong, cập nhật giao diện
                    setTopThreeUsers(view);
                    List<Leaderboard> remainingUsers = leaderboardList.subList(Math.min(3, leaderboardList.size()), leaderboardList.size());
                    leaderboardAdapter = new LeaderboardAdapter(getContext(), remainingUsers);
                    listView.setAdapter(leaderboardAdapter);
                    leaderboardAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Map<String, User>> call, Throwable t) {
                Log.e("RankFragment", "Failed to load users: " + t.getMessage());
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

    private void setTopUserView(View rootView, int layoutId, Leaderboard leaderboard) {
        View layout = rootView.findViewById(layoutId);

        TextView userName = layout.findViewById(layoutId == R.id.top1 ? R.id.tvUserName1 : (layoutId == R.id.top2 ? R.id.tvUserName2 : R.id.tvUserName3));
        TextView userScore = layout.findViewById(layoutId == R.id.top1 ? R.id.tvScore1 : (layoutId == R.id.top2 ? R.id.tvScore2 : R.id.tvScore3));
        ImageView userImage = layout.findViewById(layoutId == R.id.top1 ? R.id.imageView_user1 : (layoutId == R.id.top2 ? R.id.imageView_user2 : R.id.imageView_user3));

        userName.setText(leaderboard.getUserName());
        userScore.setText(leaderboard.getTotalScore() + " points");

        // Tải hình ảnh
        if (leaderboard.getUserImageUrl() != null) {
            Picasso.get().load(leaderboard.getUserImageUrl()).into(userImage);
        } else {
            userImage.setImageResource(R.drawable.ic_account); // Ảnh mặc định
        }
    }

}
