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

public class RegisterNotVIPActivity extends Activity {

	@ViewInject(R.id.phone)
	EditText phoneText;
	@ViewInject(R.id.random)
	EditText phoneYanZheng;
	@ViewInject(R.id.getRandom)
	ImageView imgRandom;
	@ViewInject(R.id.username)
	EditText username;
	@ViewInject(R.id.password)
	EditText password;
	@ViewInject(R.id.okpass)
	EditText okpass;
	@ViewInject(R.id.plateNO)
	EditText plateNO;
	@ViewInject(R.id.text_title)
	TextView textTitle;
	@ViewInject(R.id.nonghucansee)
	LinearLayout nonghucansee;
	@ViewInject(R.id.nongdileixing)
	LinearLayout nongdileixing;
	@ViewInject(R.id.nongjileixing)
	LinearLayout nongjileixing;
	@ViewInject(R.id.spcar)
	Spinner njSp;
	@ViewInject(R.id.btn_nd)
	Button btnNd;

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
	private CheckBox isrmb;
	private SharedPreferences sp;
	private Editor editor;

	/** 农地和农机 */
	HashMap<Integer, String> mapNH = new HashMap<Integer, String>();
	List<String> strND = new ArrayList<String>();
	String[] strsND = null;
	HashMap<Integer, String> mapCAR = new HashMap<Integer, String>();
	List<String> strCAR = new ArrayList<String>();
	boolean[] arrayNDSelected;
	StringBuilder ndstringBuilder = new StringBuilder();
	String njTypeKeyNO;
	private String njType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_not_register);
		ViewUtils.inject(this);
		getAllType();
		getUserType();
		getPhone();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Button button = (Button) findViewById(R.id.btn_back);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(RegisterNotVIPActivity.this,
						LoginActivity.class));
				finish();
			}
		});
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private void getUserType() {
		userType = getIntent().getStringExtra("USERTYPE");
		if (userType.equals("0")) {
			textTitle.setText("农户注册");
			nongjileixing.setVisibility(View.GONE);
		} else {
			nongdileixing.setVisibility(View.GONE);
			nonghucansee.setVisibility(View.GONE);
			textTitle.setText("农机手注册");
		}
	}

	/**
	 * 验证号码
	 */
	private void getPhone() {
		SIMCardInfo siminfo = new SIMCardInfo(RegisterNotVIPActivity.this);
		String simCard = siminfo.getNativePhoneNumber();
		if (simCard.equals("无SIM卡")) {
			Toast.makeText(RegisterNotVIPActivity.this, "无SIM卡！",
					Toast.LENGTH_LONG).show();
		} else if (simCard.equals("SIM卡被锁定或未知状态")) {
			Toast.makeText(RegisterNotVIPActivity.this, "SIM卡被锁定或未知状态，请您手动输入！",
					Toast.LENGTH_LONG).show();
		} else {
			System.out.println(siminfo.getProvidersName());
			System.out.println(siminfo.getNativePhoneNumber());
			// phoneText.setText(siminfo.getNativePhoneNumber());
			// phoneYanZheng.setText(siminfo.getProvidersName());
		}
	}

	// --------------------------------------------------------------
	@OnClick({ R.id.getRandom, R.id.register_free, R.id.btn_nd })
	public void myClick(View v) {
		switch (v.getId()) {
		case R.id.getRandom:
			strphone = phoneText.getText().toString().trim();
			if (Constants.isMobileNO(strphone)) {
				getcode = random();
				sendMsg(strphone, "您的验证码为:" + getcode);
				imgRandom.setVisibility(View.GONE);
			} else {
				Toast.makeText(RegisterNotVIPActivity.this, "请您输入正确手机号",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.register_free:
			strphone = phoneText.getText().toString().trim();

			String code = phoneYanZheng.getText().toString().trim();
			// if(StringUtils.isEmpty(code)){
			// Toast.makeText(RegisterNotVIPActivity.this,
			// "请输入验证码",Toast.LENGTH_SHORT)
			// .show();
			// }else if(code.equals(getcode)){
			initHttp();
			// }else{
			// Toast.makeText(RegisterNotVIPActivity.this,
			// "验证码不对请重新输入",Toast.LENGTH_SHORT)
			// .show();
			// phoneYanZheng.setText("");
			// }
			break;
		case R.id.btn_nd:
			//清空数据
			ndstringBuilder.delete( 0, ndstringBuilder.length() );
			new AlertDialog.Builder(this)
					.setTitle("农地类型")
//					.setMultiChoiceItems(
//						new String[] {"选项1", "选项2", "选项3", "选项4"},null,null)
					.setMultiChoiceItems(strsND, arrayNDSelected, new OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							arrayNDSelected[which] = isChecked;
							
						}
					})
					.setPositiveButton("确定",  new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						      for (int i = 0; i < arrayNDSelected.length; i++) {
						       if (arrayNDSelected[i] == true)
						       {
//						        stringBuilder.append(strsND[i] + ",");
						        Set<Integer> mapSet =  mapNH.keySet();	//获取所有的key值 为set的集合
								Iterator<Integer> itor =  mapSet.iterator();//获取key的Iterator便利
								while(itor.hasNext()){//存在下一个值
									int key = itor.next();//当前key值
								if(mapNH.get(key).equals(strsND[i])){//获取value 与 所知道的value比较
									System.out.println("你要找的key ："+key);//相等输出key
									 ndstringBuilder.append(key + ",");
									}
								}
						       }
						      }
						      Toast.makeText(RegisterNotVIPActivity.this, ndstringBuilder.toString(), Toast.LENGTH_SHORT).show();
						}
						})
