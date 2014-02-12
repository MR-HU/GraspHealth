package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;

/**
 * 菜品详情页
 * 
 * @author HuGuojun
 * @date 2013-12-30 下午4:00:18
 * @modify
 * @version 1.0.0
 */
public class DishDetailActivity extends BaseActivity implements OnClickListener {

	private static final int LEFT_VIEW_ID = 12345;
	private static final int RIGHT_VIEW_ID = 54321;

	private JSONObject jsonObject;
	private ProgressDialog dialog;
	private LinearLayout enterLayout;
	private TextView enterView, foodTypeView;
	private LinearLayout foodLayout, nutritionLayout;
	private String foodId;
	private GetFoodDetailTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dish_detail);
		application = MainApplication.getApplication();
		application.setActivity(this);
		foodId = getIntent().getStringExtra("foodId");
		initView();
		if (task != null) {
			task.cancel(true);
		}
		task = new GetFoodDetailTask();
		task.execute();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText(null);
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		enterLayout = (LinearLayout) findViewById(R.id.lay_enterprise);
		enterView = (TextView) findViewById(R.id.txt_enterprise);
		foodTypeView = (TextView) findViewById(R.id.txt_food_type);
		foodLayout = (LinearLayout) findViewById(R.id.lay_food);
		nutritionLayout = (LinearLayout) findViewById(R.id.lay_nutrition);
	}

	private class GetFoodDetailTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("foodId", foodId));
			return HttpPostRequest.getDataFromWebServer(
					DishDetailActivity.this, "getFoodDetail", pairs);
		}

		@Override
		protected void onPostExecute(String json) {
			System.out.println("我的收藏详情返回 : " + json);
			dialog.dismiss();
			task = null;
			try {
				jsonObject = new JSONObject(json);
				int status = jsonObject.optInt("status");
				if (status == 0) {
					titleContentView.setText(jsonObject.optString("foodName"));
					enterView.setText(jsonObject.optString("enterName"));
					foodTypeView.setText(switchFoodType(jsonObject
							.optInt("foodType")));
					// 自定义菜没有企业
					if (jsonObject.optInt("foodType") == 4) {
						enterLayout.setVisibility(View.GONE);
					}
					initFoodList();
					initNutritionList();
				} else {
					showToast(R.string.net_error);
				}
			} catch (Exception e) {
				e.printStackTrace();
				showToast(R.string.net_error);
			}
		}

	}

	private void showDialog() {
		dialog = new ProgressDialog(DishDetailActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(getResources().getString(R.string.load_loading_data));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}

	/**
	 * 初始化食材列表
	 * 
	 * @description initFoodList
	 * @exception
	 */
	private void initFoodList() {
		LinearLayout foodContainer = (LinearLayout) findViewById(R.id.lay_food_container);
		JSONArray array = jsonObject.optJSONArray("Foods");
		if (array == null || array.length() == 0) {
			return;
		}
		foodLayout.setVisibility(View.VISIBLE);
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.optJSONObject(i);
			RelativeLayout linear = new RelativeLayout(DishDetailActivity.this);
			RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			linear.setBackgroundResource(R.drawable.list_middle_click);
			linear.setLayoutParams(rp);

			TextView nameView = new TextView(DishDetailActivity.this);
			nameView.setId(LEFT_VIEW_ID);
			nameView.setTextColor(Color.BLACK);
			nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			nameView.setText(object.optString("ingredName")
					+ "("
					+ switchType(Integer.valueOf(object.optString("ingredType")))
					+ ")");
			RelativeLayout.LayoutParams nrp = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			nrp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			nrp.addRule(RelativeLayout.CENTER_VERTICAL);
			nameView.setLayoutParams(nrp);
			linear.addView(nameView);

			TextView unitView = new TextView(DishDetailActivity.this);
			unitView.setId(RIGHT_VIEW_ID);
			unitView.setTextColor(Color.BLACK);
			unitView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			unitView.setText(object.optString("ingredUnitType"));
			RelativeLayout.LayoutParams urp = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			urp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			urp.addRule(RelativeLayout.CENTER_VERTICAL);
			unitView.setLayoutParams(urp);
			linear.addView(unitView);

			TextView valueView = new TextView(DishDetailActivity.this);
			valueView.setTextColor(Color.BLACK);
			valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			valueView.setText(object.optString("ingredContent"));
			valueView.setGravity(Gravity.RIGHT);
			RelativeLayout.LayoutParams vrp = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			vrp.addRule(RelativeLayout.LEFT_OF, RIGHT_VIEW_ID);
			vrp.addRule(RelativeLayout.RIGHT_OF, LEFT_VIEW_ID);
			vrp.addRule(RelativeLayout.CENTER_VERTICAL);
			valueView.setLayoutParams(vrp);
			linear.addView(valueView);
			foodContainer.addView(linear);
		}
	}

	/**
	 * 初始化营养列表
	 * 
	 * @description initNutritionList
	 * @exception
	 */
	private void initNutritionList() {
		LinearLayout nutritionContainer = (LinearLayout) findViewById(R.id.lay_nutrition_container);
		JSONArray array = jsonObject.optJSONArray("Nutrients");
		if (array == null || array.length() == 0) {
			return;
		}
		nutritionLayout.setVisibility(View.VISIBLE);
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.optJSONObject(i);
			RelativeLayout linear = new RelativeLayout(DishDetailActivity.this);
			RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			linear.setBackgroundResource(R.drawable.list_middle_click);
			linear.setLayoutParams(rp);

			TextView nameView = new TextView(DishDetailActivity.this);
			nameView.setId(LEFT_VIEW_ID);
			nameView.setTextColor(Color.BLACK);
			nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			nameView.setText(object.optString("nutritionName")
					+ "("
					+ switchNutritionType(Integer.valueOf(object
							.optString("nutritionType"))) + ")");
			RelativeLayout.LayoutParams nrp = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			nrp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			nrp.addRule(RelativeLayout.CENTER_VERTICAL);
			nameView.setLayoutParams(nrp);
			linear.addView(nameView);

			TextView unitView = new TextView(DishDetailActivity.this);
			unitView.setId(RIGHT_VIEW_ID);
			unitView.setTextColor(Color.BLACK);
			unitView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			unitView.setText(object.optString("nutritionUnitType"));
			RelativeLayout.LayoutParams urp = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			urp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			urp.addRule(RelativeLayout.CENTER_VERTICAL);
			unitView.setLayoutParams(urp);
			linear.addView(unitView);

			TextView valueView = new TextView(DishDetailActivity.this);
			valueView.setTextColor(Color.BLACK);
			valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			valueView.setText(object.optString("nutritionContent"));
			valueView.setGravity(Gravity.RIGHT);
			RelativeLayout.LayoutParams vrp = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			vrp.addRule(RelativeLayout.LEFT_OF, RIGHT_VIEW_ID);
			vrp.addRule(RelativeLayout.RIGHT_OF, LEFT_VIEW_ID);
			vrp.addRule(RelativeLayout.CENTER_VERTICAL);
			valueView.setLayoutParams(vrp);
			linear.addView(valueView);
			nutritionContainer.addView(linear);
		}
	}

	private String switchFoodType(int type) {
		String foodType = null;
		switch (type) {
		case 1:
			foodType = "基本食材";
			break;
		case 2:
			foodType = "包装食品";
			break;
		case 3:
			foodType = "餐厅或食堂菜";
			break;
		case 4:
			foodType = "自定义菜";
			break;
		}
		return foodType;
	}

	private String switchType(int type) {
		String foodType = null;
		switch (type) {
		case 1:
			foodType = "主料";
			break;
		case 2:
			foodType = "副料";
			break;
		case 3:
			foodType = "添加剂";
			break;
		}
		return foodType;
	}

	private String switchNutritionType(int type) {
		String nutritionType = null;
		switch (type) {
		case 1:
			nutritionType = "碳水化合物";
			break;
		case 2:
			nutritionType = "蛋白质";
			break;
		case 3:
			nutritionType = "脂肪";
			break;
		case 4:
			nutritionType = "常量元素矿物质";
			break;
		case 5:
			nutritionType = "微量元素矿物质";
			break;
		case 6:
			nutritionType = "维生素";
			break;
		case 7:
			nutritionType = "水";
			break;
		case 8:
			nutritionType = "膳食纤维";
			break;
		case 9:
			nutritionType = "其他";
			break;
		}
		return nutritionType;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
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
