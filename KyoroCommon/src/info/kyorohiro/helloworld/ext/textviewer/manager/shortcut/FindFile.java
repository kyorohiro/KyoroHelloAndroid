package info.kyorohiro.helloworld.ext.textviewer.manager.shortcut;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;

//import android.webkit.ConsoleMessage.MessageLevel;

import info.kyorohiro.helloworld.display.simple.MessageDispatcher;
import info.kyorohiro.helloworld.display.simple.SimpleApplication;
import info.kyorohiro.helloworld.display.simple.MessageDispatcher.Receiver;
import info.kyorohiro.helloworld.display.widget.editview.EditableLineView;
import info.kyorohiro.helloworld.display.widget.editview.EditableLineViewBuffer;
import info.kyorohiro.helloworld.display.widget.editview.shortcut.KeyEventManager.Task;
import info.kyorohiro.helloworld.ext.textviewer.manager.BufferManager;
import info.kyorohiro.helloworld.ext.textviewer.manager.MiniBuffer;
import info.kyorohiro.helloworld.ext.textviewer.manager.shortcut.ISearchForward.ISearchForwardTask;
import info.kyorohiro.helloworld.ext.textviewer.viewer.TextViewer;
import info.kyorohiro.helloworld.io.VirtualFile;
import info.kyorohiro.helloworld.text.KyoroString;
import info.kyorohiro.helloworld.util.AutocandidateList;

public class FindFile implements Task {

	@Override
	public String getCommandName() {
		return "find-file";
	}

	@Override
	public void act(EditableLineView view, EditableLineViewBuffer buffer) {
		TextViewer target = BufferManager.getManager().getFocusingTextViewer();
		if(target == null || view != target.getLineView()) {
			return;
		}
		BufferManager.getManager().getMiniBuffer().startMiniBufferJob(new FindFileTask(target));
		buffer.clearYank();
	}

	public static class FindFileTask implements MiniBufferJob {
//		private TextViewer mViewer = null;
		private WeakReference<TextViewer> mViewer = null;

		private File mCurrentPath  = new File("");
		private File mCurrentDir  = new File("");
		private UpdateInfo mUpdate = null;
		private boolean mFirstFocosIsInfo = false;

		public FindFileTask(TextViewer viewer, File path, boolean firstFocosIsInfo) {
			mViewer = new WeakReference<TextViewer>(viewer);
			mCurrentPath = path;
			mFirstFocosIsInfo = firstFocosIsInfo;
		}

		public FindFileTask(TextViewer viewer, File path) {
			mViewer = new WeakReference<TextViewer>(viewer);
			mCurrentPath = path;
		}

		public FindFileTask(TextViewer viewer) {
			mViewer = new WeakReference<TextViewer>(viewer);
			mCurrentPath = new File(viewer.getCurrentPath());
		}

