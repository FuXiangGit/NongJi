package com.nongji.tools;

import com.fyx.nongji.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;


public class MyDialog extends Dialog {

	private String string;
	private TextView txt_dialog;

	//使用默认Dialog样式
	public MyDialog(Context context) {
		this(context, null);
	}

	//可更改Dialog文字
	public MyDialog(Context context, String string) {
		super(context, R.style.MyDialogStyle);
		this.string = string;
	}

	//自定义Dialog，样式及文字都自定义
	public MyDialog(Context context, int theme, String string) {
		super(context, R.style.MyDialogStyle);
		this.string = string;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.self_dialog);
		
		txt_dialog = (TextView) findViewById(R.id.txt_dialog);
		if ("".equals(string) || string == null) {
			string = "加载中...";
		}
		this.setCancelable(false);
		txt_dialog.setText(string);
	}
}
