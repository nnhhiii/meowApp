package com.example.meowapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.meowapp.R;
import com.example.meowapp.model.Language;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class SelectLanguageAdapter extends BaseAdapter {
    private Context context;
    private List<Map.Entry<String, Language>> list;
    private int selectedPosition = -1;

    public SelectLanguageAdapter(Context context, List<Map.Entry<String, Language>> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Map.Entry<String, Language> getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_select_language, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map.Entry<String, Language> currentEntry = getItem(position);
        Language language = currentEntry.getValue();
        String languageKey = currentEntry.getKey();

        holder.tvName.setText(language.getLanguage_name());
        if (language.getLanguage_image() != null) {
            Picasso.get().load(language.getLanguage_image()).into(holder.imageView);
        }
        holder.cardView.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selected_language_id", languageKey);
            editor.apply();

            selectedPosition = position;
            notifyDataSetChanged();
        });

        // Đổi màu nền dựa trên vị trí được chọn
        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#EFB0C9"));
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE); // Màu nền mặc định
        }
        return convertView;
    }

    public static class ViewHolder {
        private final TextView tvName;
        private final ImageView imageView;
        private CardView cardView;

        public ViewHolder(View view) {
            tvName = view.findViewById(R.id.tvName);
            imageView = view.findViewById(R.id.image);
            cardView = view.findViewById(R.id.cardView);
        }
    }
}