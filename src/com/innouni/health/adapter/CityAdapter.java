package com.innouni.health.adapter;

import java.util.List;

import com.innouni.health.activity.R;
import com.innouni.health.entity.City;
import com.innouni.health.entity.Province;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Spinner(省 市)下拉列表适配器
 * 
 * @author HuGuojun
 * @date 2013-12-24 上午11:44:17
 * @modify
 * @version 1.0.0
 */
public class CityAdapter extends BaseAdapter {
	
	public static final int TYPE_PROVINCE = 0;
	public static final int TYPE_CITY = 1;

	private int type;
	private List<Object> list;
	private LayoutInflater inflater;

	public CityAdapter(Context context, List<Object> list, int type) {
		this.list = list;
		this.type = type;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.item_city_spinner, null);
		TextView nameView = (TextView) convertView
				.findViewById(R.id.txt_city_name);
		if (type == TYPE_PROVINCE) {
			Province province = (Province) list.get(position);
			nameView.setText(province.getName());
		} else {
			City city = (City) list.get(position);
			nameView.setText(city.getName());
		}
		return convertView;
	}

}
