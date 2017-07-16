package org.musicpd.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.InputFilter;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

import org.musicpd.utils.AppSettings;
import org.musicpd.utils.Utils;
import org.musicpd.utils.MpdPreference;

import com.nononsenseapps.filepicker.FilePickerActivity;

import org.musicpd.R;

public class SettingsFragment extends Fragment implements View.OnClickListener {
	
	 private static final String TAG = "MPDSettings";
	 
	 private static final int REQUEST_MUSIC_PATH = 0x101;
	 private static final int REQUEST_PLAYLIST_PATH = 0x102;
	 
    CheckBox runOnBoot, useHttpdPlugin;
    Button mCreateConfig, mPickMusicDirectory, mPickPlaylistDirectory, mClearDB;
    TextView mMusicView, mPlaylistView;
    
    EditText mHostnameView;
    EditText mPortView;
    
    AppSettings settings;
    
    Context context;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings_fragment,container,false);
        
        context = container.getContext();        
        
        settings = AppSettings.getInstance(context);

        runOnBoot = (CheckBox) rootView.findViewById(R.id.checkBoxRunOnBoot);
        runOnBoot.setOnClickListener(this);
        
        useHttpdPlugin = (CheckBox) rootView.findViewById(R.id.checkBoxUseHttpdPlugin);
        useHttpdPlugin.setOnClickListener(this);
        
        mMusicView = (TextView) rootView.findViewById(R.id.musicDirectory);
        mPlaylistView = (TextView) rootView.findViewById(R.id.playlistDirectory);
        
        mHostnameView = (EditText) rootView.findViewById(R.id.hostName);
        mPortView = (EditText) rootView.findViewById(R.id.port);
        
        InputFilter portFilter = new Utils.PortNumberFilter();
        mPortView.setFilters(new InputFilter[]{portFilter});
        
        mPickMusicDirectory = (Button) rootView.findViewById(R.id.pickMusicDir); 
        mPickPlaylistDirectory = (Button) rootView.findViewById(R.id.pickPlaylistDir); 
        mCreateConfig = (Button) rootView.findViewById(R.id.createConfig);
        mPickMusicDirectory.setOnClickListener(this);
        mPickPlaylistDirectory.setOnClickListener(this);
        mCreateConfig.setOnClickListener(this);
        
        if(!settings.firstRun) {
            LinearLayout addSettings = (LinearLayout) rootView.findViewById(R.id.additionalSettings);
            addSettings.setVisibility(View.VISIBLE);
            mClearDB = (Button) rootView.findViewById(R.id.clearDB);
            mClearDB.setOnClickListener(this);  
        }
        
        return rootView;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        runOnBoot.setChecked(settings.runOnBoot);
        useHttpdPlugin.setChecked(settings.useHttpdPlugin);
        mHostnameView.setText(settings.mHostname);
        mMusicView.setText(settings.mMusicPath);
        mPlaylistView.setText(settings.mPlaylistPath);
        mPortView.setText(String.valueOf(settings.mPort));
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        boolean profileChanged = false;
        
        if (runOnBoot.isChecked() != settings.runOnBoot) {
            profileChanged = true;
            settings.runOnBoot = runOnBoot.isChecked();
        }
        
        if (useHttpdPlugin.isChecked() != settings.useHttpdPlugin) {
            profileChanged = true;
            settings.useHttpdPlugin = useHttpdPlugin.isChecked();
        }      
        
        if (!mHostnameView.getText().toString().equals(settings.mHostname)) {
            profileChanged = true;
            settings.mHostname = mHostnameView.getText().toString();
        }
        
        if (!mMusicView.getText().toString().equals(settings.mMusicPath)) {
            profileChanged = true;
            settings.mMusicPath = mMusicView.getText().toString();
        }
        
        if (!mPlaylistView.getText().toString().equals(settings.mPlaylistPath)) {
            profileChanged = true;
            settings.mPlaylistPath = mPlaylistView.getText().toString();
        }
        
        if (Integer.parseInt(mPortView.getText().toString()) != settings.mPort) {
            profileChanged = true;
            settings.mPort = Integer.parseInt(mPortView.getText().toString());
        }
        
        if (profileChanged) {
            settings.sharedPrefs.edit()
            	.putString(AppSettings.Res.hostname, settings.mHostname)
            	.putString(AppSettings.Res.music, settings.mMusicPath)
            	.putString(AppSettings.Res.playlist, settings.mPlaylistPath)
            	.putInt(AppSettings.Res.port, settings.mPort)
            	.putBoolean(AppSettings.Res.startup, settings.runOnBoot)
            	.putBoolean(AppSettings.Res.httpd, settings.useHttpdPlugin)
            	.apply();
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pickMusicDir:
                Intent music = new Intent(context, FilePickerActivity.class)
                    .putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)
                    .putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true)
                    .putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                startActivityForResult(music, REQUEST_MUSIC_PATH);
                break;     
            case R.id.pickPlaylistDir:
                Intent playlist = new Intent(context, FilePickerActivity.class)
                    .putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)
                    .putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true)
                    .putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                startActivityForResult(playlist, REQUEST_PLAYLIST_PATH);
                break;     	
            case R.id.createConfig:  
                Log.d(TAG, "Creating a configuration file");
                MpdPreference.createFileConfig(context, settings);
                break;
            case R.id.clearDB:
                Log.d(TAG, "Coming soon...");
                break; 
        }    
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if ((requestCode == REQUEST_MUSIC_PATH || requestCode == REQUEST_PLAYLIST_PATH) && resultCode == Activity.RESULT_OK) {
        	   String path = data.getData().getPath();
        	   if(requestCode == REQUEST_MUSIC_PATH) {
        	   	settings.mMusicPath = path;
        	   	mMusicView.setText(settings.mMusicPath);
        	   } else {
        	   	settings.mPlaylistPath = path;
        	   	mPlaylistView.setText(settings.mPlaylistPath);
            }
        }
    }
}