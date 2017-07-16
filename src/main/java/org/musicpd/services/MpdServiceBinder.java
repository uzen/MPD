package org.musicpd.services;

import org.musicpd.IMain;
import org.musicpd.IMainCallback;

public class MpdServiceBinder extends IMain.Stub {

    private final MpdService mService;

    public MpdServiceBinder(MpdService service) {
        mService = service;
    }

    public MpdService getService() {
        return mService;
    }
    
    @Override
    public void start() {
        mService.start();
    }
    
    @Override
    public void stop() {
        mService.stop();
    }
    
    @Override
    public void registerCallback(IMainCallback cb) {
        mService.registerCallback(cb);
    }
    
    @Override
    public void unregisterCallback(IMainCallback cb) {
        mService.unregisterCallback(cb);
    }
}
