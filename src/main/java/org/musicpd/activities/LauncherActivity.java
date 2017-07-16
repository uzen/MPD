package org.musicpd.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import org.musicpd.activities.adapters.SliderAdapter;
import org.musicpd.utils.AppSettings;

import org.musicpd.R;

public class LauncherActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
	
	ViewPager mViewPager;
	SliderAdapter adapter;
	Button next;
	Button prev;
	int viewpagerCurrentPosition=0;	

	AppSettings settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		settings = AppSettings.getInstance(this);
		
		if (!settings.firstRun) {
			AppSettings.invalidate();
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
		
		setContentView(R.layout.activity_launcher);
		mViewPager = (ViewPager)findViewById(R.id.pager);
		adapter = new SliderAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(adapter);
		mViewPager.addOnPageChangeListener(this);
		next = (Button) findViewById(R.id.introNext);
      prev = (Button) findViewById(R.id.introPrev);
      next.setOnClickListener(this);
      prev.setOnClickListener(this);
	};
	
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.introNext:
                if (viewpagerCurrentPosition+1 < adapter.getCount())
                    mViewPager.setCurrentItem(viewpagerCurrentPosition+1, true);
                else {
                    settings.sharedPrefs.edit().putBoolean(AppSettings.Res.first_run, false).apply();
                    AppSettings.invalidate();
                    
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
            case R.id.introPrev:
                if (viewpagerCurrentPosition-1 >=0)
                    mViewPager.setCurrentItem(viewpagerCurrentPosition-1, true);
                break;
        }
    }
    
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        viewpagerCurrentPosition=position;
        
        if (position ==0) {
            prev.setVisibility(View.INVISIBLE);
        } else {
            prev.setVisibility(View.VISIBLE);
        }

        if (position == adapter.getCount()-1) {
            next.setText(R.string.done);
        } else {
            next.setText(R.string.next);
        }
    }
    
    @Override public void onPageSelected(int position) {}
    @Override public void onPageScrollStateChanged(int state) {}
}