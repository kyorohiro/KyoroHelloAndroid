package info.kyorohiro.helloworld.ext.textviewer.manager;

//import android.view.MotionEvent;
import info.kyorohiro.helloworld.display.simple.SimpleDisplayObject;
import info.kyorohiro.helloworld.display.simple.SimpleDisplayObjectContainer;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import info.kyorohiro.helloworld.display.simple.SimpleMotionEvent;
import info.kyorohiro.helloworld.ext.textviewer.viewer.TextViewer;
import info.kyorohiro.helloworld.ext.textviewer.manager.BufferGroup;
import info.kyorohiro.helloworld.ext.textviewer.manager.BufferManager;
import info.kyorohiro.helloworld.ext.textviewer.manager.SeparateUI;

public class BufferGroup extends SimpleDisplayObjectContainer{

	private TextViewer mTextViewer = null;
	private SeparateUI mSeparate = null;

	public TextViewer getTextViewer() {
		return mTextViewer;
	}
//
//	public synchronized void setTextViewer(TextViewer viewer) {
//		removeChild(mTextViewer);
//		viewer.recovery();
//		addChild(viewer);
//	}
//
	public BufferGroup(TextViewer textViewer) {
		doAddSeparator();
		addChild(textViewer);
	}

	@Deprecated
	public void isVisible(boolean on) {
		mSeparate.isVisible(on);
	}
	private void doAddSeparator() {
		SeparateUI s=new SeparateUI(this);
		addChild(s);
	}

	public void setSeparatorPoint(float persent) {
		mSeparate.setPersentY(persent);
	}

	// following code isrefactring targe
	// isEdit() and IsGuard is same code posint.
	public boolean isGuard() {
		if(isGuard(getChild(0))){
			return true;
		}
		else if(isGuard(getChild(1))){
			return true;
		}
		else {
			return false;
		}
	}

	private static boolean isGuard(SimpleDisplayObject child) {
		if(child == null){
			return false;
		}
		if(child instanceof TextViewer){
			TextViewer v = (TextViewer)child;
			return v.isGuard();
		}
		else if(child instanceof BufferGroup){
			BufferGroup v = (BufferGroup)child;
			return v.isGuard();
		}
		else  {
			return false;
		}
	}

/*	public boolean isEdit() {
		if(isEdit(getChild(0))){
			return true;
		}
		else if(isEdit(getChild(1))){
			return true;
		}
		else {
			return false;
		}
	}
	*/
	public boolean mIsControlBuffer = false;
	public boolean isControlBuffer() {
		// todo refactring target
		//for(int i=0;i<numOfChild();i++) {
		//	if(getChild(i)
		MiniBuffer compare = BufferManager.getManager().getMiniBuffer();
		Object p = compare.getParent();
//		Object pp = (compare.getParent()!=null?((SimpleDisplayObject)p).getParent():null);
		if(this == p || mTextViewer == compare){//|| this == pp){
			
//			android.util.Log.v("kiyo","isCont=true");
			return true;
		}
		//		return true;
		//	}
		//}
//		android.util.Log.v("kiyo","isCont="+mIsControlBuffer);
		return mIsControlBuffer;
	}
	public void isControlBuffer(boolean on) {
		mIsControlBuffer = on;
	}
	/*
	private static boolean isEdit(SimpleDisplayObject child) {
		if(child == null){
			return false;
		}
		if(child instanceof TextViewer){
			TextViewer v = (TextViewer)child;
			return v.isEdit();
		}
		else if(child instanceof BufferGroup){
			BufferGroup v = (BufferGroup)child;
			return v.isEdit();
		}
		else  {
			return false;
		}
	}
*/
	@Override
	public synchronized void paint(SimpleGraphics graphics) {
		SimpleDisplayObject[] obj = new SimpleDisplayObject[2];

		int j=0;
		for(int i=0;i<numOfChild();i++) {
			if(getChild(i) instanceof TextViewer||getChild(i) instanceof BufferGroup) {
				obj[j] = getChild(i);
				j++;
				if(j>=2){
					break;
				}
			}
		}
		if(mSeparate.isVertical()){
//			android.util.Log.v("kiyo","-- vertical true");
			int z = (int)(getWidth(false)*mSeparate.getPersentY());
			if(j>=2){
				obj[0].setPoint(0, 0);
				obj[0].setRect(z, getHeight(false));
				obj[1].setPoint(z, 0);
				obj[1].setRect(getWidth(false)-z, getHeight(false));
			} else {
				obj[0].setPoint(0, 0);
				obj[0].setRect(getWidth(false), getHeight(false));			
			}
			mSeparate.setPoint(z, mSeparate.getY());
		} else {
//			android.util.Log.v("kiyo","-- vertical false");
			int z = (int)(getHeight(false)*mSeparate.getPersentY());
			if(j>=2){
				obj[0].setPoint(0, 0);
				obj[0].setRect(getWidth(false), z);
				obj[1].setPoint(0, z);
				obj[1].setRect(getWidth(false), getHeight(false)-z);
			} else {
				obj[0].setPoint(0, 0);
				obj[0].setRect(getWidth(false), getHeight(false));			
			}
			mSeparate.setPoint(mSeparate.getX(), z);
		}
		super.paint(graphics);
	}


