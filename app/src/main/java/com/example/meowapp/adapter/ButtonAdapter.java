package com.example.meowapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meowapp.R;
import com.example.meowapp.questionType.StartActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ViewHolder> {
    private List<String> lessonNames; // Danh sách tên bài học
    private List<String> lessonIds; // Danh sách ID bài học
    private Context context;
    private Set<String> completedLessons = new HashSet<>(); // Danh sách bài học đã hoàn thành

    // Constructor
    public ButtonAdapter(List<String> lessonNames, List<String> lessonIds, Context context) {
        this.lessonNames = lessonNames;
        this.lessonIds = lessonIds;
        this.context = context;
    }

    // Phương thức cập nhật danh sách bài học hoàn thành
    public void updateLessonStatus(Set<String> completedLessons) {
        this.completedLessons = completedLessons;
        notifyDataSetChanged(); // Cập nhật lại giao diện
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String lessonId = lessonIds.get(position);
        holder.textView.setText(lessonNames.get(position)); // Hiển thị tên bài học

        // Kiểm tra trạng thái hoàn thành của bài học
        if (completedLessons.contains(lessonId)) {
            holder.imageButton.setImageResource(R.drawable.buttonmeow); // Hình ảnh khi đã hoàn thành
        } else {
            holder.imageButton.setImageResource(R.drawable.buttonmeow_1); // Hình ảnh khi chưa hoàn thành
        }

        // Xử lý sự kiện click vào nút bài học
        holder.imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, StartActivity.class); // Mở Activity bài học
            intent.putExtra("LESSON_ID", lessonId);// Truyền ID bài học sang Activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lessonNames.size(); // Số lượng bài học
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageButton imageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_text); // TextView hiển thị tên bài học
            imageButton = itemView.findViewById(R.id.item_button); // ImageButton để click
        }
    }
}
