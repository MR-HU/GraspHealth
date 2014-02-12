package com.innouni.health.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innouni.health.activity.R;
import com.innouni.health.base.ArrayListAdapter;
import com.innouni.health.entity.Collectiom;

/**
 * 我的收藏列表适配器
 * 
 * @author HuGuojun
 * @date 2013-12-30 下午3:42:31
 * @modify
 * @version 1.0.0
 */
public class CollectionAdapter extends ArrayListAdapter<Object> {

	public static final int TYPE_DISH = 0;
	public static final int TYPE_SPORT = 1;

	private LayoutInflater inflater;
	private int type;

	public CollectionAdapter(Context context, int type) {
		super(context);
		inflater = LayoutInflater.from(context);
		this.type = type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_collection_list, null);
			holder.titleView = (TextView) convertView
					.findViewById(R.id.txt_title);
			holder.contentView = (TextView) convertView
					.findViewById(R.id.txt_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Collectiom collectiom = (Collectiom) mList.get(position);
		if (type == TYPE_DISH) {
			holder.titleView.setText(collectiom.getFoodName() == null ? ""
					: collectiom.getFoodName().toString());
			holder.contentView.setText(collectiom.getFoodName() == null ? ""
					: collectiom.getFoodName().toString());
		} else {
			holder.titleView.setText(collectiom.getActiveName() == null ? ""
					: collectiom.getActiveName().toString());
			holder.contentView
					.setText(collectiom.getActiveSubTypeName() == null ? ""
							: collectiom.getActiveSubTypeName().toString());
		}
		return convertView;
	}

	private class ViewHolder {
		TextView titleView;
		TextView contentView;
	}

	public void deleteItem(int index) {
		if (mList != null) {
			mList.remove(index);
			notifyDataSetChanged();
		}
	}
}
