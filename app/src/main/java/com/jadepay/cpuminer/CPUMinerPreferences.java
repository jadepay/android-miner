package com.jadepay.cpuminer;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.preference.PreferenceActivity;

public class CPUMinerPreferences extends PreferenceActivity {
    @TargetApi(Build.VERSION_CODES.KITKAT)

	@Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        ArrayList<Header> target = new ArrayList<>();
        loadHeadersFromResource(R.xml.preferences, target);
        for (Header h : target) {
            if (fragmentName.equals(h.fragment)) return true;
        }
        return false;
    }
}
