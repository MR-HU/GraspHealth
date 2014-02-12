package com.innouni.health.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.innouni.health.activity.R;
import com.innouni.health.base.ArrayListAdapter;
import com.innouni.health.entity.Opinion;
import com.innouni.health.net.ExecutorsImageLoader;
import com.innouni.health.net.ExecutorsImageLoader.ImageCallback;

/**
 * 指导信息适配器
 * 
 * @author HuGuojun
 * @date 2014-2-12 上午9:18:11
 * @modify
 * @version 1.0.0
 */
public class OpinionAdapter extends ArrayListAdapter<Object> {

	private LayoutInflater inflater = null;
	private AbsListView absListView;
	private ExecutorsImageLoader exeLoader;

	public OpinionAdapter(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
		exeLoader = ExecutorsImageLoader.getInstance(context);
	}

	public void setAbsListView(AbsListView absListView) {
		this.absListView = absListView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Opinion opinion = null;
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.item_online_question_listview, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.headView = (ImageView) convertView
					.findViewById(R.id.image_expert_head);
			viewHolder.nameView = (TextView) convertView
					.findViewById(R.id.txt_expert_name);
			viewHolder.descView = (TextView) convertView
					.findViewById(R.id.txt_expert_description);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		opinion = (Opinion) mList.get(position);
		viewHolder.nameView.setText(opinion.getName());
		viewHolder.descView.setText(opinion.getTitle());

		String url = opinion.getUrl();
		viewHolder.headView.setTag(position + "_" + url);
		loadRemoteImage(url, viewHolder.headView, position);
		return convertView;
	}

	private void loadRemoteImage(String url, final ImageView imageView, int pos) {
		final String tag = pos + "_" + url;
		imageView.setTag(tag);
		imageView.setImageResource(R.drawable.user_expert_default);
		Bitmap map = exeLoader.loadDrawable(url, new ImageCallback() {
			@Override
			public void imageLoaded(Bitmap imageDrawable) {
				ImageView imageView = (ImageView) absListView
						.findViewWithTag(tag);
				if (null != imageDrawable && null != imageView) {
					imageView.setImageBitmap(imageDrawable);
				}
			}
		});
		if (null != map) {
			imageView.setImageBitmap(map);
		}
	}

	static class ViewHolder {
		ImageView headView;
		TextView nameView;
		TextView descView;
	}
}
