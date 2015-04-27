package com.lubyte.rememberthekeys.activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.LocationServices;
import com.lubyte.rememberthekeys.R;
import com.lubyte.rememberthekeys.R.layout;
import com.lubyte.rememberthekeys.service.ActivityService;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends Activity implements ConnectionCallbacks,OnConnectionFailedListener {
    private long ACTIVITY_INTERVAL = 5000;

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


	private ConnectionResult mConnResult;
	private static final int PAM_RESOLVE_ERROR = 1;


	private GoogleApiClient mGClient;
	private static final String DIALOG_ERROR = "dialog_error";
	private static final String STATE_RESOLVING_ERROR = "ResolveErrorState";
	private static final int REQUEST_GOOGLE_SERVICES_CONNECT=0;
	private static final int REQUEST_GOOGLE_SERVICES_DISCONNECT=-1;
	private static final int REQUEST_ACTIVITY_REC_CONNECT=1;
	private static final int REQUEST_ACTIVITY_REC_DISCONNECT=-2;
	private static final int REQUEST_SERVICES_SUSPENDED=-3;

	private boolean mActivityUpdatesActive=false;
	private boolean mResolvingError = false;
	private AtomicInteger mRequestType;
	private Context mContext;
	private PendingIntent mActivityIntent;

	//private final String TAG = this.getClass().getCanonicalName();
	private final String TAG = "RMTK-Mainactivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_activity);
		mContext = this;

		mRlayout = (LinearLayout)findViewById(R.id.rellayout);
		mAlerttv = (TextView)findViewById(R.id.button_alert);
		mBeeptv = (TextView)findViewById(R.id.button_beep);
		mVibratetv = (TextView)findViewById(R.id.button_vibrate);
		mStatustv = (TextView)findViewById(R.id.button_status);

		alertContainer = (LinearLayout)findViewById(R.id.alert_container);
		beepContainer = (LinearLayout)findViewById(R.id.beep_container);
		vibrateContainer = (LinearLayout)findViewById(R.id.vibrate_container);
		statusContainer = (LinearLayout)findViewById(R.id.status_container);

		mRequestType = new AtomicInteger(0);

		setupGoogleServices();

	}

	private void setupGoogleServices(){
		int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(result==ConnectionResult.SUCCESS){
			Log.v(TAG, "Google play services connected");
			mGClient = new GoogleApiClient.Builder(mContext)
					.addApi(LocationServices.API)
					.addApi(ActivityRecognition.API)
					.addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks)this)
							.addOnConnectionFailedListener(this)
					.build();
		}else{
			Log.w(TAG,"Error connecting to Google play services");
			//handle in onConnectionFailed
		}
		//mActivityRecognitionClient = new ActivityRecognitionClient(this,(GoogleApiClient.ConnectionCallbacks)this,this);
		Intent intent = new Intent(this, ActivityService.class);
		mActivityIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	public void onStart(){

		super.onStart();
		mGClient.connect();

	}

	@Override
	public void onConnectionFailed(ConnectionResult cr){
		mConnResult = cr;
		if(mResolvingError){
			return;
		}else if(mConnResult.hasResolution()){
			mResolvingError=true;
			try {
				//mConnResult.startResolutionForResult(mContext, PAM_RESOLVE_ERROR);
				mConnResult.startResolutionForResult((MainActivity)mContext, PAM_RESOLVE_ERROR);
			} catch (IntentSender.SendIntentException e) {
				Log.e(TAG, "Error while resolving Error");
				e.printStackTrace();
				mGClient.connect();
			}
		}else{
			showErrorDialog(mConnResult.getErrorCode());
			mResolvingError=true;
		}


	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(requestCode)
		{
			case PAM_RESOLVE_ERROR:
				mResolvingError = false;
				switch(resultCode)
				{
					case Activity.RESULT_OK:
						if(!mGClient.isConnecting()||!mGClient.isConnected()){
							mGClient.connect();
						}

				}
		}
	}

	public void onConnected(Bundle connectionHint) {



		Toast.makeText(mContext, "GoogleServices Connected", Toast.LENGTH_SHORT).show();



	}

	public void onConnectionSuspended(int cause) {
		Log.v(TAG, "Google services suspended, cause:" + cause);
		stopService(new Intent(this, ActivityService.class));
	}

	public void onStop(){

		mGClient.disconnect();

		super.onStop();
	}

	public void startUpdates(PendingIntent intent, long interval){


		if(mGClient.isConnected()){

			mRequestType.set(REQUEST_ACTIVITY_REC_CONNECT);
			//ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGClient, DETECTION_INTERVAL, mActivityRecognitionPendingIntent);
			ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGClient,interval, intent);
			//Toast.makeText(this, "Registered for activity recog services", Toast.LENGTH_SHORT).show();
		}

	}

	public void stopUpdates(){
		ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGClient, mActivityIntent);
	}

	public void onDisconnected(){

	}

	private void showErrorDialog(int errorCode) {
		// Create a fragment for the error dialog
		ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
		// Pass the error that should be displayed
		Bundle args = new Bundle();
		args.putInt(DIALOG_ERROR, errorCode);
		dialogFragment.setArguments(args);
		dialogFragment.show(getFragmentManager(), "errordialog");
	}

	public static class ErrorDialogFragment extends DialogFragment {
		public ErrorDialogFragment() { }

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Get the error code and retrieve the appropriate dialog
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			return GooglePlayServicesUtil.getErrorDialog(errorCode,
					this.getActivity(), PAM_RESOLVE_ERROR);
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			((MainActivity)getActivity()).onDialogDismissed();
		}

	}

	public void onDialogDismissed() {
		mResolvingError = false;
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
				startService(new Intent(this, ActivityService.class));
				startUpdates(mActivityIntent, 5000);
			}else{
				statusContainer.setBackgroundResource(R.color.theme_greydark);
				stopUpdates();
				stopService(new Intent(this, ActivityService.class));
			}

		}
	}




}
