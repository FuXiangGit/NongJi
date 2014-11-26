package com.fyx.nongji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class XiuGaiState extends Activity {

	SharedPreferences sp;
	String changeID = null;
	int uType;
	RadioButton radioGuZhang;
	int stateInt;
	int typeInt;
	String checkd;
	String checkdType="农机手";
	private MyDialog myDialog;
	@ViewInject(R.id.etmsg)
	EditText sendmsg;
	@ViewInject(R.id.radioXian)
	RadioButton rdoXian;
	@ViewInject(R.id.radioMang)
	RadioButton rdoMang;
	@ViewInject(R.id.radioNJ)
	RadioButton radioNJ;
	@ViewInject(R.id.radioNH)
	RadioButton radioNH;
	//农地农机的类型修改
	@ViewInject(R.id.spcar)
	Spinner njSp;
	@ViewInject(R.id.btn_nd)
	Button btnNd;
	@ViewInject(R.id.nongdileixing)
	LinearLayout nongdileixing;
	@ViewInject(R.id.nongjileixing)
	LinearLayout nongjileixing;

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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xiugaistate);
		ViewUtils.inject(this);
		getSH();
		// 根据ID找到RadioGroup实例
		RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
		// 修改农机农户类型
		getAllType();
		// 绑定一个匿名监听器
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				// 获取变更后的选中项的ID
				int radioButtonId = arg0.getCheckedRadioButtonId();
				// 根据ID获取RadioButton的实例
				RadioButton rb = (RadioButton) XiuGaiState.this
						.findViewById(radioButtonId);
				// 更新文本内容，以符合选中项
				checkd = rb.getText().toString();
			}
		});
		RadioGroup userTypeGroup = (RadioGroup) this
				.findViewById(R.id.userTypeGroup);
		// 绑定一个匿名监听器
		userTypeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				// 获取变更后的选中项的ID
				int radioButtonId = arg0.getCheckedRadioButtonId();
				// 根据ID获取RadioButton的实例
				RadioButton rb = (RadioButton) XiuGaiState.this
						.findViewById(radioButtonId);
				// 更新文本内容，以符合选中项
				checkdType = rb.getText().toString().trim();
			}
		});
		/*
		Button btntijiao = (Button) findViewById(R.id.btntijiao);
		btntijiao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(XiuGaiState.this, checkd, 0).show();

				if (checkd.equals("闲")) {
					stateInt = 0;
				} else if (checkd.equals("忙")) {
					stateInt = 1;
				} else {// 障
					stateInt = 2;
				}
				httpUpState();
			}
		});
		Button btnTypetijiao = (Button) findViewById(R.id.btntypetijiao);
		btntijiao.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(XiuGaiState.this, checkdType, 0).show();
				
				if (checkdType.equals("农机手")) {
					typeInt = 1;
				} else{
					typeInt = 0;
				}
				httpChangeType();
			}
		});
	*/
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
				myDialog = new MyDialog(XiuGaiState.this, "正在链接……");
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
							XiuGaiState.this,
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
				XiuGaiState.this.finish();
				Toast.makeText(XiuGaiState.this, "链接服务器失败,检查网络！",
						Toast.LENGTH_LONG).show();
			}
		});

	}

	// =======================================================

	private void getSH() {
		sp = getSharedPreferences("uservalue", MODE_PRIVATE);
		radioGuZhang = (RadioButton) findViewById(R.id.radioGuZhang);
		if (sp != null) {
			changeID = sp.getString("uID", null);
			uType = sp.getInt("uType", -1);
			if (uType == 0) {
				radioGuZhang.setVisibility(View.GONE);
				nongjileixing.setVisibility(View.GONE);
				rdoXian.setText("有作业需求");
				rdoMang.setText("无作业需求");
				checkd = "有作业需求";
			}else{
				radioGuZhang.setVisibility(View.VISIBLE);
				nongdileixing.setVisibility(View.GONE);
				rdoXian.setText("闲");
				rdoMang.setText("忙");
				checkd = "闲";
			}
		}
	}
	
	//按钮点击事件
	@OnClick({R.id.btntijiao,R.id.btnmsgtijiao,R.id.btntypetijiao,R.id.btn_nd,R.id.btntypexiugai,R.id.delete_user})
	public void btnOnClick(View v){
		switch (v.getId()) {
		case R.id.btntijiao:
			Toast.makeText(XiuGaiState.this, checkd, 0).show();

			if (checkd.equals("闲")||checkd.equals("有作业需求")) {
				stateInt = 0;
			} else if (checkd.equals("忙")||checkd.equals("无作业需求")) {
				stateInt = 1;
			} else {// 障
				stateInt = 2;
			}
			httpUpState();
			break;
		case R.id.btnmsgtijiao:
			String msg = sendmsg.getText().toString().trim();
			if(StringUtils.isEmpty(msg)){
				Toast.makeText(XiuGaiState.this, "请填写发送信息", Toast.LENGTH_LONG).show();
			}else{
				httpSendMSG();
			}
			break;
		case R.id.btntypetijiao:
			Toast.makeText(XiuGaiState.this, checkdType, 0).show();
			
			if (checkdType.equals("农机手")) {
				typeInt = 1;
			} else{
				typeInt = 0;
			}
			httpChangeType();

			break;
		case R.id.btn_nd:
			//清空数据
			ndstringBuilder.delete( 0, ndstringBuilder.length() );
			new AlertDialog.Builder(this)
					.setTitle("农地类型")
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
						      Toast.makeText(XiuGaiState.this, ndstringBuilder.toString(), Toast.LENGTH_SHORT).show();
						}
						})
