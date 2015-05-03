package com.example.screenshot;

import java.io.ByteArrayOutputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

/**
 * 启动截屏的广播
 * 
 * @author King
 */
public class ScreenShotBroadcastReceiver extends BroadcastReceiver {

	public static final String KEY_ACTION = "screenshot";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent != null && TextUtils.equals(KEY_ACTION, intent.getAction())){
			
			Bitmap bitmap = getScreenBitmap(context);
			if(bitmap == null){
				return;
			}
			intent = new Intent(context, ScreenShotActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra(ScreenShotActivity.KEY_SCREEN_BITMAP, getBitmapByte(bitmap));
			context.startActivity(intent);
		}
	}

	private Bitmap getScreenBitmap(Context context){
		WindowManager winManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		View decorView = null;
		if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN){
			decorView =  Compat.V9(winManager);
		}else{
			decorView =  Compat.V17(winManager);
		}
		return screenShot(decorView);
	}
	
	public Bitmap screenShot(View decorView) {

		// 获取状态栏高度
		Rect rect = new Rect();
		decorView.getWindowVisibleDisplayFrame(rect);
		int statusBarHeights = rect.top;

		Bitmap screenshot = Bitmap.createBitmap(decorView.getWidth(), decorView.getHeight(), Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(screenshot);
		canvas.translate(-decorView.getScrollX(), -decorView.getScrollY());
		decorView.draw(canvas);
		
		// 去掉状态栏
		Bitmap bmp = Bitmap.createBitmap(screenshot, 0,
				statusBarHeights, decorView.getWidth(), decorView.getHeight() - statusBarHeights);

		screenshot.recycle();
		
		return bmp;
	}
	
	private byte[] getBitmapByte(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}
}
