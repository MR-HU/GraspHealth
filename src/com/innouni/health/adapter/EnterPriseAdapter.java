package com.innouni.health.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innouni.health.activity.R;
import com.innouni.health.base.ArrayListAdapter;
import com.innouni.health.entity.EnterPrise;

/**
 * 食物源企业列表适配器
 * 
 * @author HuGuojun
 * @date 2014-1-14 下午4:09:00
 * @modify
 * @version 1.0.0
 */
public class EnterPriseAdapter extends ArrayListAdapter<Object> {

	private LayoutInflater inflater;

	public EnterPriseAdapter(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_enterprise_list, null);
			holder.nameView = (TextView) convertView
					.findViewById(R.id.txt_enter_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		EnterPrise enterPrise = (EnterPrise) mList.get(position);
		holder.nameView.setText(enterPrise.getName());
		return convertView;
	}

	private class ViewHolder {
		TextView nameView;
	}
}
