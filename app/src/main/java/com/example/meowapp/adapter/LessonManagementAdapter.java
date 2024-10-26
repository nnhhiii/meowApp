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
import com.example.meowapp.model.Level;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonManagementAdapter extends BaseAdapter {
    private Context context;
    private List<Pair<String, Lesson>> list;
    private String level_name, language_name;
    private String point;

    public LessonManagementAdapter(Context context, List<Pair<String, Lesson>> list, String level_name,String language_name, String point) {
        this.context = context;
        this.list = list;
        this.level_name = level_name;
        this.language_name = language_name;
        this.point = point;
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
        TextView tvName, tvLevel, tvLanguage, tvPoint, tvCreatedAt, tvUpdatedAt;
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
            holder.tvPoint = convertView.findViewById(R.id.point);
            holder.tvCreatedAt = convertView.findViewById(R.id.createdAt);
            holder.tvUpdatedAt = convertView.findViewById(R.id.updatedAt);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
    } else {
        holder = (LessonManagementAdapter.ViewHolder) convertView.getTag();
    }

        Pair<String, Lesson> pair = list.get(position);
        Lesson lesson = pair.second;
        String lessonId = pair.first;

        holder.tvName.setText(lesson.getLesson_name());
        holder.tvLevel.setText(level_name);
        holder.tvLanguage.setText(language_name);
        holder.tvPoint.setText(point);
        holder.tvCreatedAt.setText(lesson.getCreated_at());
        holder.tvUpdatedAt.setText(lesson.getUpdated_at());

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
        FirebaseApiService.apiService.deleteLevel(lessonId).enqueue(new Callback<Level>() {
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
