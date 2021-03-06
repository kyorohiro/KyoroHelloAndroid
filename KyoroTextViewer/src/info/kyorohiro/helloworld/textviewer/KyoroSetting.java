package info.kyorohiro.helloworld.textviewer;

import info.kyorohiro.helloworld.textviewer.util.Util;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class KyoroSetting {

	public static final String TAG_CURRENT_CHARSET = "current charset";
	public static final String TAG_CURRENT_FILE = "current file";
	public static final String TAG_CURRENT_FONT_SIZE = "font size";
	public static final String TAG_DEFAULT_CRLF = "default crlf";
	public static final String VALUE_CRLF = "CRLF";
	public static final String VALUE_LF = "LF";
	public static final String DEFAULT_CRLF = VALUE_CRLF;
	public static final String TAG_COLOR = "current color";
	public static final String DEFAULT_COLOR_MOONLIGHT = "moon light(月光)";
	public static final String DEFAULT_COLOR_SHOWLIGHT = "snow light(氷菓)";
	public static final String DEFAULT_COLOR_SIMPLE    = "simple";


	public static final String CURRENT_CHARSET_DEFAULT = "utf8";
	public static final String VALUE_NONE = "none";
	public static final int CURRENT_FONT_SIZE_DEFAULT = (int)Util.inchi2pixel(Util.mm2inchi(2.6));
	private static Object lock = new Object();

	public static void setCurrentColor(String value) {
		try{
			setData(TAG_COLOR, value);
		} catch (Throwable t){
		}
	}

	public static String getCurrentColor() {
		String crlf = DEFAULT_COLOR_MOONLIGHT;
		try {
			String t = getData(TAG_COLOR);
			if(t != null && !t.equals(VALUE_NONE)){
				crlf = t;
			}			
		} catch(Throwable t) {
		}
		return crlf;
	}

	public static String getCurrentCRLF() {
		String crlf = DEFAULT_CRLF;
		try {
			String t = getData(TAG_DEFAULT_CRLF);
			if(t != null && !t.equals(VALUE_NONE)){
				crlf = t;
			}			
		} catch(Throwable t) {
		}
		return crlf;
	}

	public static void setCurrentCRLF(String value) {
		try{
			setData(TAG_DEFAULT_CRLF, value);
		} catch (Throwable t){
		}
	}

	public static int getCurrentFontSize() {
		int fontSize = CURRENT_FONT_SIZE_DEFAULT;
		try{
			String t = getData(TAG_CURRENT_FONT_SIZE);
			if(t != null && !t.equals(VALUE_NONE)){
				fontSize = Integer.parseInt(t);
			}
		}catch(Throwable t){
		}
		if(fontSize<8) {
			fontSize = 8;
		}
		return fontSize;
	}

	public static void setCurrentFontSize(String value) {
		try{
			setData(TAG_CURRENT_FONT_SIZE, value);
		} catch (Throwable t){
		}
	}


	public static String getCurrentCharset() {
		String retry = CURRENT_CHARSET_DEFAULT;
		try{
			String t = getData(TAG_CURRENT_CHARSET);
			if(t != null && !t.equals(VALUE_NONE)){
				retry = t;
			}
		}catch(Throwable t){
		}
		return retry;
	}

	public static void setCurrentCharset(String value) {
		try{
			setData(TAG_CURRENT_CHARSET, value);
		} catch (Throwable t){
		}
	}

	public static String getCurrentFile() {
		String retry = VALUE_NONE;
		try{
			String t = getData(TAG_CURRENT_FILE);
			if(t != null && !t.equals(VALUE_NONE)){
				retry = t;
			}
		}catch(Throwable t){
		}
		return retry;
	}

	public static void setCurrentFile(String value) {
		try{
			setData(TAG_CURRENT_FILE, value);
		} catch (Throwable t){
		}
	}


	public static void setData(String property, String value) {
		setData(KyoroApplication.getKyoroApplication(), property, value);
	}

	public static void setData(Context context, String property, String value) {
		setData(context, property, value, null);
	}

	public static void setData(Context context, String property, String value, String tag) {
		SharedPreferences pref = null; 
		synchronized (lock){
			if(tag == null){
				pref = PreferenceManager.getDefaultSharedPreferences(context);
			}else{
				pref = context.getSharedPreferences(tag, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
			}
			pref.edit().putString(property, value).commit();
		}
	}

	public static String getData(String property) {
		return getData(KyoroApplication.getKyoroApplication(), property);
	}

	public static String getData(Context context, String property) {
		return getData(context, property, null);
	}

	public static String getData(Context context, String property, String tag) {
		SharedPreferences pref = null; 
		synchronized (lock){
			if (tag == null){
				pref = PreferenceManager.getDefaultSharedPreferences(context);
			}else{
				pref = context.getSharedPreferences(tag, Context.MODE_WORLD_READABLE);
			}
			return pref.getString(property, VALUE_NONE);
		}
	}

}
