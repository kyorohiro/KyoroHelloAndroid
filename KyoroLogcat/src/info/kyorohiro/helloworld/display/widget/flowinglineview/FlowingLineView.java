package info.kyorohiro.helloworld.display.widget.flowinglineview;

import info.kyorohiro.helloworld.display.simple.SimpleDisplayObject;
import info.kyorohiro.helloworld.display.simple.SimpleDisplayObjectContainer;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import info.kyorohiro.helloworld.display.widget.lineview.CursorableLineView;
import info.kyorohiro.helloworld.display.widget.lineview.LineView;
import info.kyorohiro.helloworld.display.widget.lineview.LineViewBufferSpec;
import info.kyorohiro.helloworld.display.widget.lineview.extraparts.ScrollBar;

//
// for KyoroLogcat
//
public class FlowingLineView extends SimpleDisplayObjectContainer {
	private ScrollBar scrollBar = null;
	private LineView viewer = null;
//	private CursorableLineView viewer = null;

	public FlowingLineView(LineViewBufferSpec inputtedText, int textSize) {
		scrollBar = new ScrollBar(this);
//		viewer = new LineView(inputtedText, textSize);
		viewer = new CursorableLineView(inputtedText, textSize, 512);
//		viewer.setMode(CursorableLineView.MODE_SELECT);
		this.addChild(viewer);
		this.addChild(new Layout());
		this.addChild(scrollBar);
	}

	public LineView getLineView() {
		return viewer;
	}

	@Override
	public void paint(SimpleGraphics graphics) {
		super.paint(graphics);
	}

	private class Layout extends SimpleDisplayObject {
		@Override
		public void paint(SimpleGraphics graphics) {
			LineViewBufferSpec list = viewer.getLineViewBuffer();
			int size = list.getNumberOfStockedElement(); 

			viewer.setRect(graphics.getWidth(), graphics.getHeight());
			scrollBar.setStatus(viewer.getShowingTextStartPosition(), viewer.getShowingTextEndPosition(), size);
		}
	}
	public class MyLineView extends LineView {

		public int mBaseTextSize = 12;
		public MyLineView(LineViewBufferSpec inputtedText,
				int textSize) {
			super(inputtedText, textSize);
			mBaseTextSize = textSize;
		}

		@Override
		public void setTextSize(int textSize) {
			mBaseTextSize = textSize;
			super.setTextSize(textSize);
		}

		public int getBaseTextSize() {
			return mBaseTextSize;
		}
		
		public void setInternalTextSize(int textSize) {
			super.setTextSize(textSize);
		}
		
		
	}
}
