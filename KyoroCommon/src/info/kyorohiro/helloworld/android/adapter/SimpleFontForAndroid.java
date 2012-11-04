package info.kyorohiro.helloworld.android.adapter;

import android.graphics.Paint;
import info.kyorohiro.helloworld.display.simple.SimpleFont;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import info.kyorohiro.helloworld.display.simple.SimpleTypeface;
import info.kyorohiro.helloworld.text.KyoroString;

public class SimpleFontForAndroid extends SimpleFont {

	private Paint mPaint = new Paint();

	public SimpleFontForAndroid() {
		super();
		setAntiAlias(getAntiAlias());
		setFontSize(getFontSize());
		setSimpleTypeface(getSimpleTypeface());
	}

	@Override
	public void setAntiAlias(boolean state) {
		super.setAntiAlias(state);
		mPaint.setAntiAlias(state);
	}

	@Override
	public void setFontSize(float size) {
		super.setFontSize(size);
		mPaint.setTextSize(size);
	}

	@Override
	public void setSimpleTypeface(SimpleTypeface typeface) {
		super.setSimpleTypeface(typeface);
		if(typeface instanceof SimpleTypefaceForAndroid) {
			mPaint.setTypeface(((SimpleTypefaceForAndroid)typeface).getTypeface());
		}
	}

	@Override
	public int getTextWidths(KyoroString text, int start, int end, float[] widths, float textSize) {
		return mPaint.getTextWidths(text.getChars(), start, end-start, widths);
	}

	public int getControlCode(char[] buffer, int len, int start ) {
		for(int i=start;i<len;i++) {
			if(buffer[i]<=31||buffer[i]==127){
				return i;
			}
		}
		return len;
	}

	@Override
	public int getTextWidths(char[] buffer, int start, int end, float[] widths, float textSize) {
		return mPaint.getTextWidths(buffer, start, end-start, widths);
	}
}
