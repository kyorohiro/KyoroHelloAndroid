package info.kyorohiro.helloworld.stress;

import java.util.List;

import info.kyorohiro.helloworld.display.simple.SimpleDisplayObject;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import info.kyorohiro.helloworld.display.simple.SimpleStage;
import info.kyorohiro.helloworld.display.widget.SimpleCircleController;
import info.kyorohiro.helloworld.display.widget.SimpleCircleController.CircleControllerAction;
import info.kyorohiro.helloworld.display.widget.lineview.LineList;
import info.kyorohiro.helloworld.stress.appparts.KyoroStressPreferenceDialog;
import info.kyorohiro.helloworld.stress.appparts.PreferenceNumOfBigEaterDialog;
import info.kyorohiro.helloworld.stress.service.KyoroStressService;
import info.kyorohiro.helloworld.stress.task.DeadOrAliveTask;
import info.kyorohiro.helloworld.stress.uiparts.Button;
import info.kyorohiro.helloworld.util.CyclingList;
import info.kyorohiro.helloworld.util.KyoroMemoryInfo;
import android.app.Activity;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.WindowManager;
import android.widget.Toast;

public class KyoroStressActivity extends Activity {

	public final static String MENU_STOP = "stop all";
	public final static String MENU_START = "start all";
	public final static String MENU_SETTING = "setting";
	public final static String MENU_SETTING_EATUP_JAVA_HEAP_SIZE = "eatup java heap size";
	public final static String MENU_SETTING_BIGEATER_NUM = "num of bigeater";


	private SimpleStage mStage = null;
	private LineList mBigEaterListView = null;
	private SimpleCircleController mControllerView = new SimpleCircleController();
	private Button mStartButton = new Button("start all");
	private Button mStopButton = new Button("stop all");

	private CyclingList<Object> mBigEaterList = new CyclingList<Object>(100);
	private Thread mKilledProcessManager = null;
	private Thread mStartStopThread = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mStage = new SimpleStage(this);
		mBigEaterListView = new LineList(mBigEaterList, 50);
		mStage.getRoot().addChild(new Layout());
		mStage.getRoot().addChild(mBigEaterListView);
		mStage.getRoot().addChild(mControllerView);
		mStage.getRoot().addChild(mStartButton);
		mStage.getRoot().addChild(mStopButton);
		mStartButton.setCircleButtonListener(new MyStartButtonEvent());
		mStopButton.setCircleButtonListener(new MyStopButtonEvent());
		resetBigEater(KyoroSetting.getNumOfBigEater());
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(mStage);

		mBigEaterListView.setTemplate(new MyListTemplate());
		mBigEaterListView.setOnListItemUIEvent(new MyListItemUIEvent());

