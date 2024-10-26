package com.example.meowapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.meowapp.R;
import com.example.meowapp.api.FirebaseApiService;
import com.example.meowapp.language.LanguageEditActivity;
import com.example.meowapp.model.Language;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguageManagementAdapter extends BaseAdapter {
    private Context context;
    private List<Pair<String, Language>> list; // Pair chứa key và Language

    public LanguageManagementAdapter(Context context, List<Pair<String, Language>> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position).second; // Trả về Language object
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        TextView tvName, tvCreatedAt, tvUpdatedAt;
        ImageView imageView;
        CardView cardView;
        ImageButton btnDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lang_management, parent, false);

            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvCreatedAt = convertView.findViewById(R.id.tvCreatedAt);
            holder.tvUpdatedAt = convertView.findViewById(R.id.tvUpdatedAt);
            holder.imageView = convertView.findViewById(R.id.image);
            holder.cardView = convertView.findViewById(R.id.cardView);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Pair<String, Language> pair = list.get(position);
        Language language = pair.second;
        String languageId = pair.first;

        holder.tvName.setText(language.getLanguage_name());
        holder.tvCreatedAt.setText(language.getCreated_at());
        holder.tvUpdatedAt.setText(language.getUpdated_at());
        if (language.getLanguage_image() != null) {
            Picasso.get().load(language.getLanguage_image()).into(holder.imageView);
        }

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LanguageEditActivity.class);
            intent.putExtra("languageId", languageId);
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Product")
                    .setMessage("Are you sure you want to delete this product?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteLanguage(languageId, position))
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });
        return convertView;
    }

    private void deleteLanguage(String languageId, int position) {
        // Gọi API để xóa ngôn ngữ
        FirebaseApiService.apiService.deleteLanguage(languageId).enqueue(new Callback<Language>() {
            @Override
            public void onResponse(Call<Language> call, Response<Language> response) {
                if (response.isSuccessful()) {
                    list.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Language> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
