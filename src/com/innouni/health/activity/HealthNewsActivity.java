package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.innouni.health.adapter.HealthNewsAdapter;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.HealthNews;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.NetUtil;
import com.innouni.health.widget.PullToRefreshView;
import com.innouni.health.widget.PullToRefreshView.OnHeaderRefreshListener;

/**
 * 健康资讯页面
 * 
 * @author HuGuojun
 * @date 2014-2-11 下午2:01:29
 * @modify
 * @version 1.0.0
 */
public class HealthNewsActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	private PullToRefreshView refreshView;
	private ListView mListView;
	private View footView;
	private ViewSwitcher switcherView;
	private RelativeLayout loadFailLayout;
	private ImageView loadFailView;
	private HealthNewsAdapter adapter;
	private GetMarketNewsTask task;
	private int currentPage = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_health_news);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("健康资讯");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		refreshView = (PullToRefreshView) findViewById(R.id.refresh_view);
		mListView = (ListView) findViewById(R.id.my_listview);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterDividersEnabled(false);
		footView = getLayoutInflater().inflate(R.layout.app_loadmore_layout,
				null);
		switcherView = (ViewSwitcher) footView.findViewById(R.id.switcher_view);
		mListView.addFooterView(footView, null, false);
		switcherView.setVisibility(View.GONE);
		switcherView.setOnClickListener(this);

		adapter = new HealthNewsAdapter(this);
		mListView.setAdapter(adapter);
		adapter.setListView(mListView);
		mListView.setOnItemClickListener(this);

		loadFailLayout = (RelativeLayout) findViewById(R.id.lay_load_fail);
		loadFailView = (ImageView) findViewById(R.id.imageview_load_fail);
		loadFailView.setOnClickListener(this);

		refreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				currentPage = 0;
				if (NetUtil.isNetworkAvailable(HealthNewsActivity.this)) {
					if (null != task)
						task.cancel(true);
					task = new GetMarketNewsTask();
					task.execute();
				} else {
					loadFailLayout.setVisibility(View.VISIBLE);
					refreshView.onHeaderRefreshComplete();
				}
			}
		});

		refreshView.headerRefreshing();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switcher_view:
			if (NetUtil.isNetworkAvailable(this)) {
				switcherView.showNext();
				if (null != task) {
					task.cancel(true);
				}
				task = new GetMarketNewsTask();
				task.execute();
			} else {
				showToast(R.string.net_error);
			}
			break;
		case R.id.imageview_load_fail:
			mListView.setEnabled(false);
			refreshView.headerRefreshing();
			break;
		case R.id.btn_title_left:
			finish();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		HealthNews news = (HealthNews) adapter.getItem(position);
		Intent intent = new Intent(HealthNewsActivity.this,
				HealthNewsDetailActivity.class);
		intent.putExtra("id", news.getId());
		intent.putExtra("title", news.getTitle());
		startActivity(intent);
	}

	private class GetMarketNewsTask extends
			AsyncTask<String, Void, ArrayList<Object>> {

		@Override
		protected void onPreExecute() {
			currentPage += 1;
			switcherView.setEnabled(false);
		}

		@Override
		protected ArrayList<Object> doInBackground(String... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("curpage", String
					.valueOf(currentPage)));
			pairs.add(new BasicNameValuePair("percount", String.valueOf(8)));
			String json = HttpPostRequest.getDataFromWebServer(
					HealthNewsActivity.this, "getJKNews", pairs);
			System.out.println("请求健康资讯返回: " + json);
			ArrayList<Object> list = null;
			try {
				JSONObject jsonObject = new JSONObject(json);
				if (jsonObject.optInt("status") == 0) {
					list = new ArrayList<Object>();
					JSONArray array = jsonObject.optJSONArray("Lists");
					for (int i = 0; i < array.length(); i++) {
						HealthNews news = new HealthNews();
						JSONObject object = array.optJSONObject(i);
						news.setId(object.optString("jknewsId"));
						news.setTitle(object.optString("jknewsTitle"));
						news.setAddTime(object.optString("jknewsAddtime"));
						list.add(news);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(ArrayList<Object> result) {
			task = null;
			if (null != result) {
				mListView.setEnabled(true);
				loadFailLayout.setVisibility(View.GONE);
				if (result.size() > 0) {
					if (currentPage == 1 && null != adapter) {
						adapter.clear();
					}
					if (result.size() < 8) {
						switcherView.setVisibility(View.GONE);
					} else {
						switcherView.setVisibility(View.VISIBLE);
						switcherView.setDisplayedChild(0);
					}
					adapter.setList(result, true);
				} else {
					switcherView.setVisibility(View.GONE);
				}
			}
			switcherView.setEnabled(true);
			if (currentPage == 1) {
				refreshView.onHeaderRefreshComplete();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (null != task) {
			task.cancel(true);
			refreshView.onHeaderRefreshComplete();
		}
	}

}
