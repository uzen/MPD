package org.musicpd.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.widget.RemoteViews;

import java.lang.Thread;

import org.musicpd.activities.LauncherActivity;
import org.musicpd.Bridge;
import org.musicpd.Loader;
import org.musicpd.IMainCallback;
import org.musicpd.R;

public class MpdService extends Service implements Runnable {

 private static final int MSG_ERROR = -1;
 private static final int MSG_STOPPED = 0;
 private static final int MSG_STARTED = 1;

 private static final int MSG_STATUS = 0;
 private static final int MSG_LOG = 1;

 private static final RemoteCallbackList<IMainCallback> mCallbacks = new RemoteCallbackList<IMainCallback>();

 private final MpdServiceBinder mBinder = new MpdServiceBinder(this);

 private Thread mThread = null; 
 private int mStatus = MSG_STOPPED;
 private boolean mAbort = false;
 private String mError = null;

 private synchronized void sendMessage(int what, int arg1, int arg2, Object obj) {
  int i = mCallbacks.beginBroadcast();
  while (i > 0) {
   i--;
   final IMainCallback cb = mCallbacks.getBroadcastItem(i);
   try {
    switch (what) {
     case MSG_STATUS:
      switch (arg1) {
       case MSG_ERROR:
        cb.onError((String) obj);
        break;
       case MSG_STOPPED:
        cb.onStopped();
        break;
       case MSG_STARTED:
        cb.onStarted();
        break;
      }
      break;
     case MSG_LOG:
      cb.onLog(arg1, (String) obj);
      break;
    }
   } catch (RemoteException e) {}
  }
  mCallbacks.finishBroadcast();
 }
 
 private synchronized void setStatus(int status, String error) {
  mStatus = status;
  mError = error;
  sendMessage(MSG_STATUS, mStatus, 0, mError);
 }

 @Override
 public int onStartCommand(Intent intent, int flags, int startId) {
  start();
  return START_STICKY;
 }

 @Override
 public IBinder onBind(Intent intent) {
  return (IBinder) mBinder;
 }

 private Bridge.LogListener mLogListener = new Bridge.LogListener() {
  @Override
  public void onLog(int priority, String msg) {
   sendMessage(MSG_LOG, priority, 0, msg);
  }
 };

 @Override
 public void run() {

  if (!Loader.loaded) {
   final String error = getResources().getString(R.string.err_loaded) +
    "ABI=" + Build.CPU_ABI + "\n" +
    "PRODUCT=" + Build.PRODUCT + "\n" +
    "FINGERPRINT=" + Build.FINGERPRINT + "\n" +
    "error=" + Loader.error;
   sendMessage(MSG_STATUS, MSG_ERROR, 0, error);
   setStatus(MSG_ERROR, error);
   stopSelf();
   return;
  }
  synchronized(this) {
  	if (mAbort) return;
   setStatus(MSG_STARTED, null);
  }
  Bridge.run(this, mLogListener);
  setStatus(MSG_STOPPED, null);
 }

 private void shutdown() {
  Bridge.shutdown();
  synchronized(this) {
   setStatus(MSG_STOPPED, null);
  }
 }

 public void start() {
  if (mThread != null)
   return;
  mThread = new Thread(this);
  mThread.start();

  final Intent mainIntent = new Intent(this, LauncherActivity.class);
  mainIntent.setAction("android.intent.action.MAIN");
  mainIntent.addCategory("android.intent.category.LAUNCHER");
  final PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
   mainIntent, PendingIntent.FLAG_CANCEL_CURRENT);

  Notification notification;
  
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
   notification = buildNotificationU(
    R.string.mpd_run,
    R.string.notification_text_mpd_run,
    R.drawable.icon_white,
    contentIntent);
  else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
   notification = buildNotificationU(
    R.string.mpd_run,
    R.string.notification_text_mpd_run,
    R.mipmap.ic_launcher,
    contentIntent);
  else
   notification = buildNotificationHC(
    R.string.mpd_run,
    R.string.notification_text_mpd_run,
    R.mipmap.ic_launcher,
    contentIntent);

  startForeground(R.string.mpd_run, notification);
  startService(new Intent(this, MpdService.class));
 }

 @TargetApi(Build.VERSION_CODES.HONEYCOMB)
 private Notification buildNotificationHC(int title, int text, int icon, PendingIntent contentIntent) {
  return new Notification.Builder(this)
   .setContentTitle(getText(title))
   .setContentText(getText(text))
   .setSmallIcon(icon)
   .setContentIntent(contentIntent)
   .getNotification();
 }

 @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
 private Notification buildNotificationU(int title, int text, int icon, PendingIntent contentIntent) {
  return new Notification.Builder(this)
   .setContentTitle(getText(title))
   .setContentText(getText(text))
   .setSmallIcon(icon)
   .setContentIntent(contentIntent)
   .build();
 }
 
 public void stop() {
  if (mThread != null) {
   if (mThread.isAlive()) {
    synchronized(this) {
     if (mStatus == MSG_STARTED)
      shutdown();
     else
      mAbort = true;
    }
   }
   try {
    mThread.join(10000);
    if (mThread.isAlive()) {
     System.exit(-1);
    }
    mThread = null;
    mAbort = false;
   } catch (InterruptedException ie) {}
  }
  stopForeground(true);
  stopSelf();
 }

 public void registerCallback(IMainCallback cb) {
  if (cb != null) {
   mCallbacks.register(cb);
   sendMessage(MSG_STATUS, mStatus, 0, mError);
  }
 }

 public void unregisterCallback(IMainCallback cb) {
  if (cb != null) {
   mCallbacks.unregister(cb);
  }
 }

 public static void start(Context context) {
  context.startService(new Intent(context, MpdService.class));
 }
 
 public static void stop(Context context) {
  context.stopService(new Intent(context, MpdService.class));
 }
}