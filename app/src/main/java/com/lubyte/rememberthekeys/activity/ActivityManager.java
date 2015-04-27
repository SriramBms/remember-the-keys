package com.lubyte.rememberthekeys.activity;


import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.LocationServices;
import com.lubyte.rememberthekeys.service.ActivityService;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

public class ActivityManager implements ConnectionCallbacks,OnConnectionFailedListener{
	private static final String TAG = "PhoneActivityManager";
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
	private ActivityRecognitionClient mActivityRecognitionClient;
	private boolean mActivityUpdatesActive=false;
	private boolean mResolvingError = false;
	private AtomicInteger mRequestType;
	private Context mContext;
	private PendingIntent mActivityIntent;
	
	public ActivityManager(Context context) {
		mContext = context;
		int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
		if(result==ConnectionResult.SUCCESS){
			Log.v(TAG,"Google play services connected");
			mGClient = new GoogleApiClient.Builder(mContext)
						.addApi(LocationServices.API)
						.addApi(ActivityRecognition.API)
						.addConnectionCallbacks(this)
						.addOnConnectionFailedListener(this)
						.build();
		}else{
			Log.w(TAG,"Error connecting to Google play services");
			//handle in onConnectionFailed
		}
		mActivityRecognitionClient = new ActivityRecognitionClient(mContext,(com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks) this,this);
		Intent intent = new Intent(mContext, ActivityService.class);
		mActivityIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	/*@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(result==ConnectionResult.SUCCESS){
			Log.v(TAG,"Google play services connected");
			mGClient = new GoogleApiClient.Builder(this)
						.addApi(LocationServices.API)
						.addApi(ActivityRecognition.API)
						.addConnectionCallbacks(this)
						.addOnConnectionFailedListener(this)
						.build();
		}else{
			Log.w(TAG,"Error connecting to Google play services");
			//handle in onConnectionFailed
		}
		
		mResolvingError = bundle != null
	            && bundle.getBoolean(STATE_RESOLVING_ERROR, false);
		
	}*/
	
	/*@Override
	public void onStart(){
		super.onStart();
		if(!mResolvingError){
			mGClient.connect();
		}
	}*/
	
	public void onStart(){
		
		
			mActivityRecognitionClient.connect();
		
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
			} catch (SendIntentException e) {
				Log.e(TAG, "Error while resolving Error");
				e.printStackTrace();
				mGClient.connect();
			}
		}else{
			//showErrorDialog(mConnResult.getErrorCode());
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
	
		
			
			//Toast.makeText(mContext, "GoogleServices Connected",Toast.LENGTH_SHORT).show();
			
			
		
	}

	public void startUpdates(PendingIntent intent, long interval){
		
		
		if(mGClient.isConnected()){

			mRequestType.set(REQUEST_ACTIVITY_REC_CONNECT);
			//ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGClient, DETECTION_INTERVAL, mActivityRecognitionPendingIntent);
			mActivityRecognitionClient.requestActivityUpdates(interval, intent);
			//Toast.makeText(this, "Registered for activity recog services", Toast.LENGTH_SHORT).show();
		}
	
	}
	
	public void onConnectionSuspended(int cause) {
		Log.v(TAG,"Google services suspended, cause:"+cause);
		
	}
	
	
	public void onStop(){
		mGClient.disconnect();
		
	}
	
	
	
	
	

}
