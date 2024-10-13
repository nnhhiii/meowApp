package com.example.meowapp.admin;

import android.os.Bundle;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_hp);

        GridView gridView = findViewById(R.id.grid_view);
        List<GridItem> gridItems = new ArrayList<>();

        gridItems.add(new GridItem(R.drawable.ic_account, "Tài khoản"));
        gridItems.add(new GridItem(R.drawable.ic_user, "Người dùng"));
        gridItems.add(new GridItem(R.drawable.ic_lang, "Ngôn ngữ"));
        gridItems.add(new GridItem(R.drawable.ic_level, "Cấp độ"));
        gridItems.add(new GridItem(R.drawable.ic_lession, "Bài học"));
        gridItems.add(new GridItem(R.drawable.ic_question, "Câu hỏi"));
        gridItems.add(new GridItem(R.drawable.ic_noti, "Thông báo"));

        GridAdapter adapter = new GridAdapter(this, gridItems);
        gridView.setAdapter(adapter);
    }
}


