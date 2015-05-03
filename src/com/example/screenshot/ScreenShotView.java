package com.example.screenshot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ScreenShotView extends View{

	private static final int MIN_CLIP_SIZE = 90;
	private static final int EDGE_SIZE = ( MIN_CLIP_SIZE - 10 ) / 2;
	
	private static final int PADDING = 60;
	private static final int COONER_LENGTH = 30;
	
	private static final int MODE_INIT = -1;
	private static final int MODE_MOVE = 0;
	private static final int MODE_SIZE_CHANGE = 1;
	
	private Point[] mEdgePoints = new Point[4];
	private Drawable[] mArrowDrawables = new Drawable[4];
	
	private Paint mLinePaint;
	private Paint mCornerPaint;
	private Paint mCoverPaint;
	
	private Point mLastTouchPoint;
	
	private int mViewWidth;
	private int mViewHeight;
	
	private int mMode = MODE_INIT;
	private int mChangeDirect;
	
	private boolean showArrow;
	
	public ScreenShotView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ScreenShotView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScreenShotView(Context context) {
		this(context, null);
	}

	private void init(Context context){
		
		mEdgePoints[0] = new Point();
		mEdgePoints[1] = new Point();
		mEdgePoints[2] = new Point();
		mEdgePoints[3] = new Point();
		
		mArrowDrawables[0] = context.getResources().getDrawable(R.drawable.resize_v);
		mArrowDrawables[0].setBounds(0, 0, mArrowDrawables[0].getIntrinsicWidth(), mArrowDrawables[0].getIntrinsicHeight());
		mArrowDrawables[1] = context.getResources().getDrawable(R.drawable.resize_h);
		mArrowDrawables[1].setBounds(0, 0, mArrowDrawables[1].getIntrinsicWidth(), mArrowDrawables[1].getIntrinsicHeight());
		mArrowDrawables[2] = context.getResources().getDrawable(R.drawable.resize_v);
		mArrowDrawables[2].setBounds(0, 0, mArrowDrawables[2].getIntrinsicWidth(), mArrowDrawables[2].getIntrinsicHeight());
		mArrowDrawables[3] = context.getResources().getDrawable(R.drawable.resize_h);
		mArrowDrawables[3].setBounds(0, 0, mArrowDrawables[3].getIntrinsicWidth(), mArrowDrawables[3].getIntrinsicHeight());
		
		// 初始边框线画笔
		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(Color.parseColor("#FF6100"));
		mLinePaint.setStrokeWidth(1.5f);
		
		// 初始边角画笔
		mCornerPaint = new Paint();
		mCornerPaint.setAntiAlias(true);
		mCornerPaint.setColor(Color.parseColor("#FF6100"));
		mCornerPaint.setStrokeWidth(7.5f);
		
		// 蒙版
		mCoverPaint = new Paint();
		mCoverPaint.setColor(Color.parseColor("#88000000"));
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		mViewWidth = getMeasuredWidth();
		mViewHeight = getMeasuredHeight();
		
		int squareLength = mViewWidth - 2 * PADDING;
		// 计算初始四角点
		// 左上角
		mEdgePoints[0].x = PADDING;
		mEdgePoints[0].y = (mViewHeight - squareLength) / 2;
		// 右上角
		mEdgePoints[1].x = mViewWidth - PADDING;
		mEdgePoints[1].y = mEdgePoints[0].y;
		// 左下角
		mEdgePoints[2].x = mEdgePoints[0].x;
		mEdgePoints[2].y = mEdgePoints[0].y + squareLength;
		// 右下角
		mEdgePoints[3].x = mEdgePoints[1].x;
		mEdgePoints[3].y = mEdgePoints[2].y;
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// 四块蒙版区域
		canvas.drawRect(0, 0, mEdgePoints[1].x, mEdgePoints[1].y, mCoverPaint);
		canvas.drawRect(mEdgePoints[1].x, 0, mViewWidth, mEdgePoints[3].y, mCoverPaint);
		canvas.drawRect(mEdgePoints[2].x, mEdgePoints[2].y, mViewWidth, mViewHeight, mCoverPaint);
		canvas.drawRect(0, mEdgePoints[0].y, mEdgePoints[0].x, mViewHeight, mCoverPaint);
		
		// 画四条点的连线
		canvas.drawLine(mEdgePoints[0].x, mEdgePoints[0].y, mEdgePoints[1].x, mEdgePoints[1].y, mLinePaint);
		canvas.drawLine(mEdgePoints[1].x, mEdgePoints[1].y, mEdgePoints[3].x, mEdgePoints[3].y, mLinePaint);
		canvas.drawLine(mEdgePoints[3].x, mEdgePoints[3].y, mEdgePoints[2].x, mEdgePoints[2].y, mLinePaint);
		canvas.drawLine(mEdgePoints[2].x, mEdgePoints[2].y, mEdgePoints[0].x, mEdgePoints[0].y, mLinePaint);
	
		//画边角
		canvas.drawLine(mEdgePoints[0].x, mEdgePoints[0].y, mEdgePoints[0].x, mEdgePoints[0].y + COONER_LENGTH, mCornerPaint);
		canvas.drawLine(mEdgePoints[0].x, mEdgePoints[0].y, mEdgePoints[0].x + COONER_LENGTH, mEdgePoints[0].y, mCornerPaint);
		canvas.drawLine(mEdgePoints[1].x, mEdgePoints[1].y, mEdgePoints[1].x, mEdgePoints[1].y + COONER_LENGTH, mCornerPaint);
		canvas.drawLine(mEdgePoints[1].x, mEdgePoints[1].y, mEdgePoints[1].x - COONER_LENGTH, mEdgePoints[1].y, mCornerPaint);
		canvas.drawLine(mEdgePoints[2].x, mEdgePoints[2].y, mEdgePoints[2].x, mEdgePoints[2].y - COONER_LENGTH, mCornerPaint);
		canvas.drawLine(mEdgePoints[2].x, mEdgePoints[2].y, mEdgePoints[2].x + COONER_LENGTH, mEdgePoints[2].y, mCornerPaint);
		canvas.drawLine(mEdgePoints[3].x, mEdgePoints[3].y, mEdgePoints[3].x, mEdgePoints[3].y - COONER_LENGTH, mCornerPaint);
		canvas.drawLine(mEdgePoints[3].x, mEdgePoints[3].y, mEdgePoints[3].x - COONER_LENGTH, mEdgePoints[3].y, mCornerPaint);
	
		if(showArrow){
			// 上线箭头
			canvas.save();
			canvas.translate(- mArrowDrawables[0].getIntrinsicWidth() / 2, - mArrowDrawables[0].getIntrinsicHeight() / 2);
			canvas.translate((mEdgePoints[0].x + mEdgePoints[1].x) / 2, mEdgePoints[0].y);
			mArrowDrawables[0].draw(canvas);
			canvas.restore();

			// 右线箭头
			canvas.save();
			canvas.translate(- mArrowDrawables[1].getIntrinsicWidth() / 2, - mArrowDrawables[1].getIntrinsicHeight() / 2);
			canvas.translate(mEdgePoints[1].x, (mEdgePoints[1].y + mEdgePoints[3].y) / 2);
			mArrowDrawables[1].draw(canvas);
			canvas.restore();
			
			// 下线箭头
			canvas.save();
			canvas.translate(- mArrowDrawables[2].getIntrinsicWidth() / 2, - mArrowDrawables[2].getIntrinsicHeight() / 2);
			canvas.translate((mEdgePoints[2].x + mEdgePoints[3].x) / 2, mEdgePoints[2].y);
			mArrowDrawables[2].draw(canvas);
			canvas.restore();
			
			// 左线箭头
			canvas.save();
			canvas.translate(- mArrowDrawables[3].getIntrinsicWidth() / 2, - mArrowDrawables[3].getIntrinsicHeight() / 2);
			canvas.translate(mEdgePoints[0].x, (mEdgePoints[0].y + mEdgePoints[2].y) / 2);
			mArrowDrawables[3].draw(canvas);
			canvas.restore();
		}
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mLastTouchPoint == null){
			mLastTouchPoint = new Point();
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int x = (int)event.getX();
			int y = (int)event.getY();
			if(isInChangeArea(x, y)){
				mMode = MODE_SIZE_CHANGE;
				mChangeDirect = getChangeDirect(x, y);
				saveLastPoint(event);
				return true;
			}else if(isInMoveArea(x, y)){
				mMode = MODE_MOVE;
				saveLastPoint(event);
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			handleMove(event);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:	
			mMode = MODE_INIT;
			//手指松开隐藏箭头
			showArrow = false;
			invalidate();
			break;
		default:
			break;
		}
		saveLastPoint(event);
		return super.onTouchEvent(event);
	}
	
	private boolean isInMoveArea(int x, int y){
		Rect clipRect = new Rect(mEdgePoints[0].x + EDGE_SIZE , mEdgePoints[0].y + EDGE_SIZE, mEdgePoints[3].x - EDGE_SIZE, mEdgePoints[3].y - EDGE_SIZE);
		return clipRect.contains(x, y);
	}
	
	private boolean isInChangeArea(int x, int y){
		Rect bigRect = new Rect(mEdgePoints[0].x - EDGE_SIZE, mEdgePoints[0].y - EDGE_SIZE, mEdgePoints[3].x + EDGE_SIZE, mEdgePoints[3].y + EDGE_SIZE);
		Rect smallRect = new Rect(mEdgePoints[0].x + EDGE_SIZE, mEdgePoints[0].y + EDGE_SIZE, mEdgePoints[3].x - EDGE_SIZE, mEdgePoints[3].y - EDGE_SIZE);
		return bigRect.contains(x, y) && !smallRect.contains(x, y);
	}
	
	private void saveLastPoint(MotionEvent event){
		mLastTouchPoint.x = (int) event.getX();
		mLastTouchPoint.y = (int) event.getY();
	}
	
	private void handleMove(MotionEvent event){
		int x = (int)event.getX();
		int y = (int)event.getY();
		int xChange = x - mLastTouchPoint.x;
		int yChange = y - mLastTouchPoint.y;
		if(mMode == MODE_MOVE){
			// 右下角移动
			if(xChange > 0 && yChange > 0){
				if(mEdgePoints[3].x + xChange > mViewWidth){
					xChange = mViewWidth - mEdgePoints[3].x;
				}
				if(mEdgePoints[3].y + yChange > mViewHeight){
					yChange = mViewHeight - mEdgePoints[3].y;
				}
				
			}
			// 左上角移动
			if(xChange <= 0 && yChange <= 0){
				if(mEdgePoints[0].x + xChange < 0){
					xChange = - mEdgePoints[0].x;
				}
				if(mEdgePoints[0].y + yChange < 0){
					yChange = - mEdgePoints[0].y;
				}
			}
			
			// 右上角移动
			if(xChange > 0 && yChange <= 0){
				if(mEdgePoints[1].x + xChange > mViewWidth){
					xChange = mViewWidth - mEdgePoints[1].x;
				}
				if(mEdgePoints[1].y + yChange < 0){
					yChange = - mEdgePoints[1].y;
				}
			}
			
			// 左下角
			if(xChange <= 0 && yChange > 0){
				if(mEdgePoints[2].x + xChange < 0){
					xChange = - mEdgePoints[2].x;
				}
				if(mEdgePoints[2].y + yChange > mViewHeight){
					yChange = mViewHeight - mEdgePoints[2].y;
				}
			}

			mEdgePoints[1].x = mEdgePoints[3].x = mEdgePoints[3].x + xChange;
			mEdgePoints[2].y = mEdgePoints[3].y = mEdgePoints[3].y + yChange;
			mEdgePoints[2].x = mEdgePoints[0].x = mEdgePoints[0].x + xChange;
			mEdgePoints[1].y = mEdgePoints[0].y = mEdgePoints[0].y + yChange;
			
			invalidate();
		}else if(mMode == MODE_SIZE_CHANGE){
			// 显示箭头
			showArrow = true;
			changeClipSize(xChange, yChange);
		}
	}
	
	private int getChangeDirect(int x, int y){
		// 触控区域控制变化方向
		Rect rect = new Rect();
		
		// 左区域
		rect.set(mEdgePoints[0].x - EDGE_SIZE, mEdgePoints[0].y + EDGE_SIZE, mEdgePoints[2].x + EDGE_SIZE, mEdgePoints[2].y - EDGE_SIZE);
		if(rect.contains(x, y)){
			return 1;
		}
		// 上区域
		rect.set(mEdgePoints[0].x + EDGE_SIZE, mEdgePoints[0].y - EDGE_SIZE, mEdgePoints[1].x - EDGE_SIZE, mEdgePoints[1].y + EDGE_SIZE);
		if(rect.contains(x, y)){
			return 2;
		}
		// 右区域
		rect.set(mEdgePoints[1].x - EDGE_SIZE, mEdgePoints[1].y + EDGE_SIZE, mEdgePoints[3].x + EDGE_SIZE, mEdgePoints[3].y - EDGE_SIZE);
		if(rect.contains(x, y)){
			return 3;
		}
		// 下区域
		rect.set(mEdgePoints[2].x + EDGE_SIZE, mEdgePoints[2].y - EDGE_SIZE, mEdgePoints[3].x - EDGE_SIZE, mEdgePoints[3].y + EDGE_SIZE);
		if(rect.contains(x, y)){
			return 4;
		}
		// 左上区域
		rect.set(mEdgePoints[0].x - EDGE_SIZE, mEdgePoints[0].y - EDGE_SIZE, mEdgePoints[0].x + EDGE_SIZE, mEdgePoints[0].y + EDGE_SIZE);
		if(rect.contains(x, y)){
			return 5;
		}
		// 右上区域
		rect.set(mEdgePoints[1].x - EDGE_SIZE, mEdgePoints[1].y - EDGE_SIZE, mEdgePoints[1].x + EDGE_SIZE, mEdgePoints[1].y + EDGE_SIZE);
		if(rect.contains(x, y)){
			return 6;
		}
		// 左下区域
		rect.set(mEdgePoints[2].x - EDGE_SIZE, mEdgePoints[2].y - EDGE_SIZE, mEdgePoints[2].x + EDGE_SIZE, mEdgePoints[2].y + EDGE_SIZE);
		if(rect.contains(x, y)){
			return 7;
		}
		rect.set(mEdgePoints[3].x - EDGE_SIZE, mEdgePoints[3].y - EDGE_SIZE, mEdgePoints[3].x + EDGE_SIZE, mEdgePoints[3].y + EDGE_SIZE);
		// 右下区域
		if(rect.contains(x, y)){
			return 8;
		}
		return 0;
	}
	
	private void changeClipSize(int xChange, int yChange){
		switch (mChangeDirect) {
		case 1:
			translateLeftLinePoints(xChange);
			break;
		case 2:
			translateTopLinePoints(yChange);
			break;
		case 3:
			translateRightLinePoints(xChange);
			break;
		case 4:
			translateBottomLinePoints(yChange);
			break;
		case 5:
			translateLeftLinePoints(xChange);
			translateTopLinePoints(yChange);
			break;
		case 6:
			translateTopLinePoints(yChange);
			translateRightLinePoints(xChange);
			break;
		case 7:
			translateLeftLinePoints(xChange);
			translateBottomLinePoints(yChange);
			break;
		case 8:
			translateBottomLinePoints(yChange);
			translateRightLinePoints(xChange);
			break;
		default:
			break;
		}
		invalidate();
	}
	
	private void translateLeftLinePoints(int xChange){
		if(xChange < 0 && mEdgePoints[0].x + xChange < 0){
			xChange = - mEdgePoints[0].x;
		}
		if(xChange > 0 && mEdgePoints[0].x + xChange + MIN_CLIP_SIZE > mEdgePoints[1].x){
			xChange = mEdgePoints[1].x - mEdgePoints[0].x - MIN_CLIP_SIZE;
		}
		mEdgePoints[2].x = mEdgePoints[0].x = mEdgePoints[0].x + xChange;
	}
	
	private void translateTopLinePoints(int yChange){
		if(yChange < 0 && mEdgePoints[0].y + yChange < 0){
			yChange = - mEdgePoints[0].y;
		}
		if(yChange > 0 && mEdgePoints[0].y + yChange + MIN_CLIP_SIZE > mEdgePoints[2].y){
			yChange = mEdgePoints[2].y - mEdgePoints[0].y - MIN_CLIP_SIZE;
		}
		mEdgePoints[1].y = mEdgePoints[0].y = mEdgePoints[0].y + yChange;
	}
	
	private void translateRightLinePoints(int xChange){
		if(xChange < 0 && mEdgePoints[1].x + xChange - MIN_CLIP_SIZE < mEdgePoints[0].x){
			xChange = mEdgePoints[0].x + MIN_CLIP_SIZE - mEdgePoints[1].x;
		}
		if(xChange > 0 && mEdgePoints[1].x + xChange > mViewWidth){
			xChange = mViewWidth - mEdgePoints[1].x;
		}
		mEdgePoints[3].x = mEdgePoints[1].x = mEdgePoints[1].x + xChange;
	}
	
	private void translateBottomLinePoints(int yChange){
		if(yChange > 0 && mEdgePoints[2].y + yChange > mViewHeight){
			yChange = mViewHeight - mEdgePoints[2].y;
		}
		if(yChange < 0 && mEdgePoints[2].y + yChange - MIN_CLIP_SIZE < mEdgePoints[0].y){
			yChange = mEdgePoints[0].y + MIN_CLIP_SIZE - mEdgePoints[2].y;
		}
		mEdgePoints[2].y = mEdgePoints[3].y = mEdgePoints[2].y + yChange;
	}
	
    /**
     * 截取选中视图
	 * 
     * @return
     */
    public Bitmap clipSelected(Bitmap screenBitmap){
    	return Bitmap.createBitmap(screenBitmap, mEdgePoints[0].x, mEdgePoints[0].y, mEdgePoints[1].x - mEdgePoints[0].x, mEdgePoints[2].y - mEdgePoints[0].y);
    }
    
}
