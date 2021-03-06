package info.kyorohiro.helloworld.ext.textviewer.manager.shortcut;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.kyorohiro.helloworld.display.widget.editview.EditableLineView;
import info.kyorohiro.helloworld.display.widget.editview.EditableLineViewBuffer;
import info.kyorohiro.helloworld.display.widget.editview.shortcut.KeyEventManager.Task;
import info.kyorohiro.helloworld.display.widget.lineview.MyCursor;
import info.kyorohiro.helloworld.ext.textviewer.manager.BufferManager;
import info.kyorohiro.helloworld.ext.textviewer.manager.MiniBuffer;
import info.kyorohiro.helloworld.ext.textviewer.manager.MiniBufferJob;
import info.kyorohiro.helloworld.ext.textviewer.manager.task.SearchTask;
import info.kyorohiro.helloworld.ext.textviewer.viewer.TextViewer;
import info.kyorohiro.helloworld.text.KyoroString;

public class ISearchForward implements Task {
	public static String PREV_LINE = "";

	@Override
	public String getCommandName() {
		return "isearch-forward";
	}

	@Override
	public void act(EditableLineView view, EditableLineViewBuffer buffer) {
		BufferManager.getManager().getMiniBuffer().startMiniBufferJob(new ISearchForwardTask(view));
		buffer.clearYank();
	}

	public class ISearchForwardTask implements MiniBufferJob{
		private EditableLineView mTargetView = null;
		public ISearchForwardTask(EditableLineView targetView) {
			mTargetView = targetView;
		}

		@Override
		public void enter(String line){
			PREV_LINE = line;
			if(mTargetView.isDispose()) {
				return;
			}
//			android.util.Log.v("kiyo","#-#ISearchForward-enter"+line);
			MiniBuffer modeBuffer = BufferManager.getManager().getMiniBuffer();
			modeBuffer.startTask(new SearchTask(mTargetView, line));
		}

		@Override
		public void tab(String line) {
		}

		@Override
		public void begin() {
			BufferManager.getManager().changeFocus(BufferManager.getManager().getMiniBuffer());
			MiniBuffer modeBuffer = BufferManager.getManager().getMiniBuffer();
			EditableLineViewBuffer buffer = (EditableLineViewBuffer)modeBuffer.getLineView().getLineViewBuffer();
			buffer.clear();
			buffer.pushCommit(PREV_LINE, 1);
			modeBuffer.getLineView().getLeft().setCursorCol(0);
			modeBuffer.getLineView().getLeft().setCursorRow(0);
			modeBuffer.getLineView().recenter();
//			LineViewManager.getManager().getModeLineBuffer().getLineView().getLineViewBuffer()
		}

		@Override
		public void end() {
			BufferManager.getManager().changeFocus((TextViewer)mTargetView.getParent());			
		}

	}

}
