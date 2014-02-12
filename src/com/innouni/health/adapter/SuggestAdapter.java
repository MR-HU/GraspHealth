package com.innouni.health.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innouni.health.activity.R;
import com.innouni.health.base.ArrayListAdapter;
import com.innouni.health.entity.Suggest;

public class SuggestAdapter extends ArrayListAdapter<Object> {

	private LayoutInflater inflater;

	public SuggestAdapter(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_suggest_list, null);
			holder.titleView = (TextView) convertView
					.findViewById(R.id.txt_suggest_title);
			holder.timeView = (TextView) convertView
					.findViewById(R.id.txt_suggest_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Suggest suggest = (Suggest) mList.get(position);
		holder.titleView.setText(suggest.getTitle());
		holder.timeView.setText(suggest.getTime());
		return convertView;
	}

	private class ViewHolder {
		TextView titleView;
		TextView timeView;
	}
}
