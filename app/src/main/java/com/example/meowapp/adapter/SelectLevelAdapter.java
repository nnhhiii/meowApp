package com.example.meowapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.meowapp.R;
import com.example.meowapp.model.Level;

import java.util.List;
import java.util.Map;

public class SelectLevelAdapter extends BaseAdapter {

    private Context context;
    private List<Map.Entry<String, Level>> levelEntryLevel;

    public SelectLevelAdapter(Context context, List<Map.Entry<String, Level>> levelEntryLevel) {
        this.context = context;
        this.levelEntryLevel = levelEntryLevel;
    }

    @Override
    public int getCount() {
        return levelEntryLevel.size();
    }

    @Override
    public Map.Entry<String, Level> getItem(int position) {
        return levelEntryLevel.get(position);
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
                    .inflate(R.layout.item_select_level, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Map.Entry<String, Level> currentEntry = getItem(position);
        holder.tvLevel.setText(currentEntry.getValue().getLevel_name());
        return convertView;
    }

    public static class ViewHolder {
        private final TextView tvLevel;

        public ViewHolder(View view) {
            tvLevel = view.findViewById(R.id.tvLevel);
        }
    }
}