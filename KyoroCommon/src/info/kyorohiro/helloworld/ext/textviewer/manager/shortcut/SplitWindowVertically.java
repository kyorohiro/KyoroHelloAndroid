package info.kyorohiro.helloworld.ext.textviewer.manager.shortcut;

import info.kyorohiro.helloworld.display.widget.editview.EditableLineView;
import info.kyorohiro.helloworld.display.widget.editview.EditableLineViewBuffer;
import info.kyorohiro.helloworld.display.widget.editview.shortcut.KeyEventManager.Task;
import info.kyorohiro.helloworld.ext.textviewer.manager.BufferManager;
import info.kyorohiro.helloworld.ext.textviewer.manager.shortcut.ISearchForward.ISearchForwardTask;

public class SplitWindowVertically implements Task {

	@Override
	public String getCommandName() {
		return "split-window-vertically";
	}

	@Override
	public void act(EditableLineView view, EditableLineViewBuffer buffer) {
//		android.util.Log.v("kiyo","begin splitWindowVertically act");
		BufferManager.getManager().splitWindowVertically();	
//		android.util.Log.v("kiyo","begin splitWindowVertically --");
		buffer.clearYank();
//		android.util.Log.v("kiyo","begin splitWindowVertically act");
	}

}
