package com.example.meowapp.user;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meowapp.R;
import com.example.meowapp.model.User;

public class Detail extends AppCompatActivity {

    private TextView tvUserName;
    private TextView tvUserEmail;
    private ImageView imgUser; // Nếu bạn có hình ảnh của người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        tvUserName = findViewById(R.id.textViewUsername);
        tvUserEmail = findViewById(R.id.textViewEmail);
        imgUser = findViewById(R.id.userImage);

        User user = (User) getIntent().getSerializableExtra("user");

        if (user != null) {
            tvUserName.setText(user.getName());
            tvUserEmail.setText(user.getEmail());
        }
    }
}
