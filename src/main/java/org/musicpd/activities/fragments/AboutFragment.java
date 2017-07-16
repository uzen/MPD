package org.musicpd.activities.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.musicpd.R;

public class AboutFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_fragment,container,false);
        rootView.findViewById(R.id.buttonsite).setOnClickListener(this);
        rootView.findViewById(R.id.buttonsite2).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonsite:
                Intent openGit = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MusicPlayerDaemon/MPD"));
                startActivity(openGit);
                break;
            case R.id.buttonsite2:
                Intent openLicense = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.musicpd.org"));
                startActivity(openLicense);
                break;
        }    
    }
}
