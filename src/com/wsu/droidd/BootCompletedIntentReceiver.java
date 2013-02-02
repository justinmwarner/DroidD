package com.wsu.droidd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * BootCompletedIntentReceiver is used to start up service whenever Android boots up.
 * 
 */
public class BootCompletedIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			Intent pushIntent = new Intent(context, NetworkObserverService.class);
			context.startService(pushIntent);
		}
	}

}
