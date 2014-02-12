package com.innouni.health.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innouni.health.activity.R;
import com.innouni.health.base.ArrayListAdapter;
import com.innouni.health.entity.HealthNews;

public class HealthNewsAdapter extends ArrayListAdapter<Object> {

	private LayoutInflater inflater;
	
	public HealthNewsAdapter(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.item_cookbook_list, null);
		TextView titleView = (TextView) convertView.findViewById(R.id.txt_title);
		HealthNews news = (HealthNews) mList.get(position);
		titleView.setText(news.getTitle());
		return convertView;
	}

}
