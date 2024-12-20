package com.example.meowapp.admin;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meowapp.Level.LevelManagementActivity;
import com.example.meowapp.R;
import com.example.meowapp.auth.LoginActivity;
import com.example.meowapp.language.LanguageManagementActivity;
import com.example.meowapp.lesson.LessonManagementActivity;
import com.example.meowapp.mission.MissionManagementActivity;
import com.example.meowapp.notification.NotificationCreateActivity;
import com.example.meowapp.notification.NotificationManagementActivity;
import com.example.meowapp.user.UserManagementActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<GridItem> gridItems;

    public GridAdapter(Context context, List<GridItem> gridItems) {
        this.context = context;
        this.gridItems = gridItems;
    }

    @Override
    public int getCount() {
        return gridItems.size();
    }

    @Override
    public Object getItem(int position) {
        return gridItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.admin_grid_item, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.icon);
        TextView text = convertView.findViewById(R.id.text);
        GridItem gridItem = gridItems.get(position);
        icon.setImageResource(gridItem.getIconResId());
        text.setText(gridItem.getText());
        convertView.setOnClickListener(v -> {
            if (gridItem.getText().equals("Tài khoản")) {
                Intent intent = new Intent(context, AdminProfileActivity.class);
                context.startActivity(intent);
            }else if (gridItem.getText().equals("Người dùng")) {
                Intent intent = new Intent(context, UserManagementActivity.class);
                context.startActivity(intent);
            }else if (gridItem.getText().equals("Ngôn ngữ")) {
                Intent intent = new Intent(context, LanguageManagementActivity.class);
                context.startActivity(intent);
            }else if (gridItem.getText().equals("Cấp độ")) {
                Intent intent = new Intent(context, LevelManagementActivity.class);
                context.startActivity(intent);
            }else if (gridItem.getText().equals("Bài học")) {
                Intent intent = new Intent(context, LessonManagementActivity.class);
                context.startActivity(intent);
            }else if (gridItem.getText().equals("Thông báo")) {
                Intent intent = new Intent(context, NotificationManagementActivity.class);
                context.startActivity(intent);
            }else if (gridItem.getText().equals("Nhiệm vụ")) {
                Intent intent = new Intent(context, MissionManagementActivity.class);
                context.startActivity(intent);
            }else if (gridItem.getText().equals("Đăng xuất")) {
                // Đăng xuất khỏi Firebase
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);

                // Ép kiểu context thành Activity để gọi finish()
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        });



        return convertView;
    }
}
