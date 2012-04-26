package info.kyorohiro.helloworld.textviewer.viewer;

import info.kyorohiro.helloworld.android.util.SimpleLock;
import info.kyorohiro.helloworld.android.util.SimpleLockInter;
import info.kyorohiro.helloworld.display.widget.lineview.FlowingLineDatam;
import info.kyorohiro.helloworld.util.BigLineData;
import info.kyorohiro.helloworld.util.CyclingList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Color;

public class TextViewerBuffer

// todo following code:
// mod from extends to implements
// example)
// >>implements CyclingList<Flow..?>
// >> private CyclingList<FlowingLineDatam> mCash = ...;
//
extends LockableCyclingList {
//		extends CyclingList<FlowingLineDatam>  {

	private BigLineData mLineManagerFromFile = null;
	private int mCurrentBufferStartLinePosition = 0;
	private int mCurrentBufferEndLinePosition = 0;
	private int mCurrentPosition = 0;
	private FlowingLineDatam mReturnUnexpectedValue = new FlowingLineDatam(
			"..", Color.RED, FlowingLineDatam.INCLUDE_END_OF_LINE);
	private FlowingLineDatam mReturnLoadingValue = new FlowingLineDatam(
			"loading..", Color.parseColor("#33FFFF00"),
			FlowingLineDatam.INCLUDE_END_OF_LINE);
	private LookAheadCaching mCashing = null;
	private int mNumberOfStockedElement = 0;

	public TextViewerBuffer(int listSize, int textSize, int screenWidth,
			File path, String charset) {
		super(listSize);
		try {
			mLineManagerFromFile = new BigLineData(path, charset, textSize,
					screenWidth);
			mCashing = new LookAheadCaching(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public BigLineData getBigLineData() {
		return mLineManagerFromFile;
	}

	public synchronized int getNumberOfStockedElement() {
		return mNumberOfStockedElement;
	}

	public synchronized int getCurrentBufferStartLinePosition() {
		return mCurrentBufferStartLinePosition;
	}

	public synchronized int getCurrentBufferEndLinePosition() {
		return mCurrentBufferEndLinePosition;
	}

	public synchronized int getCurrentPosition() {
		return mCurrentPosition;
	}

	public void startReadFile() {
		mCashing.startReadForward(-1);
	}

	public void startReadFile(int pos) {
		mCashing.startReadForward(pos);
	}

	public void dispose() {
		if (null != mLineManagerFromFile) {
			try {
				mLineManagerFromFile.close();
				mLineManagerFromFile = null;
				mCashing = null;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	@Override
	public synchronized void head(FlowingLineDatam element) {
		int num = getNumOfAdd();
		super.head(element);
		if (element instanceof MyBufferDatam) {
			setNumOfAdd(num);
		}
	}

	@Override
	public synchronized void add(FlowingLineDatam element) {
		int num = getNumOfAdd();
		super.add(element);
		if (element instanceof MyBufferDatam) {
			MyBufferDatam b = (MyBufferDatam)element;
			int pos = b.getLinePosition();
			if(mNumberOfStockedElement<(pos+1)) {
				mNumberOfStockedElement = pos+1;
				setNumOfAdd(num+1);
			}
			else {
				setNumOfAdd(num);				
			}
		}
	}

	// todo : this method is so big
	public synchronized FlowingLineDatam get(int i) {
		// �ǂݍ��ރz�W�V�����𒲂ׂ�B
		if (i < 0) {
			return mReturnUnexpectedValue;
		}
		mCurrentBufferStartLinePosition = 0;
		mCurrentBufferEndLinePosition = 0;
		mCurrentPosition = i;
		try {
			int bufferSize = super.getNumberOfStockedElement();

			doSetCurrentBufferedPosition();
			// must to call following code after
			// mCurrentBufferStartLinePosition.
			int bufferedPoaition = i - mCurrentBufferStartLinePosition;

			if (bufferedPoaition < 0 || bufferSize < bufferedPoaition) {
				return mReturnLoadingValue;
			} else {
				FlowingLineDatam bufferedDataForReturn = super
						.get(bufferedPoaition);
				if (bufferedDataForReturn == null) {
					return mReturnUnexpectedValue;
				}
				return bufferedDataForReturn;
			}
		} catch (Throwable t) {
			t.printStackTrace();
			return mReturnUnexpectedValue;
		} finally {
			if (mCashing != null) {
				mCashing.updateBufferedStatus();
			}
		}
	}

	private void doSetCurrentBufferedPosition() {
		int bufferSize = super.getNumberOfStockedElement();
		if (0 < bufferSize) {
			CharSequence startLine = super.get(0);
			CharSequence endLine = super.get(bufferSize - 1);
			MyBufferDatam startLineWithPosition = (MyBufferDatam) startLine;
			MyBufferDatam endLineWithPosition = (MyBufferDatam) endLine;
			mCurrentBufferStartLinePosition = (int) startLineWithPosition
					.getLinePosition();
			mCurrentBufferEndLinePosition = (int) endLineWithPosition
					.getLinePosition();
		} else {
			mCurrentBufferStartLinePosition = 0;
			mCurrentBufferEndLinePosition = 0;
		}
	}

	public static class MyBufferDatam extends FlowingLineDatam {
		private int mLinePosition = 0;

		public MyBufferDatam(CharSequence line, int color, int status,
				int linePosition) {
			super(line, color, status);
			mLinePosition = linePosition;
		}

		public synchronized int getLinePosition() {
			return mLinePosition;
		}
	}
}
