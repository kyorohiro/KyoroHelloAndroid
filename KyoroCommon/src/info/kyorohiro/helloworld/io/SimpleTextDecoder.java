package info.kyorohiro.helloworld.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class SimpleTextDecoder {

	private MarkableReader mReader = null;
	private MyBuilder mBuffer = new MyBuilder();
	private Charset mCharset = null;
	private BreakText mBreakText;

	public SimpleTextDecoder(Charset _cs, MarkableReader reader,
			BreakText breakText) {
		mCharset = _cs;
		mReader = reader;
		mBreakText = breakText;
	}

	public boolean isEOF() {
		try {
			if (mReader.getFilePointer() < mReader.length()) {
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			return false;
		}
	}

	public CharSequence decodeLine() throws IOException {
		return decodeLine(null);
	}

	public char readCharacter() throws IOException {
		CharsetDecoder decoder = getCharsetDecoder();
		byte b;
		do {
			b = (byte) mReader.read();
			if(b<0){
				break;
			}
			mByteBuffer.put(b);
			mByteBuffer.flip();
			decoder.decode(mByteBuffer, mCharBuffer, false);
			mCharBuffer.flip();
		} while (!mCharBuffer.hasRemaining());

		if(0 !=mCharBuffer.length()) {
			return mCharBuffer.get();
		} else {
			return '\0';
		}
	}

	private CharsetDecoder mDecoder = null;
	private ByteBuffer mByteBuffer = ByteBuffer.allocateDirect(32); // bb�͏������߂���
	private CharBuffer mCharBuffer = CharBuffer.allocate(16); // cb�͏������߂���

	public CharsetDecoder getCharsetDecoder() {
		if (mDecoder == null) {
			mDecoder = mCharset.newDecoder();
		}
		return mDecoder;
	}

	public CharSequence decodeLine(byte[] escape) throws IOException {
		mBuffer.clear();
		CharsetDecoder decoder = getCharsetDecoder();
		boolean end = false;

		if (escape != null) {
			mByteBuffer.put(escape);
		}

		// todo
		int mode = TODOCRLFString.MODE_INCLUDE_LF;
		long todoPrevPosition = mReader.getFilePointer();
		outside: do {
			int d = mReader.read();
			if (d >= 0) {
				mByteBuffer.put((byte) d); // bb�ւ̏�������
			} else {
				break;
			}
			mByteBuffer.flip();
			decoder.decode(mByteBuffer, mCharBuffer, end);

			mCharBuffer.flip();
			char c = ' ';
			boolean added = false;
			while (mCharBuffer.hasRemaining()) {
				added = true;
				mByteBuffer.clear();
				c = mCharBuffer.get();
				mBuffer.append(c);
				{
					// following code maby out of memor error , you must change
					// kb
					int len = Integer.MAX_VALUE;// example -->1024*4;// force
												// crlf 12kb
					if (mBreakText != null) {
						len = mBreakText.breakText(mBuffer);
					}
					if (len < mBuffer.getCurrentBufferedMojiSize()) {
						// �ЂƂO�ŉ��s
						mBuffer.removeLast();
						mReader.seek(todoPrevPosition);
						mode = TODOCRLFString.MODE_EXCLUDE_LF;
						break outside;
					} else {
						todoPrevPosition = mReader.getFilePointer();
					}
				}
			}
			if (c == '\n') {
				break;
			}

			if (!added) {
				mByteBuffer.compact();
			}
			mCharBuffer.clear(); // cb���������ݏ�ԂɕύX
		} while (!end);
		return new TODOCRLFString(mBuffer.getAllBufferedMoji(),
				mBuffer.getCurrentBufferedMojiSize(), mode);
	}
}
