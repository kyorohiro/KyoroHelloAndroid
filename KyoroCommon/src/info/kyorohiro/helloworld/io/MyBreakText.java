package info.kyorohiro.helloworld.io;

import info.kyorohiro.helloworld.android.adapter.SimpleFontForAndroid;
import info.kyorohiro.helloworld.display.simple.SimpleFont;
import info.kyorohiro.helloworld.text.KyoroString;
import info.kyorohiro.helloworld.util.CharArrayBuilder;

//
//�@���̃R�[�h����A����Ȃ��@�\���폜����K�v������B
//�@--> LineView�̃J�[�\���֘A�̃R�[�h���C������^�C�~���O��
//     �悳��
public class MyBreakText extends BreakText {

	public MyBreakText() {
		super(new SimpleFontForAndroid(), 400);
	}
	
	public void setBufferWidth(int w){
		setWidth(w);
	}

	public int breakText(CharArrayBuilder b, int width) {
		int len = BreakText.breakText(this, b.getAllBufferedMoji(), 0, b.getCurrentBufferedMojiSize(), width);
		//int len = b.getCurrentBufferedMojiSize();
		return len;
	}

	@Override
	public int breakText(CharArrayBuilder mBuffer) {
		return breakText(mBuffer, getWidth());
	}
	

	@Override
	public int getTextWidths(KyoroString text, int start, int end, float[] widths, float textSize) {
		return getTextWidths(text.getChars(), start, end, widths, textSize);
	}

	@Override
	public int getTextWidths(char[] buffer, int start, int end, float[] widths, float textSize) {
		SimpleFont font = getSimpleFont();
		try {
			int ret = 0;
			ret = font.getTextWidths(buffer, start, end, widths,textSize);
			// �ȉ��̃R�[�h��SimpleFont�ɂ���ق����悢����
			for(int i=0;i<ret;i++){
				widths[i] += font.lengthOfControlCode(buffer[i], (int)textSize);
			}
			return ret;
		}catch(Throwable t){
			return 0;
		}
	}

}
