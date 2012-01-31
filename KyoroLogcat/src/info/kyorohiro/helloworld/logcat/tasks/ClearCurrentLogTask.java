package info.kyorohiro.helloworld.logcat.tasks;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import info.kyorohiro.helloworld.display.widget.CyclingFlowingLineData;
import info.kyorohiro.helloworld.logcat.util.Logcat;
import info.kyorohiro.helloworld.logcat.util.Logcat.LogcatException;

public class ClearCurrentLogTask extends Thread {
	private final Logcat mLogcat = new Logcat();
	private CyclingFlowingLineData mData;
	private String mOption = "-c";

	public ClearCurrentLogTask(CyclingFlowingLineData data) {
		mData = data;
	}

	public void terminate() {
		if(mLogcat != null){
			mLogcat.terminate();
		}
		interrupt();
	}

	public void run() {
		mLogcat.start(mOption);
		try {
			InputStream stream = mLogcat.getInputStream();
			DataInputStream input  = new DataInputStream(stream);
			while(mLogcat.isAlive()) {
				if(0 == input.available()){
					Thread.sleep(100);
				}
				input.readLine();
			}
		} catch (LogcatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// expected exception
		} finally {
			mLogcat.terminate();
			mData.clear();
		}
	}
}
