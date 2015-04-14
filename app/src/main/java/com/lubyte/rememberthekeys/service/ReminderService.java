package com.lubyte.rememberthekeys.service;


import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

public class ReminderService extends android.app.Service implements Callback {

	public final static String TAG = "ReminderService";

	private final IBinder mBinder = new ServiceBinder();
	public class ServiceBinder extends Binder {
		public ReminderService getService() {
			return ReminderService.this;
		}
	}

	@Override	
	public void onCreate() {
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	
		return super.onStartCommand(intent, flags, startId);
	}	


	@Override
	public void onDestroy() {
		Log.d(TAG, "Measurement service stopped");
		
		super.onDestroy();
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		
		return false;
	}

	@Override
	public IBinder onBind(Intent intent) {

		return mBinder;
	}

}
