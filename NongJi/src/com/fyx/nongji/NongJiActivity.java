package com.fyx.nongji;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.VersionInfo;
import com.fyx.map.PoiSearchDemo;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nongji.tools.ProgressWebView;

public class NongJiActivity extends Activity {
	private static final String LTAG = NongJiActivity.class.getSimpleName();

//	// webView控件定义
//	@ViewInject(R.id.main_web_view)
//	private WebView mainWeb;
	private WebSettings settings;
	ProgressBar progressBar;
	ProgressWebView webview;
	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Log.d(LTAG, "action: " + s);
			// TextView text = (TextView) findViewById(R.id.text_Info);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				LogUtils.e("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				LogUtils.d("网络出错");
			}
		}
	}

	private SDKReceiver mReceiver;

	// 继承方法-------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_nong_ji);
		ViewUtils.inject(this);
		LogUtils.d("欢迎使用百度地图Android SDK v" + VersionInfo.getApiVersion());

		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);

		initWebView();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);
	}

	// --------------------------------------------------------------------------------
	@OnClick({ R.id.btn_myLocation, R.id.btn_xiugai, R.id.btn_state })
	public void myClick(View v) {
		switch (v.getId()) {
		case R.id.btn_myLocation:
			// Intent intent = null;
			// intent = new Intent(NongJiActivity.this, BaseMapDemo.class);
			// this.startActivity(intent);
			// startActivity(new Intent(NongJiActivity.this,
			// BaseMapDemo.class));
			// startActivity(new Intent(NongJiActivity.this,
			// LocationDemo.class));
			startActivity(new Intent(NongJiActivity.this, PoiSearchDemo.class));

			break;
		case R.id.btn_xiugai:
			Intent intent = new Intent(NongJiActivity.this, XiuGaiInfo.class);
			startActivity(intent);

			break;
		case R.id.btn_state:
			Intent myintent = new Intent(NongJiActivity.this, XiuGaiState.class);
			startActivity(myintent);

			break;

		default:
			break;
		}
	}

	// --------------------------------------------------------------------------
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {

	/*	// requestWindowFeature(R.id.main_web_view);
		// 生成水平进度条
		progressBar = new ProgressBar(this, null,
				android.R.attr.progressBarStyleHorizontal);

		settings = mainWeb.getSettings();
		settings.setSupportZoom(true); // 支持缩放
		settings.setBuiltInZoomControls(true); // 启用内置缩放装置

		settings.setJavaScriptEnabled(true); // 启用JS脚本
		String baseURL = "http://www.csszengarden.com"; // 根URL
		mainWeb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mainWeb.loadUrl(baseURL);
		// mainWeb.loadDataWithBaseURL(baseURL, html, "text/html", "utf-8",
		// null);
		mainWeb.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Activity和Webview根据加载程度决定进度条的进度大小
				// 当加载到100%的时候 进度条自动消失
				NongJiActivity.this.setProgress(progress * 100);
			}
		});
		mainWeb.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});*/
		String baseURL = "http://www.baidu.com"; // 根URL
		webview = (ProgressWebView) findViewById(R.id.webview);
		  webview.getSettings().setJavaScriptEnabled(true);
	        webview.setDownloadListener(new DownloadListener() {
	            @Override
	            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
	                if (url != null && url.startsWith("http://"))
	                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	            }
	        });
	        webview.loadUrl(baseURL);
	        webview.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}
			});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
