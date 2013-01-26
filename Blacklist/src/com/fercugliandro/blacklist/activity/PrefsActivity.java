package com.fercugliandro.blacklist.activity;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.fercugliandro.blacklist.R;

@SuppressWarnings("deprecation")
public class PrefsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT > 10)

			addPreferencesFromResource(R.xml.prefs);
		else {
			addPreferencesFromResource(R.xml.prefs_old);
		}
	}

}
