package info.kyorohiro.helloworld.android.adapter;

import android.graphics.Bitmap;
import android.graphics.Rect;
import info.kyorohiro.helloworld.display.simple.SimpleDisplayObject;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;

public class SimpleImageForAndroid extends SimpleDisplayObject {

	private Bitmap mBitmap = null;
	//
	// �J������^�C�~���O���Ƃ��A�d�����ē���Image�𐶐��������Ȃ��̂�
	// �Ǘ�����d�g�݂͍��K�v������B
	public SimpleImageForAndroid(Bitmap bitmap) {
		mBitmap = bitmap;
		setRect(bitmap.getWidth(), bitmap.getHeight());
	}

	@Override
	public void paint(SimpleGraphics graphics) {
		if(graphics instanceof SimpleGraphicsForAndroid) {
	        int imageW = mBitmap.getWidth();
	        int imageH = mBitmap.getHeight();
	        int w = getWidth();
	        int h = getHeight();
	        Rect src = new Rect(0, 0, imageW, imageH);
	        Rect dst = new Rect(0, 200, imageW*2, 200 + imageH*2);
			((SimpleGraphicsForAndroid) graphics).getCanvas().drawBitmap(mBitmap, src, dst, null);
		}
	}
	
	@Override
	public void dispose() {
		// 
		// ���̃^�C�~���O�Ń��\�[�X���J������̂́A������
		// Image���g���܂킷�悤�ȗ��p���@���Ԃ���ƓK���ł͂Ȃ��B
		if(mBitmap != null && mBitmap.isRecycled()){
			mBitmap.recycle();
		}
		super.dispose();
	}
}
