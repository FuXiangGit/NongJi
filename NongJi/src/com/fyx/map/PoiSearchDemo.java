package com.fyx.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.fyx.adapter.UserListAdapter;
import com.fyx.nongji.LoginActivity;
import com.fyx.nongji.R;
import com.fyx.nongji.RegisterNotVIPActivity;
import com.fyx.nongji.XiuGaiInfo;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nongji.tools.Constants;
import com.nongji.tools.MyDialog;
import com.nongji.vo.SearchUser;

/**
 * 演示poi搜索功能
 */
public class PoiSearchDemo extends FragmentActivity implements
		 OnGetGeoCoderResultListener {

//	private PoiSearch mPoiSearch = null;
	// private SuggestionSearch mSuggestionSearch = null;
	private BaiduMap mBaiduMap = null;
	// LocationClientOption option = new LocationClientOption();
	/**
	 * 搜索关键字输入窗口
	 */
	// private AutoCompleteTextView keyWorldsView = null;
	// private ArrayAdapter<String> sugAdapter = null;
	private int load_Index = 1;

	/**
	 * 定位相关
	 */
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	// MapView mMapView;
	Fragment fragmentManager;
	boolean isFirstLoc = true;// 是否首次定位
	// 坐标保存为全局
	LatLng locationll;
	double myLatitude;
	double myLongitude;
	// 心跳
	int heartBreak;
	String userID;
	int uType;
	private SharedPreferences sp;
	private Editor editor;
	// 发送坐标
	private static final int UPLOAD = 0;
	// Thread myThread;
	// 泡泡图层显示用户的信息
	private PopupWindow pop;
	private Button xiangxi_info;
	private Button take_phone;
	/**
	 * 添加搜索到的位置
	 */
	private Marker mMarkerAdd;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bdX;
	BitmapDescriptor bdM;
	BitmapDescriptor bdZ;
	
	BitmapDescriptor bdCar;
	
	/**
	 * 图片的自定义添加文字
	 */
	private Bitmap imgMarkerX;
	private Bitmap imgMarkerM;
	private Bitmap imgMarkerZ;
	
	private Bitmap imgMarkerCar;
	private int width, height; // 图片的高度和宽带
	private Bitmap imgTemp; // 临时标记图

//	@ViewInject(R.id.chepai)
//	private EditText chePai;
	@ViewInject(R.id.username)
	private EditText userName;
	@ViewInject(R.id.phone)
	private EditText inputPhone;
	@ViewInject(R.id.zhuce)
	private LinearLayout parentLinearLayout;
	@ViewInject(R.id.spmeters)
	private Spinner spMeter;
	@ViewInject(R.id.zhuangtai)
	private Spinner spZhuangTai;
	@ViewInject(R.id.btnlist)
	private Button btnlist;
	@ViewInject(R.id.liebiao)
	private LinearLayout liebiao;
	@ViewInject(R.id.userlist)
	private ListView userlist;
	@ViewInject(R.id.isxsNJ)
	private LinearLayout isXianshiNJ;
	@ViewInject(R.id.isxsND)
	private LinearLayout isXianshiND;
	@ViewInject(R.id.spcar)
	private Spinner njSp;
	@ViewInject(R.id.btn_nd)
	private Spinner ndSp;

	String spSelect = null;
	private int myMeter = 5000;
	String spZTSelect = null;
	/**选择查询的车状*/
	String selectZT = "-1";
	
	int totalPage = 1;

	private MyDialog myDialog;
	// 保存获取的查询 用户
	ArrayList<SearchUser> sUserList = new ArrayList<SearchUser>();
	/** 农地和农机 */
	HashMap<Integer, String> mapNH = new HashMap<Integer, String>();
	List<String> strND = new ArrayList<String>();
	String[] strsND = null;
	HashMap<Integer, String> mapCAR = new HashMap<Integer, String>();
	List<String> strCAR = new ArrayList<String>();
	private String njType;
	private String ndType;
	//上传的农机类型编号
	String njTypeKeyNO="-1";
	String ndTypeKeyNO="-1";
	
	// 反编码功能
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	//缩放功能
	MapStatusUpdate u1;
	float selectZoom;

	// pop点击单独用户信息保存
	private String ownName;
	private String ownAddr;
	private String ownCarID;
	private String ownPhone;
	private String ownMSG;
	//获得的类型车或者用户
	private String ownTypeMsg;

	// =========================================================================11111111111111111
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poisearch);
		ViewUtils.inject(this);
		//获取对应的农机农地信息
		getAllType();
		// 获取用户登陆后返回信息
		initUser();
		// 控件初始化
		initView();
		// 弹出泡泡初始化
		// initPopuWindow();
		// sendLocation();
		// 绿色闲置
		imgMarkerX = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_gcoding_green);
		// 黄色繁忙
		imgMarkerM = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_gcoding_yellow);
		// 红色故障
		imgMarkerZ = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_gcoding);
		// 红色故障
		imgMarkerCar = BitmapFactory.decodeResource(getResources(),
				R.drawable.car);
		width = imgMarkerX.getWidth();
		height = imgMarkerX.getHeight();

		// 自定义图片添加

		bdX = BitmapDescriptorFactory.fromBitmap(createBitmap('闲', imgMarkerX));
		bdM = BitmapDescriptorFactory.fromBitmap(createBitmap('忙', imgMarkerM));
		bdZ = BitmapDescriptorFactory.fromBitmap(createBitmap('障', imgMarkerZ));
		bdCar = BitmapDescriptorFactory.fromBitmap(createBitmap('车', imgMarkerCar));

		// 初始化搜索模块，注册搜索事件监听
	/*	mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);*/
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		fragmentManager = getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager()
				.findFragmentById(R.id.map))).getBaiduMap();
		// ------------------------------------------------定位相关
		//普通定位
		mCurrentMode = LocationMode.NORMAL;