	@Override
	public void addChild(SimpleDisplayObject child) {
		if(child instanceof TextViewer) {
			mTextViewer = (TextViewer)child;
			int num = super.numOfChild()-1;
		 	if(num<0){num = 0;}
			super.insertChild(num, child);
		}
		else if(child instanceof SeparateUI) {
			mSeparate = (SeparateUI)child;
			super.addChild(child);
		}
		else if(child instanceof BufferGroup) {
			int num = super.numOfChild()-1;
		 	if(num<0){num = 0;}
			super.insertChild(num, child);			
		}
		else {
			super.addChild(child);
		}
	}

	public BufferGroup divide(SeparateUI separate) {
		if(getTextViewer() == null) {
			return null;
		}
		if(separate.getPersentY()>0.5){
			return divideAndNew(true,BufferManager.getManager().newTextViewr());
		} else{
			return divideAndNew(false,BufferManager.getManager().newTextViewr());
		}		
	}

	public synchronized BufferGroup divideAndNew(boolean leftOrTop, TextViewer viewer) {
		mSeparate.setmIsReached();
		BufferGroup ret = null;
		// todo following yaxtuke sigoto
		if(numOfChild()>=3){
			return null;
		}
		if(leftOrTop){
			addChild(ret = new BufferGroup(viewer));
			addChild(new BufferGroup(mTextViewer));
		} else{
			addChild(new BufferGroup(mTextViewer));
			addChild(ret = new BufferGroup(viewer));
		}
		removeChild(mTextViewer);
		mTextViewer = null;
		mSeparate.resetPosition();
		return ret;
	}

	
	//
	// now creating
	//
	public BufferGroup splitWindowVertically() {
		//split-window-vertically
//		android.util.Log.v("kiyo","begin splitWindowVertically()");
		BufferGroup ret = null;
		ret = divide(mSeparate);
		mSeparate.isVertical(false);
//		android.util.Log.v("kiyo","begin splitWindowVertically()"+mSeparate.isVertical());
		mSeparate.resetPosition();
//		android.util.Log.v("kiyo","end splitWindowVertically()");
		return ret;
	}

	//
	// now creating
	//
	public BufferGroup splitWindowHorizontally() {
		//split-window-vertically
	//	android.util.Log.v("kiyo","begin splitWindowHorizontally()");
		BufferGroup ret = null;
		ret = divide(mSeparate);
		mSeparate.isVertical(true);
	//	android.util.Log.v("kiyo","begin splitWindowHorizontally()"+mSeparate.isVertical());
		mSeparate.resetPosition();
	//	android.util.Log.v("kiyo","end splitWindowHorizontally()");

		return ret;
	}


	//
	// now creating
	//
	public void deleteWindow() {
		Object parent = getParent();
		if(parent == null) {
			return;
		}
		if(!(parent instanceof BufferGroup)){
			return;
		}
		BufferGroup group = (BufferGroup)parent;
		SimpleDisplayObject alive = null;
		SimpleDisplayObject kill = this;
		for(int i=0;i<group.numOfChild();i++) {
			if(alive != group.getChild(i) && group.getChild(i) instanceof BufferGroup){
				alive = group.getChild(i);
				break;
			}
		}
		if(alive == null) {
			return;
		}
		if(deleteable(alive, kill)) {
			group.combine(alive, kill);
		}
	}

