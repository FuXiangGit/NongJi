package com.fyx.nongji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.gson.JsonObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nongji.tools.Constants;
import com.nongji.tools.MyDialog;
import com.nongji.vo.SIMCardInfo;

public class ForgetPass extends Activity {

	@ViewInject(R.id.phone)
	EditText phoneText;
	// 文本
	@ViewInject(R.id.random)
	EditText phoneYanZheng;
	// 按钮
	@ViewInject(R.id.getRandom)
	Button imgRandom;
	@ViewInject(R.id.password)
	EditText password;
	@ViewInject(R.id.okpass)
	EditText okpass;

	// 电话号码
	String strphone;
	// 验证码
	String getcode;
	// 自定义对话框
	private MyDialog myDialog;
	// 用户类型
	private String userType = "0";

	/**
	 * @Fields isrmb : 是否记住密码
	 */
	private SharedPreferences sp;
	private Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_pass);
		ViewUtils.inject(this);
		getPhone();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Button button = (Button) findViewById(R.id.btn_back);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ForgetPass.this, LoginActivity.class));
				finish();
			}
		});
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================


	/**
	 * 验证号码
	 */
	private void getPhone() {
		SIMCardInfo siminfo = new SIMCardInfo(ForgetPass.this);
		String simCard = siminfo.getNativePhoneNumber();
		if (simCard.equals("无SIM卡")) {
			Toast.makeText(ForgetPass.this, "无SIM卡！", Toast.LENGTH_LONG).show();
		} else if (simCard.equals("SIM卡被锁定或未知状态")) {
			Toast.makeText(ForgetPass.this, "SIM卡被锁定或未知状态，请您手动输入！",
					Toast.LENGTH_LONG).show();
		} else {
			System.out.println(siminfo.getProvidersName());
			System.out.println(siminfo.getNativePhoneNumber());
		}
	}

	// --------------------------------------------------------------
	@OnClick({ R.id.getRandom, R.id.register_free })
	public void myClick(View v) {
		switch (v.getId()) {
		case R.id.getRandom:
			strphone = phoneText.getText().toString().trim();
			if (Constants.isMobileNO(strphone)) {
				getcode = random();
				sendMsg(strphone, "您的验证码为:" + getcode);
				// imgRandom.setVisibility(View.GONE);
				imgRandom.setClickable(false);
				new Thread(){@Override
				public void run() {
					try {
						Thread.sleep(5000);
						imgRandom.setText("重新获得验证码");
						imgRandom.setClickable(true);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					super.run();
				}};
			} else {
				Toast.makeText(ForgetPass.this, "请您输入正确手机号", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.register_free:
			strphone = phoneText.getText().toString().trim();

			String code = phoneYanZheng.getText().toString().trim();
			if (StringUtils.isEmpty(code)) {
				Toast.makeText(ForgetPass.this, "请输入验证码", Toast.LENGTH_SHORT)
						.show();
			} else if (code.equals(getcode)) {
				initHttp();
			} else {
				Toast.makeText(ForgetPass.this, "验证码不对请重新输入",
						Toast.LENGTH_SHORT).show();
				phoneYanZheng.setText("");
			}
			break;
		default:
			break;
		}
	}

	// ---------------------------------------------------------------

	// --------------------------------------------------------------
	// 手机长度验证
	public boolean telephoneLengthCheck(String telephones) {
		char tempTelep[] = telephones.toCharArray();
		if (tempTelep.length != 11) {
			return false;
		} else {
			return true;
		}
	}

	// 随机验证码
	private String random() {
		String code = "";
		Random r = new Random();
		for (int i = 0; i < 4; i++) {
			code += r.nextInt(10);
		}
		return code;
	}

	/**
	 * 发送短信
	 * 
	 * @param number
	 * @param message信息
	 */
	private void sendMsg(String number, String message) {
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(number, null, message, null, null);
	}

	/**
	 * 上传注册信息
	 */
	private void initHttp() {
		// 保存用户
		sp = getSharedPreferences("uservalue", MODE_PRIVATE);
		editor = sp.edit();
		// 密码
		String strPass = password.getText().toString().trim();
		// 确认密码
		String strOkpass = okpass.getText().toString().trim();

		if (StringUtils.isEmpty(strphone)) {
			Toast.makeText(ForgetPass.this, "手机号码不能为空", Toast.LENGTH_SHORT)
					.show();
		} else if (!Constants.isMobileNO(strphone)) {
			Toast.makeText(ForgetPass.this, "请您输入正确手机号", Toast.LENGTH_SHORT)
					.show();
		} else if (StringUtils.isEmpty(strPass)) {
			Toast.makeText(ForgetPass.this, "密码不能为空", Toast.LENGTH_SHORT)
					.show();
		} else if (!strOkpass.equals(strPass)) {
			Toast.makeText(ForgetPass.this, "请保证两次密码输入相同", Toast.LENGTH_SHORT)
					.show();
		} else {
			HttpUtils http = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("pwd", strOkpass);
			params.addBodyParameter("phone", strphone);
			final String URL = Constants.HOST + "EasyCar/deleteUserIdccccc"
					+ Constants.HOSTEND;
			Log.d("jack",URL+"mima:"+strOkpass+"dianhua"+strphone);
			http.send(HttpMethod.POST, URL, params,
					new RequestCallBack<String>() {

						@Override
						public void onStart() {
							super.onStart();
							myDialog = new MyDialog(ForgetPass.this, "正在链接……");
							myDialog.show();
							LogUtils.d("onStart" + URL);
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							LogUtils.d("responseInfo的result="
									+ responseInfo.result);
							myDialog.dismiss();
							JSONObject jObj;
							int backResult;
							try {
								jObj = new JSONObject(responseInfo.result);
								backResult = jObj.getInt("success");
								if (backResult == 1) {
									Toast.makeText(ForgetPass.this, "密码修改成功！",
											Toast.LENGTH_LONG).show();
									Intent intent = new Intent(ForgetPass.this,
											LoginActivity.class);
									startActivity(intent);
								} else if(backResult==-5){
									Toast.makeText(ForgetPass.this, "用户不存在！",
											Toast.LENGTH_LONG).show();
								}else {
									Toast.makeText(ForgetPass.this, "重新找回密码！",
											Toast.LENGTH_LONG).show();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(
								com.lidroid.xutils.exception.HttpException error,
								String msg) {
							myDialog.dismiss();
							Toast.makeText(ForgetPass.this, "链接服务器失败,检查网络！",
									Toast.LENGTH_LONG).show();
						}

					});
		}
	}


	// =======================================================
}