		setController();
	}

	@Override
	protected void onStart() {
		super.onStart();
		startKilledProcessManager();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mStage.start();
		startKilledProcessManager();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mStage.stop();
		stopKilledProcessManager();
	}

	public class Layout extends SimpleDisplayObject {
		@Override
		public void paint(SimpleGraphics graphics) {
			int w = mControllerView.getWidth() / 2;
			int h = mControllerView.getHeight() / 2;
			int p = graphics.getWidth();
			mBigEaterListView.setRect(graphics.getWidth(), graphics.getHeight());
			mControllerView.setPoint(3 * p / 3 - w, graphics.getHeight() - h);
			mStartButton.setPoint(w, graphics.getHeight() - h * 4);
			mStopButton.setPoint(w, graphics.getHeight() - h);
			graphics.drawBackGround(0xAAFFFF);
		}
	}

	public void resetBigEater(int numOfBigEater) {
		mBigEaterList.clear();
		int len = KyoroStressService.JavaHeapEater.length;
		if (numOfBigEater < len) {
			len = numOfBigEater;
		}
		for (int i = 0; i < len; i++) {
			Class clazz = KyoroStressService.JavaHeapEater[i];
			String id = KyoroStressService.ServiceProcessName[i];
			mBigEaterList.add(new MyListDatam(clazz, id, "BigEater No." + id,
					"initilize..", Color.parseColor("#FFAAAA")));
		}
	}

	private void startKilledProcessManager() {
		if (mKilledProcessManager == null || !mKilledProcessManager.isAlive()) {
			mKilledProcessManager = new Thread(new ProcessStatusChecker());
			mKilledProcessManager.start();
		}		
	}

	private void stopKilledProcessManager() {
		if (mKilledProcessManager != null) {
			mKilledProcessManager.interrupt();
			mKilledProcessManager = null;
		}
	}
	private void setController() {
		mControllerView.setEventListener(new MyCircleControllerEvent());

		int deviceWidth = getWindowManager().getDefaultDisplay().getWidth();
		int deviceHeight = getWindowManager().getDefaultDisplay().getHeight();
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		float xdpi = metric.xdpi;
		int radius = (int) (xdpi / 3);
		int deviceMinEdge = deviceWidth;
		if (deviceWidth < deviceHeight) {
			deviceMinEdge = deviceWidth;
		} else {
			deviceMinEdge = deviceHeight;
		}
		if (radius > deviceMinEdge / 1.5) {
			radius = (int) (deviceMinEdge / 1.5);
		}

		mControllerView.setRadius(radius);
	}

	public static class MyListDatam extends Object {
		private String mTitle = "";
		private String mMessage = "";
		private int mColor = 0;
		private boolean mSelected = false;
		private Class mClazz = null;
		private String mID = "";

		public MyListDatam(Class clazz, String id, String title,
				String message, int color) {
			mID = id;
			mTitle = title;
			mMessage = message;
			mColor = color;
			mClazz = clazz;
		}
	}

	public static class MyListTemplate extends LineList.ListDatamTemplate {
		@Override
		public void paint(SimpleGraphics graphics) {
			super.paint(graphics);
			try {
				MyListDatam datam = (MyListDatam) getListDatam();
				graphics.setColor(datam.mColor);
				graphics.setTextSize(15);
				if (datam.mSelected) {
					graphics.setStrokeWidth(6);
				} else {
					graphics.setStrokeWidth(2);
				}
				graphics.drawText("" + datam.mTitle, 10, 20);
				graphics.drawText("" + datam.mMessage, 35, 35);
				graphics.drawLine(0, 5, graphics.getWidth(), 5);
				graphics.drawLine(0, 40, graphics.getWidth(), 40);
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
	}

	public class MyListItemUIEvent implements LineList.ListItemUIEvent {
		private MyListDatam mCurrentSelected = null;
		private MyListDatam mDatamPrevDown = null;

		@Override
		public void selected(Object obj, int action, int index) {
			if (obj != null && obj instanceof MyListDatam) {
				//
				((MyListDatam) obj).mSelected = true;
				if (mCurrentSelected != null && mCurrentSelected != obj) {
					mCurrentSelected.mSelected = false;
				}
				mCurrentSelected = (MyListDatam) obj;

				//
				if (action == MotionEvent.ACTION_DOWN) {
					mDatamPrevDown = ((MyListDatam) obj);
				} else if (action == MotionEvent.ACTION_UP) {
					if (mDatamPrevDown == obj) {
						mDatamPrevDown.mMessage = "tap";
						// action
						// ------------------
						android.util.Log.v("kiyohiro", "==tapped="
								+ mDatamPrevDown.mID);
						if (!KyoroStressService.START_SERVICE
								.equals(KyoroSetting
										.getData(mDatamPrevDown.mID))) {
							startBigEater(mDatamPrevDown);
						} else {
							stopBigEater(mDatamPrevDown);
						}
					}
				} else {
					if (mDatamPrevDown != obj) {
						mDatamPrevDown = null;
					}
				}
			}
		}
	}

	private class MyStartButtonEvent implements Button.CircleButtonListener {
		@Override
		public void clicked(Button btn) {
			Thread th = new Thread() {
				public void run() {
					startAll();
				}
			};
			th.start();
		}
	}

	private class MyStopButtonEvent implements Button.CircleButtonListener {
		@Override
		public void clicked(Button btn) {
			Thread th = new Thread() {
				public void run() {
					stopAll();
				}
			};
			th.start();
		}
	}

	private class MyCircleControllerEvent implements
			SimpleCircleController.CircleControllerAction {
		public void moveCircle(int action, int degree, int rateDegree) {
			if (action == CircleControllerAction.ACTION_MOVE) {
				mBigEaterListView.setPosition(mBigEaterListView.getPosition() + rateDegree / 4);
			}
		}

		public void upButton(int action) {
			mBigEaterListView.setPosition(mBigEaterListView.getPosition() + 1);
		}

		public void downButton(int action) {
			mBigEaterListView.setPosition(mBigEaterListView.getPosition() - 1);
		}
	}

	public void updateStatus() throws InterruptedException {
		int len = mBigEaterList.getNumberOfStockedElement();
		List<RunningAppProcessInfo> list = null;
		KyoroMemoryInfo infos = new KyoroMemoryInfo();
		list = infos.getRunningAppList(KyoroApplication.getKyoroApplication());
		for (int i = 0; i < len; i++) {
			task((MyListDatam) mBigEaterList.get(i),
					KyoroStressService.JavaHeapEater[i],
					KyoroStressService.ServiceProcessName[i], list);
			Thread.sleep(10);
			Thread.yield();
		}
	}

	private void task(MyListDatam datam, Class clazz, String processName,
			List<RunningAppProcessInfo> list) {
		String c = KyoroApplication.getKyoroApplication()
				.getApplicationContext().getPackageName()
				+ ":" + processName;

		for (RunningAppProcessInfo i : list) {
			String p = i.processName;
			if (p.equals(c)) {
				// 存在する場合は
				// process kill かどうかで分岐
				if (KyoroStressService.START_SERVICE.equals(KyoroSetting
						.getBigEaterState(processName))) {
					datam.mMessage = "task is alive";
				} else {
					datam.mMessage = "kill task now..";
					// android.os.Process.killProcess(i.pid);
				}
				return;
			}
		}
		if (KyoroStressService.START_SERVICE.equals(KyoroSetting.getBigEaterState(processName))) {
			datam.mMessage = "task is killed by pf";
		} else {
			datam.mMessage = "task is end";
		}

	}

	public class ProcessStatusChecker implements Runnable {
		DeadOrAliveTask task = new DeadOrAliveTask(KyoroStressActivity.this);

		public void run() {
			try {
				while (true) {
					updateStatus();
					Thread.sleep(200);
					Thread.yield();
					task.run();
					Thread.sleep(200);
					Thread.yield();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
			}

		}
	}

	private void startAll() {
		int num = mBigEaterList.getNumberOfStockedElement();
		Object[] obj = new Object[num];
		mBigEaterList.getElements(obj, 0, num);
		try {
			for (int i = 0; i < num; i++) {
				if (obj[i] instanceof MyListDatam) {
					startBigEater((MyListDatam) obj[i]);
					Thread.sleep(100);
					Thread.yield();
				}
			}
		} catch (Exception e) {

		}
	}

	private void stopAll() {
		int num = mBigEaterList.getNumberOfStockedElement();
		Object[] obj = new Object[num];
		mBigEaterList.getElements(obj, 0, num);
		try {
			for (int i = 0; i < num; i++) {
				if (obj[i] instanceof MyListDatam) {
					stopBigEater((MyListDatam) obj[i]);
					Thread.sleep(100);
					Thread.yield();
				}
			}
		} catch (Exception e) {

		}

	}

	private void startBigEater(MyListDatam datam) {
		if (datam == null) {
			return;
		}
		KyoroSetting.setBigEaterState(datam.mID, KyoroStressService.START_SERVICE);
		KyoroStressService.startService(datam.mClazz,
				KyoroApplication.getKyoroApplication(), "start");
		datam.mMessage = "start";
	}

	private void stopBigEater(MyListDatam datam) {
		if (datam == null) {
			return;
		}
		KyoroSetting.setBigEaterState(datam.mID, KyoroStressService.STOP_SERVICE);
		KyoroStressService.stopService(datam.mClazz,
				KyoroApplication.getKyoroApplication());
		datam.mMessage = "end";
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(KyoroStressActivity.MENU_START);
		menu.add(KyoroStressActivity.MENU_STOP);
		{
			SubMenu s = menu.addSubMenu(KyoroStressActivity.MENU_SETTING);
			s.add(MENU_SETTING_BIGEATER_NUM);
			s.add(MENU_SETTING_EATUP_JAVA_HEAP_SIZE);
		}
		Toast.makeText(KyoroStressActivity.this, "now working..",
				Toast.LENGTH_LONG);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item != null
				&& KyoroStressActivity.MENU_STOP.equals(item.getTitle())) {
			stopAllTask();
			return true;
		} else if (item != null
				&& KyoroStressActivity.MENU_START.equals(item.getTitle())) {
			startAllTask();
			return true;
		} else if (item != null
				&& KyoroStressActivity.MENU_SETTING_EATUP_JAVA_HEAP_SIZE
						.equals(item.getTitle())) {
			stopAllTask();
			KyoroStressPreferenceDialog.createDialog(KyoroStressActivity.this)
					.show();
		} else if (item != null
				&& KyoroStressActivity.MENU_SETTING_BIGEATER_NUM.equals(item
						.getTitle())) {
			stopAllTask();
			PreferenceNumOfBigEaterDialog
					.createDialog(KyoroStressActivity.this).show();
		}

		return super.onMenuItemSelected(featureId, item);
	}

	public void stopThread() {
		if (mStartStopThread != null && mStartStopThread.isAlive()) {
			Thread t = mStartStopThread;
			mStartStopThread = null;
			t.interrupt();
		}
	}

	public void startAllTask() {
		stopThread();
		mStartStopThread = new Thread() {
			public void run() {
				startAll();
			}
		};
		mStartStopThread.start();
	}

	public void stopAllTask() {
		stopThread();
		mStartStopThread = new Thread() {
			public void run() {
				stopAll();
			}
		};
		mStartStopThread.start();
	}
}
