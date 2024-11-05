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
import com.example.meowapp.lesson.LessonEditActivity;
import com.example.meowapp.model.Lesson;
import com.example.meowapp.model.LessonInfo;
import com.example.meowapp.model.Level;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonManagementAdapter extends BaseAdapter {
    private Context context;
    private List<LessonInfo> list;

    public LessonManagementAdapter(Context context, List<LessonInfo> lessonInfoList) {
        this.context = context;
        this.list = lessonInfoList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        TextView tvName, tvLevel, tvLanguage, tvScore, tvCreatedAt, tvUpdatedAt;
        ImageButton btnDelete;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LessonManagementAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lesson_management, parent, false);

            holder = new LessonManagementAdapter.ViewHolder();
            holder.tvName = convertView.findViewById(R.id.name);
            holder.tvLevel = convertView.findViewById(R.id.level);
            holder.tvLanguage = convertView.findViewById(R.id.language);
            holder.tvScore = convertView.findViewById(R.id.point);
            holder.tvCreatedAt = convertView.findViewById(R.id.createdAt);
            holder.tvUpdatedAt = convertView.findViewById(R.id.updatedAt);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
        } else {
            holder = (LessonManagementAdapter.ViewHolder) convertView.getTag();
        }
        LessonInfo lessonInfo = list.get(position);
        holder.tvName.setText(lessonInfo.getLesson().getLesson_name());
        holder.tvLevel.setText(lessonInfo.getLevelName());
        holder.tvLanguage.setText(lessonInfo.getLanguageName());
        holder.tvScore.setText(String.valueOf(lessonInfo.getLesson().getLesson_score()));
        holder.tvCreatedAt.setText(lessonInfo.getLesson().getCreated_at());
        holder.tvUpdatedAt.setText(lessonInfo.getLesson().getUpdated_at());
        String lessonId = lessonInfo.getLessonId();

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LessonEditActivity.class);
            intent.putExtra("lessonId", lessonId);
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
        new AlertDialog.Builder(context)
                .setTitle("Xóa bài học")
                .setMessage("Bạn có chắc muốn xóa bài học này không?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteLesson(lessonId, position))
                .setNegativeButton(android.R.string.no, null)
                .show();
        });
        return convertView;
    }
    private void deleteLesson(String lessonId, int position) {
        FirebaseApiService.apiService.deleteLesson(lessonId).enqueue(new Callback<Lesson>() {
            @Override
            public void onResponse(Call<Lesson> call, Response<Lesson> response) {
                if (response.isSuccessful()) {
                    list.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Lesson> call, Throwable t) {
                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
