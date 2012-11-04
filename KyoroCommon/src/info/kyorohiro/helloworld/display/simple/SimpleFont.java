package info.kyorohiro.helloworld.display.simple;

import info.kyorohiro.helloworld.text.KyoroString;

public abstract class SimpleFont {
	private boolean mAntiAlias = false;
	private float mFontSize = 16.0f;
	private SimpleTypeface mTypeface = null;

	public void setAntiAlias(boolean state){
		mAntiAlias = state;
	}

	public boolean getAntiAlias() {
		return mAntiAlias;
	}

	public void setFontSize(float size) {
		mFontSize = size;
	}

	public float getFontSize() {
		return mFontSize;
	}

	public void setSimpleTypeface(SimpleTypeface typeface) {
		mTypeface = typeface;
	}

	public SimpleTypeface getSimpleTypeface() {
		return mTypeface;
	}

	public static int lengthOfControlCode(char code, int textSize) {
		if(code == 9) {//tab
			return textSize*2;
		} else if(code<=31||code==127){
			return textSize/2;
		}  else {
			return 0;
		}
	}
	public int getControlCode(char[] buffer, int len, int start ) {
		for(int i=start;i<len;i++) {
			if(buffer[i]<=31||buffer[i]==127){
				return i;
			}
		}
		return len;
	}

	public abstract int getTextWidths(KyoroString text, int start, int end, float[] widths, float textSize);
	public abstract int getTextWidths(char[] buffer, int start, int end, float[] widths, float textSize);

}