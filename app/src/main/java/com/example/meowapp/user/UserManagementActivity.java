package com.example.meowapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.user.UserAdapter;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagementActivity extends AppCompatActivity {
    private UserAdapter adapter;
    private ListView listView;
    private FloatingActionButton btnAdd;
    private ImageButton btnBack;
    private EditText etSearch;
    private List<Pair<String, User>> usersList = new ArrayList<>();
    private List<Pair<String, User>> filteredUsersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management); // Đảm bảo layout này tồn tại

        listView = findViewById(R.id.listViewStaff); // ID của ListView trong layout
        etSearch = findViewById(R.id.etSearch);
        btnBack = findViewById(R.id.back_btn);

        // Sự kiện cho nút quay lại
        btnBack.setOnClickListener(v -> finish());

        btnAdd = findViewById(R.id.add_user_btn);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddUserActivity.class); // Đảm bảo UserCreateActivity tồn tại
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không làm gì cả
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsersList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không làm gì cả
            }
        });

        loadData();
    }

    private void loadData() {
        FirebaseApiService.apiService.getAllUsers().enqueue(new Callback<Map<String, User>>() {
            @Override
            public void onResponse(Call<Map<String, User>> call, Response<Map<String, User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, User> userMap = response.body();

                    // Xóa dữ liệu cũ trong danh sách chính và danh sách lọc
                    usersList.clear();
                    filteredUsersList.clear();

                    // Chuyển đổi từ Map<String, User> thành List<Pair<String, User>>
                    for (Map.Entry<String, User> entry : userMap.entrySet()) {
                        Pair<String, User> pair = new Pair<>(entry.getKey(), entry.getValue());
                        usersList.add(pair);
                        filteredUsersList.add(pair); // Bắt đầu với toàn bộ danh sách
                    }

                    // Truyền danh sách lọc cho Adapter
                    adapter = new UserAdapter(UserManagementActivity.this, filteredUsersList);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(UserManagementActivity.this, "Failed to get info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, User>> call, Throwable t) {
                Toast.makeText(UserManagementActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error:", t.getMessage(), t);
            }
        });
    }

    private void filterUsersList(String keyword) {
        filteredUsersList.clear();
        if (keyword.isEmpty()) {
            filteredUsersList.addAll(usersList); // Nếu không có từ khóa, hiển thị tất cả
        } else {
            for (Pair<String, User> pair : usersList) {
                User user = pair.second; // Lấy đối tượng User từ cặp Pair
                if (user.getName().toLowerCase().contains(keyword.toLowerCase())) { // Thay đổi theo thuộc tính tên của User
                    filteredUsersList.add(pair); // Thêm cặp Pair vào danh sách lọc
                }
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged(); // Cập nhật lại danh sách hiển thị
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
