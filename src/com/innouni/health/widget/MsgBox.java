package com.innouni.health.widget;

import com.innouni.health.activity.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 对话框封装类
 * 
 * @author HuGuojun
 * @date 2014-2-10 下午2:58:54
 * @modify
 * @version 1.0.0
 */
public class MsgBox {

	private static AlertDialog alert;
	private DialogInterface.OnClickListener positiveListener, negativeListener;
	private String message;
	private String positiveText = "";
	private String negativeText = "";

	/**
	 * 设置左边按钮上的文字
	 */
	public void setPositiveText(String positiveText) {
		this.positiveText = positiveText;
	}

	/**
	 * 设置右边按钮上的文字
	 */
	public void setNegativeText(String negativeText) {
		this.negativeText = negativeText;
	}

	public void setPositiveListener(DialogInterface.OnClickListener positiveListener) {
		this.positiveListener = positiveListener;
	}

	public void setNegativeListener(DialogInterface.OnClickListener negativeListener) {
		this.negativeListener = negativeListener;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void show(Context context) {
		dismiss();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getResources().getString(R.string.alter_title));
		builder.setMessage(message).setCancelable(false);
		if ("".equals(positiveText)) {
			positiveText = context.getResources().getString(R.string.dialog_positive);
		}
		if ("".equals(negativeText)) {
			negativeText = context.getResources().getString(R.string.dialog_negative);
		}
		if (null != positiveListener) {
			builder.setPositiveButton(positiveText, positiveListener);
		}
		if (null != negativeListener) {
			builder.setNegativeButton(negativeText, negativeListener);
		}
		AlertDialog alert = builder.create();
		try {
			alert.show();
		} catch (Exception e) {
			
		}
	}

	public static void dismiss() {
		if (null != alert) {
			alert.dismiss();
			alert = null;
		}
	}

	public static void alert(String msg, Context context) {
		alert(msg, context, null);
	}

	/**
	 * 显示提示信息
	 * @param msg
	 * @param context
	 * @param listener
	 */
	public static void alert(String msg, Context context, DialogInterface.OnClickListener listener) {
		MsgBox msgbox = new MsgBox();
		msgbox.message = msg;
		if (null == listener) {
			msgbox.positiveListener = new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MsgBox.dismiss();
				}
			};
		} else {
			msgbox.positiveListener = listener;
		}
		msgbox.show(context);
	}
}