//		mCurrentMode = LocationMode.FOLLOWING;
		// 修改为自定义marker
		mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_geo);
		 mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
		 mCurrentMode, true, mCurrentMarker));
		// 开启定位图层
		MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(13.0f);
		mBaiduMap.animateMapStatus(u);
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		// 只有设置了这个才能够获得地址信息
		option.setAddrType("all");
		// option.setPoiExtraInfo(true);
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);// 扫描时间毫秒
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		// --------------------------------------------------

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
//		mPoiSearch.destroy();
		// mSuggestionSearch.destroy();

		// 添加回收 bitmap 资源
		bdX.recycle();
		bdM.recycle();
		bdZ.recycle();
		bdCar.recycle();
		mSearch.destroy();
		handler.removeCallbacks(upload);
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	// ============================================================================
	
	private void initUser() {
		sp = getSharedPreferences("uservalue", MODE_PRIVATE);
		editor = sp.edit();
		heartBreak = sp.getInt("uNO", 10);
		userID = sp.getString("uID", null);
		uType = sp.getInt("uType", -1);
		if (uType == 1) {
			//本人农机手
			isXianshiNJ.setVisibility(View.GONE);
			inputPhone.setWidth(260);
		}else{
			//本人农地
			isXianshiND.setVisibility(View.GONE);
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
					myDialog = new MyDialog(PoiSearchDemo.this, "正在获取信息……");
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
						//农地类型
						mapNH.put(-1, "全部");
						strND.add("全部");
						for (int i = 0; i < arrND.length(); i++) {
							ndObj = arrND.getJSONObject(i);
							inID = ndObj.optInt("TYPEID");
							inObj = ndObj.optString("TYPENAME");
							mapNH.put(inID, inObj);
							strND.add(inObj);
						}
						strsND = (String[]) strND.toArray(new String[strND.size()]);
						// 建立Adapter并且绑定数据源
						ArrayAdapter<String> sp_Adapter1 = new ArrayAdapter<String>(
								PoiSearchDemo.this,
								R.layout.myspinner, strND);
						sp_Adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
						ndSp.setAdapter(sp_Adapter1);
						ndType = ndSp.getSelectedItem().toString();
						ndSp.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								ndType = ndSp.getSelectedItem().toString();
								Set<Integer> mapSet =  mapNH.keySet();	//获取所有的key值 为set的集合
								Iterator<Integer> itor =  mapSet.iterator();//获取key的Iterator便利
								while(itor.hasNext()){//存在下一个值
									int key = itor.next();//当前key值
								if(mapNH.get(key).equals(ndType)){//获取value 与 所知道的value比较
									System.out.println("你要找的key ："+key);//相等输出key
									ndTypeKeyNO = key+"";
									LogUtils.d(ndTypeKeyNO);
									}
								}
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {
							}
							});
						//农机类型
						arrCar = objResult.getJSONArray("cartype");
						mapCAR.put(-1, "全部");
						strCAR.add("全部");
						for (int i = 0; i < arrCar.length(); i++) {
							ndObj = arrCar.getJSONObject(i);
							inID = ndObj.optInt("ID");
							inObj = ndObj.optString("TYPENAME");
							mapCAR.put(inID, inObj);
							strCAR.add(inObj);
						}
						// 建立Adapter并且绑定数据源
						ArrayAdapter<String> sp_Adapter2 = new ArrayAdapter<String>(
								PoiSearchDemo.this,
								R.layout.myspinner, strCAR);
						sp_Adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
						njSp.setAdapter(sp_Adapter2);
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
									LogUtils.d(njTypeKeyNO);
									}
								}
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {
							}
							});
						
						LogUtils.d(strND.size() + "两个长度" + strCAR.size());
						
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

				@Override
				public void onFailure(HttpException error, String msg) {
					myDialog.dismiss();
					PoiSearchDemo.this.finish();
					Toast.makeText(PoiSearchDemo.this, "链接服务器失败,检查网络！",
							Toast.LENGTH_LONG).show();
				}
			});

		}

	// ---------------------------------------------------------------------------------
	private void initView() {
		String[] mItems = getResources().getStringArray(R.array.spinnername);
		String[] zhuangTai;
		if(uType==1){
			zhuangTai = getResources().getStringArray(R.array.spnonghuzhuangtai);
		}else{
			zhuangTai = getResources().getStringArray(R.array.spnongjizhuangtai);
		}
		ArrayAdapter<String> zt_Adapter = new ArrayAdapter<String>(this,
				R.layout.myspinner, zhuangTai);
		zt_Adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spZhuangTai.setAdapter(zt_Adapter);
		spZTSelect = spZhuangTai.getSelectedItem().toString();
		if(spZTSelect.equals("所有")){
			selectZT="-1";
		}
		spZhuangTai.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				spZTSelect = spZhuangTai.getSelectedItem().toString();
				if (spZTSelect.equals("所有")) {
					selectZT="-1";
				} else if (spZTSelect.equals("闲置")||spZTSelect.equals("有需求")) {
					selectZT="0";
				} else if(spZTSelect.equals("忙碌")||spZTSelect.equals("无需求")){
					selectZT="1";
				}else{
					selectZT="2";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		// 建立Adapter并且绑定数据源
		ArrayAdapter<String> sp_Adapter = new ArrayAdapter<String>(this,
				R.layout.myspinner, mItems);
		sp_Adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		// 绑定 Adapter到控件
		spMeter.setAdapter(sp_Adapter);
		spSelect = spMeter.getSelectedItem().toString();
		if (spSelect.equals("5公里")) {
			myMeter = 5000;
		}
		spMeter.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				spSelect = spMeter.getSelectedItem().toString();
				if (spSelect.equals("5公里")) {
					myMeter = 5000;
//					 u1 = MapStatusUpdateFactory.zoomTo(13.0f);
					 selectZoom = 13.0f;
				} else if (spSelect.equals("10公里")) {
//					 u1 = MapStatusUpdateFactory.zoomTo(12.0f);
					 selectZoom = 12.0f;
					myMeter = 10000;
				} else if (spSelect.equals("15公里")) {
//					u1 = MapStatusUpdateFactory.zoomTo(11.5f);
					selectZoom = 11.5f;
					myMeter = 15000;
				} else if (spSelect.equals("20公里")) {
//					u1 = MapStatusUpdateFactory.zoomTo(11.0f);
					selectZoom = 11.0f;
					myMeter = 20000;
				} else if(spSelect.equals("30公里")){
//					u1 = MapStatusUpdateFactory.zoomTo(10.5f);
					selectZoom = 10.5f;
					myMeter = 30000;
				}else{
//					u1 = MapStatusUpdateFactory.zoomTo(6.0f);
					selectZoom = 6.0f;
					myMeter = 1000000;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	// --------------------------------------------------------------------------------
	private void popupClick() {
		if (pop.isShowing()) {
			// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
			pop.dismiss();
		} else {
			// 显示窗口
			pop.showAtLocation(parentLinearLayout, Gravity.BOTTOM, 0, 0);
		}
	}

	// -----------------------------------------------------------------------
	private void initPopuWindow(String name, final String phone, String Addr,
			String chepai,String msg,String typeMsg) {
		LayoutInflater inflater = LayoutInflater.from(this);
		// 引入窗口配置文件
		View view = inflater.inflate(R.layout.user_show, null);
		pop = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		// 需要设置一下此参数，点击外边可消失
		pop.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		pop.setOutsideTouchable(true);
		// 设置此参数获得焦点，否则无法点击
		pop.setFocusable(true);

		TextView popuser = (TextView) view.findViewById(R.id.popuser);
		TextView popadd = (TextView) view.findViewById(R.id.popadd);
		TextView popphone = (TextView) view.findViewById(R.id.popphone);
		TextView popchepai = (TextView) view.findViewById(R.id.popchepai);
		TextView popomsg = (TextView) view.findViewById(R.id.popMsg);
		TextView popNDTypeMsg = (TextView) view.findViewById(R.id.popNDTypeMsg);
		LinearLayout isXSChePai = (LinearLayout) view.findViewById(R.id.isXSChePai);
		LinearLayout popisndshow = (LinearLayout) view.findViewById(R.id.popisndshow);
		if(uType==1){
			//本人是农机手，农机手信息不见
//			popchepai.setVisibility(View.GONE);
			isXSChePai.setVisibility(View.GONE);
		}else{
			popisndshow.setVisibility(View.GONE);
		}
		popuser.setText(name);
		popadd.setText(Addr);
		popphone.setText(phone);
		popchepai.setText(typeMsg);
		popomsg.setText(msg);
		popNDTypeMsg.setText(typeMsg);
		// 进入详细信息页面
		xiangxi_info = (Button) view.findViewById(R.id.xiangxiinfo);
		// 打电话
		take_phone = (Button) view.findViewById(R.id.takephone);
		xiangxi_info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		});
		take_phone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// 传入服务， parse（）解析号码
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ phone));
				startActivity(intent);
				pop.dismiss();
			}
		});

	}

	// ------------------------------------------------------------------------------------

	/**
	 * 影响搜索按钮点击事件
	 * 
	 * @param v
	 */
	public void searchButtonProcess(View v) {
		load_Index=1;
		//--------------------------------
		// 保存坐标位置
		mBaiduMap.clear();
		locationll = new LatLng(myLatitude,myLongitude);
//		u1 = MapStatusUpdateFactory
//				.newLatLng(locationll);
		u1 = MapStatusUpdateFactory.newLatLngZoom(locationll, selectZoom);
		mBaiduMap.animateMapStatus(u1);
		addCustomElementsDemo();
		//--------------------------------
		httpGetSearch();
		// 添加搜索到的结果到图上
	}

	public void ShowList(View v) {
		if (liebiao.getVisibility() == View.GONE) {
			liebiao.setVisibility(View.VISIBLE);
			btnlist.setText("地图");
		} else {
			liebiao.setVisibility(View.GONE);
			btnlist.setText("列表");
		}
		// 添加搜索到的结果到图上
	}

	/**
	 * 查询结果
	 */
	private void httpGetSearch() {
		heartBreak = sp.getInt("uNO", 10);
		userID = sp.getString("uID", null);
		uType = sp.getInt("uType", -1);

		String myID = userID + "";
		String myType = uType + "";
		String myName = userName.getText().toString().trim();
		//车牌不要了
//		String myCarID = chePai.getText().toString().trim();
		String myPhone = inputPhone.getText().toString().trim();
		String kmNumber = myMeter + "";
		String yeShu = load_Index+"";
		LogUtils.d("onStart" + myID +"onStart" + myType +"onStart" + myName +"onStart" + "onStart" +myPhone
				+ kmNumber);

		final String URL = Constants.HOST
				+ "EasyCar/queryMapNoddessPhoneUsera.do";
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", myID);
		params.addBodyParameter("userType", myType);
		params.addBodyParameter("username", myName);
		//车牌
//		params.addBodyParameter("carid", myCarID);
		params.addBodyParameter("phone", myPhone);
		params.addBodyParameter("kmNumber", kmNumber);
		params.addBodyParameter("state",selectZT);
		params.addBodyParameter("yeshu",yeShu);
		//车类型农地类型
		if(uType==0){
			//本人农户
			LogUtils.d(njTypeKeyNO+"aaa111");
			LogUtils.d(ndTypeKeyNO);
			params.addBodyParameter("carid", njTypeKeyNO.trim());
		}else{
			LogUtils.d(njTypeKeyNO+"bb22"+uType);
			LogUtils.d(ndTypeKeyNO);
			//本人农机手
			params.addBodyParameter("carid", ndTypeKeyNO.trim());
		}
		http.send(HttpMethod.POST, URL, params, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				myDialog = new MyDialog(PoiSearchDemo.this, "数据加载中……");
				myDialog.show();
				LogUtils.d("onStart" + URL);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				myDialog.dismiss();
				LogUtils.d("responseInfo的result=" + responseInfo.result);
				JSONObject allObj;
				JSONArray jsonArr;
				JSONArray jsonArrCar;
				JSONArray jsonArrTypeMsg;
				
				JSONObject jsonObj;
				//3333
				JSONObject jsonTypeObj;
				sUserList.clear();
				String userID;
				Double userLA;
				Double userLO;
				String userPhone;
				String userName;
				String carID;
				int userState;
				int searchUserID;
				String userMsg;
				//33333
				String userTypeMsg;
				LogUtils.d("没有返回值显示1111");
				mBaiduMap.clear();
				addCustomElementsDemo();
				LogUtils.d("没有返回值显示222");
				try {
					Log.d("jack",responseInfo.result);
					allObj = new JSONObject(responseInfo.result);
					totalPage = allObj.optInt("numbers");
					//1111
					jsonArr = allObj.getJSONArray("map");
					//2222
					jsonArrCar = allObj.getJSONArray("car");
					//3333
					jsonArrTypeMsg = allObj.getJSONArray("type");
					//-------------------
					LogUtils.d("没有返回值显示"+jsonArrCar.length()+"dkd"+jsonArr.length());
					for (int i = 0; i < jsonArr.length(); i++) {
						jsonObj = jsonArr.getJSONObject(i);
						//另一个对象
						jsonTypeObj = jsonArrTypeMsg.getJSONObject(i);
						
						SearchUser nearUser = new SearchUser();
						userID = jsonObj.optString("USERID");
						userLA = jsonObj.optDouble("LA");
						userLO = jsonObj.optDouble("LO");
						userPhone = jsonObj.optString("PHONE");
						userName = jsonObj.optString("USERNAME");
						carID = jsonObj.optString("CARID");
						userState = jsonObj.optInt("STATE");
						userMsg = jsonObj.optString("REMARK");
						//搜索到的车的状态和农田的转台
						userTypeMsg = jsonTypeObj.optString("NAME");
						
						nearUser.setUserID(userID);
						nearUser.setLa(userLA);
						nearUser.setLo(userLO);
						nearUser.setPhone(userPhone);
						nearUser.setUserName(userName);
						nearUser.setCarID(carID);
						nearUser.setState(userState);
						nearUser.setMSG(userMsg);
						//3333
						nearUser.setTypeMsg(userTypeMsg);
						

						sUserList.add(nearUser);
						LatLng addLL = new LatLng(userLA, userLO);
						OverlayOptions ooAdd;
						if (userState == 0) {
							ooAdd = new MarkerOptions().position(addLL)
									.icon(bdX).zIndex(5);
						} else if (userState == 1) {
							ooAdd = new MarkerOptions().position(addLL)
									.icon(bdM).zIndex(5);
						} else {
							ooAdd = new MarkerOptions().position(addLL)
									.icon(bdZ).zIndex(5);
						}
						mMarkerAdd = (Marker) (mBaiduMap.addOverlay(ooAdd));
						mMarkerAdd.setTitle(userID + "");
						// Toast.makeText(PoiSearchDemo.this,
						// mMarkerAdd.getTitle(), 0).show();
					}
					for(int i= 0;i<jsonArrCar.length();i++){
						jsonObj = jsonArrCar.getJSONObject(i);
						SearchUser nearUser = new SearchUser();
						userPhone = jsonObj.optString("CARPHONE");
						userLA = jsonObj.optDouble("CLA");
						userLO = jsonObj.optDouble("CLO");
						userID = jsonObj.optString("CARID");
						userName = jsonObj.optString("USERNAME");
						userState = jsonObj.optInt("CARSTATE");
						userMsg = jsonObj.optString("CARTYPE");
						carID = jsonObj.optString("CARNAME");
						
						nearUser.setUserID(userID);
						nearUser.setLa(userLA);
						nearUser.setLo(userLO);
						nearUser.setPhone(userPhone);
						nearUser.setUserName(userName);
						nearUser.setCarID(carID);
						nearUser.setState(userState);
						nearUser.setMSG(userMsg);
						
						sUserList.add(nearUser);
						LatLng addLL = new LatLng(userLA, userLO);
						OverlayOptions ooAdd;
						if (userState == 0) {
							ooAdd = new MarkerOptions().position(addLL)
									.icon(bdCar).zIndex(5);
						} else if (userState == 1) {
							ooAdd = new MarkerOptions().position(addLL)
									.icon(bdCar).zIndex(5);
						} else {
							ooAdd = new MarkerOptions().position(addLL)
									.icon(bdCar).zIndex(5);
						}
						mMarkerAdd = (Marker) (mBaiduMap.addOverlay(ooAdd));
						mMarkerAdd.setTitle(userID + "");
						
					}
					LogUtils.d("没有返回值显示"+jsonArrCar.length()+"dkd"+jsonArr.length());
					UserListAdapter userAdapter = new UserListAdapter(
							PoiSearchDemo.this, sUserList);
					userlist.setAdapter(userAdapter);
					userlist.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							myDialog = new MyDialog(PoiSearchDemo.this, "地址加载中……");
							myDialog.show();
							TextView tv = (TextView) view
									.findViewById(R.id.tvID);
							String phone = tv.getText().toString().trim();
							LatLng ll = null;
							for (SearchUser ownUser : sUserList) {
								if (phone.equals(ownUser.getUserID())) {
									ownName = ownUser.getUserName();
									ownCarID = ownUser.getCarID();
									ownPhone = ownUser.getPhone();
									ownMSG = ownUser.getMSG();
									ownTypeMsg = ownUser.getTypeMsg();
									ll = new LatLng(ownUser.getLa(), ownUser
											.getLo());
									//定位地图点击位置20141119-----------------------
									MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
									mBaiduMap.animateMapStatus(u);
									//------------------------------------
								}
							}
							// marker.getPosition();
							mSearch.reverseGeoCode(new ReverseGeoCodeOption()
									.location(ll));

						}
					});
					mBaiduMap
							.setOnMarkerClickListener(new OnMarkerClickListener() {

								@Override
								public boolean onMarkerClick(Marker marker) {
									myDialog = new MyDialog(PoiSearchDemo.this, "地址加载中……");
									myDialog.show();
									String markTitle = marker.getTitle();
									for (SearchUser ownUser : sUserList) {
										if (markTitle.equals(String
												.valueOf(ownUser.getUserID()))) {
											ownName = ownUser.getUserName();
											ownCarID = ownUser.getCarID();
											ownPhone = ownUser.getPhone();
											ownMSG = ownUser.getMSG();
											ownTypeMsg =ownUser.getTypeMsg();
										}
									}
									LatLng ll = new LatLng(
											marker.getPosition().latitude,
											marker.getPosition().longitude);
									// marker.getPosition();
									mSearch.reverseGeoCode(new ReverseGeoCodeOption()
											.location(ll));
									
									//定位地图点击位置20141119-----------------------
									MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
									mBaiduMap.animateMapStatus(u);
									//------------------------------------
									return false;
								}
							});
					Toast.makeText(PoiSearchDemo.this, "当前页条数"+sUserList.size() + "总页数"+totalPage, 0)
							.show();

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				myDialog.dismiss();
				Toast.makeText(PoiSearchDemo.this, "链接服务器失败，请检查网络！",
						Toast.LENGTH_LONG).show();
			}
		});
	}

	public void goToNextPage(View v) {
		if(load_Index<totalPage){
		load_Index++;
		httpGetSearch();
		}
	}
	public void goToBeforePage(View v) {
		if(load_Index>1){
		load_Index--;
		httpGetSearch();
		}
	}

	// -----------------------------------------------------------

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || fragmentManager == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				locationll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory
						.newLatLng(locationll);
				mBaiduMap.animateMapStatus(u);
				addCustomElementsDemo();
				handler.post(upload);
			}
			// 保存坐标位置
			myLatitude = location.getLatitude();
			myLongitude = location.getLongitude();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	// -------------------------------------------------------------------
	/**
	 * 添加点范围图层
	 */
	public void addCustomElementsDemo() {
//		Toast.makeText(PoiSearchDemo.this, myMeter + "", 0).show();
		// 添加圆
		OverlayOptions ooCircle = new CircleOptions().fillColor(0xAA66CCFF)
				.center(locationll).radius(myMeter);
		// mBaiduMap.clear();
		mBaiduMap.addOverlay(ooCircle);
	}


	// -----------------------------------------------------------自定义图片的文字添加
	// 穿件带字母的标记图片
	private Bitmap createBitmap(char letter, Bitmap imgMarker) {
		imgTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(imgTemp);
		Paint paint = new Paint(); // 建立画笔
		paint.setDither(true);
		paint.setFilterBitmap(true);
		Rect src = new Rect(0, 0, width, height);
		Rect dst = new Rect(0, 0, width, height);
		canvas.drawBitmap(imgMarker, src, dst, paint);

		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		textPaint.setTextSize(20.0f);
		textPaint.setTypeface(Typeface.DEFAULT_BOLD); // 采用默认的宽度
		textPaint.setColor(Color.WHITE);

		canvas.drawText(String.valueOf(letter), width / 2 - 9, height / 2 + 3,
				textPaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		// return (Drawable) new BitmapDrawable(getResources(), imgTemp);
		return imgTemp;

	}

	// ------------------------------------------------------------
	/**
	 * 发送坐标
	 */
	Handler handler = new Handler();
	Runnable upload = new Runnable() {
		@Override
		public void run() {
			httpUpLoadLocation();
			LogUtils.d(myLatitude + "坐标要上传" + heartBreak);
			handler.postDelayed(upload, heartBreak * 1000);
		}
	};

	// -----------------------------------------------------------
	/**
	 * 上传坐标
	 */
	protected void httpUpLoadLocation() {
		final String URL = Constants.HOST + "EasyCar/addLoLaNumberPhone.do";
		String uID = userID + "";
		String myLo = myLongitude + "";
		String myLa = myLatitude + "";
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid", uID.trim());
		params.addBodyParameter("lo", myLo);
		params.addBodyParameter("la", myLa);
		LogUtils.d(uID.trim() + "ddddd" + myLo + "ddd" + myLa);
		http.send(HttpMethod.POST, URL, params, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				LogUtils.d("onStart上传" + URL);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtils.d("responseInfo的result=" + responseInfo.result);
				Log.d("jack", "responseInfo的result=" + responseInfo.result);
				JSONObject jObj;
				int myresult;
				try {
					jObj = new JSONObject(responseInfo.result);
					myresult = jObj.getInt("success");
					if (myresult == 1) {
						LogUtils.d("上传成功！");
					} else {
						LogUtils.d("上传失败！");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtils.d("联网失败,检查网络");
			}
		});
	}

	// ------------------------------------------------------------

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(PoiSearchDemo.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		myDialog.dismiss();
		ownAddr = result.getAddress();
		initPopuWindow(ownName, ownPhone, ownAddr, ownCarID,ownMSG,ownTypeMsg);
		popupClick();
	}

}
