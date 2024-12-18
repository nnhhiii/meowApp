package com.example.meowapp.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.meowapp.R;

public class PracticeFinishFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_practice_finish, container, false); // Inflate layout

        TextView tvCount = view.findViewById(R.id.tvcount);
        TextView textCongrats = view.findViewById(R.id.text1);
        TextView textComplete = view.findViewById(R.id.text3);
        Button btnNext = view.findViewById(R.id.btnnext);

        if (getArguments() != null) {
            int correctAnswersCount = getArguments().getInt("correctAnswersCount", 0);
            int totalQuestions = getArguments().getInt("totalQuestions", 0);

            String countMessage = correctAnswersCount + " / " + totalQuestions + " câu";
            tvCount.setText(countMessage);
        }

        textCongrats.setText("Tuyệt lắm!");
        textComplete.setText("Bạn đã hoàn thành xong ôn tập!");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new PracticeFragment())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        return view;
    }
}
