package com.example.meowapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.model.Language;
import com.example.meowapp.model.LanguagePreference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguagePreferenceAdapter extends BaseAdapter {
    private Context context;
    private List<Pair<String, LanguagePreference>> list;
    private Map<String, String> languageNameCache = new HashMap<>();

    public LanguagePreferenceAdapter(Context context, List<Pair<String, LanguagePreference>> list) {
        this.context = context;
        this.list = list;
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
        private TextView tvScore, tvLanguage;
        private ImageButton btnDelete;

        public ViewHolder(View view) {
            tvLanguage = view.findViewById(R.id.tvLanguage);
            tvScore = view.findViewById(R.id.tvScore);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_settings_course, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Pair<String, LanguagePreference> pair = list.get(position);
        String languagePreferenceId = pair.first;
        LanguagePreference languagePreference = pair.second;

        if (languagePreference.getLanguage_id() != null && !languagePreference.getLanguage_id().isEmpty()) {
            holder.tvLanguage.setText(languagePreference.getLanguage_id());
        } else {
            holder.tvLanguage.setText("Unknown Language");
        }
        holder.tvScore.setText(" Điểm: " + languagePreference.getLanguage_score());
        String languageId = languagePreference.getLanguage_id();

        // Kiểm tra cache trước khi gọi API
        if (languageNameCache.containsKey(languageId)) {
            holder.tvLanguage.setText(languageNameCache.get(languageId));
        } else {
            FirebaseApiService.apiService.getLanguageById(languageId).enqueue(new Callback<Language>() {
                @Override
                public void onResponse(Call<Language> call, Response<Language> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String languageName = response.body().getLanguage_name();
                        languageNameCache.put(languageId, languageName);
                        holder.tvLanguage.setText(languageName);
                    } else {
                        Log.e("LanguageApi", "Failed to get language data");
                    }
                }

                @Override
                public void onFailure(Call<Language> call, Throwable t) {
                    Log.e("LanguageApi", "Error fetching language data", t);
                }
            });
        }

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa ngôn ngữ")
                    .setMessage("Bạn có chắc chắn muốn xóa ngôn ngữ này không?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteLanguagePreference(languagePreferenceId, position))
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        return convertView;
    }

    private void deleteLanguagePreference(String languagePreferenceId, int position) {
        FirebaseApiService.apiService.deleteLanguagePreference(languagePreferenceId).enqueue(new Callback<LanguagePreference>() {
            @Override
            public void onResponse(Call<LanguagePreference> call, Response<LanguagePreference> response) {
                if (response.isSuccessful()) {
                    list.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<LanguagePreference> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

