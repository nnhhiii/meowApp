package com.example.meowapp.admin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meowapp.Level.LevelManagementActivity;
import com.example.meowapp.R;
import com.example.meowapp.language.LanguageManagementActivity;
import com.example.meowapp.lesson.LessonManagementActivity;
import com.example.meowapp.user.EditUserActivity;
import com.example.meowapp.user.UserManagementActivity;

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
            if (gridItem.getText().equals("Người dùng")) {
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
            }
        });



        return convertView;
    }
}
