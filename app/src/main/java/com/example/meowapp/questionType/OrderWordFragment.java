package com.example.meowapp.questionType;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.meowapp.R;
import com.google.android.flexbox.FlexboxLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderWordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderWordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderWordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment orderWord.
     */
    public static OrderWordFragment newInstance(String param1, String param2) {
        OrderWordFragment fragment = new OrderWordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question_type_order_word, container, false);

        Button submitButton = view.findViewById(R.id.btnSubmit);
        FlexboxLayout wordContainer = view.findViewById(R.id.wordContainer);

        // Chuỗi order_word (ở đây bạn có thể lấy từ API hoặc cơ sở dữ liệu)
        String orderWord = "Hello, world, this, is, a test";

        // Tách chuỗi thành các từ
        String[] words = orderWord.split(", ");

        // Hiển thị mỗi từ trong TextView
        for (String word : words) {
            TextView wordView = new TextView(getContext());
            wordView.setText(word);
            wordView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            wordView.setSingleLine(true);
            wordView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.word_background));

            // Đặt margin cho TextView
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(16, 16, 16, 16);
            wordView.setLayoutParams(params);

            wordContainer.addView(wordView);
        }

        submitButton.setOnClickListener(v -> {
            boolean isCorrect = checkAnswer();
            String explanation = "Giải thích lý do vì sao đáp án này đúng hoặc sai.";

            ResultBottomSheet bottomSheet = new ResultBottomSheet(isCorrect, explanation);
            bottomSheet.show(getParentFragmentManager(), "ResultBottomSheet");
        });

        return view;
    }
    private boolean checkAnswer() {
        return true;
    }
}
