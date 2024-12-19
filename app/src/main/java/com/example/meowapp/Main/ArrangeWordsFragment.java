//package com.example.meowapp.Main;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import androidx.fragment.app.Fragment;
//
//import com.example.meowapp.R;
//import com.google.android.flexbox.FlexboxLayout;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//public class ArrangeWordsFragment extends Fragment {
//
//    private TextView questionTextView;
//    private ImageButton btnVolume;
//    private FlexboxLayout wordContainer;
//    private Button btnSubmit;
//
//    private static final String ARG_CORRECT_ORDER = "correct_order";
//    private static final String ARG_WORDS_LIST = "words_list";
//
//    public ArrangeWordsFragment() {
//        // Required empty public constructor
//    }
//
//    public static ArrangeWordsFragment newInstance(String correctOrder, String lessonId, String[] words) {
//        ArrangeWordsFragment fragment = new ArrangeWordsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_CORRECT_ORDER, correctOrder);
//        args.putStringArray(ARG_WORDS_LIST, words);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_question_type_order_word, container, false);
//
//        questionTextView = view.findViewById(R.id.question);
//        btnVolume = view.findViewById(R.id.btnVolume);
//        wordContainer = view.findViewById(R.id.wordContainer);
//        btnSubmit = view.findViewById(R.id.btnSubmit);
//
//        if (getArguments() != null) {
//            String correctOrder = getArguments().getString(ARG_CORRECT_ORDER);
//            String[] words = getArguments().getStringArray(ARG_WORDS_LIST);
//
//            // Set the question text
//            questionTextView.setText(correctOrder);
//
//            // Shuffle words and add to FlexboxLayout
//            List<String> wordList = Arrays.asList(words);
//            Collections.shuffle(wordList);
//            for (String word : wordList) {
//                TextView wordView = new TextView(getActivity());
//                wordView.setText(word);
//                wordView.setBackgroundResource(R.drawable.edittext_with_lines);
//                wordContainer.addView(wordView);
//
//                wordView.setOnClickListener(v -> {
//                    // Handle word click event
//                });
//            }
//        }
//
//        btnSubmit.setOnClickListener(v -> {
//            // Handle submission logic
//        });
//
//        btnVolume.setOnClickListener(v -> {
//            // Handle volume button click
//        });
//
//        return view;
//    }
//}
