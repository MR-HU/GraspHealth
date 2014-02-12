package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innouni.health.adapter.RecommendAdapter;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.Recommend;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.NetUtil;
import com.innouni.health.widget.PullToRefreshView;
import com.innouni.health.widget.PullToRefreshView.OnHeaderRefreshListener;

/**
 * 我的推荐页面
 * 
 * @author HuGuojun
 * @date 2013-12-26 下午12:10:55
 * @modify
 * @version 1.0.0
 */
public class RecommendActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	private ProgressDialog dialog;
	private PullToRefreshView refreshView;
	private ListView mListView;
	private RelativeLayout loadFailLayout;
	private ImageView loadFailView;
	private RecommendAdapter adapter;
	private GetRecommendTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommend);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRefreshBar = (ProgressBar) findViewById(R.id.progress_title_right);
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);
		titleContentView.setText("我的推荐");

		refreshView = (PullToRefreshView) findViewById(R.id.refresh_view);
		mListView = (ListView) findViewById(R.id.my_listview);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterDividersEnabled(false);

		adapter = new RecommendAdapter(this);
		mListView.setAdapter(adapter);
		adapter.setListView(mListView);
		mListView.setOnItemClickListener(this);

		loadFailLayout = (RelativeLayout) findViewById(R.id.lay_load_fail);
		loadFailView = (ImageView) findViewById(R.id.imageview_load_fail);
		loadFailView.setOnClickListener(this);

		refreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				if (NetUtil.isNetworkAvailable(RecommendActivity.this)) {
					if (task != null) {
						task.cancel(true);
					}
					task = new GetRecommendTask();
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Recommend recommend = (Recommend) adapter.getItem(position);
		Intent intent = new Intent(RecommendActivity.this,
				RecommendDetailActivity.class);
		intent.putExtra("recommendId", recommend.getId().toString());
		startActivity(intent);
	}

	private class GetRecommendTask extends
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
					RecommendActivity.this, "getMyPush", pairs);
			System.out.println("获取我的推荐返回: " + json);
			try {
				if (new JSONObject(json).optInt("status") == 0) {
					ArrayList<Object> data = new ArrayList<Object>();
					JSONArray array = new JSONObject(json)
							.optJSONArray("Recommends");
					for (int i = 0; i < array.length(); i++) {
						Recommend recommend = new Recommend();
						JSONObject object = array.optJSONObject(i);
						recommend.setId(object.opt("id"));
						recommend.setTitle(object.opt("title"));
						recommend.setContent(object.opt("context"));
						recommend.setAddTime(object.opt("addTime"));
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
		dialog = new ProgressDialog(RecommendActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(getResources().getString(R.string.load_loading_data));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.imageview_load_fail:
			mListView.setEnabled(false);
			refreshView.headerRefreshing();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
	}
}
