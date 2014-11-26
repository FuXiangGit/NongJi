package com.fyx.nongji;

import com.nongji.tools.Constants;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private SharedPreferences sp;
	private int count;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_splash);
		startAnimations();

		sp = getSharedPreferences(Constants.spWelcomeName, MODE_PRIVATE);
		count = sp.getInt(Constants.spWelcomeColumnLoginCount, 1);


		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				if (count > 1) {
//					intent.setClass(SplashActivity.this, NongJiActivity.class);
					intent.setClass(SplashActivity.this, LoginActivity.class);
				} else {
					intent.setClass(SplashActivity.this, LoginActivity.class);
				}
				startActivity(intent);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

				sp.edit().putInt(Constants.spWelcomeColumnLoginCount, ++count).commit();
			}
		}, 1000);
		super.onCreate(savedInstanceState);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	private void startAnimations() {
		Animation alpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
		RelativeLayout root = (RelativeLayout) findViewById(R.id.splash_root);
		root.startAnimation(alpha);

		Animation translate = AnimationUtils.loadAnimation(this, R.anim.translate);
		ImageView logo = (ImageView) findViewById(R.id.splash_logo);
		logo.startAnimation(translate);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
