package com.example.meowapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meowapp.Main.SettingsNotificationActivity;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends BaseAdapter {
    private Context context;
    private List<Pair<String, Notification>> list;

    public NotificationAdapter(Context context, List<Pair<String, Notification>> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position).second;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public static class ViewHolder {
        private TextView title, body, createdAt;
        private ImageButton btnDelete;

        public ViewHolder(View view) {
            title = view.findViewById(R.id.title);
            body = view.findViewById(R.id.body);
            createdAt = view.findViewById(R.id.createdAt);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_notification, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Pair<String, Notification> pair = list.get(position);
        String notificationId = pair.first;
        Notification notification = pair.second;
        
        holder.title.setText(notification.getTitle());
        holder.body.setText(notification.getMessage());
        holder.createdAt.setText(notification.getCreatedAt());

        if (context instanceof SettingsNotificationActivity) {
            holder.btnDelete.setVisibility(View.GONE);
            holder.createdAt.setVisibility(View.GONE);
        } else {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.createdAt.setVisibility(View.VISIBLE);
        }
        
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa thông báo")
                    .setMessage("Bạn có chắc chắn muốn xóa thông báo này không?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteNotification(notificationId, position))
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        return convertView;
    }

    private void deleteNotification(String NotificationId, int position) {
        FirebaseApiService.apiService.deleteNotification(NotificationId).enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                if (response.isSuccessful()) {
                    list.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
