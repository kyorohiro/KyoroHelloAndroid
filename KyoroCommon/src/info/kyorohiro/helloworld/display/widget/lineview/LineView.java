package info.kyorohiro.helloworld.display.widget.lineview;

import info.kyorohiro.helloworld.display.simple.SimpleDisplayObject;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import info.kyorohiro.helloworld.util.CyclingListInter;
import android.graphics.Color;

public class LineView extends SimpleDisplayObject {
	private int mNumOfLine = 100;
	private CyclingListInter<FlowingLineDatam> mInputtedText = null;
	private int mPosition = 0;
	private int mPosX = 0;//todo
	private int mTextSize = 16;
	private int mShowingTextStartPosition = 0;
	private int mShowingTextEndPosition = 0;
	private float mScale = 1.0f;
	private int mAddedPoint = 0;

	public LineView(CyclingListInter<FlowingLineDatam> inputtedText, int textSize) {
		mInputtedText = inputtedText;
		mTextSize = textSize;
	}

	public void setScale(float scale) {
		mScale = scale;
	}

	public float getScale() {
		return mScale;
	}

	public void setTextSize(int textSize) {
		mTextSize = textSize;
	}

	public int getTextSize() {
		return mTextSize;
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


	// todo refactaring
	private int cash  = 0;
	@Override
	public void paint(SimpleGraphics graphics) {
		CyclingListInter<FlowingLineDatam> showingText = mInputtedText;
		if(mPosition > 1) {
			// todo refactaring
			int a = resetAddPositionY();
			if(a != 0){
				cash += showingText.getNumOfAdd();
				mPosition = cash+a;
			} else {
				cash = 0;
				mPosition += showingText.getNumOfAdd();				
			}
		}
		showingText.clearNumOfAdd();

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
		mShowingTextEndPosition = end;
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

	private void showLineDate(SimpleGraphics graphics, FlowingLineDatam[] list,
			int blank) {
		for (int i = 0; i < list.length; i++) {
			if (list[i] == null) {
				continue;
			}

			// drawLine
			graphics.setColor(list[i].getColor());
			int startStopY = (int)(graphics.getTextSize()*1.2) * (blank + i + 1);
			if (list[i].getStatus() == FlowingLineDatam.INCLUDE_END_OF_LINE) {
				graphics.drawLine(10, startStopY, graphics.getWidth() - 10,
						startStopY);
			}
			if(mPosX >0) {
				mPosX = 0;
			}
			if(mPosX < -1*(getWidth()*mScale-getWidth())) {
				mPosX = -1*(int)(getWidth()*mScale-getWidth());
			}
			int x = (getWidth()) / 20 + mPosX*19/20; //todo mPosY
			int y = (int)(graphics.getTextSize()*1.2) * (blank + i + 1);
			graphics.drawText("" + list[i], x, y);
		}
	}

	private void drawBG(SimpleGraphics graphics) {
		graphics.drawBackGround(Color.parseColor("#FF000022"));//#FF777788"));
		//graphics.drawBackGround(Color.parseColor("#cc795514"));
		//graphics.setColor(Color.parseColor("#ccc9f486"));
	}

	private void updateStatus(SimpleGraphics graphics,
			CyclingListInter<FlowingLineDatam> showingText) {
		mNumOfLine = (int)(getHeight() / (mTextSize*1.2*mScale));//todo mScale
		int blankSpace = mNumOfLine / 2;
		if (mPosition < -(mNumOfLine - blankSpace)) {
			setPositionY(-(mNumOfLine - blankSpace) - 1);
		} else if (mPosition > (showingText.getNumberOfStockedElement() - blankSpace)) {
			setPositionY(showingText.getNumberOfStockedElement() - blankSpace);
		}
		graphics.setTextSize((int)(mTextSize*mScale));//todo mScale
	}

	public synchronized void addPositionY(int position) {
		mAddedPoint += position;
	}

	private synchronized int resetAddPositionY() {
		int tmp = mAddedPoint;
		mAddedPoint = 0;
		return tmp;
	}

	public synchronized void setPositionY(int position) {
		mPosition = position;
	}

	public void setPositionX(int x) {
		mPosX = x;
	}

	public synchronized int getPositionY() {
		return mPosition;
	}

	public int getPositionX() {
		return mPosX;
	}
}
