package info.kyorohiro.helloworld.display.widget.editview.differ;

import java.io.File;

import android.os.Environment;
import info.kyorohiro.helloworld.display.widget.editview.differ.TestForDiffer.MyBuffer;
import info.kyorohiro.helloworld.io.VirtualFile;
import info.kyorohiro.helloworld.text.KyoroString;
import junit.framework.TestCase;

public class TestForSaveTaskForDiffer extends TestCase {

	public void testHello() {
		System.out.println("hello");
	}

	public void testRandomSenario() {
		String[] message = {
				"abcdefg",
				"ABCDEFG",
				"NONONON",
				"0123456",
		};
		{
			// init
			Differ differ = new Differ();
			MyBuffer buffer = new MyBuffer(message);
			KyoroString ret = differ.get(buffer, 0);
			assertEquals("check get", message[0], ret.toString());
			assertEquals("check get", message[1], differ.get(buffer, 1).toString());
			assertEquals("check get", message[2], differ.get(buffer, 2).toString());
			assertEquals("check get", message[3], differ.get(buffer, 3).toString());
			assertEquals("check get", 0, differ.length());
			
			// senario
			File path = new File(Environment.getExternalStorageDirectory(),"__kyoro_test__.txt");
			VirtualFile vfile = new VirtualFile(path, 501);
			differ.deleteLine(0);
			SaveTaskForDiffer task = new SaveTaskForDiffer(buffer, differ, vfile);
			task.save();
			
		}

		
	}
}