package com.nongji.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fyx.nongji.YuMingChoice;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class Constants {

	// ===========================================================
	// Constants
	// ===========================================================
	public static String spWelcomeName = "welcome";
	public static String spWelcomeColumnLoginCount = "loginCount";

//	public final static String HOST = "http://192.168.0.214:8080/";
	
//	public static  String INPUTURL = "http://192.168.2.103:8080/";
//	public static  String HOST = INPUTURL+"/";
//	public static  String HOST = "http://192.168.0.32:8080/";
	
	public static String HOST = "http://www.smartamd.com/";
	public final static String HOSTEND = ".do";
	
	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	public static String getURL(Context applicationContext) {
		SharedPreferences preferences= applicationContext.getSharedPreferences("NONGJI", Context.MODE_PRIVATE);
		String yumingIp=preferences.getString("YUMING", null);
		return yumingIp;
	}
	//手机正则表达式
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		System.out.println(m.matches() + "---");
		return m.matches();
	}
	
	

}
