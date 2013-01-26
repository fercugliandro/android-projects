package com.fercugliandro.blacklist;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.fercugliandro.blacklist.activity.BlacklistActivity;
import com.fercugliandro.blacklist.activity.BlacklistOldActivity;

public class MainActivity extends FragmentActivity {
	
	@Override
	public void onCreate(Bundle saveIntanceState) {
		super.onCreate(saveIntanceState);
	
		super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		if (Build.VERSION.SDK_INT <= 10 ) {
			startActivity(new Intent(getBaseContext(), BlacklistOldActivity.class));
		} else {
			startActivity(new Intent(getBaseContext(), BlacklistActivity.class));
		}
	}	
}