//					.setPositiveButton("确定", null)
					.setNegativeButton("取消", null).show();
			break;
		case R.id.btntypexiugai:
			httpTypeXiuGai();
			break;
		case R.id.delete_user:
			httpDeleteSelf();
			break;
		default:
			break;
		}
	}
	/**
	 * 删除用户
	 */
	private void httpDeleteSelf() {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", changeID);
		final String URL = Constants.HOST + "EasyCar/deletePhoneUserId.do";
		http.send(HttpMethod.POST, URL, params, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				myDialog = new MyDialog(XiuGaiState.this, "正在删除……");
				myDialog.show();
				LogUtils.d("onStart" + URL);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtils.d("responseInfo的result=" + responseInfo.result);
				myDialog.dismiss();
				JSONObject jObj;
				int myresult;
				try {
					jObj = new JSONObject(responseInfo.result);
					myresult = jObj.getInt("success");
					// 修改成功
					if (myresult > 0) {
						Toast.makeText(XiuGaiState.this, "删除成功！",
								Toast.LENGTH_LONG).show();
						Intent intent = new Intent(XiuGaiState.this,LoginActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(XiuGaiState.this, "删除失败！",
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				myDialog.dismiss();
				Toast.makeText(XiuGaiState.this, "链接服务器失败，请检查网络！",
						Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * 修改农地和农机类型
	 */
	private void httpTypeXiuGai() {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		if (uType==1) {
			params.addBodyParameter("typeid", njTypeKeyNO.trim());
		}else{
			params.addBodyParameter("typeid", ndstringBuilder.toString().trim());
		}
		params.addBodyParameter("id", changeID);
		params.addBodyParameter("type", uType+"");
		final String URL = Constants.HOST + "EasyCar/updatePhoneCarType.do";
		http.send(HttpMethod.POST, URL, params, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				myDialog = new MyDialog(XiuGaiState.this, "正在发送……");
				myDialog.show();
				LogUtils.d("onStart" + URL);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtils.d("responseInfo的result=" + responseInfo.result);
				myDialog.dismiss();
				JSONObject jObj;
				int myresult;
				try {
					jObj = new JSONObject(responseInfo.result);
					myresult = jObj.getInt("success");
					// 修改成功
					if (myresult > 0) {
						Toast.makeText(XiuGaiState.this, "修改成功！",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(XiuGaiState.this, "修改失败！",
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				myDialog.dismiss();
				Toast.makeText(XiuGaiState.this, "链接服务器失败，请检查网络！",
						Toast.LENGTH_LONG).show();
			}
		});
	}

	private void httpSendMSG() {
		
		String stateUp = sendmsg.getText().toString().trim();
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", changeID);
		params.addBodyParameter("remark", stateUp);
		final String URL = Constants.HOST + "EasyCar/updateUserRemarkPhone.do";
		http.send(HttpMethod.POST, URL, params, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				myDialog = new MyDialog(XiuGaiState.this, "正在发送……");
				myDialog.show();
				LogUtils.d("onStart" + URL);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtils.d("responseInfo的result=" + responseInfo.result);
				myDialog.dismiss();
				JSONObject jObj;
				int myresult;
				try {
					jObj = new JSONObject(responseInfo.result);
					myresult = jObj.getInt("success");
					// 修改成功
					if (myresult > 0) {
						Toast.makeText(XiuGaiState.this, "信息发送成功！",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(XiuGaiState.this, "信息发送失败！",
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				myDialog.dismiss();
				Toast.makeText(XiuGaiState.this, "链接服务器失败，请检查网络！",
						Toast.LENGTH_LONG).show();
			}
		});

	}

	private void httpUpState() {

		String stateUp = stateInt + "";
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", changeID);
		params.addBodyParameter("stateid", stateUp);
		final String URL = Constants.HOST + "EasyCar/updateUserStatePhone.do";
//		params.addBodyParameter("name", "abcdefg");
		http.send(HttpMethod.POST, URL, params, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				myDialog = new MyDialog(XiuGaiState.this, "正在修改……");
				myDialog.show();
				LogUtils.d("onStart" + URL);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtils.d("responseInfo的result=" + responseInfo.result);
				myDialog.dismiss();
				JSONObject jObj;
				int myresult;
				try {
					jObj = new JSONObject(responseInfo.result);
					myresult = jObj.getInt("success");
					// 修改成功
					if (myresult > 0) {
						Toast.makeText(XiuGaiState.this, "车状修改成功！",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(XiuGaiState.this, "车状修改失败！",
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				myDialog.dismiss();
				Toast.makeText(XiuGaiState.this, "链接服务器失败，请检查网络！",
						Toast.LENGTH_LONG).show();
			}
		});
	}
		private void httpChangeType() {
			
			String stateUp = typeInt + "";
			HttpUtils http = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("id", changeID);
			params.addBodyParameter("typeid", stateUp);
			final String URL = Constants.HOST + "EasyCar/updateUserZhuanHuanPhone.do";
			http.send(HttpMethod.POST, URL, params, new RequestCallBack<String>() {
				
				@Override
				public void onStart() {
					super.onStart();
					myDialog = new MyDialog(XiuGaiState.this, "正在修改……");
					myDialog.show();
					LogUtils.d("onStart" + URL);
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					LogUtils.d("responseInfo的result=" + responseInfo.result);
					myDialog.dismiss();
					JSONObject jObj;
					int myresult;
					try {
						jObj = new JSONObject(responseInfo.result);
						myresult = jObj.getInt("success");
						// 修改成功
						if (myresult > 0) {
							Toast.makeText(XiuGaiState.this, "用户类型修改成功！",
									Toast.LENGTH_LONG).show();
							startActivity(new Intent(XiuGaiState.this,LoginActivity.class));
						} else {
							Toast.makeText(XiuGaiState.this, "用户类型修改失败！",
									Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void onFailure(HttpException error, String msg) {
					// TODO Auto-generated method stub
					myDialog.dismiss();
					Toast.makeText(XiuGaiState.this, "链接服务器失败，请检查网络！",
							Toast.LENGTH_LONG).show();
				}
			});

	}
}
