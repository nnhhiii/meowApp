package com.example.meowapp.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.meowapp.R;
import com.example.meowapp.model.Question;
import com.example.meowapp.model.User;

import java.util.List;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        }


        User user = userList.get(position);

        TextView tvName = convertView.findViewById(R.id.staff_name);
        TextView tvEmail = convertView.findViewById(R.id.staff_email);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());


        btnDelete.setOnClickListener(v -> {
            deleteUser(user);
        });

        return convertView;
    }

    private void deleteUser(User user) {
        userList.remove(user);
        notifyDataSetChanged();
    }
}
