package com.example.meowapp.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.meowapp.Main.MainActivity;
import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.language.LanguageEditActivity;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.Mission;
import com.example.meowapp.model.User;
import com.example.meowapp.model.UserProgress;
import com.example.meowapp.questionType.FinishActivity;
import com.example.meowapp.questionType.StreakActivity;
import com.example.meowapp.service.TimerService;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardAdapter extends BaseAdapter {
    private Context context;
    private List<Pair<String, Mission>> list;
    private String userId;
    private int score, lessons, perfectLessons, eightyLessons, streaks, hearts, diamonds;
    private List<String> userMissionIdList;


    public RewardAdapter(Context context, List<Pair<String, Mission>> list, String userId, int score, int lessons, int perfectLessons, int eightyLessons, int streaks, int hearts, int diamonds, List<String> userMissionIdList) {
        this.context = context;
        this.list = list;
        this.userId = userId;
        this.score = score;
        this.lessons = lessons;
        this.perfectLessons = perfectLessons;
        this.eightyLessons = eightyLessons;
        this.streaks = streaks;
        this.hearts = hearts;
        this.diamonds = diamonds;
        this.userMissionIdList = userMissionIdList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position).second;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        TextView tvName;
        ProgressBar progressBar;
        ImageView imageView, imgAward;
        RelativeLayout btnAward;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_reward, parent, false);

            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.progressBar = convertView.findViewById(R.id.progressBar);
            holder.imageView = convertView.findViewById(R.id.imgIcon);
            holder.imgAward = convertView.findViewById(R.id.award);
            holder.btnAward = convertView.findViewById(R.id.btnAward);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Pair<String,Mission> pair = list.get(position);
        String missionId = pair.first;
        Mission mission = pair.second;

        holder.tvName.setText(mission.getMissionName());

        int progress;
        if (mission.getRequiredScore() > 0) {
            holder.imageView.setImageResource(R.drawable.ic_samset);
            if (userMissionIdList.contains(missionId)) {
                holder.imgAward.setImageResource(R.drawable.award_silver_open);
            }else {
                holder.imgAward.setImageResource(R.drawable.award_silver);
            }
            progress = (int) ((float) score / mission.getRequiredScore() * 100);
        } else if (mission.getRequiredStreaks() > 0) {
            holder.imageView.setImageResource(R.drawable.streak);
            if (userMissionIdList.contains(missionId)) {
                holder.imgAward.setImageResource(R.drawable.award_gold_open);
            }else {
                holder.imgAward.setImageResource(R.drawable.award_gold);
            }
            progress = (int) ((float) streaks / mission.getRequiredStreaks() * 100);
        } else if (mission.getRequiredLessons() > 0) {
            if (userMissionIdList.contains(missionId)) {
                holder.imgAward.setImageResource(R.drawable.award_bronze_open);
            }else {
                holder.imgAward.setImageResource(R.drawable.award_bronze);
            }
            progress = (int) ((float) lessons / mission.getRequiredLessons() * 100);
        } else if (mission.getRequiredEightyLessons() > 0) {
            if (userMissionIdList.contains(missionId)) {
                holder.imgAward.setImageResource(R.drawable.award_bronze_open);
            }else {
                holder.imgAward.setImageResource(R.drawable.award_bronze);
            }
            progress = (int) ((float) eightyLessons / mission.getRequiredEightyLessons() * 100);
        } else if (mission.getRequiredPerfectLessons() > 0) {
            if (userMissionIdList.contains(missionId)) {
                holder.imgAward.setImageResource(R.drawable.award_bronze_open);
            }else {
                holder.imgAward.setImageResource(R.drawable.award_bronze);
            }
            progress = (int) ((float) perfectLessons / mission.getRequiredPerfectLessons() * 100);
        } else {
            progress = 0;
        }
        holder.progressBar.setProgress(Math.min(progress, 100));

        holder.btnAward.setOnClickListener(v -> {
            if (userMissionIdList.contains(missionId)) {
                Toast.makeText(context, "Bạn đã nhận thưởng này rồi!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(progress >= 100){
                dialogGetReward(mission, missionId);
            }else {
                Toast.makeText(context, "Hãy hoàn thành nhiệm vụ", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
    private void dialogGetReward(Mission mission, String missionId) {
        // Tạo dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        View dialogView = inflater.inflate(R.layout.dialog_get_reward, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView img = dialogView.findViewById(R.id.imageView);
        TextView tvName = dialogView.findViewById(R.id.tvName);
        Button getRewardButton = dialogView.findViewById(R.id.btnGet);

        if(mission.getRewardType().equals("heart")){
            img.setImageResource(R.drawable.heart);
            tvName.setText("Chúc mừng bạn nhận được " + mission.getRewardAmount() + " tim");
        }else if(mission.getRewardType().equals("diamond")){
            img.setImageResource(R.drawable.gem);
            tvName.setText("Chúc mừng bạn nhận được " + mission.getRewardAmount() + " đá");
        }
        getRewardButton.setOnClickListener(v -> {
            if(mission.getRewardType().equals("heart")){
                int amount = mission.getRewardAmount();
                hearts += amount;
                updateUserHeart(hearts);
            }else if(mission.getRewardType().equals("diamond")){
                int amount = mission.getRewardAmount();
                diamonds += amount;
                updateUserDiamond(diamonds);
            }

            addUserProgress(userId, missionId);
            dialog.dismiss();

            userMissionIdList.add(missionId);
            notifyDataSetChanged();
        });

        dialog.show();
    }
    private void updateUserHeart(int hearts){
        Map<String, Object> field = new HashMap<>();
        field.put("hearts", hearts);
        FirebaseApiService.apiService.updateUserField(userId, field).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Cập nhật tim "+hearts, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Không lấy được tim", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserDiamond(int diamonds){
        Map<String, Object> field = new HashMap<>();
        field.put("diamonds", diamonds);
        FirebaseApiService.apiService.updateUserField(userId, field).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Cập nhật đá "+diamonds, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Không lấy được đá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserProgress(String userId, String missionId) {
        UserProgress newUserProgress = new UserProgress();
        newUserProgress.setUser_id(userId);
        newUserProgress.setMission_id(missionId);

        FirebaseApiService.apiService.addUserProgress(newUserProgress).enqueue(new Callback<UserProgress>() {
            @Override
            public void onResponse(Call<UserProgress> call, Response<UserProgress> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đã thêm tiến trình nhận quà", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Không thể cập nhật", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProgress> call, Throwable t) {
                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

