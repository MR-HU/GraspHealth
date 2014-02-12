package com.innouni.health.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

/**
 * ListView的适配器的基类 <br>
 * 对ArrayList的抽象封装
 * 
 * @author HuGuojun
 * @date 2013-12-26 下午5:12:41
 * @modify
 * @version 1.0.0
 */
public abstract class ArrayListAdapter<T> extends BaseAdapter {

	protected List<T> mList = new ArrayList<T>();
	protected Context mContext;
	protected AbsListView mListView;

	public ArrayListAdapter(Context context) {
		this.mContext = context;
	}

	/**
	 * 清除列表中的数据
	 */
	public void clear() {
		mList.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	abstract public View getView(int position, View convertView,
			ViewGroup parent);

	public void setList(List<T> list, boolean boo) {
		this.mList.addAll(list);
		if (boo) {
			notifyDataSetChanged();
		}
	}

	public void setListToFirst(List<T> list, boolean boo) {
		this.mList.addAll(0, list);
		if (boo) {
			notifyDataSetChanged();
		}
	}

	public List<T> getList() {
		return mList;
	}

	public void setList(T[] list, boolean boo) {
		ArrayList<T> arrayList = new ArrayList<T>(list.length);
		for (T t : list) {
			arrayList.add(t);
		}
		setList(arrayList, boo);
	}

	public AbsListView getListView() {
		return mListView;
	}

	public void setListView(AbsListView listView) {
		mListView = listView;
	}

}
