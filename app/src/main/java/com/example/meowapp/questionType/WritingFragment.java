package com.example.meowapp.questionType;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.meowapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WritingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WritingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WritingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment writting.
     */
    // TODO: Rename and change types and number of parameters
    public static WritingFragment newInstance(String param1, String param2) {
        WritingFragment fragment = new WritingFragment();
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
        View view = inflater.inflate(R.layout.fragment_question_type_writing, container, false);

        Button submitButton = view.findViewById(R.id.btnSubmit);
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