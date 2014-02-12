package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innouni.health.adapter.MessageAdapter;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.MessageInfo;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.NetUtil;
import com.innouni.health.util.Util;
import com.innouni.health.widget.PullToRefreshView;
import com.innouni.health.widget.PullToRefreshView.OnHeaderRefreshListener;

/**
 * 专家留言
 * 
 * @author HuGuojun
 * @date 2014-2-12 上午10:39:01
 * @modify
 * @version 1.0.0
 */
public class MessageActivity extends BaseActivity implements OnClickListener {

	private PullToRefreshView refreshView;
	private ListView mListView;
	private RelativeLayout loadFailLayout;
	private ImageView loadFailView;

	private MessageAdapter adapter;
	private GetMessageTask task;
	private SendMessageTask sendTask;

	private String userImage, expertImage;
	private String expertId, userId, expertName;
	private UserInfo user;
	private Button sendButton;
	private EditText contentText;
	private int currentPage = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		application = MainApplication.getApplication();
		application.setActivity(this);
		user = application.getUserInfo();
		userId = user.getId();
		userImage = user.getAvatar();
		Intent intent = getIntent();
		if (null != intent) {
			expertId = intent.getStringExtra("expertId");
			expertName = intent.getStringExtra("expertName");
			expertImage = intent.getStringExtra("expertImageUrl");
		}
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);
		titleContentView.setText(expertName);

		sendButton = (Button) findViewById(R.id.btn_send_message);
		sendButton.setOnClickListener(this);
		contentText = (EditText) findViewById(R.id.edit_message_write);
		contentText.setTextColor(Color.BLACK);

		refreshView = (PullToRefreshView) findViewById(R.id.refresh_view);
		mListView = (ListView) findViewById(R.id.my_listview);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterDividersEnabled(false);
		mListView.setStackFromBottom(true);

		adapter = new MessageAdapter(this);
		adapter.setUserImage(userImage);
		adapter.setExpertImage(expertImage);
		mListView.setAdapter(adapter);
		adapter.setAbsListView(mListView);

		loadFailLayout = (RelativeLayout) findViewById(R.id.lay_load_fail);
		loadFailView = (ImageView) findViewById(R.id.imageview_load_fail);
		loadFailView.setOnClickListener(this);

		refreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				if (NetUtil.isNetworkAvailable(MessageActivity.this)) {
					if (null != task)
						task.cancel(true);
					task = new GetMessageTask();
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
		case R.id.btn_title_left:
			backByTag();
			break;
		case R.id.imageview_load_fail:
			currentPage = 0;
			mListView.setEnabled(false);
			refreshView.headerRefreshing();
			break;
		case R.id.btn_send_message:
			handleSendBtn();
			break;
		default:
			break;
		}
	}

	private void handleSendBtn() {
		String content = contentText.getText().toString();
		if (!Util.isEmpty(content)) {
			if (null != sendTask) {
				sendTask.cancel(true);
			}
			sendTask = new SendMessageTask();
			sendTask.execute(content);
		}
	}

	private class GetMessageTask extends
			AsyncTask<String, Void, ArrayList<Object>> {

		@Override
		protected void onPreExecute() {
			currentPage += 1;
		}

		@Override
		protected ArrayList<Object> doInBackground(String... params) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("mId", userId));
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("expertId", expertId));
			pairs.add(new BasicNameValuePair("curpage", String
					.valueOf(currentPage)));
			pairs.add(new BasicNameValuePair("percount", String.valueOf(8)));
			String json = HttpPostRequest.getDataFromWebServer(
					MessageActivity.this, "getOLQuestionsByStart", pairs);
			System.out.println("请求留言返回: " + json);
			ArrayList<Object> list = null;
			try {
				JSONObject jsonObject = new JSONObject(json);
				if (jsonObject.optInt("status") == 0) {
					list = new ArrayList<Object>();
					JSONArray array = jsonObject.optJSONArray("Lists");
					for (int i = 0; i < array.length(); i++) {
						MessageInfo message = new MessageInfo();
						JSONObject object = array.optJSONObject(i);
						message.setContent(object.opt("content"));
						message.setTime(object.opt("addtime"));
						message.setType(object.opt("type"));
						list.add(message);
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
					adapter.setListToFirst(result, true);
					mListView.setSelection(adapter.getCount());
				} else {
					showToast(R.string.load_no_more_data);
				}
			}
			refreshView.onHeaderRefreshComplete();
		}
	}

	private class SendMessageTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("content", params[0]));
			pairs.add(new BasicNameValuePair("mId", userId));
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("expertId", expertId));
			return HttpPostRequest.getDataFromWebServer(MessageActivity.this,
					"sendOLQuestions", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			sendTask = null;
			System.out.println("提交留言返回: " + result);
			try {
				JSONObject object = new JSONObject(result);
				if (object.optInt("status") == 0) {
					contentText.setText(null);
				} else {
					showToast(R.string.net_error);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showToast(R.string.net_error);
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
		if (null != sendTask) {
			sendTask.cancel(true);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backByTag();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void backByTag() {
		if (application.isInActivity()) {
			finish();
		}
	}
}
//我勒个去去
