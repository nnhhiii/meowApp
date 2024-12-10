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

        btnBack.setOnClickListener(v -> finish());

        // Ánh xạ RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCourses();
    }

    private void loadCourses() {
        FirebaseApiService.apiService.getAllCourses().enqueue(new Callback<Map<String, Course>>() {
            @Override
            public void onResponse(Call<Map<String, Course>> call, Response<Map<String, Course>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Course> courseMap = response.body();
                    List<Course> courseList = new ArrayList<>(courseMap.values());
                    courseAdapter = new CourseAdapter(courseList);
                    recyclerView.setAdapter(courseAdapter);
                } else {
                    Toast.makeText(CourseSettingsActivity.this, "Không tải được khóa học!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Course>> call, Throwable throwable) {
                Toast.makeText(CourseSettingsActivity.this, "Lỗi khi tải khóa học!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
