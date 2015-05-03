package com.example.screenshot;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

/**
 * 注册全局的重力感应器，保证整个app的生命周期内都能截图。
 * 
 * @author King
 */
public class ScreenShotApplication extends Application implements SensorEventListener{

	private SensorManager sm;
	
	private static final int MIN_TRIGGER = 15;
	
	@Override
	public void onCreate() {
		super.onCreate();
		initSensor(this);
	}
	
	public void initSensor(Context context){
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
	
		// 绑定Activity生命周期
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			
			@Override
			public void onActivityStopped(Activity arg0) {
			}
			
			@Override
			public void onActivityStarted(Activity arg0) {
			}
			
			@Override
			public void onActivitySaveInstanceState(Activity arg0, Bundle arg1) {
			}
			
			@Override
			public void onActivityResumed(Activity arg0) {
				sm.registerListener(ScreenShotApplication.this, sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
			}
			
			@Override
			public void onActivityPaused(Activity arg0) {
				sm.unregisterListener(ScreenShotApplication.this);
			}
			
			@Override
			public void onActivityDestroyed(Activity arg0) {
			}
			
			@Override
			public void onActivityCreated(Activity arg0, Bundle arg1) {
				
			}
		});
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values = event.values;
		float x = values[0]; 
		float y = values[1]; 
		float z = values[2]; 
		if((Math.abs(x) > MIN_TRIGGER || Math.abs(y) > MIN_TRIGGER || Math.abs(z) > MIN_TRIGGER)){
			Intent intent = new Intent(ScreenShotBroadcastReceiver.KEY_ACTION);
			sendBroadcast(intent);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
}
