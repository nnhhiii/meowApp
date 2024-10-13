package com.example.meowapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meowapp.R;
import com.example.meowapp.model.Leaderboard;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private List<Leaderboard> leaderboardList;
    private Context context;

    public LeaderboardAdapter(Context context, List<Leaderboard> leaderboardList) {
        this.context = context;
        this.leaderboardList = leaderboardList;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rank, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        Leaderboard leaderboard = leaderboardList.get(position);
        holder.tvRank.setText(String.valueOf(leaderboard.getRank()));
        holder.tvScore.setText(String.valueOf(leaderboard.getTotalScore())); // Hiển thị điểm
        holder.tvUserName.setText(leaderboard.getUserName()); // Hiển thị tên người dùng
    }

    @Override
    public int getItemCount() {
        return leaderboardList.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvScore, tvUserName;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvrank);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvUserName = itemView.findViewById(R.id.tvUserName);
        }
    }
}
