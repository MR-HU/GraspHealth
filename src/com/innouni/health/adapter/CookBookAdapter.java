package com.innouni.health.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innouni.health.activity.R;
import com.innouni.health.base.ArrayListAdapter;
import com.innouni.health.entity.Recommend;

/**
 * 我的菜谱列表适配器
 * 
 * @author HuGuojun
 * @date 2014-1-10 下午2:52:14
 * @modify
 * @version 1.0.0
 */
public class CookBookAdapter extends ArrayListAdapter<Object> {

	private LayoutInflater inflater;

	public CookBookAdapter(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_cookbook_list, null);
			holder.titleView = (TextView) convertView
					.findViewById(R.id.txt_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Recommend recommend = (Recommend) mList.get(position);
		holder.titleView.setText(recommend.getTitle().toString());
		return convertView;
	}

	private class ViewHolder {
		TextView titleView;
	}

	public void deleteItem(int index) {
		if (mList != null) {
			mList.remove(index);
			notifyDataSetChanged();
		}
	}
}