	//
	//
	public boolean deleteable(SimpleDisplayObject child, SimpleDisplayObject kill) {
		if(isControlBuffer()) {
			return false;
		}
//		TextViewer info =BufferManager.getManager().getInfoBuffer();
//		if(child == info) {
//			return false;
//		}
		if(child instanceof BufferGroup){
			if(((BufferGroup) child).isControlBuffer()){
				return false;
			}
//			if(((BufferGroup) child).getTextViewer() == info){
//				return false;
//			}
		}
		if(kill instanceof BufferGroup){
			if(((BufferGroup) kill).isControlBuffer()){
				return false;
			}
		}
		return BufferManager.getManager().notifyEvent(child, kill);
	}
	///
	///
	public boolean combine(SimpleDisplayObject child, SimpleDisplayObject kill) {
		Object parent = getParent();
//		android.util.Log.v("kiyo","----de1");
		if(!deleteable(child, kill)) {
//			android.util.Log.v("kiyo","----deleteable^^1");
			return false;
		}
//		android.util.Log.v("kiyo","----de2");
		if(child != null){
//			android.util.Log.v("kiyo","----de3");
			// refactaring
			int index = ((SimpleDisplayObjectContainer)parent).getIndex(this);
			this.removeChild(child);
			((SimpleDisplayObjectContainer)parent).insertChild(index, child);
			((SimpleDisplayObjectContainer)parent).removeChild(this);
			if(includeFocusingChild()) {
				chFocus((SimpleDisplayObject)parent); 
			}
			dispose();
			BufferManager.getManager().getBufferList().doGrabage();
		} 
//		android.util.Log.v("kiyo","----de4");
		return true;
	}

	public boolean combine(SeparateUI separate) {
//		android.util.Log.v("kiyo","----com1");
		Object parent = getParent();
		SimpleDisplayObject child = null;
		SimpleDisplayObject kill = null;
		//if(isControlBuffer()) {
		//	return false;
		//}
		if(separate.getPersentY()>0.5){
			child = getChild(0);
			kill = getChild(1);
		} else{
			child = getChild(1);
			kill = getChild(0);
		}

		//if(!BufferManager.getManager().notifyEvent(child, kill)){
		//	//todo mSeparate.resetPosition();
		//	return false;
		//}
		return combine(child, kill);
/*
		if(child != null){
			// refactaring
			int index = ((SimpleDisplayObjectContainer)parent).getIndex(this);
			this.removeChild(child);
			((SimpleDisplayObjectContainer)parent).insertChild(index, child);//addChild((SimpleDisplayObject)child);
			((SimpleDisplayObjectContainer)parent).removeChild(this);
			if(includeFocusingChild()) {
				chFocus((SimpleDisplayObject)parent); 
			}
			dispose();
			BufferManager.getManager().getBufferList().doGrabage();
		} 
		*/
		//return true;
	}

	private boolean includeFocusingChild() {
		SimpleDisplayObject o = BufferManager.getManager().getFocusingTextViewer();
		SimpleDisplayObject c = BufferManager.getManager().getFocusingTextViewer();
		SimpleDisplayObject root = SimpleDisplayObject.getStage(o).getRoot();
		while(o != null && o!=root) {
			if(c == o){
				return true;
			}
			o = (SimpleDisplayObject)o.getParent();
		}
		return false;
	}

	private boolean  chFocus(SimpleDisplayObject parent) {
		if(parent instanceof TextViewer) {
			TextViewer v = (TextViewer)parent;
			v.getLineView().isFocus(true);
			BufferManager.getManager().changeFocus(v);
			return true;
		}
		if(parent instanceof SimpleDisplayObjectContainer){
			SimpleDisplayObjectContainer _parent = (SimpleDisplayObjectContainer)parent;
			for(int i=0;i<_parent.numOfChild();i++){
				if(_parent.getChild(i) instanceof TextViewer || _parent.getChild(i) instanceof BufferGroup) {
					if(chFocus(_parent.getChild(i))){
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public  synchronized boolean onTouchTest(int x, int y, int action) {
		if(mSeparate.isVisible()) {
			if(SimpleMotionEvent.ACTION_DOWN == action){
				focusTest(x, y);
			}
			boolean ret = mSeparate._onTouchTest(x-mSeparate.getX(), y-mSeparate.getY(), action);
			if(ret) {
				return ret;
			}
		}
		return super.onTouchTest(x, y, action);
	}

	private void focusTest(int x, int y) {
		for(int i=0;i<numOfChild();i++) {
			if(getChild(i) instanceof TextViewer) {
				int cx = ((TextViewer)getChild(i)).getX();
				int cy = ((TextViewer)getChild(i)).getY();
				int cw = ((TextViewer)getChild(i)).getWidth(false);
				int ch = ((TextViewer)getChild(i)).getHeight(false);
				if(cx<=x&&x<cx+cw) {
					if(cy<=y&&y<cy+ch){
//						android.util.Log.v("kiyo","-----focusChange"+cy+"<="+y+"&&"+y+"<"+cy+"+"+ch);
						BufferManager.getManager().changeFocus((TextViewer)getChild(i));
						break;
					}
				}
			}
		}		
	}

}
