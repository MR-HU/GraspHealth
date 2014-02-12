package com.innouni.health.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innouni.health.activity.R;
import com.innouni.health.base.ArrayListAdapter;
import com.innouni.health.entity.Diet;

public class DietAdapter extends ArrayListAdapter<Object> {

	private LayoutInflater inflater;

	public DietAdapter(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_diet_list, null);
			holder.nameView = (TextView) convertView
					.findViewById(R.id.txt_diet_name);
			holder.numView = (TextView) convertView
					.findViewById(R.id.txt_diet_num);
			holder.caloyView = (TextView) convertView
					.findViewById(R.id.txt_diet_calory);
			holder.totalView = (TextView) convertView
					.findViewById(R.id.txt_diet_total);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Diet diet = (Diet) mList.get(position);
		holder.nameView.setText(diet.getFoodName().toString());
		holder.numView.setText(diet.getFoodAmount().toString()
				+ diet.getFoodUnitType().toString());
		holder.caloyView.setText(diet.getCalorieIn().toString() + "cal");
		holder.totalView.setText(Double
				.valueOf(diet.getFoodAmount().toString())
				* Double.valueOf(diet.getCalorieIn().toString()) + "");
		return convertView;
	}

	private class ViewHolder {
		TextView nameView;
		TextView numView;
		TextView caloyView;
		TextView totalView;
	}

	public void deleteItem(int index) {
		if (mList != null) {
			mList.remove(index);
			notifyDataSetChanged();
		}
	}
}
