package info.kyorohiro.helloworld.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import android.graphics.Paint;

import junit.framework.TestCase;

public class TestSimpleTextDecoder extends TestCase {
	String[] expect = {
			" !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefhijklmnopqrstuvwxyz{|}~"
					+ "\r\n",
			"���������������������������������������������������������������"
					+ "\r\n",
			"�@�A�B�C�D�E�F�G�H�I�J�K�L�M�N�O�P�Q�R�S�T�U�V�W�X�Y����������������������" + "\r\n",
			"�@�A�B�C�D�E�F�G�H�I�J�K�L�M�N�O�P�Q�R�S�T�U�V�W�X�Y�Z�[�\�]�^�_�`�a�b�c�d�e�f�g�h�i�j�k�l�m�n�o�p�q�r�s�t�u�v�w�x�y�z�{�|�}�~��������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������"
					+ "\r\n",
			"�\�]�^�_�`�a�b�c�d�e�f�g�h�i�j�k�l�m�n�o�p�q�r�s�t�u�v�w�x�y�z�{�|�}�~�����������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������@�A�B�C�D�E�F�G�H�I�J�K"
					+ "\r\n" };

	public void testHello() {
		System.out.println("hello test");
		File f = TestSimpleTextDecoder.getTestFile("test_sjis_crlf_001.txt");
		System.out.println("hello test " + f.getAbsolutePath());
		System.out.println("hello test " + f.exists());
		System.out.println("hello test " + f.isFile());
	}

	public void testSJIS001() throws FileNotFoundException, IOException {
		File f = TestSimpleTextDecoder.getTestFile("test_sjis_crlf_001.txt");
		VirtualMemory vm = new VirtualMemory(f, 64);
		Charset cs = Charset.forName("Shift_Jis");
		BreakText br = new MyBreakText(12);
		SimpleTextDecoder decoder = new SimpleTextDecoder(cs, vm, br);
		for (int i = 0; i < expect.length - 1; i++) {
			CharSequence c = decoder.decodeLine();
			String result = c.toString();
			assertEquals("[" + i + "]", expect[i], result);
		}
	}

	public void testISO2022JP001() throws FileNotFoundException, IOException {
		File f = TestSimpleTextDecoder.getTestFile("iso_2022_jp_crlf_001.txt");
		VirtualMemory vm = new VirtualMemory(f, 64);
		Charset cs = Charset.forName("ISO-2022-JP");
		BreakText br = new MyBreakText(12);
		SimpleTextDecoder decoder = new SimpleTextDecoder(cs, vm, br);
		for (int i = 0; i < expect.length - 1; i++) {
			CharSequence c = decoder.decodeLine();
			String result = c.toString();
			assertEquals("[" + i + "]", expect[i], result);
		}
	}

	public void testISO2022JP002() throws FileNotFoundException, IOException {
		File f = TestSimpleTextDecoder.getTestFile("iso_2022_jp_crlf_002.txt");
		VirtualMemory vm = new VirtualMemory(f, 64);
		Charset cs = Charset.forName("ISO-2022-JP");
		BreakText br = new MyBreakText(12);
		SimpleTextDecoder decoder = new SimpleTextDecoder(cs, vm, br);
		{
			CharSequence c = decoder.decodeLine();
			String result = c.toString();
			assertEquals("abc������def���Ȃ�", result);
		}
		{
			vm.seek(3);// abc
			byte[] escape = new byte[3];
			escape[0] = (byte) vm.read();
			escape[1] = (byte) vm.read();
			escape[2] = (byte) vm.read();
			// vm.seek(3+3);//abc + ESC(ESC $ B)
			CharSequence c = decoder.decodeLine(escape);
			String result = c.toString();
			assertEquals("������def���Ȃ�", result);
		}
	}

	public void testISO2022JP003() throws FileNotFoundException, IOException {
		File f = TestSimpleTextDecoder.getTestFile("iso_2022_jp_crlf_003.txt");
		VirtualMemory vm = new VirtualMemory(f, 64);
		Charset cs = Charset.forName("ISO-2022-JP");
		MyBreakText br = new MyBreakText(12);
		br.nextBreakText(3);
		SimpleTextDecoder decoder = new SimpleTextDecoder(cs, vm, br);
		byte[] escape = new byte[3];
		escape[0] = (byte) vm.read();
		escape[1] = (byte) vm.read();
		escape[2] = (byte) vm.read();
		vm.seek(0);
		{
			CharSequence c = decoder.decodeLine();
			String result = c.toString();
			assertEquals("������", result);
		}
		{
			CharSequence c = decoder.decodeLine(escape);
			String result = c.toString();
			assertEquals("������", result);
		}
	}

	public static File getTestFile(String filename) {
		File dir = new File("test/info/kyorohiro/helloworld/io");
		File path = new File(dir, filename);
		return path;
	}

	public static class MyBreakText implements BreakText {
		private float mFontSize = 12;
		private int mNextBreakText = -1;

		public MyBreakText(float fontSize) {
			mFontSize = fontSize;
		}

		public void nextBreakText(int nextBreakText) {
			mNextBreakText = nextBreakText;
		}

		public int breakText(MyBuilder mBuffer) {
			if (mNextBreakText >= 0) {
				int t = mNextBreakText;
				//mNextBreakText = -1;
				return t;
			}
			return mBuffer.getCurrentBufferedMojiSize();
		}
		@Override
		public void setTextSize(float textSize) {}
		@Override
		public int breakText(MyBuilder mBuffer, int width) {return 0;}
		@Override
		public int getTextWidths(char[] text, int index, int count,float[] widths) {return 0;}
		@Override
		public int getTextWidths(CharSequence text, int start, int end,float[] widths) {return 0;}

		@Override
		public int breakText(CharSequence data, int index, int count, int width) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
}
