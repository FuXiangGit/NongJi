package com.fyx.nongji;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nongji.tools.Constants;
import com.nongji.tools.MyDialog;

/**
 * @ClassName LoginActivity
 * @Description 登录界面
 * @author 付昱翔
 * @date 2014-1-13
 */
public class LoginActivity extends Activity {
	/**
	 * @Fields username : 用户
	 */
	@ViewInject(R.id.edit_username)
	private EditText username;
	/**
	 * @Fields password : 密码
	 */
	@ViewInject(R.id.edit_userpassword)
	private EditText password;
	/**
	 * @Fields zhuce : 注册
	 */
	@ViewInject(R.id.zhuce)
	private LinearLayout zhuceParent;
@ViewInject(R.id.inputIp)
EditText inputIp;
	/**
	 * @Fields isrmb : 是否记住密码
	 */
	private CheckBox isrmb;
	private SharedPreferences sp;
	private Editor editor;
	private MyDialog myDialog;
	private String yuMingIP;
	private PopupWindow pop;

	private Button register_vip;
	private Button register_not_vip;
	private Button register_cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		ViewUtils.inject(this);
		yuMingIP = Constants.getURL(getApplicationContext());
//		username.setText("hehe1");
		Log.d("jack", "进来了" + yuMingIP);
		initPopuWindow();
		//初始化用户名密码
		initUser();
		
	}

	//模式回头调用
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.d("jack", "进来了");
		initUser();
		super.onNewIntent(intent);
	}

	@OnClick({ R.id.btn_back, R.id.button_login, R.id.button_register,
			R.id.txtyuming ,R.id.button_ip,R.id.text_forgetpassword})
	public void btnOnClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.button_login:
			
			httpLogin();
			break;
		case R.id.button_ip:
			String ip = inputIp.getText().toString().trim();
			if(StringUtils.isEmpty(ip)){
				Toast.makeText(LoginActivity.this, "请输入ip或者域名"+ip,Toast.LENGTH_LONG
							).show();
			}else{
				Toast.makeText(LoginActivity.this, "您输入了"+ip,Toast.LENGTH_LONG
						).show();
				Constants.HOST = ip;
			}
			break;
		case R.id.button_register:
			popupClick();
			break;
		case R.id.txtyuming:
			startActivity(new Intent(LoginActivity.this, YuMingChoice.class));
			break;
		case R.id.text_forgetpassword:
			startActivity(new Intent(LoginActivity.this, ForgetPass.class));
			break;

		default:
			break;
		}
	}

	// --------------------------------------------------------------------------------
	private void popupClick() {
		// TODO Auto-generated method stub
		if (pop.isShowing()) {
			// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
			pop.dismiss();
		} else {
			// 显示窗口
			pop.showAtLocation(zhuceParent, Gravity.BOTTOM, 0, 0);
		}
	}
	//----------------------------------------------------------------------
	private void initUser() {
		// TODO Auto-generated method stub
		isrmb = (CheckBox) findViewById(R.id.checkbox_password);
		// 是否记住密码
		sp = getSharedPreferences("uservalue", MODE_PRIVATE);
		editor = sp.edit();
		if (sp != null) {
			if (sp.getBoolean("isrmb", false)) {
				username.setText(sp.getString("phone", null));
				username.setSelection(username.getText().toString().length());
				password.setText(sp.getString("password", null));
				isrmb.setChecked(true);
			}
		}
	}
	// -----------------------------------------------------------------------
	private void initPopuWindow() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(this);
		// 引入窗口配置文件
		View view = inflater.inflate(R.layout.activity_register_selector, null);
		pop = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		// 需要设置一下此参数，点击外边可消失
		pop.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		pop.setOutsideTouchable(true);
		// 设置此参数获得焦点，否则无法点击
		pop.setFocusable(true);

		// 管理员
		register_vip = (Button) view.findViewById(R.id.register_vip);
		// 普通会员
		register_not_vip = (Button) view.findViewById(R.id.register_not_vip);
		// 取消
		register_cancel = (Button) view.findViewById(R.id.register_cancel);
		register_vip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterNotVIPActivity.class);
				intent.putExtra("USERTYPE", "1");
				startActivity(intent);
				pop.dismiss();
			}
		});
		register_not_vip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterNotVIPActivity.class);
				intent.putExtra("USERTYPE", "0");
				startActivity(intent);
				pop.dismiss();
			}
		});
		register_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		});

	}

	// -----------------------------------------------------------------------
	private void httpLogin() {
		String strUser = username.getText().toString().trim();
		String strPass = password.getText().toString().trim();
		if (StringUtils.isEmpty(strUser) || StringUtils.isEmpty(strPass)) {
			Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT)
					.show();
		} else {
			HttpUtils http = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("phone",strUser);
			params.addBodyParameter("pwd", strPass);
			final String URL = Constants.HOST + "EasyCar/loginInfophone.do";
			params.addBodyParameter("name", "abcdefg");
			LogUtils.d("onStart" + params.toString());
			http.send(HttpMethod.POST, URL, params,
					new RequestCallBack<String>() {

						@Override
						public void onStart() {
							super.onStart();
							myDialog = new MyDialog(LoginActivity.this,
									"正在登录……");
							myDialog.show();
							LogUtils.d("onStart" + URL);
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							LogUtils.d("responseInfo的result="
									+ responseInfo.result);
							myDialog.dismiss();
							JSONObject jObj;
							int myresult;
							//用户ID
							String myID;
							//心跳
							int myNumber;
							//用户名字
							String myName;
							//用户类型
							int myType;
							int myState;
							try {
								jObj = new JSONObject(responseInfo.result);
								myresult = jObj.getInt("success");
							
								
								// 登陆成功
								if (myresult == 1) {
									myID = jObj.getString("id");
									myNumber = jObj.getInt("number");
									myName = jObj.getString("username");
									myType = jObj.getInt("userType");
									myState = jObj.getInt("state");
									// 保存车状
									editor.putInt("state", myState);
									// 保存手机
									editor.putString("phone", username
											.getText().toString());
									// 保存密码
									editor.putString("password", password
											.getText().toString());
									// 保存用户名
									editor.putString("uname", myName);
									// 保存ID
									editor.putString("uID", myID);
									// 保存用户类型
									editor.putInt("uType", myType);
									// 保存心跳
									editor.putInt("uNO", myNumber);
									if (isrmb.isChecked()) {
										editor.putBoolean("isrmb", true);
										editor.commit();
									} else {
										editor.putBoolean("isrmb", false);
										editor.commit();
									}
									//登陆成功
									Intent intent = new Intent(
											LoginActivity.this,
											NongJiActivity.class);
									startActivity(intent);
								} else if (myresult == -5) {
									Toast.makeText(LoginActivity.this, "用户不存在！",
											Toast.LENGTH_LONG).show();
								}else{
									Toast.makeText(LoginActivity.this, "用户名或者密码错误！",
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
							Toast.makeText(LoginActivity.this,
									"链接服务器失败，请检查网络！", Toast.LENGTH_LONG).show();
						}

					});
		}

	}

}
