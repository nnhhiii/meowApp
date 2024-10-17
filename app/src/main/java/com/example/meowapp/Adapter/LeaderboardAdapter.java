package com.example.meowapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meowapp.R;
import com.example.meowapp.model.Leaderboard;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LeaderboardAdapter extends BaseAdapter {

    private Context context;
    private List<Leaderboard> leaderboardList;

    public LeaderboardAdapter(Context context, List<Leaderboard> leaderboardList) {
        this.context = context;
        this.leaderboardList = leaderboardList;
    }

    @Override
    public int getCount() {
        return leaderboardList.size();
    }

    @Override
    public Object getItem(int position) {
        return leaderboardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_rank, parent, false);
        }

        TextView rankView = convertView.findViewById(R.id.tvrank);
        ImageView userImageView = convertView.findViewById(R.id.imageView_user);
        TextView userNameView = convertView.findViewById(R.id.tvUserName);
        TextView scoreView = convertView.findViewById(R.id.tvScore);

        Leaderboard leaderboard = leaderboardList.get(position);

        // Cập nhật dữ liệu
        rankView.setText(String.valueOf(position + 1)); // Cập nhật thứ hạng
        userNameView.setText(leaderboard.getUserName() != null ? leaderboard.getUserName() : "Unknown"); // Cập nhật tên người dùng
        scoreView.setText(leaderboard.getTotalScore() + " points"); // Cập nhật điểm số

        // Cập nhật ảnh của người dùng (sử dụng URL từ API nếu có, nếu không hiển thị ảnh mặc định)
        if (leaderboard.getUserImageUrl() != null) {
            Picasso.get().load(leaderboard.getUserImageUrl()).into(userImageView); // Tải ảnh bằng Picasso
        } else {
            userImageView.setImageResource(R.drawable.ic_account); // Ảnh mặc định nếu không có URL
        }

        return convertView;
    }
}
