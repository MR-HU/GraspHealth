package com.innouni.health.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innouni.health.activity.R;
import com.innouni.health.base.ArrayListAdapter;
import com.innouni.health.entity.Sport;
import com.innouni.health.fragment.SportListFragment;

/**
 * 运动列表适配器
 * 
 * @author HuGuojun
 * @date 2014-1-17 上午9:17:39
 * @modify
 * @version 1.0.0
 */
public class SportAdapter extends ArrayListAdapter<Object> {

	private LayoutInflater inflater;

	public SportAdapter(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_sport_list, null);
			holder.typeView = (TextView) convertView
					.findViewById(R.id.txt_sport_type);
			holder.nameView = (TextView) convertView
					.findViewById(R.id.txt_sport_name);
			holder.minuteView = (TextView) convertView
					.findViewById(R.id.txt_sport_minute);
			holder.caloryView = (TextView) convertView
					.findViewById(R.id.txt_sport_calory);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Sport sport = (Sport) mList.get(position);
		int type = Integer.valueOf(sport.getSportType().toString());
		if (type == SportListFragment.NORMAL_SPORT) {
			holder.typeView.setText(sport.getName().toString());
			holder.nameView.setText(sport.getTypeName().toString());
			holder.minuteView.setText(sport.getTime().toString()
					+ sport.getUnit().toString());
			holder.caloryView.setText(sport.getCalory().toString());
		} else if (type == SportListFragment.GOTO_WORK) {
			holder.typeView.setText("上班");
			holder.nameView.setText(sport.getName().toString()
					+ sport.getTypeName().toString());
			holder.minuteView.setText(sport.getTime().toString()
					+ sport.getUnit().toString());
			holder.caloryView.setText(sport.getCalory().toString());
		} else {
			holder.typeView.setText("下班");
			holder.nameView.setText(sport.getName().toString()
					+ sport.getTypeName().toString());
			holder.minuteView.setText(sport.getTime().toString()
					+ sport.getUnit().toString());
			holder.caloryView.setText(sport.getCalory().toString());
		}
		return convertView;
	}

	private class ViewHolder {
		TextView typeView;
		TextView nameView;
		TextView minuteView;
		TextView caloryView;
	}

	public void deleteItem(int index) {
		if (mList != null) {
			mList.remove(index);
			notifyDataSetChanged();
		}
	}
}
