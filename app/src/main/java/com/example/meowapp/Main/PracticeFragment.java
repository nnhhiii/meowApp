package com.example.meowapp.Main;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.fragment.app.Fragment;

import com.example.meowapp.R;
import com.example.meowapp.questionType.BlankActivity;

public class PracticeFragment extends Fragment {
    private RelativeLayout btnNghe, btnNoi, btnTracNghiem, btnTracNghiemHinh, btnViet, btnSapXepChu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_practice, container, false);
        btnTracNghiemHinh = rootView.findViewById(R.id.btnTracNghiemHinh);
        btnTracNghiem = rootView.findViewById(R.id.btnTracNghiem);
        btnNghe = rootView.findViewById(R.id.btnNghe);
        btnNoi = rootView.findViewById(R.id.btnNoi);
        btnViet = rootView.findViewById(R.id.btnViet);
        btnSapXepChu = rootView.findViewById(R.id.btnSapXepChu);

        btnSapXepChu.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BlankActivity.class);
            intent.putExtra("QUESTION_TYPE", "1");
            startActivity(intent);
        });
        btnTracNghiem.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BlankActivity.class);
            intent.putExtra("QUESTION_TYPE", "2");
            startActivity(intent);
        });
        btnTracNghiemHinh.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BlankActivity.class);
            intent.putExtra("QUESTION_TYPE", "3");
            startActivity(intent);
        });
        btnNghe.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BlankActivity.class);
            intent.putExtra("QUESTION_TYPE", "4");
            startActivity(intent);
        });
        btnNoi.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BlankActivity.class);
            intent.putExtra("QUESTION_TYPE", "5");
            startActivity(intent);
        });
        btnViet.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BlankActivity.class);
            intent.putExtra("QUESTION_TYPE", "l1");
            startActivity(intent);
        });

        return rootView;
    }
}