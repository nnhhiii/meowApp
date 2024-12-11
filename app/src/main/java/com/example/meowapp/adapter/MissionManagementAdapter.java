package com.example.meowapp.adapter;

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
import com.example.meowapp.mission.MissionEditActivity;
import com.example.meowapp.model.Level;
import com.example.meowapp.model.Mission;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissionManagementAdapter extends BaseAdapter {
    private Context context;
    private List<Pair<String, Mission>> list;

    public MissionManagementAdapter(Context context, List<Pair<String, Mission>> list) {
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
        TextView tvName, tvRequired, tvReward, tvType, tvCreatedAt, tvUpdatedAt;
        ImageButton btnDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_mission_management, parent, false);

            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.name);
            holder.tvRequired = convertView.findViewById(R.id.required);
            holder.tvReward = convertView.findViewById(R.id.reward);
            holder.tvType = convertView.findViewById(R.id.type);
            holder.tvCreatedAt = convertView.findViewById(R.id.createdAt);
            holder.tvUpdatedAt = convertView.findViewById(R.id.updatedAt);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Pair<String, Mission> pair = list.get(position);
        Mission mission = pair.second;
        String missionId = pair.first;

        holder.tvName.setText(mission.getMissionName());
        holder.tvReward.setText("Phần thưởng: " + mission.getRewardAmount());
        holder.tvType.setText("Kiểu phần thưởng: " + mission.getRewardType());
        holder.tvCreatedAt.setText(mission.getCreated_at());
        holder.tvUpdatedAt.setText(mission.getUpdated_at());
        if (mission.getRequiredScore() > 0) {
            holder.tvRequired.setText("Điểm: " + mission.getRequiredScore());
        } else if (mission.getRequiredPerfectLessons() > 0) {
            holder.tvRequired.setText("Bài học xuất sắc: " + mission.getRequiredPerfectLessons());
        } else if (mission.getRequiredEightyLessons() > 0) {
            holder.tvRequired.setText("Bài học đúng trên 80%: " + mission.getRequiredEightyLessons());
        } else if (mission.getRequiredLessons() > 0) {
            holder.tvRequired.setText("Bài học: " + mission.getRequiredLessons());
        } else if (mission.getRequiredStreaks() > 0) {
            holder.tvRequired.setText("Chuỗi: " + mission.getRequiredStreaks());
        } else {
            holder.tvRequired.setText("Không có yêu cầu");
        }



        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MissionEditActivity.class);
            intent.putExtra("missionId", missionId);
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa phần thưởng")
                    .setMessage("Bạn có chắc muốn xóa phần thưởng này không?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteMission(missionId, position))
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        return convertView;
    }
    private void deleteMission(String missionId, int position) {
        FirebaseApiService.apiService.deleteMission(missionId).enqueue(new Callback<Mission>() {
            @Override
            public void onResponse(Call<Mission> call, Response<Mission> response) {
                if (response.isSuccessful()) {
                    list.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Mission> call, Throwable t) {
                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
