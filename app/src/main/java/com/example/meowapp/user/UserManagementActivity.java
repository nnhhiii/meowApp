package com.example.meowapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.model.Question;
import com.example.meowapp.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {
    public static final int REQUEST_EDIT_USER = 1;

    private ListView listViewUsers;
    private FloatingActionButton fabAddUser;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        listViewUsers = findViewById(R.id.listViewStaff);
        fabAddUser = findViewById(R.id.add_user_btn);
        ImageButton btnBack = findViewById(R.id.back_btn);


        userList = new ArrayList<>();
        userList.add(new User("A", "a@example.com", "defaultPassword"));
        userList.add(new User("B", "b@example.com", "defaultPassword"));


        userAdapter = new UserAdapter(this, userList);
        listViewUsers.setAdapter(userAdapter);


        fabAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserManagementActivity.this, AddUserActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = userList.get(position);
            Intent intent = new Intent(UserManagementActivity.this, EditUserActivity.class);
            intent.putExtra("user", selectedUser);
            startActivityForResult(intent, REQUEST_EDIT_USER);
        });
    }

    private void addUser() {
        userList.add(new User("New User", "newuser@example.com", "defaultpassword"));
        userAdapter.notifyDataSetChanged();
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_USER && resultCode == RESULT_OK && data != null) {
            User updatedUser = (User) data.getSerializableExtra("updatedUser");
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getEmail().equals(updatedUser.getEmail())) {
                    userList.set(i, updatedUser);
                    break;
                }
            }
            userAdapter.notifyDataSetChanged();
        }
    }
    public void DeleteUser() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa người dùng này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    Toast.makeText(this, "Người dùng đã được xóa!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Không", null)
                .show();
    }

}