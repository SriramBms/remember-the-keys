package com.lubyte.rememberthekeys.service;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;



import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;


public class ActivityService extends IntentService {
	private final static String NAME="PhoneActivityService";
	private Handler handler=new Handler();

	private Context mContext;

	public static final String ACTION_STOP_UPDATES = "android.action.stop_updates";
	public static final String ACTION_START_UPDATES = "android.action.start_updates";
	public static final String ACTION_PHONE_STATIONARY = "com.lubyte.rememberthekeys.stationary";
	public static final String ACTION_PHONE_IN_MOTION = "com.lubyte.rememberthekeys.inmotion";
	private static final String TAG = "PhoneActivityService";

	
	public ActivityService() {
		super(NAME);
		Log.v(TAG,TAG+":Service started");

	}

	@Override
	public void onCreate(){
		super.onCreate();
		Log.v(TAG,"OnCreate");
		mContext=this;


	}


	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v(TAG,TAG+"Intent received");
		if(ActivityRecognitionResult.hasResult(intent)){
			ActivityRecognitionResult activityresult = ActivityRecognitionResult.extractResult(intent);
			DetectedActivity activity = activityresult.getMostProbableActivity();
			final int activityconfidence = activity.getConfidence();
			final String activityname = getNameFromType(activity.getType());
			Log.v(TAG,activityname+":"+activityconfidence);
			/*// Logging for debugging. Not really required in final build
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mContext, activityname+":"+activityconfidence, Toast.LENGTH_SHORT).show();
				}
			});
			 */


			if(activity.getType()==DetectedActivity.STILL){

				Log.v(TAG,"Phone stationary");
				Intent phonestationary = new Intent();
				phonestationary.setAction(ACTION_PHONE_STATIONARY);

				sendBroadcast(phonestationary);

			}else{
				Intent phoneinmotion = new Intent();
				phoneinmotion.setAction(ACTION_PHONE_IN_MOTION);
				sendBroadcast(phoneinmotion);
				Log.v(TAG,"Phone not stationary");

			}

		}else{
			Log.v(TAG,intent.toString());
		}

	}



	private String getNameFromType(int activityType) {
		switch(activityType) {
		case DetectedActivity.IN_VEHICLE:
			return "In_vehicle";
		case DetectedActivity.ON_BICYCLE:
			return "On_bicycle";
		case DetectedActivity.ON_FOOT:
			return "On_foot";
		case DetectedActivity.STILL:
			return "Still";
		case DetectedActivity.UNKNOWN:
			return "Unknown";
		case DetectedActivity.TILTING:
			return "Tilting";
		case DetectedActivity.WALKING:
			return "Walking";
		case DetectedActivity.RUNNING:
			return "Running";

		}
		return "Not in list";
	}

}
