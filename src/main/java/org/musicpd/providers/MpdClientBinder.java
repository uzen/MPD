package org.musicpd.providers;

import android.os.RemoteException;

import org.musicpd.IMainCallback;
import org.musicpd.providers.MpdClient.Callback;

public class MpdClientBinder extends IMainCallback.Stub {
	
	private final Callback mCallback;
	
	public MpdClientBinder(Callback callback) {
        mCallback = callback;
   }
   
	@Override
	public void onStopped() throws RemoteException {
        mCallback.onStopped();
	}

	@Override
	public void onStarted() throws RemoteException {
        mCallback.onStarted();
	}

	@Override
	public void onError(String error) throws RemoteException {
        mCallback.onError(error);
	}

	@Override
	public void onLog(int priority, String msg) throws RemoteException {
        mCallback.onLog(priority, msg);
	}
}