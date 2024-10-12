package com.example.meowapp.user;

import static com.example.meowapp.user.UserManagementActivity.REQUEST_EDIT_USER;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.meowapp.R;
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
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvName = convertView.findViewById(R.id.staff_name);
            viewHolder.tvEmail = convertView.findViewById(R.id.staff_email);
            viewHolder.btnDelete = convertView.findViewById(R.id.btnDelete);
            viewHolder.btnEditUser = convertView.findViewById(R.id.btn_edit_user);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User user = userList.get(position);
        viewHolder.tvName.setText(user.getName());
        viewHolder.tvEmail.setText(user.getEmail());

        viewHolder.btnDelete.setOnClickListener(v -> {
            deleteUser(user);
        });
        viewHolder.btnEditUser.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditUserActivity.class);
            intent.putExtra("user", user);
            ((Activity) context).startActivityForResult(intent, REQUEST_EDIT_USER);
        });

        return convertView;
    }

    private void deleteUser(User user) {
        userList.remove(user);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView tvName;
        TextView tvEmail;
        ImageButton btnDelete;
        ImageButton btnEditUser;
    }
}