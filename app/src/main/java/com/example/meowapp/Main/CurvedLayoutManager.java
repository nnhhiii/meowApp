package com.example.meowapp.Main;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CurvedLayoutManager extends LinearLayoutManager {
    public CurvedLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

        int childCount = getChildCount();
        int parentWidth = getWidth();
        int centerX = parentWidth / 2;

        // Đặt khoảng cách giữa các item
        int spacing = 20; // Khoảng cách giữa các item, có thể điều chỉnh

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child != null) {
                // Lấy chiều rộng của item
                int childWidth = child.getWidth();

                // Tính toán độ cong
                float offset = (float) Math.sin((i - (childCount / 2)) * 0.6) * 50; // Điều chỉnh để thay đổi độ cong

                // Tăng cường độ lệch theo chiều ngang
                float translationX = centerX - (childWidth / 2) + ((i - (childCount / 2)) * (childWidth + spacing * 4) * 10); // Nhân với 2

                if (translationX < 5) { // Cách viền bên trái 10dp
                    translationX = 5;
                    offset = -offset; // Nếu sát bên trái, cong vào
                } else if (translationX + childWidth > parentWidth - 5) {
                    translationX = parentWidth - childWidth - 5; // Giới hạn ở bên phải
                    offset = -offset; // Nếu sát bên phải, cong ra
                }

                // Cong vào và cong ra dựa trên vị trí của item
                if (i < childCount / 2) {
                    // Cong vào cho các item bên trái
                    child.setTranslationY(-Math.abs(offset)); // Cong vào
                } else {
                    // Cong ra cho các item bên phải
                    child.setTranslationY(Math.abs(offset)); // Cong ra
                }

                // Đặt vị trí cho các item
                child.setTranslationX(translationX);
            }
        }
    }
}
