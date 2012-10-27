package info.kyorohiro.helloworld.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Utility {
	/**
	 * copy from http://sattontanabe.blog86.fc2.com/blog-entry-71.html
	 * �R�s�[���̃p�X[srcPath]����A�R�s�[��̃p�X[destPath]��
	 * �t�@�C���̃R�s�[���s���܂��B
	 * �R�s�[�����ɂ�FileChannel#transferTo���\�b�h�𗘗p���܂��B
	 * ���A�R�s�[�����I����A���́E�o�͂̃`���l�����N���[�Y���܂��B
	 * @param srcPath    �R�s�[���̃p�X
	 * @param destPath    �R�s�[��̃p�X
	 * @throws IOException    ���炩�̓��o�͏�����O�����������ꍇ
	 */
	public static void copyTransfer(File srcPath, File destPath) throws IOException {
	    
	    FileChannel srcChannel = new FileInputStream(srcPath).getChannel();
	    FileChannel destChannel = new FileOutputStream(destPath).getChannel();
	    try {
	        srcChannel.transferTo(0, srcChannel.size(), destChannel);
	    } finally {
	        srcChannel.close();
	        destChannel.close();
	    }

	}

}
