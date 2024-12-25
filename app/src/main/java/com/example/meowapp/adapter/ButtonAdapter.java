package com.example.meowapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

        // Cập nhật margin để tạo kiểu zigzag
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.imageButton.getLayoutParams();

        if (position % 2 == 0) {
            // Item lẻ: margin bên trái lớn hơn
            params.setMargins(250, 8, 0, 8); // Left margin > Right margin
        } else {
            // Item chẵn: margin bên phải lớn hơn
            params.setMargins(0, 8, 250, 8); // Right margin > Left margin
        }

        // Kiểm tra trạng thái hoàn thành của bài học
        if (completedLessons.contains(lessonId)) {
            holder.imageButton.setImageResource(R.drawable.buttonmeow); // Hình ảnh khi đã hoàn thành
            holder.imageButton.setClickable(true); // Bật click cho bài học hoàn thành
        } else {
            holder.imageButton.setImageResource(R.drawable.buttonmeow_1); // Hình ảnh khi chưa hoàn thành
            holder.imageButton.setClickable(false); // Tắt click cho bài học chưa hoàn thành
        }

        holder.imageButton.setOnClickListener(v -> {
            if (completedLessons.contains(lessonId)) {
                Intent intent = new Intent(context, StartActivity.class);
                intent.putExtra("LESSON_ID", lessonId);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Bạn cần hoàn thành các bài học trước!", Toast.LENGTH_SHORT).show(); // Thông báo cần hoàn thành bài học trước
            }
        });
    }

    @Override
    public int getItemCount() {
        return lessonNames.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageButton imageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_text);
            imageButton = itemView.findViewById(R.id.item_button);
        }
    }
}
