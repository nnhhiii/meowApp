package com.example.meowapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.meowapp.R;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {

    private List<String> lessonIds; // Danh sách ID bài học
    private Context context;

    public ButtonAdapter(List<String> lessonIds, Context context) {
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
        String lessonId = lessonIds.get(position);
        holder.button.setText(lessonId); // Hiển thị ID của bài học

        holder.button.setOnClickListener(v -> {
            // Xử lý click ở đây
        });
    }

    @Override
    public int getItemCount() {
        return lessonIds.size();
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        Button button;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.item_button);
        }
    }
}
