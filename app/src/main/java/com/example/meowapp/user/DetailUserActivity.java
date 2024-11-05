package com.example.meowapp.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.User;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailUserActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvCreatedAt, tvUpdatedAt, tvRole;
    private String userId;
    private User user;
    private ImageButton btnBack;
    private Button btnDelete, btnEdit;
    private ImageView imgUser; // Nếu bạn có hình ảnh của người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        tvUserName = findViewById(R.id.username);
        tvUserEmail = findViewById(R.id.email);
        tvCreatedAt = findViewById(R.id.createdAt);
        tvUpdatedAt = findViewById(R.id.updatedAt);
        tvRole = findViewById(R.id.role);
        imgUser = findViewById(R.id.image);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        userId = getIntent().getStringExtra("userId");

        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v ->{
            Intent intent = new Intent(this, EditUserActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v ->{
            new AlertDialog.Builder(this)
                    .setTitle("Xóa người dùng")
                    .setMessage("Bạn có chắc chắn muốn xóa người dùng này không?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteUser(userId))
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });
        loadData();
    }

    private void loadData() {
        FirebaseApiService.apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    tvUserName.setText(user.getUsername());
                    tvUserEmail.setText(user.getEmail());
                    tvRole.setText(user.getRole());
                    tvCreatedAt.setText(user.getCreated_at());
                    tvUpdatedAt.setText(user.getUpdated_at());
                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Picasso.get().load(user.getAvatar()).into(imgUser);
                    }
                } else {
                    Toast.makeText(DetailUserActivity.this, "Không tải được dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Toast.makeText(DetailUserActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser(String userId) {
        FirebaseApiService.apiService.deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    finish();
                    Toast.makeText(DetailUserActivity.this, "Xóa người dùng thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailUserActivity.this, "Xóa người dùng thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Toast.makeText(DetailUserActivity.this, "Xóa người dùng thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
