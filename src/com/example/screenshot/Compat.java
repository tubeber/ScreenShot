package com.example.screenshot;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * ����������ͼ�¼�������Acitivity�У�Ҫ��ȡWindow�����DecorView����ֻ�ܼ��ͨ��
 * ����WindowManager��ȡ������ͬ�汾ʵ�ֲ�һ�����������ְ汾�����ݴ���
 * 
 * @author King
 */
public class Compat {

	public static View V9(WindowManager winManager){
		try {
			// ����WindowManagerImpl
			Field wmImpl = winManager.getClass().getDeclaredField("mWindowManager");
			
			wmImpl.setAccessible(true);

			WindowManager winManagerImpl = (WindowManager) wmImpl.get(winManager);

			// ����WindowManagerImpl�е�View[]���� Ȼ���ȡ DecorView����
			Field field = winManagerImpl.getClass().getDeclaredField("mViews");
			field.setAccessible(true);
			
			View views[] = (View[]) field.get(winManagerImpl);
			
			if(views != null && views.length >= 0){
				return views[0];
			}
		} catch (Exception e) {
			Log.wtf("test", e);
		}
		return null;
	}
	
	public static View V17(WindowManager winManager){
		try {
			// ����WindowManagerGlobal
			Field wmImpl = winManager.getClass().getDeclaredField("mGlobal");
			
			wmImpl.setAccessible(true);
			
			Object object = wmImpl.get(winManager);

			// ����WindowManagerGlobal�е�ArrayList<View>���� Ȼ���ȡ DecorView����
			Field field = object.getClass().getDeclaredField("mViews");
			field.setAccessible(true);
			
			ArrayList<View> views = (ArrayList<View>) field.get(object);
			
			if(views != null && !views.isEmpty()){
				return views.get(0);
			}
			
		} catch (Exception e) {
			Log.wtf("test", e);
		}
		return null;
	}
}
