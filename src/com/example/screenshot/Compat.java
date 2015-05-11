package com.example.screenshot;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * 由于启动截图事件不是在Acitivity中，要获取Window对象的DecorView对象，只能间接通过
 * 反射WindowManager获取。而不同版本实现不一样，这里区分版本做兼容处理
 * 
 * @author King
 */
public class Compat {

	public static View V9(WindowManager winManager){
		try {
			// 反射WindowManagerImpl
			Field wmImpl = winManager.getClass().getDeclaredField("mWindowManager");
			
			wmImpl.setAccessible(true);

			WindowManager winManagerImpl = (WindowManager) wmImpl.get(winManager);

			// 反射WindowManagerImpl中的View[]对象 然后获取 DecorView对象
			Field field = winManagerImpl.getClass().getDeclaredField("mViews");
			field.setAccessible(true);
			
			View views[] = (View[]) field.get(winManagerImpl);
			
			if(views != null && views.length >= 0){
				for (int i = views.length - 1; i >= 0; i--) {
					View view = views[i];
					if(view.getClass().getSimpleName().equals("DecorView")){
						return view;
					}
				}
			}
		} catch (Exception e) {
			Log.wtf("test", e);
		}
		return null;
	}
	
	public static View V17(WindowManager winManager){
		try {
			// 反射WindowManagerGlobal
			Field wmImpl = winManager.getClass().getDeclaredField("mGlobal");
			
			wmImpl.setAccessible(true);
			
			Object object = wmImpl.get(winManager);

			// 反射WindowManagerGlobal中的ArrayList<View>对象 然后获取 DecorView对象
			Field field = object.getClass().getDeclaredField("mViews");
			field.setAccessible(true);
			
			ArrayList<View> views = (ArrayList<View>) field.get(object);
			
			if(views != null && !views.isEmpty()){
				for (int i = views.size() - 1; i >= 0; i--) {
					View view = views.get(i);
					if(view.getClass().getSimpleName().equals("DecorView")){
						return view;
					}
				}
			}
			
		} catch (Exception e) {
			Log.wtf("test", e);
		}
		return null;
	}
}
