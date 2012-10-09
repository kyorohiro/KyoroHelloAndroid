package info.kyorohiro.helloworld.display.widget.lineview.edit;

import info.kyorohiro.helloworld.display.widget.lineview.LineViewBufferSpec;
import info.kyorohiro.helloworld.display.widget.lineview.LineViewData;
import info.kyorohiro.helloworld.io.BreakText;

public class EditableLineViewBuffer implements LineViewBufferSpec, IMEClient {

	private Differ mDiffer = new Differ();
	private LineViewBufferSpec mOwner = null;
	private int mCursorRow = 0;
	private int mCursorLine = 0;

	public EditableLineViewBuffer(LineViewBufferSpec owner) {
		super();
		mOwner = owner;
	}

	@Override
	public int getNumOfAdd() {
		return mOwner.getNumOfAdd();
	}

	@Override
	public void clearNumOfAdd() {
		mOwner.clearNumOfAdd();
	}

	@Override
	public int getNumberOfStockedElement() {
		return mOwner.getNumberOfStockedElement() + mDiffer.length();
	}


	@Override
	public LineViewData get(int i) {
		return mDiffer.get(mOwner, i);
	}

	@Override
	public BreakText getBreakText() {
		return mOwner.getBreakText();
	}

	@Override
	public LineViewData[] getElements(LineViewData[] ret, int start, int end) {
		for (int i = start, j = 0; i < end; i++, j++) {
			ret[j] = get(i);
		}
		return ret;
	}

	public int getRow() {
		return mCursorRow;
	}

	public int getCol() {
		return mCursorLine;
	}

	@Override
	public void setCursor(int row, int col) {
		mCursorRow = row;
		mCursorLine = col;
	}

	public void crlf() {
		int index = getNumberOfStockedElement()-1;//+1;
		if(index > mCursorLine) {
			index = mCursorLine;
		}
		CharSequence c = get(mCursorLine);
		int row = c.length();
		if(row>mCursorRow) {
			row = mCursorRow;
		}

		if(row == c.length()) {
			mCursorLine += 1;
			mDiffer.addLine(mCursorLine, "");
		}
		else if(row ==0){
			mDiffer.setLine(mCursorLine, "");
			mCursorLine += 1;
			mDiffer.addLine(mCursorLine, c);
		}
		else {
			mDiffer.setLine(mCursorLine, c.subSequence(0, row));
			mCursorLine += 1;
			mDiffer.addLine(mCursorLine, c.subSequence(row,c.length()));
			
		}
	}

	public void delete() {
		deleteLine();
		if(true){
			return;
		}
		int index = getNumberOfStockedElement()-1;
		if(index > mCursorLine) {
			index = mCursorLine;
		}

		CharSequence l = get(index);
		// �w�肳�ꂽ�s����
		if(l == null || l.length() == 0||
		  (l.length()==1&&l.subSequence(0, 1).equals("\n")) || 
		  (l.length()==2&&l.subSequence(0, 2).equals("\r\n"))
		  ) {
			deleteLine();
			return;
		} 

		int row = mCursorRow;
		if(row>=l.length()) {
			row = l.length();
		}
		if((l.length()>=2&&l.length()-2<=row&&row<=l.length()
				&&l.charAt(l.length()-2) == '\r'
				&&l.charAt(l.length()-1) == '\n')){
			row =l.length()-2;
		}
		else if((l.length()>=1&&l.length()-1<=row&&row<=l.length()
				&&l.charAt(l.length()-1) == '\n')){
			row =l.length()-1;
		}
		if(l.charAt(l.length()-1)=='\n'||index==getNumberOfStockedElement()-1) {
			CharSequence f = l.subSequence(0, row-1);
			CharSequence e = "";
			if(row < l.length()){
				e = l.subSequence(row, l.length());
			}
			mDiffer.setLine(index, ""+f+e);
			mCursorRow =row-1;
		} else {
			CharSequence f = l.subSequence(0, row-1);
			CharSequence e = "";
			if(row < l.length()){
				e = l.subSequence(row, l.length());
			}
			deleteLine();
			commit(""+f+e, index);
		}
	}

	public void deleteLine() {
		int index = getNumberOfStockedElement();//+1;
		if(index >= mCursorLine) {
			index = mCursorLine;
		}
		android.util.Log.v("log","delete:"+index);
		mDiffer.deleteLine(index);	
		mCursorLine-=1;
		if(mCursorLine >=0){
			mCursorRow = get(mCursorLine).length();
		}
	}

	@Override
	public void pushCommit(CharSequence text, int cursor) {
		//android.util.Log.v("log","col="+mCursorCol + ",row="+mCursorRow);
		int index = getNumberOfStockedElement();//+1;
		if(index > mCursorLine) {
			index = mCursorLine;
		}
		commit(text, cursor);
	}

	@Override
	public int getMaxOfStackedElement() {
		return mOwner.getMaxOfStackedElement();
	}

	private void commit(CharSequence text, int cursor) {
		CharSequence ct = get(mCursorLine);
		if(mCursorRow >= ct.length()) {
			mCursorRow = ct.length();
		}
		CharSequence f = composeString(ct, mCursorRow, text);

		int w = this.getBreakText().getWidth();
		int len = 0;
		int le = 0;
		boolean a = true;
		int i=0;
		do {
			android.util.Log.v("kiyo","_aaa0="+i);i++;
			android.util.Log.v("kiyo","_aaa1="+f.length()+","+w+","+f);
			len = getBreakText().breakText(f, 0, f.length(), w);
			le = f.length();
			android.util.Log.v("kiyo","_aaa2="+len+","+le);
			if(a) {
				mDiffer.setLine(mCursorLine, f.subSequence(0, len));
				a = false;
				if(le>len) {
					mCursorLine+=1;
					mCursorRow = 0;
				} else {
					mCursorRow += text.length();
//					mCursorRow = 0;
				}
			} else {
				mDiffer.addLine(mCursorLine, f.subSequence(0, len));				
				if(le>len) {
					mCursorLine+=1;
					mCursorRow += text.length();;
				} else {
					mCursorRow = len;
//					mCursorRow = 0;
				}
			}
			f = f.subSequence(len, le);
		} while(le>len);
	}

	public static CharSequence composeString(CharSequence b, int i, CharSequence s) {
		if(i>=b.length()){
			i=b.length();
		}
		if(i==0){
			return ""+s+b;
		}
		else if(i==b.length()){
			return ""+b+s;
		} else {
			CharSequence f = b.subSequence(0, i);
			CharSequence e = b.subSequence(i,b.length());
			return ""+f+s+e;
		}
	}

}