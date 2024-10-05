package com.example.meowapp.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meowapp.R;
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

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.admin_grid_item, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.icon);
        TextView text = convertView.findViewById(R.id.text);
        ImageButton btnEdit = convertView.findViewById(R.id.btn_edit_user);

        GridItem gridItem = gridItems.get(position);
        icon.setImageResource(gridItem.getIconResId());
        text.setText(gridItem.getText());

        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        text.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        text.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });
        if (gridItem.getText().equals("Người dùng")) {
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserManagementActivity.class);
                context.startActivity(intent);
            });
        }
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditUserActivity.class);
            // Thêm các thông tin cần thiết để chuyển sang Activity sửa user nếu cần
            context.startActivity(intent);
        });

        return convertView;
    }


}