		public boolean isAlive() {
			TextViewer viewer = mViewer.get();
			if(viewer == null) {
				return false;
			}
			if(viewer.isDispose()) {
				return false;
			}
			return true;
		}
		@Override
		public void enter(String line) {
//			android.util.Log.v("kiyo","enter " + line);
			File newFile = new File(line);
			File parent = newFile.getParentFile();
			TextViewer viewer = mViewer.get();
			if(viewer == null||!isAlive()) {
				return;
			}
			
			if(!parent.exists()) {
				parent.mkdirs();
			}
			if(!newFile.exists()) {
				try {
					newFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
			try {
				SimpleApplication app = BufferManager.getManager().getApplication();
				if(newFile.getParentFile().equals(app.getApplicationDirectory())) {
					viewer.readFile(newFile);
				} else {
					viewer.readFile(newFile);
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
//			android.util.Log.v("kiyo","/enter " + line);
			}
		}

		public void input(String mini) {
//			android.util.Log.v("kiyo","input " + mini);
			TextViewer viewer = mViewer.get();
			if(viewer == null||!isAlive()) {
				return;
			}

			MiniBuffer minibuffer = BufferManager.getManager().getMiniBuffer();
			minibuffer.startMiniBufferJob(this);
			File path = null;
			File parent = mCurrentPath.getParentFile();
			mini = mini.replaceAll("\r\n|\n|", "");
//			if(mini.equals("..")) {
//				path = parent;
//			}else 
//			if(mCurrentPath.isDirectory()) {
//				path = new File(mCurrentPath, mini);
//			} else {
//				if(parent == null) {
//					return;
//				}
//				path = new File(parent, mini);
//			}
			path = new File(mini);
//			android.util.Log.v("kiyo","##--A-"+path.isFile());
//			android.util.Log.v("kiyo","##--A-"+path.isDirectory());
//			android.util.Log.v("kiyo","##--A->"+path.exists()+"<");
//			android.util.Log.v("kiyo","##--A->"+path+"<");
			if(!path.exists()) {
//				android.util.Log.v("kiyo","##--AZ-");				
				return;
			}
//			android.util.Log.v("kiyo","##--AV-");				
	
			MiniBuffer modeBuffer = BufferManager.getManager().getMiniBuffer();
			EditableLineViewBuffer buffer = (EditableLineViewBuffer)modeBuffer.getLineView().getLineViewBuffer();
			buffer.clear();
			modeBuffer.getLineView().getLeft().setCursorCol(0);
			modeBuffer.getLineView().getLeft().setCursorRow(0);
			//
			buffer.pushCommit(path.getAbsolutePath(), 1);
			modeBuffer.getLineView().recenter();
			//
			//
//			android.util.Log.v("kiyo","#------"+path);
			if(path.exists()&&!path.isDirectory()&&path.isFile()) {
//				android.util.Log.v("kiyo","##--------A-1-----------");
				enter(path.toString());
			} else {
//				android.util.Log.v("kiyo","##--------A-3-----------");
				tab(path.toString());
			}
			
		}

		@Override
		public void tab(String line) {
//			android.util.Log.v("kiyo","tab " + line);
			TextViewer viewer = mViewer.get();
			if(viewer == null||!isAlive()) {
				return;
			}
			File lf = new File(line);
			File pf = lf.getParentFile();
			MiniBuffer modeBuffer = BufferManager.getManager().getMiniBuffer();
			if(mCurrentPath.getAbsoluteFile().equals(lf.getAbsoluteFile())) {
				//android.util.Log.v("kiyo","##--------3------------"+mCurrentPath+","+pf.getAbsolutePath());
//				android.util.Log.v("kiyo","##--------1------------");
				mCurrentPath = lf.getAbsoluteFile();
				TextViewer mInfo = BufferManager.getManager().getInfoBuffer();
				if(mInfo == null || mInfo.isDispose()) {
					return;
				}
				int num = mInfo.getLineView().getShowingTextStartPosition();
				int p = mInfo.getLineView().getPositionY();
				int v = mInfo.getLineView().getShowingTextSize();
				//android.util.Log.v("kiyo","##p="+p+",num="+num+",v="+v);
				if(0<num) {
					mInfo.getLineView().setPositionY(mInfo.getLineView().getPositionY()+3);
				} else {
					mInfo.getLineView().setPositionY(0);					
				}
			} else {
//				android.util.Log.v("kiyo","##--------2------------");
				File cp = mCurrentPath.getParentFile();
			//	if(!cp.exists()) {
			//		cp = mCurrentPath;
			//	}
				if(cp != null&&cp.equals(pf)) {
					if(mUpdate != null) {
						String candidate = mUpdate.getCandidate(lf.getName());
						//android.util.Log.v("kiyo","##---4--"+candidate+","+lf.getName());
						if(lf.getName().length()<candidate.length()) {
							//					MiniBuffer buffer = BufferManager.getManager().getMiniBuffer();
							EditableLineViewBuffer buffer = (EditableLineViewBuffer)modeBuffer.getLineView().getLineViewBuffer();
							buffer.clear();
							modeBuffer.getLineView().getLeft().setCursorCol(0);
							modeBuffer.getLineView().getLeft().setCursorRow(0);
							//
							File t = new File(pf,candidate);
							buffer.pushCommit(t.getAbsolutePath(), 1);
							modeBuffer.getLineView().recenter();
						}
					}
				}
//				android.util.Log.v("kiyo","##--------4------------"+mCurrentPath+","+lf.getAbsolutePath());
				if(lf.exists() && lf.isDirectory()) {
//					android.util.Log.v("kiyo","##--------4-1-----------");
					mCurrentPath = lf.getAbsoluteFile();
					mCurrentDir = lf.getAbsoluteFile();
//					android.util.Log.v("kiyo","FT: start 00");
					modeBuffer.startTask(mUpdate = new UpdateInfo(BufferManager.getManager().getInfoBuffer(), new File(line), ""));
				} else if(pf.exists()){
//					android.util.Log.v("kiyo","##--------4-2-----------");
					mCurrentPath = lf.getAbsoluteFile();
					mCurrentDir = pf;
//					android.util.Log.v("kiyo","FT: start 01");
					modeBuffer.startTask(mUpdate = new UpdateInfo(BufferManager.getManager().getInfoBuffer(), pf, lf.getName()));	
				}
			}
		}

		private boolean mFirst = true;
		@Override
		public void begin() {
//.util.Log.v("kiyo","begin");
			//
			File target = mCurrentPath;
			File base = target.getParentFile();
			if(base == null||target.isDirectory()){
				base = target;
			}
			// todo 
			BufferManager.getManager().beginInfoBuffer();
			if(mFirst) {
				mFirst = false;
				if(mFirstFocosIsInfo) {
					BufferManager.getManager().changeFocus(BufferManager.getManager().getInfoBuffer());					
				} else{
					BufferManager.getManager().changeFocus(BufferManager.getManager().getMiniBuffer());
				}
			}
			MiniBuffer modeBuffer = BufferManager.getManager().getMiniBuffer();
			EditableLineViewBuffer buffer = (EditableLineViewBuffer)modeBuffer.getLineView().getLineViewBuffer();
			buffer.clear();
			modeBuffer.getLineView().getLeft().setCursorCol(0);
			modeBuffer.getLineView().getLeft().setCursorRow(0);
			buffer.pushCommit(""+base.getAbsolutePath(), 1);
			modeBuffer.getLineView().recenter();
//			android.util.Log.v("kiyo","FT: start 03");
			modeBuffer.startTask(mUpdate = new UpdateInfo(BufferManager.getManager().getInfoBuffer(), mCurrentPath = base.getAbsoluteFile(),""));
			
			//
			MessageDispatcher.getInstance().addReceiver(new MyReceiver(this));
		}
		@Override
		public void end() {
//			android.util.Log.v("kiyo","end ");
			BufferManager.getManager().endInfoBuffer();			
		}
	}

	public static class UpdateInfo implements Runnable {
		private TextViewer mInfo = null;
		private File mPath = null;
		private String mFilter = "";
		private AutocandidateList mCandidate = new AutocandidateList(); 
		public UpdateInfo(TextViewer info, File path, String filter) {
//			android.util.Log.v("kiyo","---UpdateInfo"+path+","+filter);
			mInfo = info;
			mPath = path;
			mFilter = filter;
		}

		public String getCandidate(String c) {
			return mCandidate.candidateText(c);
		}

		@Override
		public void run() {
			try {
//				android.util.Log.v("kiyo","FT: start ");
				int c=0;
				mCandidate.clear();
//				android.util.Log.v("kiyo","FT: start 00001-1-");

				MyFilter filter = new MyFilter(mFilter);
//				android.util.Log.v("kiyo","FT: start 00001-2-");
				BufferManager.getManager().beginInfoBuffer();
//				android.util.Log.v("kiyo","FT: start 00001-3-");
				EditableLineView viewer = mInfo.getLineView();
//				android.util.Log.v("kiyo","FT: start 00001-4-");
				mInfo.getTextViewerBuffer().getBigLineData().ffformatterOn();
//				android.util.Log.v("kiyo","FT: start 00001-5-");
				EditableLineViewBuffer buffer = (EditableLineViewBuffer)mInfo.getLineView().getLineViewBuffer();
				viewer.setTextSize(BufferManager.getManager().getBaseTextSize());
//				android.util.Log.v("kiyo","FT: start 00001-6-");
				buffer.setCursor(0, 0);
//				android.util.Log.v("kiyo","FT: start 00001-7-");
				if(mPath == null) {
					return;
				}
//				android.util.Log.v("kiyo","FT: start 00001-6-");
///*
//				android.util.Log.v("kiyo","FT: start 00001");
				VirtualFile vfile = mInfo.getTextViewerBuffer().getBigLineData().getVFile();

				{
					File p = mPath.getParentFile();
					if(p!=null&&p.isDirectory()) {
						addFile(vfile, p, "..1");
					}
				}
				if(!mPath.isDirectory()&&mPath.isFile()) {
					addFile(vfile, mPath, null);
				}
//				android.util.Log.v("kiyo","FT: start 00002");
				if(mPath.isDirectory()) {
					mPath.listFiles(filter);
					Collection<File> dir = filter.getDirs();
					Collection<File> files = filter.getFiles();
					b(vfile, viewer, buffer, dir);
					b(vfile, viewer, buffer, files);
					{
						File p = mPath.getParentFile();
						if(p!=null&&p.isDirectory()) {
							addFile(vfile, p, "..2");
						}
					}
					dir.clear();
					files.clear();
					filter.clear();
				} else {
					{
						File p = mPath.getParentFile();
						if(p!=null&&p.isDirectory()) {
							addFile(vfile, p, "..");
						}
					}
				}
//				android.util.Log.v("kiyo","FT: start 00003");
			} catch(Throwable t) {
				t.printStackTrace();
			} finally {
//				android.util.Log.v("kiyo","FT: end ");
			}
		}


		private void b(VirtualFile v, EditableLineView viewer,	
				EditableLineViewBuffer buffer, Collection<File> fileList) throws InterruptedException, UnsupportedEncodingException, IOException {
			int size = buffer.getDiffer().length();
			int c=0;
			for(File f : fileList) {
				Thread.sleep(0);
				if(f == null) {
					continue;
				}
				c++;
				if(c<100){
					mCandidate.add(f.getName());
				}
				addFile(v, f, null);
				if(c%100==0){
					Thread.sleep(10);
				}
			}
		}

		private void addFile(VirtualFile vFile, File file, String label) throws UnsupportedEncodingException, IOException, InterruptedException {
			Thread.sleep(0);
			String INFO = ":::"+file.getAbsolutePath();
			if(label == null) {
				label = file.getName();
			}
			vFile.addChunk(("●"+label+(file.isDirectory()?"/":"")+INFO).getBytes("utf8"));
			String date = DateFormat.getDateTimeInstance().format(new Date(file.lastModified()));
			vFile.addChunk(("\r\n  "+date+""+INFO).getBytes("utf8"));
			vFile.addChunk(("\r\n  "+file.length()+"byte"+INFO+":::HR").getBytes("utf8"));			
			vFile.addChunk(("\r\n").getBytes("utf8"));			
		}


	}
	public static class MyFilter implements FilenameFilter {
		private String mFilter = "";
		private TreeMap<String,File> mFile = new TreeMap<String,File>();
		private TreeMap<String,File> mDir = new TreeMap<String,File>();
		
		public MyFilter(String filter) {
			mFilter = filter;
		}

		public Collection<File> getFiles() {
			Collection<File> ret = mFile.values();
			return mFile.values();
		}
		public Collection<File> getDirs() {
			Collection<File> ret = mDir.values();
			return mDir.values();
		}

		@Override
		public boolean accept(File dir, String filename) {
			if(filename.startsWith(mFilter)) {
				File tmp = new File(dir,filename);
				if(tmp.isDirectory()) {
					mDir.put(tmp.getName().toLowerCase()+":"+tmp.getName(), tmp);
				} else {
					mFile.put(tmp.getName().toLowerCase()+":"+tmp.getName(), tmp);
				}
				//android.util.Log.v("kiyo","aa="+filename+","+mFilter);
				//return true;
				return false;
			}
			return false;
		}
		public void clear() {
			if(mFile != null) {
				mFile.clear();
			}
			if(mDir != null) {
				mDir.clear();
			}
		}
	}

	public static class MyReceiver implements Receiver {
		public static FindFileTask sTask = null;
		private WeakReference<FindFileTask> mTask = null;
		public MyReceiver(FindFileTask task) {
			sTask = task;
			mTask = new WeakReference<FindFileTask>(task);
		}

		@Override
		public String getType() {
			return "find";
		}

		@Override
		public void onReceived(KyoroString message, String type) {
//			android.util.Log.v("kiyo","rev="+message);
			FindFileTask task = mTask.get();
			if(!task.isAlive()){
				return;
			}
			if(task != null) {
				//task.input(message.toString());
				task.input(message.getExtra());
				//task.tab(message.getExtra());
			}
		}
	}
}