package com.yasee.yaseejava.lifeforever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class YaseeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, YaseeService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}