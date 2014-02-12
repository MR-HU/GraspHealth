package com.innouni.health.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.util.Util;

/**
 * 添加菜谱页面
 * 
 * @author HuGuojun
 * @date 2014-1-10 下午3:12:23
 * @modify
 * @version 1.0.0
 */
public class AddCookBookActivity extends BaseActivity implements
		OnClickListener {

	private static final int LEFT_VIEW_ID = 12345;
	private static final int Right_VIEW_ID = 54321;
	public static final int RESULT_CODE = 20;
	public static final int REQUEST_CODE = 100;

	private EditText nameView;
	private TextView addView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_cookbook);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleLeftBtn.setOnClickListener(this);
		titleContentView.setText("我的菜谱");
		titleRightBtn.setVisibility(View.GONE);

		nameView = (EditText) findViewById(R.id.edit_cook_name);
		addView = (TextView) findViewById(R.id.txt_add_ingredient);
		addView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.txt_add_ingredient:
			String foodName = nameView.getText().toString();
			if (Util.isEmpty(foodName)) {
				showToast(R.string.ingre_name_first);
			} else {
				Intent intent = new Intent(AddCookBookActivity.this,
						AddIngredientsActivity.class);
				intent.putExtra("foodName", foodName);
				startActivityForResult(intent, REQUEST_CODE);
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CODE && data != null) {
			String id = data.getStringExtra("ingreId");
			String name = data.getStringExtra("ingreName");
			String type = data.getStringExtra("ingreType");
			String num = data.getStringExtra("ingreNum");
			String unit = data.getStringExtra("ingreUnit");
			String json = "{\"Foods\":[{\"ingredId\":\"" + id
					+ "\",\"ingredType\":\"" + type + "\",\"ingredName\":\""
					+ name + "\",\"ingredContent\":\"" + num
					+ "\",\"ingredUnitType\":\"" + unit + "\"}]}";
			try {
				initFoodList(new JSONObject(json));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			System.out.println(json);
		}
	}

	private void initFoodList(JSONObject jsonObject) {
		LinearLayout foodContainer = (LinearLayout) findViewById(R.id.lay_ingredient_container);
		JSONArray array = jsonObject.optJSONArray("Foods");
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.optJSONObject(i);
			RelativeLayout linear = new RelativeLayout(this);
			RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			linear.setBackgroundResource(R.drawable.list_middle_click);
			linear.setLayoutParams(rp);

			TextView nameView = new TextView(this);
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

			TextView unitView = new TextView(this);
			unitView.setId(Right_VIEW_ID);
			unitView.setTextColor(Color.BLACK);
			unitView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			unitView.setText(object.optString("ingredUnitType"));
			RelativeLayout.LayoutParams urp = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			urp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			urp.addRule(RelativeLayout.CENTER_VERTICAL);
			unitView.setLayoutParams(urp);
			linear.addView(unitView);

			TextView valueView = new TextView(this);
			valueView.setTextColor(Color.BLACK);
			valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			valueView.setText(object.optString("ingredContent"));
			valueView.setGravity(Gravity.RIGHT);
			RelativeLayout.LayoutParams vrp = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			vrp.addRule(RelativeLayout.LEFT_OF, Right_VIEW_ID);
			vrp.addRule(RelativeLayout.RIGHT_OF, LEFT_VIEW_ID);
			vrp.addRule(RelativeLayout.CENTER_VERTICAL);
			valueView.setLayoutParams(vrp);
			linear.addView(valueView);
			foodContainer.addView(linear);
		}
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

}
