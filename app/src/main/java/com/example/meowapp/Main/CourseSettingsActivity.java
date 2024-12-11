package com.example.meowapp.Main;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meowapp.R;
import com.example.meowapp.adapter.CourseAdapter;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseSettingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private List<Course> courseList;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_courses);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Ánh xạ RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCourses();
    }

    private void loadCourses() {
        // Lấy người dùng hiện tại từ FirebaseAuth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid(); // ID của người dùng đang đăng nhập

            // Lấy danh sách khóa học mà người dùng đã tham gia từ Firebase
            FirebaseApiService.apiService.getUserCourses(userId).enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<String> userCourses = response.body(); // Đây là danh sách các khóa học người dùng đã tham gia

                        // Lấy tất cả khóa học từ API
                        FirebaseApiService.apiService.getAllCourses().enqueue(new Callback<Map<String, Course>>() {
                            @Override
                            public void onResponse(Call<Map<String, Course>> call, Response<Map<String, Course>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Map<String, Course> courseMap = response.body();
                                    List<Course> allCourses = new ArrayList<>(courseMap.values());

                                    // Lọc các khóa học mà người dùng đã tham gia
                                    List<Course> userJoinedCourses = new ArrayList<>();
                                    for (Course course : allCourses) {
                                        if (userCourses.contains(course.getId())) { // Kiểm tra nếu khóa học này là khóa học người dùng đã tham gia
                                            userJoinedCourses.add(course);
                                        }
                                    }

                                    // Cập nhật adapter với các khóa học đã tham gia
                                    if (!userJoinedCourses.isEmpty()) {
                                        courseAdapter = new CourseAdapter(userJoinedCourses);
                                        recyclerView.setAdapter(courseAdapter);
                                    } else {
                                        Toast.makeText(CourseSettingsActivity.this, "Bạn chưa tham gia khóa học nào!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(CourseSettingsActivity.this, "Không tải được khóa học!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Map<String, Course>> call, Throwable throwable) {
                                Toast.makeText(CourseSettingsActivity.this, "Lỗi khi tải khóa học!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(CourseSettingsActivity.this, "Không lấy được khóa học người dùng đã tham gia!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable throwable) {
                    Toast.makeText(CourseSettingsActivity.this, "Lỗi khi lấy khóa học người dùng đã tham gia!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Người dùng chưa đăng nhập
            Toast.makeText(CourseSettingsActivity.this, "Vui lòng đăng nhập để xem khóa học!", Toast.LENGTH_SHORT).show();
        }
    }
}
