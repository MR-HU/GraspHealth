package com.innouni.health.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.innouni.health.activity.R;
import com.innouni.health.activity.SuggestDetailActivity;
import com.innouni.health.adapter.SuggestAdapter;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseFragment;
import com.innouni.health.entity.Suggest;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.NetUtil;
import com.innouni.health.widget.PullToRefreshView;
import com.innouni.health.widget.PullToRefreshView.OnHeaderRefreshListener;

/**
 * 运动伙伴中的建议
 * 
 * @author HuGuojun
 * @date 2014-1-17 下午2:51:31
 * @modify
 * @version 1.0.0
 */
public class SuggestFragment extends BaseFragment implements
		OnItemClickListener, OnClickListener {

	private View view;
	private PullToRefreshView refreshView;
	private ListView mListView;
	private RelativeLayout loadFailLayout;
	private ImageView loadFailView;
	private SuggestAdapter adapter;
	private GetSuggestTask task;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_suggest, container, false);
		initView();
		return view;
	}

	private void initView() {
		refreshView = (PullToRefreshView) view.findViewById(R.id.refresh_view);
		mListView = (ListView) view.findViewById(R.id.my_listview);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterDividersEnabled(false);

		adapter = new SuggestAdapter(getActivity());
		mListView.setAdapter(adapter);
		adapter.setListView(mListView);
		mListView.setOnItemClickListener(this);

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
					task = new GetSuggestTask();
					task.execute();
				} else {
					loadFailLayout.setVisibility(View.VISIBLE);
					refreshView.onHeaderRefreshComplete();
				}
			}
		});
		refreshView.headerRefreshing();
	}

	private class GetSuggestTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			application = MainApplication.getApplication();
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			return HttpPostRequest.getDataFromWebServer(getActivity(),
					"getAdvise", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("获取运动建议: " + result);
			task = null;
			try {
				JSONObject jsonObject = new JSONObject(result);
				int status = jsonObject.optInt("status");
				if (status == 0) {
					List<Object> data = new ArrayList<Object>();
					JSONArray array = jsonObject.optJSONArray("Recommends");
					for (int i = 0; i < array.length(); i++) {
						Suggest suggest = new Suggest();
						JSONObject object = array.optJSONObject(i);
						suggest.setId(object.optString("id"));
						suggest.setTitle(object.optString("title"));
						suggest.setContent(object.optString("context"));
						suggest.setTime(object.optString("addTime"));
						data.add(suggest);
					}
					if (adapter != null) {
						adapter.clear();
						adapter.setList(data, true);
					}
				} else {
					showToast(R.string.net_error);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showToast(R.string.net_error);
			}
			mListView.setEnabled(true);
			loadFailLayout.setVisibility(View.GONE);
			refreshView.onHeaderRefreshComplete();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Suggest suggest = (Suggest) adapter.getItem(position);
		Intent intent = new Intent(getActivity(), SuggestDetailActivity.class);
		intent.putExtra("id", suggest.getId());
		startActivity(intent);
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
	public void onPause() {
		super.onPause();
		refreshView.onHeaderRefreshComplete();
		if (task != null) {
			task.cancel(true);
		}
	}

}
