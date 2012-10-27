package info.kyorohiro.helloworld.textviewer.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import info.kyorohiro.helloworld.display.widget.lineview.LineView;
import info.kyorohiro.helloworld.display.widget.lineview.edit.EditableLineView;
import info.kyorohiro.helloworld.display.widget.lineview.edit.EditableLineViewBuffer;
import info.kyorohiro.helloworld.text.KyoroString;
import info.kyorohiro.helloworld.textviewer.KyoroApplication;
import info.kyorohiro.helloworld.textviewer.viewer.TextViewer;
import info.kyorohiro.helloworld.util.Utility;

public class SaveTask implements Runnable {

	private TextViewer mViewer = null;
	private EditableLineView mEditor = null;
	private EditableLineViewBuffer mBuffer = null;
	private File mSaveFilePath = null;
	private String mCharset = "UTF8";

	public SaveTask(TextViewer viewer, File path){//EditableLineView editor, String charset, File path) {
		LineView _viewer = viewer.getLineView();
		EditableLineView editor = (EditableLineView)_viewer;
		mViewer = viewer;
		mEditor = editor;
		mBuffer = (EditableLineViewBuffer)editor.getLineViewBuffer();
		mSaveFilePath = path;
	}

	@Override
	public void run() {
		try {
			KyoroApplication.showMessage("start save");
			action();
			KyoroApplication.showMessage("end save");
		} catch (IOException e) {
			e.printStackTrace();
			KyoroApplication.showMessage("failed save "+e);
		}
	}

	public void action() throws IOException {
		try {
			mEditor.isLockScreen(true);
			mBuffer.isSync(true);
			mViewer.getManagedLineViewBuffer().reserve();
			Utility.copyTransfer(new File(mViewer.getCurrentPath()), getBackupFile());
			save_init();
			for(int i=0;i<mBuffer.getNumberOfStockedElement();i++) {
				KyoroString str = mBuffer.get(i);
				byte[] b = (""+str).getBytes(mCharset);
				mStream.write(b, 0, b.length);
				Thread.yield();
			}
		}
		finally {
			mEditor.isLockScreen(false);
			mBuffer.isSync(false);
			mViewer.getManagedLineViewBuffer().release();
			save_end();
		}
	}

	private OutputStream mStream = null;
	public void save_init() throws IOException {
		if(!mSaveFilePath.exists()){
			mSaveFilePath.createNewFile();
		}
		mStream = new FileOutputStream(mSaveFilePath);
	}
	public void save_end() throws IOException {
		if(mStream != null) {
			mStream.close();
		}
	}

	public File getBackupFile() {
		if(!mSaveFilePath.exists()){
			return null;
		}
		int num = 0;
		File file = null;
		for(int i=0;i<10;i++){
			file = new File(mSaveFilePath.getAbsolutePath()+"."+num+".kyorohiro.bak");
			if(!file.exists()){
				return file;
			}
		}
		return file;
	}

}
