package org.musicpd.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import org.musicpd.activities.fragments.SettingsFragment;

import org.musicpd.R;

public class SettingsActivity extends FragmentActivity {
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_settings);
  
  if (findViewById(R.id.settings_container) != null) {
  	if (savedInstanceState != null) {
  		return;
  	}
  	SettingsFragment settingsFragment = new SettingsFragment();
  	settingsFragment.setArguments(getIntent().getExtras());
   getSupportFragmentManager().beginTransaction()
                    .add(R.id.settings_container, settingsFragment).commit();
  }
 }
}