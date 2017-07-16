package org.musicpd.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppSettings {
	
	public static class Res {
		public static final String hostname = "hostname";		
		public static final String port = "port";
		public static final String storage_permission = "storage_permission_dont_show_again";
		public static final String startup = "run_on_boot";
		public static final String first_run = "first_run";
		public static final String music = "music_path";
		public static final String playlist = "playlist_path";
		public static final String httpd = "httpd_plugin";
	}
	
	private static final String TAG = "Settings";
	
	public String mHostname;
	public String mMusicPath;
	public String mPlaylistPath;
	
	public boolean storagePermission;
	public boolean runOnBoot;
	public boolean firstRun;
	public boolean useHttpdPlugin;
	
	public int mPort;
	
	public static AppSettings AppSettings;
		
	public SharedPreferences sharedPrefs;
	
	public static AppSettings getInstance(Context context) {
     if (AppSettings == null) {
         AppSettings = new AppSettings(context);
     }
     return AppSettings;
	}
	
	public static void invalidate() {
		AppSettings = null;
	}
	
	public AppSettings(Context context) {
     sharedPrefs = getSharedPreferences(context);
     setPrefs(sharedPrefs, context);
	}
	
	public AppSettings(SharedPreferences sharedPrefs, Context context) {
     setPrefs(sharedPrefs, context);
	}
	
	public void setPrefs(SharedPreferences sharedPreferences, Context context) {
		mHostname = sharedPreferences.getString(Res.hostname, "");
		mMusicPath = sharedPreferences.getString(Res.music, "");
		mPlaylistPath = sharedPreferences.getString(Res.playlist, "");
		
		mPort = sharedPreferences.getInt(Res.port, 6601);
		
		storagePermission = sharedPreferences.getBoolean(Res.storage_permission, false);
		runOnBoot = sharedPreferences.getBoolean(Res.startup, true);
		firstRun = sharedPreferences.getBoolean(Res.first_run, true);
		useHttpdPlugin = sharedPreferences.getBoolean(Res.httpd, false);
	}
	
	public static SharedPreferences getSharedPreferences(Context context) {	
		return context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
	}

	public static void setValue(Context context, String key, boolean value) {
		final SharedPreferences SharedPrefs = getSharedPreferences(context);

		if (SharedPrefs == null)
			return;
		final Editor editor = SharedPrefs.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}
	
	public static void setValue(Context context, String key, int value) {
		final SharedPreferences SharedPrefs = getSharedPreferences(context);

		if (SharedPrefs == null)
			return;
		final Editor editor = SharedPrefs.edit();
		editor.putInt(key, value);
		editor.apply();
	}
	
	public static int getValue(Context context, String key, int defValue) {
		final SharedPreferences SharedPrefs = getSharedPreferences(context);

		return SharedPrefs != null ? SharedPrefs.getInt(key, defValue) : defValue;
	}
	
	public static boolean getValue(Context context, String key, boolean defValue) {
		final SharedPreferences SharedPrefs = getSharedPreferences(context);

		return SharedPrefs != null ? SharedPrefs.getBoolean(key, defValue) : defValue;
	}
	
	public static boolean isBoot(Context context) {
		return getValue(context, Res.startup, true);
	}
}