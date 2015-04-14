package com.lubyte.rememberthekeys.activity;

import com.lubyte.rememberthekeys.R;
import com.lubyte.rememberthekeys.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends Activity {
	private boolean enableAlert=false;
	private boolean enableBeep=false;
	private boolean enableVibrate=false;
	private boolean status=false;
	private TextView mAlerttv;
	private TextView mBeeptv;
	private TextView mVibratetv;
	private TextView mStatustv;
	private LinearLayout mRlayout;

	private LinearLayout alertContainer;
	private LinearLayout beepContainer;
	private LinearLayout vibrateContainer;
	private LinearLayout statusContainer;
	
	private final String TAG = this.getClass().getCanonicalName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_activity);

		mRlayout = (LinearLayout)findViewById(R.id.rellayout);
		mAlerttv = (TextView)findViewById(R.id.button_alert);
		mBeeptv = (TextView)findViewById(R.id.button_beep);
		mVibratetv = (TextView)findViewById(R.id.button_vibrate);
		mStatustv = (TextView)findViewById(R.id.button_status);

		alertContainer = (LinearLayout)findViewById(R.id.alert_container);
		beepContainer = (LinearLayout)findViewById(R.id.beep_container);
		vibrateContainer = (LinearLayout)findViewById(R.id.vibrate_container);
		statusContainer = (LinearLayout)findViewById(R.id.status_container);



	}

	public void onButtonSelected(View view){
		Log.v(TAG, "Button clicked view id"+view.getId());
		switch(view.getId()){
		
		
		case R.id.alert_container:
			enableAlert=!enableAlert;
			Log.v(TAG, "Alert button clicked");
			if(enableAlert){
				Log.v(TAG, "Alert button enabled");
				alertContainer.setBackgroundResource(R.color.theme_blue);
				
			}else{
				Log.v(TAG, "Alert button disabled");
				alertContainer.setBackgroundResource(R.color.theme_greymedium);
			}
			break;
		case R.id.beep_container:
			Log.v(TAG, "Beep button clicked");
			enableBeep=!enableBeep;
			if(enableBeep){
				beepContainer.setBackgroundResource(R.color.theme_lightblue);

			}else{
				beepContainer.setBackgroundResource(R.color.theme_greylight);
			}
			break;
		case R.id.vibrate_container:
			enableVibrate=!enableVibrate;

			if(enableVibrate){
				vibrateContainer.setBackgroundResource(R.color.theme_blue);
				Intent alertIntent = new Intent(this, KeyAlertActivity.class);
				startActivity(alertIntent);
			}else{
				vibrateContainer.setBackgroundResource(R.color.theme_greymedium);
			}
			break;
		case R.id.status_container:
			status=!status;
			if(status){
				statusContainer.setBackgroundResource(R.color.theme_orange);
			}else{
				statusContainer.setBackgroundResource(R.color.theme_greydark);
			}

		}
	}




}
