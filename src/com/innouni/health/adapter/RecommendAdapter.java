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
 * 我的推荐列表适配器
 * 
 * @author HuGuojun
 * @date 2013-12-30 下午3:40:45
 * @modify
 * @version 1.0.0
 */
public class RecommendAdapter extends ArrayListAdapter<Object> {

	private LayoutInflater inflater;

	public RecommendAdapter(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_recommend_list, null);
			holder.titleView = (TextView) convertView
					.findViewById(R.id.txt_title);
			holder.contentView = (TextView) convertView
					.findViewById(R.id.txt_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Recommend recommend = (Recommend) mList.get(position);
		holder.titleView.setText(recommend.getTitle().toString());
		holder.contentView.setText(recommend.getContent().toString());
		return convertView;
	}

	private class ViewHolder {
		TextView titleView;
		TextView contentView;
	}
}
