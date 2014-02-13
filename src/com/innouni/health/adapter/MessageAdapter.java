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
import com.innouni.health.entity.MessageInfo;
import com.innouni.health.net.ExecutorsImageLoader;
import com.innouni.health.net.ExecutorsImageLoader.ImageCallback;

/**
 * 留言适配器
 * 
 * @author HuGuojun
 * @date 2014-2-12 上午11:06:39
 * @modify
 * @version 1.0.0
 */
public class MessageAdapter extends ArrayListAdapter<Object> {

	private static final int TYPE_USER = 0;
	private static final int TYPE_EXPERT = 1;

	private String userImage;
	private String expertImage;

	private LayoutInflater inflater = null;
	private AbsListView absListView;
	private ExecutorsImageLoader exeLoader;

	public MessageAdapter(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
		exeLoader = ExecutorsImageLoader.getInstance(context);
	}

	public void setAbsListView(AbsListView absListView) {
		this.absListView = absListView;
	}

	//在使用ListView时,如果使用了getItemViewType,它的值一定要是从0开始计数,
	//且要覆盖getViewTypeCount方法,
	//并且让getViewTypeCount > getItemViewType
	@Override
	public int getItemViewType(int position) {
		MessageInfo message = (MessageInfo) mList.get(position);
		if ("1".equals(message.getType().toString())) {
			return TYPE_USER;
		} else {
			return TYPE_EXPERT;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String url;
		ViewHolder viewHolder = null;
		int type = getItemViewType(position);
		if (type == TYPE_USER) {
			url = getUserImage();
		} else {
			url = getExpertImage();
		}

		if (null == convertView) {
			viewHolder = new ViewHolder();
			if (type == TYPE_USER) {
				convertView = inflater.inflate(
						R.layout.item_user_message_listview, parent, false);
				viewHolder.headView = (ImageView) convertView
						.findViewById(R.id.image_message_head);
				viewHolder.contentView = (TextView) convertView
						.findViewById(R.id.message_content);
			} else {
				convertView = inflater.inflate(
						R.layout.item_expert_message_listview, parent, false);
				viewHolder.headView = (ImageView) convertView
						.findViewById(R.id.image_message_head);
				viewHolder.contentView = (TextView) convertView
						.findViewById(R.id.message_content);
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		MessageInfo message = (MessageInfo) mList.get(position);
		viewHolder.contentView.setText(message.getContent().toString());
		viewHolder.headView.setTag(position + "_" + url);
		loadRemoteImage(url, viewHolder.headView, position, type);
		return convertView;
	}

	private void loadRemoteImage(String url, final ImageView imageView,
			int pos, int type) {
		final String tag = pos + "_" + url;
		imageView.setTag(tag);
		if (type == TYPE_USER) {
			imageView.setImageResource(R.drawable.user_expert_default);
		} else {
			imageView.setImageResource(R.drawable.user_expert_default);
		}
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
		TextView contentView;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String getExpertImage() {
		return expertImage;
	}

	public void setExpertImage(String expertImage) {
		this.expertImage = expertImage;
	}
	
	public void addItem(MessageInfo message, boolean boo) {
		if (mList != null) {
			mList.add(message);
			if (boo) {
				notifyDataSetChanged();
			}
		}
	}
}
