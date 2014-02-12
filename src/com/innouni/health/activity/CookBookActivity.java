package com.innouni.health.activity;

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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innouni.health.adapter.CookBookAdapter;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.Recommend;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.NetUtil;
import com.innouni.health.widget.PullToRefreshView;
import com.innouni.health.widget.PullToRefreshView.OnHeaderRefreshListener;

/**
 * 我的菜谱页面
 * 
 * @author HuGuojun
 * @date 2013-12-30 上午11:49:58
 * @modify
 * @version 1.0.0
 */
public class CookBookActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	private static final int DELETE_ITEM = 1;

	private ProgressDialog dialog;
	private PullToRefreshView refreshView;
	private ListView mListView;
	private RelativeLayout loadFailLayout;
	private ImageView loadFailView;
	private CookBookAdapter adapter;
	private GetCookBookTask task;
	private int index = -1; // 被删除的item的索引
	private DeleteTask deleteTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cookbook);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setOnClickListener(this);
		titleRightBtn.setBackgroundResource(R.drawable.titlebar_add_selector);
		titleContentView.setText("我的菜谱");

		refreshView = (PullToRefreshView) findViewById(R.id.refresh_view);
		mListView = (ListView) findViewById(R.id.my_listview);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterDividersEnabled(false);

		adapter = new CookBookAdapter(this);
		mListView.setAdapter(adapter);
		adapter.setListView(mListView);
		mListView.setOnItemClickListener(this);
		mListView.setOnCreateContextMenuListener(this);

		loadFailLayout = (RelativeLayout) findViewById(R.id.lay_load_fail);
		loadFailView = (ImageView) findViewById(R.id.imageview_load_fail);
		loadFailView.setOnClickListener(this);

		refreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				if (NetUtil.isNetworkAvailable(CookBookActivity.this)) {
					if (task != null) {
						task.cancel(true);
					}
					task = new GetCookBookTask();
					task.execute();
				} else {
					loadFailLayout.setVisibility(View.VISIBLE);
					refreshView.onHeaderRefreshComplete();
				}
			}
		});
		refreshView.headerRefreshing();
	}

	private class GetCookBookTask extends
			AsyncTask<Void, Void, ArrayList<Object>> {

		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected ArrayList<Object> doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			String json = HttpPostRequest.getDataFromWebServer(
					CookBookActivity.this, "getMyFoods", pairs);
			System.out.println("我的菜谱返回: " + json);
			try {
				if (new JSONObject(json).optInt("status") == 0) {
					ArrayList<Object> data = new ArrayList<Object>();
					JSONArray array = new JSONObject(json)
							.optJSONArray("Foods");
					for (int i = 0; i < array.length(); i++) {
						Recommend recommend = new Recommend();
						JSONObject object = array.optJSONObject(i);
						recommend.setId(object.opt("foodId"));
						recommend.setTitle(object.opt("foodName"));
						recommend.setAddTime(object.opt("addTime"));
						recommend.setCalory(object.opt("food_cal"));
						data.add(recommend);
					}
					return data;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Object> result) {
			dialog.dismiss();
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
		dialog = new ProgressDialog(CookBookActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(getResources().getString(R.string.load_loading_data));
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
			pairs.add(new BasicNameValuePair("foodId", params[0]));
			String json = HttpPostRequest.getDataFromWebServer(
					CookBookActivity.this, "delMyFoods", pairs);
			System.out.println("删除我的菜谱 : " + json);
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Recommend recommend = (Recommend) adapter.getItem(position);
		Intent intent = new Intent(CookBookActivity.this,
				DishDetailActivity.class);
		intent.putExtra("foodId", recommend.getId().toString());
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			Intent intent = new Intent(CookBookActivity.this,
					AddCookBookActivity.class);
			startActivity(intent);
			break;
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
		Recommend recommend = (Recommend) adapter.getItem(index);
		String foodId = recommend.getId().toString();
		switch (item.getItemId()) {
		case DELETE_ITEM:
			if (deleteTask != null) {
				deleteTask.cancel(true);
			}
			deleteTask = new DeleteTask();
			deleteTask.execute(foodId);
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
