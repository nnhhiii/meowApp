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

public class SelectLanguageAdapter extends BaseAdapter {
    private Context context;
    private List<Language> list;


    public SelectLanguageAdapter(Context context, List<Language> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public static class ViewHolder{
        TextView tvName;
        ImageView imageView;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView ==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_language, parent, false);

            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.imageView = convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else{
            holder = (ViewHolder) convertView.getTag();
        }

        Language language = list.get(position);
        holder.tvName.setText(language.getLanguage_name());
        if(language.getLanguage_image() != null){
            Picasso.get().load(language.getLanguage_image()).into(holder.imageView);
        }
        return convertView;
    }
}
