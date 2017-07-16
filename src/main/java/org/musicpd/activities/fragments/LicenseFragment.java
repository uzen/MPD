package org.musicpd.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.musicpd.R;

public class LicenseFragment extends Fragment {

    private final static String LCNSE_FILE = "license.txt";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.license_fragment, container, false);
        
        TextView licenseTv = (TextView) rootView.findViewById(R.id.licenseText);
        licenseTv.setText(loadLicenseText(getActivity()));
        
        return rootView;
    }
    
    private static String loadLicenseText(Context context) {
        String out = null;
         
        try {
            InputStream is = context.getAssets().open(LCNSE_FILE);
            
            if (is != null) {
            	InputStreamReader isr = new InputStreamReader(is);
            	BufferedReader br = new BufferedReader(isr);
            	
            	StringBuilder license = new StringBuilder();
            	
            	while (true) {
            		String readLine = br.readLine();
            		if (readLine == null) break;
            		license.append(readLine).append("\n");
            	}
            	
            	br.close();
            	
            	out = license.toString();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
        } catch (Exception e) {		
            System.out.println("Can not read file: " + e.toString());
        }
        
        return out;
    }
}
