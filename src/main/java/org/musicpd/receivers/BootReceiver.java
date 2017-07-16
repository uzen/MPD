package org.musicpd.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.musicpd.services.MpdService;
import org.musicpd.utils.AppSettings;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == "android.intent.action.BOOT_COMPLETED") {
         if (AppSettings.isBoot(context)) {
        		MpdService.start(context);
        	}
        }
    }
}