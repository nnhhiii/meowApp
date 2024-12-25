package com.example.meowapp.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.LanguagePreference;
import com.example.meowapp.model.User;
import com.example.meowapp.model.UserProgress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailUserActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvCreatedAt, tvUpdatedAt, tvRole, tvTotalCourses, tvTotalLessons;
    private String userId;
    private User user;
    private ImageButton btnBack;
    private Button btnDelete, btnEdit;
    private ImageView imgUser; // Nếu bạn có hình ảnh của người dùng
    private int languagesCount, lessonsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        tvUserName = findViewById(R.id.tvName);
        tvUserEmail = findViewById(R.id.tvEmail);
        tvCreatedAt = findViewById(R.id.createdAt);
        tvUpdatedAt = findViewById(R.id.updatedAt);
        tvRole = findViewById(R.id.tvRole);
        imgUser = findViewById(R.id.imgAvatar);
        btnBack = findViewById(R.id.btnBack);
        tvTotalLessons = findViewById(R.id.tvTotalLessons);
        tvTotalCourses = findViewById(R.id.tvTotalCourses);
        btnBack.setOnClickListener(v -> finish());

        userId = getIntent().getStringExtra("userId");

        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditUserActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xóa người dùng")
                    .setMessage("Bạn có chắc chắn muốn xóa người dùng này không?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteUser())
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });
        fetchLanguagePreference();
    }

    private void fetchLanguagePreference() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("language_preferences");
        database.orderByChild("user_id").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<LanguagePreference> languagePreferenceList = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            LanguagePreference userProgress = snapshot.getValue(LanguagePreference.class);
                            languagePreferenceList.add(userProgress);

                        }
                        languagesCount = languagePreferenceList.size();
                        fetchUserProgressByUserId();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FirebaseError", "Error: " + databaseError.getMessage());
                    }
                });
    }

    private void fetchUserProgressByUserId() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("user_progress");
        database.orderByChild("user_id").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<UserProgress> userProgressList = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserProgress userProgress = snapshot.getValue(UserProgress.class);
                            // Kiểm tra xem lesson_id có tồn tại không
                            String lessonId = userProgress.getLesson_id();
                            if (lessonId != null && !lessonId.isEmpty()) {
                                userProgressList.add(userProgress);
                            } else {
                                Log.e("UserFragment", "Bỏ qua mục không có lesson_id");
                            }
                        }
                        lessonsCount = userProgressList.size();
                        loadData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FirebaseError", "Error: " + databaseError.getMessage());
                    }
                });
    }

    private void loadData() {
        FirebaseApiService.apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    tvUserName.setText(user.getUsername());
                    tvUserEmail.setText(user.getEmail());
                    tvRole.setText("Vai trò: " + user.getRole());
                    tvCreatedAt.setText(user.getCreated_at());
                    tvUpdatedAt.setText(user.getUpdated_at());
                    tvTotalCourses.setText(String.valueOf(languagesCount));
                    tvTotalLessons.setText(String.valueOf(lessonsCount));

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

    private void deleteUser() {
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
}
