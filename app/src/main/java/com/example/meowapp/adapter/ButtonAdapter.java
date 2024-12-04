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
import java.util.List;
import com.example.meowapp.R;
import com.example.meowapp.questionType.BlankActivity;
import com.example.meowapp.questionType.StartActivity;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {

    private List<String> lessonNames, lessonIds;
    private Context context;

    public ButtonAdapter(List<String> lessonNames, List<String> lessonIds, Context context) {
        this.lessonNames = lessonNames;
        this.lessonIds = lessonIds;
        this.context = context;
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_button, parent, false);
        return new ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        String lessonName = lessonNames.get(position); // Lấy tên bài học
        String lessonKey = lessonIds.get(position);

        holder.textView.setText(lessonName); // Hiển thị tên bài học trong TextView

        holder.button.setOnClickListener(v -> {
            // Tạo intent để chuyển đến Activity khác
            Intent intent = new Intent(context, StartActivity.class);
            intent.putExtra("LESSON_ID", lessonKey);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lessonNames.size(); // Trả về kích thước của danh sách tên bài học
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        ImageButton button; // Sử dụng ImageButton thay cho Button
        TextView textView;  // TextView hiển thị tên bài học

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.item_button);
            textView = itemView.findViewById(R.id.item_text); // Lấy TextView
        }
    }
}
