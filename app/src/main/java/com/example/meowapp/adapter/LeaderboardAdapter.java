package com.example.meowapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meowapp.R;
import com.example.meowapp.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LeaderboardAdapter extends BaseAdapter {

    private Context context;
    private List<User> leaderboardList;
    private int startingRank;

    public LeaderboardAdapter(Context context, List<User> leaderboardList, int startingRank) {
        this.context = context;
        this.leaderboardList = leaderboardList;
        this.startingRank = startingRank;
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

        User user = leaderboardList.get(position);

        rankView.setText(String.valueOf(startingRank + position)); // Rank starts from the value of startingRank
        userNameView.setText(user.getUsername() != null ? user.getUsername() : "Unknown");
        scoreView.setText(user.getScore() + " points");

        if (user.getAvatar() != null) {
            Picasso.get().load(user.getAvatar()).into(userImageView);
        } else {
            userImageView.setImageResource(R.drawable.ic_account);
        }

        return convertView;
    }
}