//					.setPositiveButton("确定", null)
					.setNegativeButton("取消", null).show();

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
		// 用户名
		String strUser = username.getText().toString().trim();
		// 密码
		String strPass = password.getText().toString().trim();
		// 确认密码
		String strOkpass = okpass.getText().toString().trim();
		// 车牌
		String strPlateNO = plateNO.getText().toString().trim();

		if (StringUtils.isEmpty(strphone)) {
			Toast.makeText(RegisterNotVIPActivity.this, "手机号码不能为空",
					Toast.LENGTH_SHORT).show();
		} else if (!Constants.isMobileNO(strphone)) {
			Toast.makeText(RegisterNotVIPActivity.this, "请您输入正确手机号",
					Toast.LENGTH_SHORT).show();
		} else if (StringUtils.isEmpty(strUser)) {
			Toast.makeText(RegisterNotVIPActivity.this, "用户名不能为空",
					Toast.LENGTH_SHORT).show();
		} else if (StringUtils.isEmpty(strPass)) {
			Toast.makeText(RegisterNotVIPActivity.this, "密码不能为空",
					Toast.LENGTH_SHORT).show();
		} else if (!strOkpass.equals(strPass)) {
			Toast.makeText(RegisterNotVIPActivity.this, "请保证两次密码输入相同",
					Toast.LENGTH_SHORT).show();
		} else {
			HttpUtils http = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("userName", strUser);
			params.addBodyParameter("pwd", strOkpass);
			params.addBodyParameter("phone", strphone);
			//农田面积
			params.addBodyParameter("plateNO", strPlateNO);
//			if (userType.equals("1")) {
//				if (StringUtils.isEmpty(strPlateNO)) {
//					Toast.makeText(RegisterNotVIPActivity.this, "请输入车牌",
//							Toast.LENGTH_SHORT).show();
//				} else {
//					params.addBodyParameter("plateNO", strPlateNO);
//				}
//			}
			if (userType.equals("1")) {
				params.addBodyParameter("typeid", njTypeKeyNO.trim());
			}else{
				params.addBodyParameter("typeid", ndstringBuilder.toString().trim());
			}
			params.addBodyParameter("type", userType);
			final String URL = Constants.HOST + "EasyCar/addUserPhone"
					+ Constants.HOSTEND;
			http.send(HttpMethod.POST, URL, params,
					new RequestCallBack<String>() {

						@Override
						public void onStart() {
							super.onStart();
							myDialog = new MyDialog(
									RegisterNotVIPActivity.this, "正在链接……");
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
									Toast.makeText(RegisterNotVIPActivity.this,
											"注册成功！", Toast.LENGTH_LONG).show();
									editor.putString("phone", phoneText
											.getText().toString().trim());
									editor.putString("password", okpass
											.getText().toString().trim());
									editor.commit();
									Intent intent = new Intent(
											RegisterNotVIPActivity.this,
											LoginActivity.class);
									startActivity(intent);
								} else if (backResult == 0) {
									Toast.makeText(RegisterNotVIPActivity.this,
											"注册失败！", Toast.LENGTH_LONG).show();
								} else if (backResult == 2) {
									Toast.makeText(RegisterNotVIPActivity.this,
											"车牌号码重复！", Toast.LENGTH_LONG)
											.show();
								} else if (backResult == 3) {
									Toast.makeText(RegisterNotVIPActivity.this,
											"注册手机号码重复！", Toast.LENGTH_LONG).show();
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
							Toast.makeText(RegisterNotVIPActivity.this,
									"链接服务器失败,检查网络！", Toast.LENGTH_LONG).show();
						}

					});
		}
	}

	// --------------------------------------------------------
	private void getAllType() {
		HttpUtils http = new HttpUtils();
		final String URL = Constants.HOST + "EasyCar/queryTypeInfoPhone"
				+ Constants.HOSTEND;
		http.send(HttpMethod.POST, URL, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				myDialog = new MyDialog(RegisterNotVIPActivity.this, "正在链接……");
				myDialog.show();
				LogUtils.d(URL);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtils.d("responseInfo的result=" + responseInfo.result);
				myDialog.dismiss();
				JSONObject objResult;
				JSONArray arrND;
				JSONArray arrCar;
				JSONObject ndObj;
				int inID;
				String inObj;

				try {
					objResult = new JSONObject(responseInfo.result);
					arrND = objResult.getJSONArray("usertype");
					for (int i = 0; i < arrND.length(); i++) {
						ndObj = arrND.getJSONObject(i);
						inID = ndObj.optInt("TYPEID");
						inObj = ndObj.optString("TYPENAME");
						mapNH.put(inID, inObj);
						strND.add(inObj);
					}
					strsND = (String[]) strND.toArray(new String[strND.size()]);
					arrayNDSelected = new boolean[arrND.length()];
					// --------------农机类型
					arrCar = objResult.getJSONArray("cartype");
					for (int i = 0; i < arrCar.length(); i++) {
						ndObj = arrCar.getJSONObject(i);
						inID = ndObj.optInt("ID");
						inObj = ndObj.optString("TYPENAME");
						mapCAR.put(inID, inObj);
						strCAR.add(inObj);
					}

					// 建立Adapter并且绑定数据源
					ArrayAdapter<String> sp_Adapter1 = new ArrayAdapter<String>(
							RegisterNotVIPActivity.this,
							android.R.layout.simple_dropdown_item_1line, strCAR);
					njSp.setAdapter(sp_Adapter1);
					njType = njSp.getSelectedItem().toString();
					njSp.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							njType = njSp.getSelectedItem().toString();
							Set<Integer> mapSet =  mapCAR.keySet();	//获取所有的key值 为set的集合
							Iterator<Integer> itor =  mapSet.iterator();//获取key的Iterator便利
							while(itor.hasNext()){//存在下一个值
								int key = itor.next();//当前key值
							if(mapCAR.get(key).equals(njType)){//获取value 与 所知道的value比较
								System.out.println("你要找的key ："+key);//相等输出key
								njTypeKeyNO = key+"";
								}
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
						}});
					LogUtils.d(strND.size() + "两个长度" + strCAR.size());
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				myDialog.dismiss();
				RegisterNotVIPActivity.this.finish();
				Toast.makeText(RegisterNotVIPActivity.this, "链接服务器失败,检查网络！",
						Toast.LENGTH_LONG).show();
			}
		});

	}

	// =======================================================
}
