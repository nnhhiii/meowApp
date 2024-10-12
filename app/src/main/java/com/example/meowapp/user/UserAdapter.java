package com.example.meowapp.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.user.EditUserActivity;
import com.example.meowapp.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private List<Pair<String, User>> list;

    public UserAdapter(Context context, List<Pair<String, User>> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position).second; // Trả về User object
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        TextView tvUserName; // Giả sử User có tên
        CardView cardView;
        ImageButton btnDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);

            holder = new ViewHolder();
            holder.tvUserName = convertView.findViewById(R.id.username);
            holder.cardView = convertView.findViewById(R.id.cardView);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Pair<String, User> pair = list.get(position);
        User user = pair.second;
        String userId = pair.first;

        holder.tvUserName.setText(user.getName());

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditUserActivity.class);
            intent.putExtra("userId", userId);
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa người dùng")
                    .setMessage("Bạn có chắc chắn muốn xóa người dùng này không?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteUser(userId, position))
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });
        return convertView;
    }

    private void deleteUser(String userId, int position) {
        FirebaseApiService.apiService.deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Xóa người dùng thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa người dùng thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Toast.makeText(context, "Xóa người dùng thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
