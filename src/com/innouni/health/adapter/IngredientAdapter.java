package com.innouni.health.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innouni.health.activity.R;
import com.innouni.health.base.ArrayListAdapter;
import com.innouni.health.entity.Ingredient;

/**
 * 食材联想列表适配器
 * 
 * @author HuGuojun
 * @date 2014-1-10 下午5:18:33
 * @modify
 * @version 1.0.0
 */
public class IngredientAdapter extends ArrayListAdapter<Object> {

	private LayoutInflater inflater;

	public IngredientAdapter(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_ingredient_list, null);
			holder.titleView = (TextView) convertView
					.findViewById(R.id.txt_ingredient_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Ingredient ingredient = (Ingredient) mList.get(position);
		holder.titleView.setText(ingredient.getName());
		return convertView;
	}

	private class ViewHolder {
		TextView titleView;
	}
}
