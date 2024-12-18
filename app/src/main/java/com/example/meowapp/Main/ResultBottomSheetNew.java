package com.example.meowapp.Main;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.meowapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ResultBottomSheetNew extends BottomSheetDialogFragment {

    private boolean isCorrect;
    private String explanation;

    public ResultBottomSheetNew(boolean isCorrect, String explanation) {
        this.isCorrect = isCorrect;
        this.explanation = explanation;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_result, container, false);

        // Thông báo cho người dùng kết quả đúng/sai
        TextView resultTv = view.findViewById(R.id.tvResult);
        TextView explainTv = view.findViewById(R.id.tvExplanation);
        Button button = view.findViewById(R.id.btnNext);

        if (isCorrect) {
            resultTv.setText("Đúng rồi!");
            resultTv.setTextColor(ContextCompat.getColor(getContext(), R.color.green1));
            explainTv.setTextColor(ContextCompat.getColor(getContext(), R.color.green1));
            button.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green2));


            playSound(R.raw.correct_sound);
        } else {
            resultTv.setText("Sai rồi!");
            resultTv.setTextColor(ContextCompat.getColor(getContext(), R.color.red1));
            explainTv.setTextColor(ContextCompat.getColor(getContext(), R.color.red1));
            button.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red2));



            playSound(R.raw.error_sound);
        }

        button.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        explainTv.setText(explanation);

        button.setOnClickListener(v -> {
            dismiss(); // Đóng BottomSheetDialogFragment
            if (getParentFragmentManager() != null) {
                Fragment fragment = getParentFragmentManager().findFragmentById(R.id.frameLayout);
                if (fragment instanceof MultipleChoiceImageFragmentNew) {
                    MultipleChoiceImageFragmentNew multipleChoiceFragment = (MultipleChoiceImageFragmentNew) fragment;
                    multipleChoiceFragment.displayNextQuestion(); // Gửi sự kiện đến fragment cha xử lý câu hỏi tiếp theo
                } else {
                    Toast.makeText(getContext(), "Fragment không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "FragmentManager không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void playSound(int soundResId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), soundResId);
        mediaPlayer.setOnCompletionListener(mp -> mp.release());  // Giải phóng tài nguyên khi phát xong
        mediaPlayer.start();
    }
}
