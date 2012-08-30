package info.kyorohiro.helloworld.textviewer.manager;

import info.kyorohiro.helloworld.display.simple.SimpleDisplayObject;
import info.kyorohiro.helloworld.display.simple.SimpleDisplayObjectContainer;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import android.graphics.Color;
import android.view.MotionEvent;

public class SeparateUI extends SimpleDisplayObject {
	public static int COLOR_UNLOCK = Color.GREEN;
	public static int COLOR_LOCK = Color.RED;
	private int mPrevTouchDownX = 0;
	private int mPrevTouchDownY = 0;
	private int mPrevX = 0;
	private int mPrevY = 0;
	public static final int MODE_SEPARATE_ORIENTATION = 0;
	public static final int MODE_SEPARATE_VERTICAL = 1;
	private int mModeSeparate = MODE_SEPARATE_ORIENTATION;
	private boolean mIsInside = false;
	private boolean mIsReached = false;
	private LineViewGroup mManager = null;

	private double mPersentY = 0.5;
	
	public SeparateUI(LineViewGroup manager) {
		setRect(20, 20);
		mManager = manager;
	}

	public double getPersentY(){
		return mPersentY;
	}

	public boolean isVertical(){
		if(mModeSeparate == MODE_SEPARATE_ORIENTATION){
			return false;
		} else {
			return true;
		}
	}
	@Override
	public void paint(SimpleGraphics graphics) {
		if(mIsReached){
			graphics.setColor(COLOR_LOCK);			
		} else {
			graphics.setColor(COLOR_UNLOCK);		
		}
		setPoint(getX(), getY());
		graphics.drawCircle(0, 0, getWidth()/2);
		if(mModeSeparate == MODE_SEPARATE_VERTICAL) {
			graphics.drawLine(0, 0, 0, -getY());
		} else {
			graphics.drawLine(0, 0, -getX(), 0);
		}
	}

	@Override
	public void setPoint(int x, int y) {
		Object parent = getParent();
		if(parent != null &&  parent instanceof SimpleDisplayObjectContainer) {
			SimpleDisplayObjectContainer parentAtSDO = (SimpleDisplayObjectContainer)parent;
			int w = parentAtSDO.getWidth(false);
			int h = parentAtSDO.getHeight(false);
			if(x>w){x=w;}
			if(y>h){y=h;}
			if(isVertical()){
				mPersentY=x/(double)w;				
			} else {
				mPersentY=y/(double)h;
			}
		}
		if(x < 0) {x=0;}
		if(y< 0) {y=0;}
		super.setPoint(x, y);
	}

	@Override
	public boolean onTouchTest(int x, int y, int action) {
		if(mIsInside&&!mIsReached){
			if(isVertical(x, y)){
				mModeSeparate = MODE_SEPARATE_VERTICAL;					
			} else {
				mModeSeparate = MODE_SEPARATE_ORIENTATION;
			}
		}
		if(action == MotionEvent.ACTION_DOWN){
			if(isInside(x, y)){
				mIsInside = true;
				int[] xy = new int[2];
				getGlobalXY(xy);
				mPrevTouchDownX = xy[0]+x;
				mPrevTouchDownY = xy[1]+y;
				mPrevX = getX();
				mPrevY = getY();
				return true;
			}
		} else if(action == MotionEvent.ACTION_MOVE) {
			if(mIsInside){
				int[] xy = new int[2];
				getGlobalXY(xy);
				setPoint(xy[0]+x-mPrevTouchDownX+mPrevX, xy[1]+y-mPrevTouchDownY+mPrevY);
				
				if(!mIsReached) {
					if(isReached(x, y)){
						mIsReached = true;
						mManager.divide(this);
					}
				} else {
					if(isUnreached(x, y)){
						mIsReached = false;
						mManager.combine(this);
					}
				}
				return true;
			}
		} else if(action == MotionEvent.ACTION_UP){
			mIsInside = false;				
		}
		return super.onTouchTest(x, y, action);
	}

	private boolean isVertical(int x, int y){
		SimpleDisplayObject _target = this;
		SimpleDisplayObject _parent = (SimpleDisplayObject)getParent();
		
		int _x = _target.getX() + x;
		int _y = _target.getY() + y;
		int _w = _parent.getWidth();
		int _h = _parent.getHeight();
		// use current value 
		if(_x > _w/4 && _y > _h/4) {
			return mModeSeparate==MODE_SEPARATE_VERTICAL;
		}

		if(_x/(double)_w > _y/(double)_h) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isReached(int x, int y) {
		SimpleDisplayObject target = this;
		SimpleDisplayObjectContainer parent = (SimpleDisplayObjectContainer)getParent();
		int a = 0;
		if(mModeSeparate == MODE_SEPARATE_ORIENTATION){
			a = parent.getWidth(false)-(x+getX()+target.getWidth()*4);
		} else {
			a = parent.getHeight(false)-(y+getY()+target.getHeight()*4);
		}
//		android.util.Log.v("kiyo","a="+a+","+parent.getWidth()+","+parent.getHeight());
		if(a<0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isUnreached(int x, int y) {
		SimpleDisplayObject target = this;
		SimpleDisplayObject parent = (SimpleDisplayObject)getParent();
		int a = 0;
		if(isVertical()){
			a = (y+getY()-target.getHeight()*4);			
		} else {
			a = (x+getX()-target.getWidth()*4);
		}
		if(a<0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isInside(int x, int y) {
		SimpleDisplayObject target = this;
		int width = target.getWidth()/2;
		int height = target.getWidth()/2;
		boolean isInsideAboutH = false;
		boolean isInsideAboutW = false;
		width *=2;
		height *=2;
		
		if(-width<x&& x<width){
			isInsideAboutW = true;
		}
		if(-height<y&& y<height){
			isInsideAboutH = true;
		}
		return isInsideAboutW&isInsideAboutH;
	}

}