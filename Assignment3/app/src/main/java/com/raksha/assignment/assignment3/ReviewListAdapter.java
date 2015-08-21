package com.raksha.assignment.assignment3;

/**
 * Created by Raksha on 3/7/2015.
 */
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReviewListAdapter extends BaseAdapter {
    public static ArrayList<Review> reviews;

    private LayoutInflater mInflater;

    public ReviewListAdapter(Context context, ArrayList<Review> results) {
        reviews = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return reviews.size();
    }

    public Object getItem(int position) {
        return reviews.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_layout, null);

            holder = new ViewHolder();
            holder.review = (TextView) convertView.findViewById(R.id.review);
            holder.date = (TextView) convertView.findViewById(R.id.date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.review.setText(reviews.get(position).getComment());
        holder.date.setText(reviews.get(position).getDate());

        return convertView;
    }

    static class ViewHolder {
        TextView review;
        TextView date;
    }
}