package com.example.meowapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meowapp.R;
import com.example.meowapp.model.Language;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class SelectLanguageAdapter extends BaseAdapter {

    private Context context;
    private List<Map.Entry<String, Language>> languageEntryList;

    public SelectLanguageAdapter(Context context, List<Map.Entry<String, Language>> languageEntryList) {
        this.context = context;
        this.languageEntryList = languageEntryList;
    }

    @Override
    public int getCount() {
        return languageEntryList.size();
    }

    @Override
    public Map.Entry<String, Language> getItem(int position) {
        return languageEntryList.get(position);
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
        holder.tvName.setText(language.getLanguage_name());
        if (language.getLanguage_image() != null) {
            Picasso.get().load(language.getLanguage_image()).into(holder.imageView);
        }
        return convertView;
    }

    public static class ViewHolder {
        private final TextView tvName;
        private final ImageView imageView;

        public ViewHolder(View view) {
            tvName = view.findViewById(R.id.tvName);
            imageView = view.findViewById(R.id.image);
        }
    }
}