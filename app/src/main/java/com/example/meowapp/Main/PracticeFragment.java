package com.example.meowapp.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.meowapp.R;
import com.example.meowapp.questionType.ListeningFragment;
import com.example.meowapp.questionType.SpeakingFragment;
import com.example.meowapp.questionType.WritingFragment;
import com.example.meowapp.questionType.OrderWordFragment;
import com.example.meowapp.questionType.MultipleChoiceImageFragment;
import com.example.meowapp.questionType.MultipleChoiceFragment;
public class PracticeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View rootView = inflater.inflate(R.layout.fragment_main_practice, container, false);

        // Các RelativeLayout để thay thế Fragment
        RelativeLayout btnTracNghiemHinh = rootView.findViewById(R.id.btnTracNghiemHinh);
        RelativeLayout btnTracNghiem = rootView.findViewById(R.id.btnTracNghiem);
        RelativeLayout btnNghe = rootView.findViewById(R.id.btnNghe);
        RelativeLayout btnNoi = rootView.findViewById(R.id.btnNoi);
        RelativeLayout btnViet = rootView.findViewById(R.id.btnViet);
        RelativeLayout btnSapXepChu = rootView.findViewById(R.id.btnSapXepChu);

        // Chuyển sang các Fragment khi nhấn vào từng nút
        btnTracNghiemHinh.setOnClickListener(v -> replaceFragment(new MultipleChoiceImageFragmentNew()));
        btnTracNghiem.setOnClickListener(v -> replaceFragment(new MultipleChoiceFragment()));  // Trắc nghiệm
        btnNghe.setOnClickListener(v -> replaceFragment(new ListeningFragment()));
        btnNoi.setOnClickListener(v -> replaceFragment(new SpeakingFragment()));
        btnViet.setOnClickListener(v -> replaceFragment(new WritingFragment()));
        btnSapXepChu.setOnClickListener(v -> replaceFragment(new OrderWordFragment()));

        return rootView;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
