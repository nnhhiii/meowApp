package com.example.meowapp.mission;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissionEditActivity extends AppCompatActivity {
    private EditText etMissionName, etRequire, etType;
    private Spinner spRequire, spType;
    private ImageButton btnBack;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_edit);

        etMissionName = findViewById(R.id.et_mission_name);
        etRequire = findViewById(R.id.et_require);
        etType = findViewById(R.id.et_type);
        spRequire = findViewById(R.id.sp_require);
        spType = findViewById(R.id.sp_type);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnSave = findViewById(R.id.btn_save_changes);

    }

}
