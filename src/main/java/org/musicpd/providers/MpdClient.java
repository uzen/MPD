package org.musicpd.providers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.musicpd.IMain;
import org.musicpd.services.MpdService;

/**
 * Connects to {@link MpdService} and provides access to it.
 */
public class MpdClient implements ServiceConnection {

 private static final String TAG = "MPDClient";
 private static final String REMOTE_ERROR = "MPD process was killed";

 private IMain mIMain = null;
 private boolean mBound = false;
 private final Context mContext;
 private final MpdClientBinder mCallback;
 
 public interface Callback {
  public void onStarted();
  public void onStopped();
  public void onError(String error);
  public void onLog(int priority, String msg);
 }

 public MpdClient(Context context, Callback callback) throws IllegalArgumentException {
  if (context == null || callback == null)
   throw new IllegalArgumentException("Context or callback can't be null");
  mContext = context;
  mCallback = new MpdClientBinder(callback);
 }

 @Override
 public void onServiceConnected(ComponentName componentName, IBinder service) {
  synchronized(this) {
  	Log.d(TAG, "Connection to the service...");
   mIMain = IMain.Stub.asInterface(service);
   try {
    if (mCallback != null) {
     mIMain.registerCallback(mCallback);
    }
   } catch (RemoteException e) {
   }
  }
 }

 @Override
 public void onServiceDisconnected(ComponentName componentName) {
  synchronized (this) {
   Log.d(TAG, "Disconnection from service...");
   try {
    if (mCallback != null) {
     mIMain.unregisterCallback(mCallback);
    }
   } catch (RemoteException e) {
   }
   mIMain = null;
  }
 }

 /**
  * Returns service object (or null if not bound).
  */
 private IMain getService() {
  synchronized (this) {
    return mIMain;
  }
 }

 /**
  * Used for Fragments to use the Activity's service connection.
  */
  
 public void bindService() {
  Log.d(TAG, "Client binding...");
  mBound = mContext.bindService(new Intent(mContext, MpdService.class), this, Context.BIND_AUTO_CREATE);
 }
 	 
 public void unbindService() {
  if (mBound) {
  	Log.d(TAG, "client unbinding...");
  	mContext.unbindService(this);
  	mBound = false;
  }
}

 public boolean start() {
  synchronized(this) {
   if (getService() != null) {
    try {
     mIMain.start();
     return true;
    } catch (RemoteException e) {}
   }
   return false;
  }
 }

 public boolean stop() {
  synchronized(this) {
   if (getService() != null) {
    try {
     mIMain.stop();
     return true;
    } catch (RemoteException e) {}
   }
   return false;
  }
 }
}