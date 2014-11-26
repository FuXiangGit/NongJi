package com.fyx.nongji;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class YuMingChoice extends Activity {

	/**
	 * @Fields password : 域名解析下拉选择
	 */
	@ViewInject(R.id.spyuming)
	private ListView viewYuMing;
	private ArrayAdapter<String> adpYuMing;
	private List<String> listYuMing;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yuming);
		ViewUtils.inject(this);
		
		//域名解析
		initYuMing();
		//并保存
		selectYuMing();
	}
	
	private void selectYuMing() {
		sp = YuMingChoice.this.getSharedPreferences("NONGJI", MODE_PRIVATE);
		viewYuMing.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String ym = viewYuMing.getItemAtPosition(position).toString();
				Log.d("jack","onItemSelected答onItemSelected之"+ym);
				Editor editor = sp.edit();
				editor.putString("YUMING", ym);
				editor.commit();
//				startActivity(new Intent(YuMingChoice.this,LoginActivity.class));
				finish();
				
			}
		});
		/*spYuMing.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}

			
		});*/
	}

	private void initYuMing() {
		try {
			Log.d("jack","0答应之");
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
			String name = "www.baidu.com";
			 InetAddress[] addresses = InetAddress.getAllByName(name);
			 listYuMing = new ArrayList<String>();
//			java.net.InetAddress address=java.net.InetAddress.getByName("www.baidu.com");
			 for (int i = 0; i < addresses.length; i++) {
	                System.out.println("http://" + addresses[i].getHostAddress()+"/");  
	                listYuMing.add("http://" + addresses[i].getHostAddress()+"/");
	            } 
			 adpYuMing = new ArrayAdapter<String>(YuMingChoice.this, android.R.layout.simple_spinner_item,listYuMing);
			 viewYuMing.setAdapter(adpYuMing);
			 LogUtils.d(addresses[0]+"0答应之");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
		
		Log.d("jack","答应之");
	}
	
}
