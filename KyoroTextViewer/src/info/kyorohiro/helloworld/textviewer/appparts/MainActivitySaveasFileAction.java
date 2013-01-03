package info.kyorohiro.helloworld.textviewer.appparts;

import java.io.File;
import java.lang.ref.WeakReference;

import info.kyorohiro.helloworld.pfdep.android.base.MainActivityMenuAction;
import info.kyorohiro.helloworld.pfdep.android.util.SimpleFileExplorer;
import info.kyorohiro.helloworld.pfdep.android.util.SimpleFileExplorer.SelectedFileAction;
import info.kyorohiro.helloworld.display.widget.editview.EditableLineView;
import info.kyorohiro.helloworld.display.widget.editview.EditableLineViewBuffer;
import info.kyorohiro.helloworld.display.widget.lineview.LineView;
import info.kyorohiro.helloworld.textviewer.KyoroSetting;
import info.kyorohiro.helloworld.textviewer.KyoroTextViewerActivity;
import info.kyorohiro.helloworld.ext.textviewer.manager.LineViewManager;
import info.kyorohiro.helloworld.ext.textviewer.manager.task.SaveTask;
import info.kyorohiro.helloworld.ext.textviewer.viewer.TextViewer;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;


//
// ���̃R�[�h�͋߁X�폜����B
// �� ���j���[�̎g�p�͂�߂�\��Ȃ̂ŁA�����Ηǂ������Ƃ���B
public class MainActivitySaveasFileAction implements MainActivityMenuAction {

	public static String TITLE = "saveas";
	private LineViewManager mViewer = null;

	public MainActivitySaveasFileAction(LineViewManager viewer) {
		mViewer = viewer;
	}

	public boolean onPrepareOptionsMenu(Activity activity, Menu menu) {
		menu.add(TITLE);
		return false;
	}

	public boolean onMenuItemSelected(Activity activity, int featureId,
			MenuItem item) {
		if (item.getTitle().equals(TITLE)) {
			showExplorer(activity);
			return true;
		}
		return false;
	}

	private WeakReference<Activity> mRefActivity = null;
	private void showExplorer(Activity activity) {
		mRefActivity = new WeakReference<Activity>(activity);
		File directory = new File(KyoroSetting.getCurrentFile());
		File firstCandidateDirectory = directory.getParentFile();
		File secondCandidateDirectory = Environment.getExternalStorageDirectory();
		File thirdCandidateDirectory = Environment.getRootDirectory();

		File showedDirectry = firstCandidateDirectory;
		if (showedDirectry == null || !showedDirectry.exists() || !showedDirectry.canRead()) {
			showedDirectry = secondCandidateDirectory;
		} 

		if (showedDirectry == null || !showedDirectry.exists()||!showedDirectry.canRead()) {
			showedDirectry = thirdCandidateDirectory;
		} 

		SimpleFileExplorer dialog = SimpleFileExplorer.createDialog(activity, showedDirectry,
				SimpleFileExplorer.MODE_FILE_SELECT|SimpleFileExplorer.MODE_NEW_FILE);
		((KyoroTextViewerActivity)activity).stopStage();
		dialog.show();
		dialog.setOnSelectedFileAction(new SelectedFileAction() {
			@Override
			public boolean onSelectedFile(File file, String action) {
				if (file.isDirectory()){
					return false;
				}
				if(mViewer.getFocusingTextViewer() == null){
					return false;
				}
				TextViewer tViewr = mViewer.getFocusingTextViewer();
				Thread th = new Thread(new SaveTask(tViewr,file));
				th.start();
				return true;
			}
		});
		dialog.setOnCancelListener(new OnCancelListener() {			
			@Override
			public void onCancel(DialogInterface dialog) {
				if(mRefActivity!=null){
					Activity a = mRefActivity.get();
					((KyoroTextViewerActivity)a).startStage();
				}
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(mRefActivity!=null){
					Activity a = mRefActivity.get();
					((KyoroTextViewerActivity)a).startStage();
				}
			}
		});
	}
}
