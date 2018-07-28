package com.jadepay.cpuminer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PreferenceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		final String key = arg1.getStringExtra("com.jadepay.cpuminer.preferenceKey");
		final CPUMinerApplication application = ((CPUMinerApplication) arg0);

		if (key.equals(R.string.pref_battery_key) || key.equals(R.string.pref_battery_level_key))
			application.handleBatteryEvent();
		else
			application.stop();
	}

}
