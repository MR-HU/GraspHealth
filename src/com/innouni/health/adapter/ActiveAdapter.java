package com.innouni.health.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innouni.health.activity.AddSportStepOneActivity;
import com.innouni.health.activity.R;
import com.innouni.health.base.ArrayListAdapter;
import com.innouni.health.entity.Activite;

/**
 * 运动源运动列表适配器
 * 
 * @author HuGuojun
 * @date 2014-1-14 下午4:11:14
 * @modify
 * @version 1.0.0
 */
public class ActiveAdapter extends ArrayListAdapter<Object> {

	private LayoutInflater inflater;
	private int type = 0;

	public ActiveAdapter(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_sportsrc_list, null);
			holder.numView = (TextView) convertView
					.findViewById(R.id.txt_foodsrc_num);
			holder.calView = (TextView) convertView
					.findViewById(R.id.txt_foodsrc_calory);
			holder.operationView = (TextView) convertView
					.findViewById(R.id.txt_foodsrc_operation);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Activite activite = (Activite) mList.get(position);
		holder.numView.setText(activite.getActiveName().toString());
		holder.calView.setText(activite.getActiveSubTypeName().toString());
		holder.operationView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (operateListener != null) {
					operateListener.onOperate(position);
				}
			}
		});
		if (type == AddSportStepOneActivity.PUSH) {
			holder.operationView.setVisibility(View.VISIBLE);
		} else {
			holder.operationView.setVisibility(View.GONE);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView numView;
		TextView calView;
		TextView operationView;
	}

	public interface OnOperateListener {
		void onOperate(int position);
	}

	private OnOperateListener operateListener;

	public void setOnOperateListener(OnOperateListener operateListener) {
		this.operateListener = operateListener;
	}

}
