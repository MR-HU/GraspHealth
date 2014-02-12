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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.innouni.health.adapter.FoodsrcAdapter;
import com.innouni.health.adapter.FoodsrcAdapter.OnOperateListener;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.FoodSrc;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.zijunlin.Zxing.Demo.CaptureActivity;

/**
 * 饮食添加第四步<br>
 * 选择某种具体的食物(比如:来必堡餐厅的蛋炒饭)
 * 
 * @author HuGuojun
 * @date 2014-1-14 下午3:28:32
 * @modify
 * @version 1.0.0
 */
public class AddDietStepFourActivity extends BaseActivity implements
		OnClickListener, TextWatcher, OnItemClickListener {

	public static final int COLLECT_TYPE = 1;
	public static final int NORMAL_TYPE = 2;
	
	private ProgressDialog dialog;
	private String entryType, mealsType, enterId;
	private List<Object> data;
	private ListView listView;
	private FoodsrcAdapter adapter;
	private EditText keyView;

	private GetCollectionTask collectionTask;
	private GetFoodInfoTask foodInfoTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_diet_four);
		application = MainApplication.getApplication();
		application.setActivity(this);
		application.setDietActivity(this);
		Bundle bundle = getIntent().getExtras();
		entryType = bundle.getString("entryType");
		mealsType = bundle.getString("mealsType");
		if (entryType.equals("4")) {
			enterId = "";
		} else {
			enterId = bundle.getString("enterId");
		}
		initView();
		if (collectionTask != null) {
			collectionTask.cancel(true);
		}
		collectionTask = new GetCollectionTask();
		collectionTask.execute();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("食物源");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setOnClickListener(this);

		keyView = (EditText) findViewById(R.id.edit_food_source_key);
		keyView.addTextChangedListener(this);
		listView = (ListView) findViewById(R.id.listview_food_source);
		adapter = new FoodsrcAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		adapter.setOnOperateListener(new OnOperateListener() {
			
			@Override
			public void onOperate(int position) {
//				showToast("你好");
			}
		});
	}

	/**
	 * 默认获取我的收藏中的食物列表
	 */
	private class GetCollectionTask extends AsyncTask<Void, Void, List<Object>> {

		@Override
		protected void onPreExecute() {
			if (data == null) {
				data = new ArrayList<Object>();
			} else {
				data.clear();
			}
			showDialog();
		}

		@Override
		protected List<Object> doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			String json = HttpPostRequest.getDataFromWebServer(
					AddDietStepFourActivity.this, "getCollectFood", pairs);
			System.out.println("请求食物源返回 --->默认我的收藏: " + json);
			try {
				int status = new JSONObject(json).optInt("status");
				if (status == 0) {
					JSONArray array = new JSONObject(json)
							.optJSONArray("Foods");
					for (int i = 0; i < array.length(); i++) {
						FoodSrc food = new FoodSrc();
						JSONObject object = array.optJSONObject(i);
						food.setFoodId(object.opt("foodId"));
						food.setFoodName(object.opt("foodName"));
						food.setFoodNum(1);
						food.setFoodCal(object.opt("food_cal"));
						data.add(food);
					}
				}
				return data;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Object> result) {
			dialog.dismiss();
			collectionTask = null;
			if (result != null) {
				adapter.clear();
				adapter.setType(COLLECT_TYPE);
				adapter.setList(result, true);
			} else {
				showToast(R.string.net_error);
			}
		}

	}

	/**
	 * 根据关键字查找食物列表
	 */
	private class GetFoodInfoTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			if (data == null) {
				data = new ArrayList<Object>();
			} else {
				data.clear();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("name", params[0]));
			pairs.add(new BasicNameValuePair("enterId", enterId));
			pairs.add(new BasicNameValuePair("type", entryType));
			return HttpPostRequest.getDataFromWebServer(
					AddDietStepFourActivity.this, "seachFoodInfo", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("请求食品信息返回: " + result);
			foodInfoTask = null;
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.optInt("status") == 0) {
					JSONArray array = jsonObject.optJSONArray("foods");
					for (int i = 0; i < array.length(); i++) {
						FoodSrc food = new FoodSrc();
						JSONObject object = array.optJSONObject(i);
						food.setFoodId(object.opt("foodId"));
						food.setFoodName(object.opt("foodName"));
						food.setFoodNum(1);
						food.setFoodCal(object.opt("food_cal"));
						data.add(food);
					}
					adapter.clear();
					adapter.setType(NORMAL_TYPE);
					adapter.setList(data, true);
				} else {
					showToast(R.string.net_error);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showToast(R.string.net_error);
			}
		}

	}

	private void showDialog() {
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(getResources().getString(R.string.load_loading_data));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FoodSrc food = (FoodSrc) adapter.getItem(position);
		Intent intent = new Intent(this, AddDietDishDetailActivity.class);
		intent.putExtra("foodId", food.getFoodId().toString());
		intent.putExtra("mealsType", mealsType);
		intent.putExtra("calory", food.getFoodCal().toString());
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			Intent intent = new Intent(this, CaptureActivity.class);
			intent.putExtra("mealsType", mealsType);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		String key = s.toString();
		if (!key.equals("")) {
			if (foodInfoTask != null) {
				foodInfoTask.cancel(true);
			}
			foodInfoTask = new GetFoodInfoTask();
			foodInfoTask.execute(key);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (collectionTask != null) {
			collectionTask.cancel(true);
		}
		if (foodInfoTask != null) {
			foodInfoTask.cancel(true);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(application.getDietActivity().contains(this)){
			application.getDietActivity().remove(this);
		}
	}
}
