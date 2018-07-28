package com.jadepay.cpuminer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;


@TargetApi(Build.VERSION_CODES.KITKAT)
public class DevicePreferences extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	private EditTextPreference textInfo;
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	final Context context = this.getActivity().getApplicationContext();
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.device_preferences);
        loadPreferences(prefs);
		textInfo = (EditTextPreference)findPreference(getString(R.string.pref_cpu_key));
		textInfo.setSummary(getInfo());

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		final Context context = this.getActivity();
		if (context != null) {
			final Intent i = new Intent("com.jadepay.cpuminer.preferenceChanged");
			i.putExtra("com.jadepay.cpuminer.preferenceKey", key);
			LocalBroadcastManager.getInstance(context).sendBroadcast(i);
		}

		loadPreferences(sharedPreferences);
	}

	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onPause() {
	    getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	    super.onPause();
	}

	public void loadPreferences(SharedPreferences prefs) {
		// Battery level option should only be enabled if we can run on battery
		CheckBoxPreference battery = (CheckBoxPreference)findPreference(getString(R.string.pref_battery_key));
		SliderPreference batteryLevel = (SliderPreference)findPreference(getString(R.string.pref_battery_level_key));
		String currentBatteryLevel = String.format(Locale.getDefault(), "%d%%", (int)(batteryLevel.getValue() * 100));
		batteryLevel.setEnabled(battery.isChecked());
		batteryLevel.setSummary(currentBatteryLevel);
	}

	private String getInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append("abi: ").append(Build.CPU_ABI).append("\n");
		if (new File("/proc/cpuinfo").exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
				String aLine;
				while ((aLine = br.readLine()) != null) {
					sb.append(aLine + "\n");
				}
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
