package com.example.meowapp.Kiet;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meowapp.R;

public class SelectLanguage extends AppCompatActivity {

    private Button btnContinue;
    private TextView txtTiengAnh, txtTiengHoa;
    private String selectedLanguage = null; // Biến để lưu trữ ngôn ngữ được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        // Khởi tạo các TextView từ layout
        txtTiengAnh = findViewById(R.id.txtTiengAnh);
        txtTiengHoa = findViewById(R.id.txtTiengHoa);
        btnContinue = findViewById(R.id.btn);

        // Gán sự kiện Click cho từng TextView
        txtTiengAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage("Tiếng Anh"); // Chọn Tiếng Anh
            }
        });

        txtTiengHoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage("Tiếng Hoa"); // Chọn Tiếng Hoa
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi nhấn vào Button Tiếp tục
                if (selectedLanguage != null) {
                    Toast.makeText(SelectLanguage.this, "Tiếp tục với ngôn ngữ: " + selectedLanguage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SelectLanguage.this, "Vui lòng chọn ngôn ngữ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectLanguage(String language) {
        // Cập nhật ngôn ngữ và màu sắc cho các TextView
        selectedLanguage = language;
        updateTextViewColors();
        Toast.makeText(SelectLanguage.this, "Bạn chọn " + language, Toast.LENGTH_SHORT).show();
    }

    private void updateTextViewColors() {
        // Đặt màu sắc cho các TextView dựa trên lựa chọn hiện tại
        if ("Tiếng Anh".equals(selectedLanguage)) {
            txtTiengAnh.setBackgroundColor(Color.parseColor("#FF4081")); // Màu hồng
            txtTiengHoa.setBackgroundColor(Color.WHITE); // Màu trắng
        } else if ("Tiếng Hoa".equals(selectedLanguage)) {
            txtTiengHoa.setBackgroundColor(Color.parseColor("#FF4081")); // Màu hồng
            txtTiengAnh.setBackgroundColor(Color.WHITE); // Màu trắng
        }
    }
}
