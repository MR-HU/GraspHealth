package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innouni.health.adapter.IngredientAdapter;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.Ingredient;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.Util;

/**
 * 添加食材页面
 * 
 * @author HuGuojun
 * @date 2014-1-2 上午9:53:49
 * @modify
 * @version 1.0.0
 */
public class AddIngredientsActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, OnFocusChangeListener {

	private ProgressDialog dialog;
	private Button submitBtn;
	private PopupWindow popupWindow;
	private TextView typeView, unitView;
	private EditText ingredientView, numView;
	private String ingreType = "1"; // 默认类型主料
	private String foodName, ingreId, ingreNum;
	private String key = "";

	private IngredientAdapter adapter;
	private IngredientTextWatcher watcher;
	private SubmitIngreTask task;

	private boolean isMeasured = false;
	private int offsetHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_ingredients);
		application = MainApplication.getApplication();
		application.setActivity(this);
		foodName = getIntent().getStringExtra("foodName");
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);
		titleContentView.setText("食材添加");

		submitBtn = (Button) findViewById(R.id.btn_add_food_commit);
		submitBtn.setOnClickListener(this);

		typeView = (TextView) findViewById(R.id.edit_add_food_type);
		numView = (EditText) findViewById(R.id.edit_add_food_num);
		unitView = (TextView) findViewById(R.id.edit_add_food_unit);
		ingredientView = (EditText) findViewById(R.id.edit_add_food_name);
		ingredientView.setFocusable(true);
		ingredientView.setFocusableInTouchMode(true);
		watcher = new IngredientTextWatcher();
		ingredientView.setOnFocusChangeListener(this);
		typeView.setOnClickListener(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!isMeasured) {
			Rect frame = new Rect();
			getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int statusBarHeight = frame.top; // 获取状态栏高度
			RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.lay_title_bar);
			int titleBarHeight = titleBar.getHeight();
			LinearLayout addFoodLay = (LinearLayout) findViewById(R.id.lay_add_food);
			int ingreHeight = addFoodLay.getBottom();
			offsetHeight = statusBarHeight + titleBarHeight + ingreHeight;
			isMeasured = true;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.edit_add_food_type:
			final String array[] = { "主料", "副料", "添加剂" };
			// 弹出框食材类型选择
			// 1:表示主料 2:表示副料 3:表示添加剂
			Dialog alertDialog = new AlertDialog.Builder(this)
					.setItems(array, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								ingreType = "1";
							}
							if (which == 1) {
								ingreType = "2";
							}
							if (which == 2) {
								ingreType = "3";
							}
							typeView.setText(array[which]);
						}
					})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).create();
			alertDialog.show();
			break;
		case R.id.btn_add_food_commit:
			ingreNum = numView.getText().toString();
			if (Util.isEmpty(ingreId) || Util.isEmpty(ingreType)
					|| Util.isEmpty(ingreNum)) {
				showToast(R.string.register_info_uncomplete);
			} else {
				if (task != null) {
					task.cancel(true);
				}
				task = new SubmitIngreTask();
				task.execute();
			}
			break;
		}
	}

	private class GetFoodTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("name", params[0]));
			return HttpPostRequest.getDataFromWebServer(
					AddIngredientsActivity.this, "getBFoods", pairs);
		}

		@Override
		protected void onPostExecute(String json) {
			System.out.println("食材联想返回: " + json);
			try {
				if (new JSONObject(json).optInt("status") == 0) {
					JSONArray array = new JSONObject(json)
							.optJSONArray("Foods");
					if (array.length() > 0) {
						List<Object> list = new ArrayList<Object>();
						for (int i = 0; i < array.length(); i++) {
							Ingredient ingredient = new Ingredient();
							JSONObject object = array.optJSONObject(i);
							ingredient.setId(object.optString("ingredId"));
							ingredient.setName(object.optString("ingredName"));
							ingredient.setUnit(object
									.optString("ingredUnitType"));
							list.add(ingredient);
						}
						showPopWindow(list);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				showToast(R.string.net_error);
			}
		}

	}

	public void showPopWindow(List<Object> list) {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.pop_ingredient_list, null);
		popupWindow = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, false);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.showAtLocation(ingredientView, Gravity.LEFT | Gravity.TOP,
				Util.dip2px(this, 15), offsetHeight);

		ListView listView = (ListView) layout
				.findViewById(R.id.listview_ingredient);
		adapter = new IngredientAdapter(this);
		listView.setAdapter(adapter);
		adapter.setList(list, true);
		listView.setOnItemClickListener(this);

		WindowManager.LayoutParams attribute = getWindow().getAttributes();
		attribute.gravity = Gravity.TOP;
		attribute.alpha = 0.5f;
		getWindow().setAttributes(attribute);
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams attribute = getWindow()
						.getAttributes();
				attribute.gravity = Gravity.TOP;
				attribute.alpha = 1.0f;
				getWindow().setAttributes(attribute);
			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		popupWindow.dismiss();
		Ingredient ingredient = (Ingredient) adapter.getItem(position);
		key = ingredient.getName();
		ingredientView.setText(key);
		ingredientView.setSelection(key.length()); // 设置光标位置
		unitView.setText(ingredient.getUnit());
		ingreId = ingredient.getId();
	}

	private void showDialog() {
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(getResources().getString(R.string.net_submit));
		dialog.setMessage(getResources().getString(R.string.net_submiting));
		dialog.setIndeterminate(false);
		dialog.setCancelable(false);
		dialog.show();
	}

	private class SubmitIngreTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("foodName", foodName));
			pairs.add(new BasicNameValuePair(ingreId, ingreNum));
			pairs.add(new BasicNameValuePair(ingreId + "_type", ingreType));
			String json = HttpPostRequest.getDataFromWebServer(
					AddIngredientsActivity.this, "addMyFood", pairs);
			System.out.println("添加食材返回 : " + json);
			try {
				JSONObject object = new JSONObject(json);
				int status = object.optInt("status");
				return status;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Integer status) {
			dialog.dismiss();
			task = null;
			if (status == null) {
				showToast(R.string.net_error);
			} else if (status == 0) {
				showToast(R.string.ingre_add_success);
				Intent data = new Intent();
				data.putExtra("ingreId", ingreId);
				data.putExtra("ingreName", ingredientView.getText().toString());
				data.putExtra("ingreType", ingreType);
				data.putExtra("ingreNum", numView.getText().toString());
				data.putExtra("ingreUnit", unitView.getText().toString());
				setResult(AddCookBookActivity.RESULT_CODE, data);
				finish();
			} else {
				showToast(R.string.ingre_add_fail);
			}
		}

	}

	@Override
	public void onFocusChange(View view, boolean isFocused) {
		if (ingredientView.hasFocus()) {
			ingredientView.addTextChangedListener(watcher);
		} else {
			ingredientView.removeTextChangedListener(watcher);
		}
	}

	private class IngredientTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			if (!s.toString().equals(key)) {
				GetFoodTask task = new GetFoodTask();
				task.execute(s.toString());
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
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