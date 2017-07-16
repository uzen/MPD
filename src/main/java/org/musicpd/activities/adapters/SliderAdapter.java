package org.musicpd.activities.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import org.musicpd.activities.fragments.LogoFragment;
import org.musicpd.activities.fragments.AboutFragment;
import org.musicpd.activities.fragments.PermissionsFragment;
import org.musicpd.activities.fragments.SettingsFragment;
import org.musicpd.activities.fragments.LicenseFragment;

public class SliderAdapter extends FragmentStatePagerAdapter {
    static final int NUM_ITEMS = 5;
	
    public SliderAdapter(FragmentManager fm) {
        super(fm);
    }
    
    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        
        switch (position){
            case 0:
                frag = new LogoFragment();
                break;
            case 1:  
                frag = new AboutFragment();
                break;
            case 2:
                frag = new PermissionsFragment();
                break;
            case 3:
                frag = new SettingsFragment();
                break;
            case 4:
                frag = new LicenseFragment();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}