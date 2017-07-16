package org.musicpd.activities.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.musicpd.R;

public class PermissionsFragment extends Fragment implements View.OnClickListener {
	
    private static final String[] NECESSARY_PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 0x100;
    
    Button askPermissions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.permissions_fragment, container, false);
        askPermissions = rootView.findViewById(R.id.askPermissions);
        
        updatePermissionsUI();
        
        return rootView;
    }
    
    private void updatePermissionsUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && needsPermissions()) {
            askPermissions.setOnClickListener(this);
            askPermissions.setAlpha(1.0f);
        } else {
            askPermissions.setClickable(false);
            askPermissions.setAlpha(0.5f);
        }
    }
	
    private boolean needsPermissions() {
        boolean success = false;
        for (int i = 0; i < NECESSARY_PERMISSIONS.length; i++) {
            int permission = ContextCompat.checkSelfPermission(getContext(), NECESSARY_PERMISSIONS[i]);
            if(permission != PackageManager.PERMISSION_GRANTED)
                success = true;
        }
        return success;
    }
    
    @Override
    public void onClick(View v) {
    	  if(needsPermissions())
        	  requestPermissions(NECESSARY_PERMISSIONS, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
    }
    
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
         case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
             if (grantResults.length == 0 ||
                        grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                 Toast.makeText(getActivity(), R.string.toast_write_storage_permission_required,
                        Toast.LENGTH_LONG).show();
             }
             break;
         default:
             super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updatePermissionsUI();
    }

}
