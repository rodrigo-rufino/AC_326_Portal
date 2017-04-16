package com.example.rodri.portal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rodri on 15-Apr-17.
 */

public class CustomAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<User> objects;

    private class ViewHolder {
        TextView userTextView;
        TextView macTextView;
    }

    public CustomAdapter(Context context, ArrayList<User> objects) {
        inflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    public int getCount() {
        return objects.size();
    }

    public User getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.adapter_list_view, null);
            holder.userTextView = (TextView) convertView.findViewById(R.id.user_text_view);
            holder.macTextView = (TextView) convertView.findViewById(R.id.mac_text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.userTextView.setText(objects.get(position).getUsername());
        holder.macTextView.setText(objects.get(position).getMac());
        return convertView;
    }
}