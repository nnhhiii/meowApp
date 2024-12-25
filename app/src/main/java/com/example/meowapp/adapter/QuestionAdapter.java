package com.example.meowapp.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.lesson.QuestionCreateActivity;
import com.example.meowapp.model.Question;
import com.example.meowapp.model.QuestionType;
import com.example.meowapp.user.EditUserActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionAdapter  extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private Context context;
    private List<Pair<String, Question>> list;
    private List<Pair<String, QuestionType>> questionTypeList;
    private RecyclerView recyclerView; // Biến để lưu RecyclerView
    private String lessonId, questionId, questionTypeName, questionTypeId;
    private int currentImageIndex = -1; // Để lưu vị trí ảnh nào đang được chọn
    private int currentItemPosition = -1; // Vị trí của item trong RecyclerView
    public static final int PICK_IMAGE_REQUEST = 1;


    public QuestionAdapter(RecyclerView recyclerView, Context context, List<Pair<String, Question>> list,
                           List<Pair<String, QuestionType>> questionTypeList, String lessonId) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.list = list;
        this.questionTypeList = questionTypeList;
        this.lessonId = lessonId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lesson_question, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private EditText optionA, optionB, optionC, optionD, questionText, correctAnswer, orderWords;
        private ImageButton btnEdit;
        private ImageView imageA, imageB, imageC, imageD;
        private Spinner spQuestionType;
        private Button btnImageA, btnImageB, btnImageC, btnImageD, btnDelete, btnCancel, btnSave;
        private LinearLayout layoutOrderWords, layoutOptionA, layoutOptionB, layoutOptionC, layoutOptionD,
                layoutImageA, layoutImageB, layoutImageC, layoutImageD;
        private RelativeLayout layoutBtnEdit;
        private Uri imgUriA, imgUriB, imgUriC, imgUriD;


        public ViewHolder(View itemView) {
            super(itemView);
            spQuestionType = itemView.findViewById(R.id.spQuestionType);
            questionText = itemView.findViewById(R.id.etQuestion);
            correctAnswer = itemView.findViewById(R.id.etAnswer);

            layoutOrderWords = itemView.findViewById(R.id.layoutOrderWords);
            layoutImageA = itemView.findViewById(R.id.layoutImageA);
            layoutImageB = itemView.findViewById(R.id.layoutImageB);
            layoutImageC = itemView.findViewById(R.id.layoutImageC);
            layoutImageD = itemView.findViewById(R.id.layoutImageD);
            layoutOptionA = itemView.findViewById(R.id.layoutOptionA);
            layoutOptionB = itemView.findViewById(R.id.layoutOptionB);
            layoutOptionC = itemView.findViewById(R.id.layoutOptionC);
            layoutOptionD = itemView.findViewById(R.id.layoutOptionD);
            layoutBtnEdit = itemView.findViewById(R.id.layoutBtnEdit);

            orderWords = itemView.findViewById(R.id.etOrderWords);
            imageA = itemView.findViewById(R.id.imageA);
            imageB = itemView.findViewById(R.id.imageB);
            imageC = itemView.findViewById(R.id.imageC);
            imageD = itemView.findViewById(R.id.imageD);
            optionA = itemView.findViewById(R.id.etOptionA);
            optionB = itemView.findViewById(R.id.etOptionB);
            optionC = itemView.findViewById(R.id.etOptionC);
            optionD = itemView.findViewById(R.id.etOptionD);
            btnImageA = itemView.findViewById(R.id.btnImageA);
            btnImageB = itemView.findViewById(R.id.btnImageB);
            btnImageC = itemView.findViewById(R.id.btnImageC);
            btnImageD = itemView.findViewById(R.id.btnImageD);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnSave = itemView.findViewById(R.id.btnSaveQuestion);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnDelete = itemView.findViewById(R.id.btnDeleteQuestion);
        }
        public void updateImage(Uri imageUri, int index) {
            switch (index) {
                case 0:
                    imgUriA = imageUri; // Lưu URI cho hình ảnh A
                    imageA.setImageURI(imgUriA); // Cập nhật ImageView A
                    break;
                case 1:
                    imgUriB = imageUri; // Lưu URI cho hình ảnh B
                    imageB.setImageURI(imgUriB); // Cập nhật ImageView B
                    break;
                case 2:
                    imgUriC = imageUri; // Lưu URI cho hình ảnh C
                    imageC.setImageURI(imgUriC); // Cập nhật ImageView C
                    break;
                case 3:
                    imgUriD = imageUri; // Lưu URI cho hình ảnh D
                    imageD.setImageURI(imgUriD); // Cập nhật ImageView D
                    break;
            }
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = list.get(position).second;
        String questionId = list.get(position).first;

        // Đổ dữ liệu 
        holder.questionText.setText(question.getQuestion_text());
        holder.correctAnswer.setText(question.getCorrect_answer());


        int selectedIndex = -1;
        int index = 0;
        List<String> questionTypeNames = new ArrayList<>();
        for (Pair<String, QuestionType> typePair : questionTypeList) {
            questionTypeNames.add(typePair.second.getQuestion_type_name());
            if (typePair.first.equals(question.getQuestion_type())) {
                selectedIndex = index;  // Lưu lại index của questionType đã chọn
            }
            index++;
        }

        // Set up ArrayAdapter for Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, questionTypeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spQuestionType.setAdapter(adapter);

        // Đặt Spinner vào vị trí ngôn ngữ tương ứng
        if (selectedIndex != -1) {
            holder.spQuestionType.setSelection(selectedIndex);
            updateView(holder, questionTypeNames.get(selectedIndex), question);
        } else {
            Log.e("Adapter", "Không tìm thấy loại câu hỏi tương ứng");
        }


        holder.spQuestionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < questionTypeList.size()) {
                    questionTypeId = questionTypeList.get(position).first;
                    questionTypeName = questionTypeNames.get(position);
                    // Cập nhật giao diện
                    updateView(holder, questionTypeName, question);
                } else {
                    Log.e("Adapter", "Vị trí không hợp lệ: " + position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không cần xử lý nếu không chọn gì
            }
        });


        holder.btnImageA.setOnClickListener(v -> openGallery(holder, 0, position));
        holder.btnImageB.setOnClickListener(v -> openGallery(holder, 1, position));
        holder.btnImageC.setOnClickListener(v -> openGallery(holder, 2, position));
        holder.btnImageD.setOnClickListener(v -> openGallery(holder, 3, position));

        holder.btnEdit.setOnClickListener(v -> {
            holder.layoutBtnEdit.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnImageA.setVisibility(View.VISIBLE);
            holder.btnImageB.setVisibility(View.VISIBLE);
            holder.btnImageC.setVisibility(View.VISIBLE);
            holder.btnImageD.setVisibility(View.VISIBLE);
        });
        holder.btnCancel.setOnClickListener(v -> {
            holder.layoutBtnEdit.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnImageA.setVisibility(View.GONE);
            holder.btnImageB.setVisibility(View.GONE);
            holder.btnImageC.setVisibility(View.GONE);
            holder.btnImageD.setVisibility(View.GONE);
        });
        holder.btnSave.setOnClickListener(v -> {
            checkNull(holder, questionTypeName, question, questionId);
        });

        // Thiết lập hành vi cho nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa câu hỏi")
                    .setMessage("Bạn có chắc chắn muốn xóa câu hỏi này không?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteQuestion(questionId, position))
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });
    }
    private void openGallery(ViewHolder holder, int imageIndex, int itemPosition) {
        currentImageIndex = imageIndex; // Lưu chỉ số của ảnh đang chọn
        currentItemPosition = itemPosition; // Lưu vị trí của item

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    public void updateImageAtPosition(Uri imageUri) {
        if (currentItemPosition != -1 && currentImageIndex != -1) {
            Pair<String, Question> item = list.get(currentItemPosition);

            // Cập nhật URI ảnh tương ứng
            switch (currentImageIndex) {
                case 0:
                    item.second.setImage_option_a(imageUri.toString());
                    break;
                case 1:
                    item.second.setImage_option_b(imageUri.toString());
                    break;
                case 2:
                    item.second.setImage_option_c(imageUri.toString());
                    break;
                case 3:
                    item.second.setImage_option_d(imageUri.toString());
                    break;
            }

            // Cập nhật ảnh vào ViewHolder
            notifyItemChanged(currentItemPosition);
            // Tìm ViewHolder tương ứng và gọi phương thức cập nhật ảnh
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(currentItemPosition);
            if (viewHolder != null) {
                ((ViewHolder) viewHolder).updateImage(imageUri, currentImageIndex);
            }

            currentImageIndex = -1; // Đặt lại để tránh lỗi
            currentItemPosition = -1; // Đặt lại để tránh lỗi
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    private void updateView(ViewHolder holder, String questionTypeName, Question question) {
        holder.layoutOrderWords.setVisibility(View.GONE);
        holder.layoutOptionA.setVisibility(View.GONE);
        holder.layoutOptionB.setVisibility(View.GONE);
        holder.layoutOptionC.setVisibility(View.GONE);
        holder.layoutOptionD.setVisibility(View.GONE);
        holder.layoutImageA.setVisibility(View.GONE);
        holder.layoutImageB.setVisibility(View.GONE);
        holder.layoutImageC.setVisibility(View.GONE);
        holder.layoutImageD.setVisibility(View.GONE);

        switch (questionTypeName) {
            case "order_words":
                holder.layoutOrderWords.setVisibility(View.VISIBLE);
                holder.orderWords.setText(question.getOrder_words());
                break;
            case "multiple_choice":
                holder.layoutOptionA.setVisibility(View.VISIBLE);
                holder.layoutOptionB.setVisibility(View.VISIBLE);
                holder.layoutOptionC.setVisibility(View.VISIBLE);
                holder.layoutOptionD.setVisibility(View.VISIBLE);
                holder.optionA.setText(question.getOption_a());
                holder.optionB.setText(question.getOption_b());
                holder.optionC.setText(question.getOption_c());
                holder.optionD.setText(question.getOption_d());
                break;
            case "multiple_choice_image":
                holder.layoutOptionA.setVisibility(View.VISIBLE);
                holder.layoutOptionB.setVisibility(View.VISIBLE);
                holder.layoutOptionC.setVisibility(View.VISIBLE);
                holder.layoutOptionD.setVisibility(View.VISIBLE);
                holder.layoutImageA.setVisibility(View.VISIBLE);
                holder.layoutImageB.setVisibility(View.VISIBLE);
                holder.layoutImageC.setVisibility(View.VISIBLE);
                holder.layoutImageD.setVisibility(View.VISIBLE);
                holder.optionA.setText(question.getOption_a());
                holder.optionB.setText(question.getOption_b());
                holder.optionC.setText(question.getOption_c());
                holder.optionD.setText(question.getOption_d());

                // Load images using Picasso
                if (question.getImage_option_a() != null && !question.getImage_option_a().isEmpty()) {
                    Picasso.get().load(question.getImage_option_a()).into(holder.imageA);
                }
                if (question.getImage_option_b() != null && !question.getImage_option_b().isEmpty()) {
                    Picasso.get().load(question.getImage_option_b()).into(holder.imageB);
                }
                if (question.getImage_option_c() != null && !question.getImage_option_c().isEmpty()) {
                    Picasso.get().load(question.getImage_option_c()).into(holder.imageC);
                }
                if (question.getImage_option_d() != null && !question.getImage_option_d().isEmpty()) {
                    Picasso.get().load(question.getImage_option_d()).into(holder.imageD);
                }
                break;
            default:
                break;
        }
    }

    private void checkNull(ViewHolder holder, String questionTypeName, Question question, String questionId){
        String questionTv = holder.questionText.getText().toString().trim();
        String newCorrectAnswer = holder.correctAnswer.getText().toString().trim();
        String newOrderWords = holder.orderWords.getText().toString().trim();
        String newOptionA = holder.optionA.getText().toString().trim();
        String newOptionB = holder.optionB.getText().toString().trim();
        String newOptionC = holder.optionC.getText().toString().trim();
        String newOptionD = holder.optionD.getText().toString().trim();

        // Kiểm tra thông tin đầu vào
        if (TextUtils.isEmpty(questionTv)) {
            Toast.makeText(context, "Vui lòng nhập câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newCorrectAnswer)) {
            Toast.makeText(context, "Vui lòng nhập đáp án", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (questionTypeName) {
            case "order_words":
                if (TextUtils.isEmpty(newOrderWords)) {
                    Toast.makeText(context, "Vui lòng nhập các từ theo thứ tự", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    question.setOrder_words(newOrderWords);
                }
                break;
            case "multiple_choice":
                if (TextUtils.isEmpty(newOptionA) || TextUtils.isEmpty(newOptionB) ||
                        TextUtils.isEmpty(newOptionC) || TextUtils.isEmpty(newOptionD)) {
                    Toast.makeText(context, "Vui lòng nhập tất cả các đáp án", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    question.setOption_a(newOptionA);
                    question.setOption_b(newOptionB);
                    question.setOption_c(newOptionC);
                    question.setOption_d(newOptionD);
                }
                break;
            case "multiple_choice_image":
                if(TextUtils.isEmpty(newOptionA) || TextUtils.isEmpty(newOptionB) ||
                        TextUtils.isEmpty(newOptionC) || TextUtils.isEmpty(newOptionD) ||
                holder.imgUriA == null || holder.imgUriB == null||holder.imgUriC == null||holder.imgUriD == null){
                    Toast.makeText(context, "Vui lòng nhập đủ dữ liệu và các ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }else if(holder.imgUriA != null || holder.imgUriB != null||holder.imgUriC != null||holder.imgUriD != null){
                    question.setOption_a(newOptionA);
                    question.setOption_b(newOptionB);
                    question.setOption_c(newOptionC);
                    question.setOption_d(newOptionD);
                    Log.e("QuestionAdapter", "dang chay imguri");
                    uploadImagesToFirebaseStorage(questionId, question, holder.imgUriA, holder.imgUriB, holder.imgUriC, holder.imgUriD);
                }
                else{
                    question.setOption_a(newOptionA);
                    question.setOption_b(newOptionB);
                    question.setOption_c(newOptionC);
                    question.setOption_d(newOptionD);
                    saveToFirebase(questionId, question);
                }
                break;
            default:
                break;
        }
        question.setQuestion_text(questionTv);
        question.setCorrect_answer(newCorrectAnswer);
        question.setQuestion_type(questionTypeId);
        question.setLesson_id(lessonId);

        // Lấy thời gian hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());
        question.setUpdated_at(currentTime);

        if (!questionTypeName.equals("multiple_choice_image")) {
            saveToFirebase(questionId, question);
        }

    }
    private void uploadImagesToFirebaseStorage(String questionId,Question question, Uri imgUriA, Uri imgUriB, Uri imgUriC, Uri imgUriD) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Map<String, String> imageUrls = new HashMap<>();
        // Upload ảnh A nếu URI không null
        if (imgUriA != null) {
            StorageReference refA = storageRef.child("questions/" + System.currentTimeMillis() + "_A.jpg");
            refA.putFile(imgUriA).addOnSuccessListener(taskSnapshot ->
                    refA.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrls.put("imageA", uri.toString());
                        question.setImage_option_a(uri.toString());
                    }));
        }

        // Upload ảnh B nếu URI không null
        if (imgUriB != null) {
            StorageReference refB = storageRef.child("questions/" + System.currentTimeMillis() + "_B.jpg");
            refB.putFile(imgUriB).addOnSuccessListener(taskSnapshot ->
                    refB.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrls.put("imageB", uri.toString());
                        question.setImage_option_b(uri.toString());
                    }));
        }

        // Upload ảnh C nếu URI không null
        if (imgUriC != null) {
            StorageReference refC = storageRef.child("questions/" + System.currentTimeMillis() + "_C.jpg");
            refC.putFile(imgUriC).addOnSuccessListener(taskSnapshot ->
                    refC.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrls.put("imageC", uri.toString());
                        question.setImage_option_c(uri.toString());
                    }));
        }

        // Upload ảnh D nếu URI không null
        if (imgUriD != null) {
            StorageReference refD = storageRef.child("questions/" + System.currentTimeMillis() + "_D.jpg");
            refD.putFile(imgUriD).addOnSuccessListener(taskSnapshot ->
                    refD.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrls.put("imageD", uri.toString());
                        question.setImage_option_d(uri.toString());
                    }));
        }

        saveToFirebase(questionId, question);
    }
    private void saveToFirebase(String questionId, Question question){
        FirebaseApiService.apiService.updateQuestion(questionId, question).enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Cập nhật câu hỏi thành công!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "Thêm câu hỏi thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteQuestion(String questionId, int position) {
        // Gọi API để xóa ngôn ngữ
        FirebaseApiService.apiService.deleteQuestion(questionId).enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if (response.isSuccessful()) {
                    list.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
