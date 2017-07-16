package org.musicpd.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.util.Log;

import java.util.ArrayList;

import org.musicpd.services.MpdService;
import org.musicpd.providers.MpdClient;

import org.musicpd.R;

public class MainActivity extends Activity {

 private static final String TAG = "MPD";
 
 private static final int MSG_ERROR = 0;
 private static final int MSG_STOPPED = 1;
 private static final int MSG_STARTED = 2;
 private static final int MSG_LOG = 3;

 private TextView mTextStatus; 
 private ListView mLogListView;
 
 private ArrayList<String> mLogListArray = new ArrayList<String>();
 private ArrayAdapter<String> mLogListAdapter;

 private static final int MAX_LOGS = 500;

 private MpdClient mClient;

 final Handler mHandler = new Handler(new Handler.Callback() {
  @Override
  public boolean handleMessage(Message msg) {
   switch (msg.what) {
    case MSG_ERROR:
     Log.d(TAG, "onError " + (String) msg.obj);
     removeEventListener();
     addEventListener();
     mTextStatus.setText((String) msg.obj);
     break;
    case MSG_STOPPED:
     Log.d(TAG, "onStopped");
     mTextStatus.setText(R.string.mpd_quit);
     break;
    case MSG_STARTED:
     Log.d(TAG, "onStarted");
     mTextStatus.setText(R.string.mpd_run);
     break;
    case MSG_LOG:
     if (mLogListArray.size() > MAX_LOGS)
      mLogListArray.remove(0);
     String priority;
     switch (msg.arg1) {
      case Log.DEBUG:
       priority = "D";
       break;
      case Log.ERROR:
       priority = "E";
       break;
      case Log.INFO:
       priority = "I";
       break;
      case Log.VERBOSE:
       priority = "V";
       break;
      case Log.WARN:
       priority = "W";
       break;
      default:
       priority = "";
     }
     mLogListArray.add(priority + "/ " + (String) msg.obj);
     mLogListAdapter.notifyDataSetChanged();
     break;
   }
   return true;
  }
 });

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);

  mTextStatus = (TextView) findViewById(R.id.status);
  mTextStatus.setOnLongClickListener(new OnLongOpenSettings());
  
  if (savedInstanceState != null) {
    mLogListArray = savedInstanceState.getStringArrayList("logList");
    mTextStatus.setText(savedInstanceState.getCharSequence("textStatus"));
  } 
  
  mLogListAdapter = new ArrayAdapter<String>(this, R.layout.log_item, mLogListArray);

  mLogListView = (ListView) findViewById(R.id.log_list);
  mLogListView.setAdapter(mLogListAdapter);
  mLogListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
  
  onNewIntent(getIntent());
  //cold start
  MpdService.start(this);
 }
 
 private class OnLongOpenSettings implements View.OnLongClickListener {
 	private int pressCount = 0;
 	
 	@Override
 	public boolean onLongClick(View v) {
    pressCount++;
    if (pressCount >= 2) {
       Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
       startActivity(intent);
    } else {
      try {
        mHandler.sendMessage(Message.obtain(mHandler, MSG_LOG, Log.DEBUG, 0, "¯\\_(ツ)_/¯"));
      } catch (Exception e) {
        Log.d(TAG, e.getMessage());
      }
    }
    return true;
 	}
 }

 @Override
 protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putStringArrayList("logList", mLogListArray);
    outState.putCharSequence("textStatus", mTextStatus.getText());
 }

 @Override
 public void onBackPressed() {
  if (mClient != null) {
  	mClient.stop();
  }
  finish();
 }

 @Override
 public void onStart() {
  updateStorageInfo();
 	
  addEventListener();
  super.onStart();
 }

 @Override
 public void onStop() {
  removeEventListener();
  super.onStop();
 }
 
 public void addEventListener() {
  mClient = new MpdClient(this, new MpdClient.Callback() {
	  private void removeMessages() {
	   /* don't remove log messages */
	   mHandler.removeMessages(MSG_STOPPED);
	   mHandler.removeMessages(MSG_STARTED);
	   mHandler.removeMessages(MSG_ERROR);
	  }
	
	  @Override
	  public void onStopped() {
	   removeMessages();
	   mHandler.sendEmptyMessage(MSG_STOPPED);
	  }
	
	  @Override
	  public void onStarted() {
	   removeMessages();
	   mHandler.sendEmptyMessage(MSG_STARTED);
	  }
	
	  @Override
	  public void onError(String error) {
	   removeMessages();
	   mHandler.sendMessage(Message.obtain(mHandler, MSG_ERROR, error));
	  }
	
	  @Override
	  public void onLog(int priority, String msg) {
	   mHandler.sendMessage(Message.obtain(mHandler, MSG_LOG, priority, 0, msg));
	  }
  });
  mClient.bindService();
 }
 
 public void removeEventListener() {
  mClient.unbindService();
  mClient = null;
 }
 
 private void updateStorageInfo(){
  TextView tv = (TextView) findViewById(R.id.sdcardstate_value);
  final String state = Environment.getExternalStorageState();

  boolean mSdCardAvailable = Environment.MEDIA_MOUNTED.equals(state);
  tv.setText((mSdCardAvailable ? R.string.sd_available : R.string.sd_not_available) );
  if (!mSdCardAvailable) {
    tv.setTextColor(R.color.red);
  }
  Log.d(TAG, "update storage status");
 }

}