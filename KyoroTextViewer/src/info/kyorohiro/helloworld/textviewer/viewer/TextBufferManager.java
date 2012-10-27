package info.kyorohiro.helloworld.textviewer.viewer;

import info.kyorohiro.helloworld.display.widget.lineview.LineViewBufferSpec;
import info.kyorohiro.helloworld.display.widget.lineview.ManagedLineViewBuffer;
import info.kyorohiro.helloworld.display.widget.lineview.edit.EditableLineView;
import info.kyorohiro.helloworld.display.widget.lineview.edit.EditableLineViewBuffer;
import info.kyorohiro.helloworld.io.BreakText;
import info.kyorohiro.helloworld.text.KyoroString;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

//
// ���̃N���X����肠���@�\����悤�ɂȂ�����A�}�[�P�b�g�ɃA�b�v����B
// 
public class TextBufferManager {

	private static TextBufferManager sManager = new TextBufferManager();
	public static TextBufferManager getInstance() {
		return sManager;
	}

	private WeakHashMap<Integer,ManagedLineViewBuffer> mMap = new WeakHashMap<Integer, ManagedLineViewBuffer>();
	

}
