package com.example.meowapp.Adapter;

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

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.Level.LevelEditActivity;
import com.example.meowapp.model.Level;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LevelManagementAdapter extends BaseAdapter {
    private Context context;
    private List<Pair<String, Level>> list;
    private String lang_name;

    public LevelManagementAdapter(Context context, List<Pair<String, Level>> list, String lang_name) {
        this.context = context;
        this.list = list;
        this.lang_name = lang_name;
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
        TextView tvName, tvLanguage, tvCreatedAt, tvUpdatedAt;
        ImageButton btnDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_level_management, parent, false);

            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.name);
            holder.tvLanguage = convertView.findViewById(R.id.language);
            holder.tvCreatedAt = convertView.findViewById(R.id.createdAt);
            holder.tvUpdatedAt = convertView.findViewById(R.id.updatedAt);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Pair<String, Level> pair = list.get(position);
        Level level = pair.second;
        String levelId = pair.first;

        holder.tvName.setText(level.getLevel_name());
        holder.tvLanguage.setText(lang_name);
        holder.tvCreatedAt.setText(level.getCreated_at());
        holder.tvUpdatedAt.setText(level.getUpdated_at());

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LevelEditActivity.class);
            intent.putExtra("levelId", levelId);
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa cấp độ")
                    .setMessage("Bạn có chắc muốn xóa cấp độ này không?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteLevel(levelId, position))
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        return convertView;
    }
    private void deleteLevel(String levelId, int position) {
        FirebaseApiService.apiService.deleteLevel(levelId).enqueue(new Callback<Level>() {
            @Override
            public void onResponse(Call<Level> call, Response<Level> response) {
                if (response.isSuccessful()) {
                    list.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Level> call, Throwable t) {
                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
