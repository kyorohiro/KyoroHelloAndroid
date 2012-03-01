package info.kyorohiro.helloworld.stress.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

public class BigEater010Gouki extends KyoroStressService {

	public BigEater010Gouki() {
		super(110);
	}

	public static Intent startService(Context context, String message) {
		Intent startIntent = new Intent(context, BigEater010Gouki.class);
	    if(message != null){
	    	startIntent.putExtra("message", message);
	    }
	    context.startService(startIntent);
	    return startIntent;
	}

	public static int getColor() {
		return Color.WHITE;
	}

	public static String getNickName() {
		return "No10 JavaHeapEater";
	}

	@Override
	public String getProperty() {
		return KyoroStressService.ID_10;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

}