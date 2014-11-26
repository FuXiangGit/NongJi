package com.fyx.nongji;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.nongji.vo.SIMCardInfo;

public class XiuGaiInfo extends Activity {

	@ViewInject(R.id.username)
	EditText username;
	@ViewInject(R.id.password)
	EditText password;
	@ViewInject(R.id.okpass)
	EditText okpass;
	@ViewInject(R.id.oldpass)
	EditText oldPass;
	@ViewInject(R.id.text_title)
	TextView textTitle;
	//电话号码
	String strphone;
	//自定义对话框
	private MyDialog myDialog;
	private SharedPreferences sp;
	private Editor editor;
	//用户名
	private String uName;
	//用户ID
	private String uID;
	//老密码
	private String oldPwd;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xiugai_user);
		ViewUtils.inject(this);
		//初始化用户名密码
		initUser();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Button button = (Button) findViewById(R.id.btn_back);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(XiuGaiInfo.this,
						LoginActivity.class));
				finish();
			}
		});
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	//--------------------------------------------------------------
	@OnClick({R.id.register_free})
	public void myClick(View v){
		switch (v.getId()) {
		case R.id.register_free:
				initHttp();
			break;

		default:
			break;
		}
	}
	//--------------------------------------------------------------
	//----------------------------------------------------------------------
		private void initUser() {
			// TODO Auto-generated method stub
			// 是否记住密码
			sp = getSharedPreferences("uservalue", MODE_PRIVATE);
			editor = sp.edit();
			if (sp != null) {
				uName = sp.getString("uname", null);
				oldPwd = sp.getString("password", null);
				uID = sp.getString("uID", null);
					username.setText(uName);
					username.setSelection(username.getText().toString().length());
			}
		}
	//----------------------------------------------------------------
	/**
	 * 上传注册信息
	 */
	private void initHttp() {
		// TODO Auto-generated method stub
		//用户名
		String strUser = username.getText().toString().trim();
		//密码
		String strPass = password.getText().toString().trim();
		//确认密码
		String strOkpass = okpass.getText().toString().trim();
		if(StringUtils.isEmpty(strUser)){
			Toast.makeText(XiuGaiInfo.this, "用户名不能为空",Toast.LENGTH_SHORT)
			.show();
		}else if(!oldPwd.equals(oldPass.getText().toString().trim())){
			Toast.makeText(XiuGaiInfo.this, "旧密码不正确",Toast.LENGTH_SHORT)
			.show();
		}else if(StringUtils.isEmpty(strPass)){
			Toast.makeText(XiuGaiInfo.this, "新密码不能为空",Toast.LENGTH_SHORT)
			.show();
		}else if(!strOkpass.equals(strPass)){
			Toast.makeText(XiuGaiInfo.this, "请保证两次新密码输入相同",Toast.LENGTH_SHORT)
			.show();
		}else{
			final String URL = Constants.HOST+"EasyCar/updateUserPwdPhone.do";
			HttpUtils http = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("id", uID.trim());
			params.addBodyParameter("passd", strOkpass);
			params.addBodyParameter("username",strUser);
			http.send(HttpMethod.POST,
					URL,
					params, new RequestCallBack<String>() {

						@Override
						public void onStart() {
							super.onStart();
							myDialog = new MyDialog(XiuGaiInfo.this,
									"正在登录……");
							myDialog.show();
							LogUtils.d("onStart"+URL);
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							LogUtils.d("responseInfo的result="
									+ responseInfo.result);
							Log.d("jack","responseInfo的result="+ responseInfo.result);
							myDialog.dismiss();
							JSONObject jObj;
							int myresult;
							try {
								jObj = new JSONObject(responseInfo.result);
								myresult = jObj.getInt("success");
								if (myresult==1) {
									//保存密码
									editor.putString("password", okpass.getText().toString().trim());
									//保存用户名
									editor.putString("uname", username.getText().toString().trim());
									editor.commit();
									Toast.makeText(XiuGaiInfo.this, "修改成功！",
											Toast.LENGTH_LONG).show();
									Intent intent = new Intent(XiuGaiInfo.this,NongJiActivity.class);
									startActivity(intent);
								}else{
									Toast.makeText(XiuGaiInfo.this, "修改失败！",
											Toast.LENGTH_LONG).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(
								com.lidroid.xutils.exception.HttpException error,
								String msg) {
							 myDialog.dismiss();
							Toast.makeText(XiuGaiInfo.this, "联网失败,检查网络",
									Toast.LENGTH_LONG).show();
						}

					});
		}
	}
	 

	//=======================================================
}
