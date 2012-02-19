package info.kyorohiro.helloworld.display.widget.lineview;

import info.kyorohiro.helloworld.display.simple.SimpleDisplayObject;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import info.kyorohiro.helloworld.util.CyclingListInter;
import android.graphics.Color;
import android.graphics.Paint;

public class LineView extends SimpleDisplayObject {
	private int mNumOfLine = 100;
	private CyclingListInter<FlowingLineDatam> mInputtedText = null;
	private int mPosition = 0;
	private int mTextSize = 14;
	private int mShowingTextStartPosition = 0;
	private int mShowingTextEndPosition = 0;
	private int mTouchX = 0;
	private int mTouchY = 0;

	public LineView(CyclingListInter<FlowingLineDatam> inputtedText) {
		mInputtedText = inputtedText;
	}

	public void setCyclingList(CyclingListInter<FlowingLineDatam> inputtedText) {
		mInputtedText = inputtedText;
	}

	public CyclingListInter<FlowingLineDatam> getCyclingList() {
		return mInputtedText;
	}

	public int getShowingTextStartPosition() {
		return mShowingTextStartPosition;
	}

	public int getShowingTextEndPosition() {
		return mShowingTextEndPosition;
	}

	@Override
	public void paint(SimpleGraphics graphics) {
		CyclingListInter<FlowingLineDatam> showingText = mInputtedText;
		updateStatus(graphics, showingText);
		drawBG(graphics);
		int start = start(showingText);
		int end = end(showingText);
		int blank = blank(showingText);

		FlowingLineDatam[] list = null;
		if (start > end) {
			list = new FlowingLineDatam[0];
		} else {
			list = new FlowingLineDatam[end - start];
			list = showingText.getElements(list, start, end);
		}

		showLineDate(graphics, list, blank);
		mShowingTextStartPosition = start;
		mShowingTextStartPosition = end;
	}

	public int start(CyclingListInter<FlowingLineDatam> showingText) {
		int numOfStackedString = showingText.getNumberOfStockedElement();
		int referPoint = numOfStackedString - (mPosition + mNumOfLine);
		int start = referPoint;
		if (start < 0) {
			start = 0;
		}
		return start;
	}

	public int end(CyclingListInter<FlowingLineDatam> showingText) {
		int numOfStackedString = showingText.getNumberOfStockedElement();
		int referPoint = numOfStackedString - (mPosition + mNumOfLine);
		int end = referPoint + mNumOfLine;
		if (end < 0) {
			end = 0;
		}
		if (end >= numOfStackedString) {
			end = numOfStackedString;
		}
		return end;
	}

	public int blank(CyclingListInter<FlowingLineDatam> showingText) {
		int numOfStackedString = showingText.getNumberOfStockedElement();
		int referPoint = numOfStackedString - (mPosition + mNumOfLine);
		int blank = 0;
		boolean uppserSideBlankisViewed = (referPoint) < 0;
		if (uppserSideBlankisViewed) {
			blank = -1 * referPoint;
		}
		return blank;
	}

	private void showLineDate(SimpleGraphics graphics, FlowingLineDatam[] list, int blank) {
		for (int i = 0; i < list.length; i++) {
			if (list[i] == null) {
				continue;
			}

			// drawLine
			graphics.setColor(list[i].getColor());
			int startStopY = graphics.getTextSize() * (blank + i + 1);
			if (list[i].getStatus() == FlowingLineDatam.INCLUDE_END_OF_LINE) {
				graphics.drawLine(10, startStopY, graphics.getWidth() - 10,
						startStopY);
			}
			int x = getWidth()/20;
			int y = graphics.getTextSize()*(blank+i+1);
			graphics.drawText("" + list[i], x, y);
			
			// touchEvent
			if(mTouchY>0){
				if(y<mTouchY&& mTouchY<(y+mTextSize)){
					searchX(list[i], mTouchX);
					mTouchX = -999;
					mTouchY = -999;
				}
			}
		}
	}

	private void searchX(FlowingLineDatam datam, int x) {
		String line = datam.toString();
		Paint paint = new Paint();
		paint.setTextSize(mTextSize);
		int p = paint.breakText(line.toCharArray(), 0, line.length(), x, null);
		datam.insert("��", p);
	}

	private void drawBG(SimpleGraphics graphics) {
		graphics.drawBackGround(Color.parseColor("#cc795514"));
		graphics.setColor(Color.parseColor("#ccc9f486"));
	}

	private void updateStatus(SimpleGraphics graphics, CyclingListInter<FlowingLineDatam> showingText) {
		mNumOfLine = getHeight() / mTextSize;
		int blankSpace = mNumOfLine / 2;
		if (mPosition < -(mNumOfLine - blankSpace)) {
			setPosition(-(mNumOfLine - blankSpace) - 1);
		} else if (mPosition > (showingText.getNumberOfStockedElement() - blankSpace)) {
			setPosition(showingText.getNumberOfStockedElement() - blankSpace);
		}
		graphics.setTextSize(mTextSize);
	}

	public void setPosition(int position) {
		mPosition = position;
	}

	public int getPosition() {
		return mPosition;
	}

	@Override
	public boolean onTouchTest(int x, int y, int action) {
		mTouchX = x;
		mTouchY = y;
		return true;
	}
}