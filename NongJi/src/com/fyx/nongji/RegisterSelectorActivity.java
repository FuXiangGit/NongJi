package com.fyx.nongji;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RegisterSelectorActivity extends Activity implements OnClickListener {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	private Button register_vip;
	private Button register_not_vip;
	private Button register_cancel;

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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_selector);
		register_vip = (Button) findViewById(R.id.register_vip);
		register_not_vip = (Button) findViewById(R.id.register_not_vip);
		register_cancel = (Button) findViewById(R.id.register_cancel);
		register_vip.setOnClickListener(this);
		register_not_vip.setOnClickListener(this);
		register_cancel.setOnClickListener(this);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_vip:
			startActivity(new Intent(RegisterSelectorActivity.this, RegisterNotVIPActivity.class));
			this.finish();
			break;
		case R.id.register_not_vip:
			startActivity(new Intent(RegisterSelectorActivity.this, RegisterNotVIPActivity.class));
			this.finish();
			break;
		case R.id.register_cancel:
			this.finish();
			break;
		default:
			break;
		}

	}
}
