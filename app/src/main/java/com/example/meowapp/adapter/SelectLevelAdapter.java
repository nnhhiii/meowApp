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

public class SelectLevelAdapter extends BaseAdapter {
    private Context context;
    private List<Level> list;

    public SelectLevelAdapter(Context context, List<Level> list) {
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
        TextView tvCB;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SelectLevelAdapter.ViewHolder holder;
        if (convertView ==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_level, parent, false);

            holder = new SelectLevelAdapter.ViewHolder();
            holder.tvCB = convertView.findViewById(R.id.tvCB);
            convertView.setTag(holder);
        } else{
            holder = (SelectLevelAdapter.ViewHolder) convertView.getTag();
        }

        Level level = list.get(position);
        holder.tvCB.setText(level.getLevel_name());
        return convertView;
    }
}
