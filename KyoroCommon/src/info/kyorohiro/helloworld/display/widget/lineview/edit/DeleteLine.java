package info.kyorohiro.helloworld.display.widget.lineview.edit;

import info.kyorohiro.helloworld.display.widget.lineview.edit.Differ.Line;

import java.util.ArrayList;

public class DeleteLine  implements Line {
	private int mStart = 0;
	private int mLength = 0;
	public static final String RET_STRING = "";

	public DeleteLine(int index) {
		mStart = index;
		mLength = 1;
	}

	@Override
	public CharSequence get(int i) {
		return RET_STRING;
	}

	@Override
	public void rm(int index) {
	}
	@Override
	public int begin() {
		return mStart;
	}

	@Override
	public int length() {
		return mLength;
	}

	@Override
	public void set(int index, CharSequence line) {
		// 何もしない
	}

	@Override
	public void insert(int index, CharSequence line) {
		// 何もしない
	}

	@Override
	public void setStart(int start) {
		// TODO Auto-generated method stub
		mStart = start;
	}
}