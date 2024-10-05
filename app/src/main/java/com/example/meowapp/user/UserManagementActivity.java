package com.example.meowapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.model.Question;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    private ListView listViewUsers;
    private FloatingActionButton fabAddUser;
    private UserAdapter userAdapter;
    private List<Question.User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        listViewUsers = findViewById(R.id.listViewStaff);
        fabAddUser = findViewById(R.id.add_user_btn);
        ImageButton btnBack = findViewById(R.id.back_btn);


        userList = new ArrayList<>();
        userList.add(new Question.User("A", "a@example.com"));
        userList.add(new Question.User("B", "b@example.com"));


        userAdapter = new UserAdapter(this, userList);
        listViewUsers.setAdapter(userAdapter);


        fabAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserManagementActivity.this, AddUserActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void addUser() {
        userList.add(new Question.User("New User", "newuser@example.com"));
        userAdapter.notifyDataSetChanged();
    }


}

