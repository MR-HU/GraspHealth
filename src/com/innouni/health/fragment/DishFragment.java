package com.innouni.health.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.innouni.health.activity.DishDetailActivity;
import com.innouni.health.activity.R;
import com.innouni.health.adapter.CollectionAdapter;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseFragment;
import com.innouni.health.entity.Collectiom;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.NetUtil;
import com.innouni.health.widget.PullToRefreshView;
import com.innouni.health.widget.PullToRefreshView.OnHeaderRefreshListener;

/**
 * 我的收藏中的菜品
 * 
 * @author HuGuojun
 * @date 2013-12-30 下午12:11:39
 * @modify
 * @version 1.0.0
 */
public class DishFragment extends BaseFragment implements OnClickListener,
		OnItemClickListener {

	private static final int DELETE_ITEM = 1;

	private View view;
	private ProgressDialog dialog;
	private PullToRefreshView refreshView;
	private ListView mListView;
	private RelativeLayout loadFailLayout;
	private ImageView loadFailView;
	private CollectionAdapter adapter;
	private GetCollectionTask task;
	private boolean isLoaded = false;
	private ArrayList<Object> data = new ArrayList<Object>();
	private int index = -1; // 被删除的item的索引
	private DeleteTask deleteTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_dish, container, false);
		application = MainApplication.getApplication();
		initView();
		return view;
	}

	private void initView() {
		refreshView = (PullToRefreshView) view.findViewById(R.id.refresh_view);
		mListView = (ListView) view.findViewById(R.id.my_listview);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterDividersEnabled(false);

		adapter = new CollectionAdapter(getActivity(),
				CollectionAdapter.TYPE_DISH);
		mListView.setAdapter(adapter);
		adapter.setListView(mListView);
		mListView.setOnItemClickListener(this);
		mListView.setOnCreateContextMenuListener(this);

		loadFailLayout = (RelativeLayout) view.findViewById(R.id.lay_load_fail);
		loadFailView = (ImageView) view.findViewById(R.id.imageview_load_fail);
		loadFailView.setOnClickListener(this);

		refreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				if (NetUtil.isNetworkAvailable(getActivity())) {
					if (task != null) {
						task.cancel(true);
					}
					task = new GetCollectionTask();
					task.execute();
				} else {
					loadFailLayout.setVisibility(View.VISIBLE);
					refreshView.onHeaderRefreshComplete();
				}
			}
		});
		if (!isLoaded) {
			refreshView.headerRefreshing();
		} else {
			adapter.setList(data, true);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Collectiom collectiom = (Collectiom) adapter.getItem(position);
		Intent intent = new Intent(getActivity(), DishDetailActivity.class);
		intent.putExtra("foodId", collectiom.getFoodId().toString());
		startActivity(intent);
	}

	private class GetCollectionTask extends
			AsyncTask<Void, Void, ArrayList<Object>> {

		@Override
		protected void onPreExecute() {
			if (data != null && data.size() > 0) {
				data.clear();
			}
		}

		@Override
		protected ArrayList<Object> doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			String json = HttpPostRequest.getDataFromWebServer(getActivity(),
					"getCollectFood", pairs);
			System.out.println("我的收藏菜品返回 : " + json);
			try {
				int status = new JSONObject(json).optInt("status");
				if (status == 0) {
					JSONArray array = new JSONObject(json)
							.optJSONArray("Foods");
					for (int i = 0; i < array.length(); i++) {
						Collectiom collectiom = new Collectiom();
						JSONObject object = array.optJSONObject(i);
						collectiom.setId(object.opt("id"));
						collectiom.setFoodId(object.opt("foodId"));
						collectiom.setFoodName(object.opt("foodName"));
						collectiom.setAddTime(object.opt("addTime"));
						collectiom.setCalory(object.opt("food_cal"));
						data.add(collectiom);
					}
				}
				return data;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Object> result) {
			isLoaded = true;
			task = null;
			if (result != null) {
				mListView.setEnabled(true);
				loadFailLayout.setVisibility(View.GONE);
				if (result.size() > 0) {
					adapter.clear();
					adapter.setList(result, true);
				}
			} else {
				showToast(R.string.net_error);
			}
			refreshView.onHeaderRefreshComplete();
		}

	}

	private void showDialog() {
		dialog = new ProgressDialog(getActivity());
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(getResources().getString(R.string.net_deleting));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}

	private class DeleteTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected String doInBackground(String... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("delType", "0"));
			pairs.add(new BasicNameValuePair("id", params[0]));
			String json = HttpPostRequest.getDataFromWebServer(getActivity(),
					"delCollect", pairs);
			System.out.println("删除我的收藏中的菜品 : " + json);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			deleteTask = null;
			try {
				int status = new JSONObject(result).optInt("status");
				if (status == 0) {
					showToast(R.string.net_delete_success);
					adapter.deleteItem(index);
				} else {
					showToast(R.string.net_delete_fail);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showToast(R.string.net_error);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageview_load_fail:
			mListView.setEnabled(false);
			refreshView.headerRefreshing();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, DELETE_ITEM, 0, "删除");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		index = (int) adapter.getItemId(info.position);
		Collectiom collectiom = (Collectiom) adapter.getItem(index);
		String collectionId = collectiom.getId().toString();
		switch (item.getItemId()) {
		case DELETE_ITEM:
			if (deleteTask != null) {
				deleteTask.cancel(true);
			}
			deleteTask = new DeleteTask();
			deleteTask.execute(collectionId);
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onPause() {
		super.onPause();
		refreshView.onHeaderRefreshComplete();
		if (task != null) {
			task.cancel(true);
		}
		if (deleteTask != null) {
			deleteTask.cancel(true);
		}
	}
}
