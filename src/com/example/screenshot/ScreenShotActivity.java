package com.example.screenshot;

import java.io.ByteArrayOutputStream;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class ScreenShotActivity extends Activity{

	public static final String KEY_SCREEN_BITMAP = "screenBitmap";
	
	private ScreenShotView mScreenShotView;
	
	private ObjectAnimator mBottomViewAnimator;
	
	private View mBottomBar;
	private View mBtnClipSelected;
	private View mBtnClipAllScreen;
	private View mBtnCancel;
	
	private Bitmap mScreenBitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_screenshot);
		
		initView();
		
		byte[] bis = getIntent().getByteArrayExtra(KEY_SCREEN_BITMAP);
		mScreenBitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
	}
	
	private void initView(){
		mScreenShotView = (ScreenShotView) findViewById(R.id.screenshot_view);
		mScreenShotView.setOnTouchListener(mOnTouchListener);
		
		mBottomBar = findViewById(R.id.bottom_bar);
		
		// 截取选中
		mBtnClipSelected = findViewById(R.id.btn_clip_selected);
		mBtnClipSelected.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				gotoPreView(mScreenShotView.clipSelected(mScreenBitmap));
			}
		});
		
		// 截取全屏
		mBtnClipAllScreen = findViewById(R.id.btn_clip_allscreen);
		mBtnClipAllScreen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				gotoPreView(mScreenBitmap);
			}
		});
		
		// 取消
		mBtnCancel = findViewById(R.id.btn_cancel);
		mBtnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		mBottomViewAnimator = ObjectAnimator.ofFloat(mBottomBar, "translationY", 0, 200);
		mBottomViewAnimator.setDuration(500);
	}
	
	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				hideBottomBar();
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				showBottomBar();
				break;
			default:
				break;
			}
			return false;
		}
	};
	
	private void hideBottomBar(){
		if(mBottomViewAnimator.isRunning()){
			mBottomViewAnimator.cancel();
		}
		mBottomViewAnimator.start();
	}
	
	private void showBottomBar(){
		if(mBottomViewAnimator.isRunning()){
			mBottomViewAnimator.cancel();
		}
		mBottomViewAnimator.reverse();
	}
	
	private void gotoPreView(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] bitmapByte = baos.toByteArray();
		
		Intent intent = new Intent(this, ScreenShotPreviewActivity.class);
		intent.putExtra("bitmap", bitmapByte);
		startActivity(intent);
	}
}
