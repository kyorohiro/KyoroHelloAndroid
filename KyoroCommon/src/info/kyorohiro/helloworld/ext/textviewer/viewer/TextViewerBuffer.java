package info.kyorohiro.helloworld.ext.textviewer.viewer;

import info.kyorohiro.helloworld.display.simple.BreakText;
import info.kyorohiro.helloworld.display.simple.SimpleGraphicUtil;
import info.kyorohiro.helloworld.display.widget.lineview.LineViewBufferSpec;
import info.kyorohiro.helloworld.ext.textviewer.manager.LineViewManager;
import info.kyorohiro.helloworld.io.BigLineData;
import info.kyorohiro.helloworld.text.KyoroString;
import info.kyorohiro.helloworld.util.LockableCyclingList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class TextViewerBuffer extends LockableCyclingList implements LineViewBufferSpec {
	private BigLineData mLineManagerFromFile = null;
	private int mCurrentBufferStartLinePosition = 0;
	private int mCurrentBufferEndLinePosition = 0;
	private int mCurrentPosition = 0;
	private KyoroString mErrorLineMessage = new KyoroString("error..\n", SimpleGraphicUtil.parseColor("#FFFF0000"));
	private KyoroString mLoadingLineMessage = new KyoroString("loading..\n", SimpleGraphicUtil.parseColor("#33FFFF00"));

	public static final int SYNC_GET_LINE_COLOR = SimpleGraphicUtil.parseColor("#FF2222FF"); 
	private NeiborhoodCashing mNeiborCashing = null;
	private LatestAccessCashing mLatestCashing = new LatestAccessCashing(100);
	private long mNumberOfStockedElement = 0;
	private boolean mIsSync = false;

	public TextViewerBuffer(int listSize, int cash2, BreakText breakText, File path, String charset) throws FileNotFoundException {
		super(listSize);
		//android.util.Log.v("kiyo","cash2="+cash2);
		mLatestCashing = new LatestAccessCashing(cash2);
		mErrorLineMessage.isNowLoading(true);
		mLoadingLineMessage.isNowLoading(true);
		mLineManagerFromFile = new BigLineData(path, charset, breakText);
		mNeiborCashing = new NeiborhoodCashing(this);
	}

	public BigLineData getBigLineData() {
		return mLineManagerFromFile;
	}

	public synchronized int getNumberOfStockedElement() {
		//todo
		return (int)mNumberOfStockedElement;
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
		mNeiborCashing.startReadForward(-1);
	}

	public void startReadFile(int pos) {
		mNeiborCashing.startReadForward(pos);
	}

	public void dispose() {
//		android.util.Log.v("kiyo","buffer =dispose()");
		if (null != mLineManagerFromFile) {
			try {
				if(mNeiborCashing != null){
					mNeiborCashing.dispose();
//					mCashing.stopTask();
					mNeiborCashing = null;
				}
				mLineManagerFromFile.close();
				mLineManagerFromFile = null;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	@Override
	public synchronized void head(KyoroString element) {
		int num = getNumOfAdd();
		super.head(element);
		if (element instanceof KyoroString) {
			setNumOfAdd(num);
		}
	}

	@Override
	public synchronized void add(KyoroString element) {
		int num = getNumOfAdd();
		super.add(element);
		if (element instanceof KyoroString) {
			KyoroString b = (KyoroString)element;
			//
			// change to folllowing code . need to test
//			setNumberOfStockedElement(b);
			long pos = b.getLinePosition();
			if(mNumberOfStockedElement<(pos+1)) {
				mNumberOfStockedElement = pos+1;
				setNumOfAdd(num+1);
			}
			else {
				setNumOfAdd(num);				
			}
		}
	}

	
	// so bad performance!! must to improve
	private KyoroString getSync(int i) {
		if (i < 0) {
			return mErrorLineMessage;
		}
		else if(i>=getNumberOfStockedElement()&&!isLoading()) {
			return mErrorLineMessage;
		}
		try {
			mLineManagerFromFile.moveLine(i);
			// = mLineManagerFromFile.getLastLinePosition();
			KyoroString ret = mLineManagerFromFile.readLine();
			ret.setColor(SYNC_GET_LINE_COLOR);
//			android.util.Log.v("kiyo","#sync=mNumberOfStockedElement-1--"+mNumberOfStockedElement+","+ret.getLinePosition());
			setNumberOfStockedElement(ret);
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			return mErrorLineMessage;
		}
	}

	public void setNumberOfStockedElement(KyoroString ret) {
		if(!mLineManagerFromFile.wasEOF()&&mNumberOfStockedElement<=ret.getLinePosition()+2){
			mNumberOfStockedElement = ret.getLinePosition()+2;
			setNumOfAdd(getNumOfAdd()+2);
//			android.util.Log.v("kiyo","-mNumberOfStockedElement-2--"+mNumberOfStockedElement);
		}
	}
	private KyoroString getAsync(int i, boolean alookhead) {
		if (i < 0) {
			return mErrorLineMessage;
		}
		try {
			resetBufferedStartEndPosition(i);
			if(!lineIsLoaded(i)||super.getNumberOfStockedElement()<=lineNumberToBufferedNumber(i)) {
				return mLoadingLineMessage;
			}

			KyoroString bufferedDataForReturn = super.get(lineNumberToBufferedNumber(i));
			// todo
			setNumberOfStockedElement(bufferedDataForReturn);
			if (bufferedDataForReturn == null) {
			//	android.util.Log.v("kiyo","ERROR --2--");
				return mErrorLineMessage;
			}
			return bufferedDataForReturn;
		} catch (Throwable t) {
		//	android.util.Log.v("kiyo","ERROR --1--");
			t.printStackTrace();
			return mErrorLineMessage;
		} finally {
			if (mNeiborCashing != null && alookhead) {
				mNeiborCashing.updateBufferedStatus();
			}
		}		
	}

	public synchronized KyoroString get(int i) {
		KyoroString ret = mLatestCashing.get(i);
		if(ret == null) {
			if(mIsSync) {
				ret = getSync(i);			
			} else {
				ret = getAsync(i, true);
			}
		}
		if(!ret.isNowLoading()) {
			mLatestCashing.addLine(ret);
		}
		return ret;
	}

	private int lineNumberToBufferedNumber(int lineNumber) {
		int ret = lineNumber - mCurrentBufferStartLinePosition;
		return ret;
	}

	private boolean lineIsLoaded(int lineNumber) {
		int bufferSize = super.getNumberOfStockedElement();
		int bufferedPoaition = lineNumberToBufferedNumber(lineNumber);
		if (bufferedPoaition < 0 || bufferSize < bufferedPoaition) {
			return false;
		} else {
			return true;
		}
	}

	private void resetBufferedStartEndPosition(int lineNumber) {
		mCurrentPosition = lineNumber;
		int bufferSize = super.getNumberOfStockedElement();
		if (0 < bufferSize) {
			CharSequence startLine = super.get(0);
			CharSequence endLine = super.get(bufferSize - 1);
			KyoroString startLineWithPosition = (KyoroString) startLine;
			KyoroString endLineWithPosition = (KyoroString) endLine;
			mCurrentBufferStartLinePosition = (int) startLineWithPosition.getLinePosition();
			mCurrentBufferEndLinePosition = (int) endLineWithPosition.getLinePosition();
//			android.util.Log.v("kiyo","AA="+startLine+","+endLine);
		} else {
			mCurrentBufferStartLinePosition = 0;
			mCurrentBufferEndLinePosition = 0;
//			android.util.Log.v("kiyo","AA= clear");
		}
	}

	@Override
	public BreakText getBreakText() {
		return mLineManagerFromFile.getBreakText();
	}

	@Override
	public void isSync(boolean isSync) {
		 mIsSync = isSync;
		 if(isSync){
			 mNeiborCashing.stopTask();
		 }
	}
	@Override
	public boolean isSync() {
		return mIsSync;
	}

	@Override
	public boolean isLoading() {
		return !mLineManagerFromFile.wasEOF();
	}
}
