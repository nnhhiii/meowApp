package com.example.meowapp.Kiet;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.meowapp.R;
import androidx.appcompat.app.AppCompatActivity;

public class SelectLevel extends AppCompatActivity {

    private TextView tvCB, tvTC, tvNC;
    private Button btnContinue;
    private String selectedLevel = null; // Biến để lưu trữ cấp độ được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);

        // Khởi tạo các TextView và Button từ layout
        tvCB = findViewById(R.id.tvCB);
        tvTC = findViewById(R.id.tvTC);
        tvNC = findViewById(R.id.tvNC);
        btnContinue = findViewById(R.id.btn);

        // Gán sự kiện Click cho từng TextView
        tvCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLevel = "Cơ Bản";
                updateTextViewColors();
                Toast.makeText(SelectLevel.this, "Bạn chọn Cơ Bản", Toast.LENGTH_SHORT).show();
            }
        });

        tvTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLevel = "Trung Cấp";
                updateTextViewColors();
                Toast.makeText(SelectLevel.this, "Bạn chọn Trung Cấp", Toast.LENGTH_SHORT).show();
            }
        });

        tvNC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLevel = "Nâng Cao";
                updateTextViewColors();
                Toast.makeText(SelectLevel.this, "Bạn chọn Nâng Cao", Toast.LENGTH_SHORT).show();
            }
        });

        // Gán sự kiện Click cho Button Tiếp tục
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLevel != null) {
                    Toast.makeText(SelectLevel.this, "Tiếp tục với cấp độ: " + selectedLevel, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SelectLevel.this, "Vui lòng chọn một cấp độ trước khi tiếp tục", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTextViewColors() {
        // Đặt màu sắc cho các TextView dựa trên lựa chọn hiện tại
        if ("Cơ Bản".equals(selectedLevel)) {
            tvCB.setBackgroundColor(Color.parseColor("#FF4081")); // Màu hồng
            tvTC.setBackgroundColor(Color.WHITE); // Màu trắng
            tvNC.setBackgroundColor(Color.WHITE); // Màu trắng
        } else if ("Trung Cấp".equals(selectedLevel)) {
            tvTC.setBackgroundColor(Color.parseColor("#FF4081")); // Màu hồng
            tvCB.setBackgroundColor(Color.WHITE); // Màu trắng
            tvNC.setBackgroundColor(Color.WHITE); // Màu trắng
        } else if ("Nâng Cao".equals(selectedLevel)) {
            tvNC.setBackgroundColor(Color.parseColor("#FF4081")); // Màu hồng
            tvCB.setBackgroundColor(Color.WHITE); // Màu trắng
            tvTC.setBackgroundColor(Color.WHITE); // Màu trắng
        }
    }
}
