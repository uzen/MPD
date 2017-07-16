package org.musicpd.utils;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

public class MpdPreference {
	private final static String TAG = "MPDPref";
	
	private final static String CONF_FILE = "mpd.conf";
	private final static String STATE_FILE = "state";
	private final static String DB_FILE = "database";
	
	public final static String DEFAULT_MUSIC_DIRECTORY = Environment.
		getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
		
	public final static int SLES = 0;
		
	private static abstract class Entry {
		private final String key;
		
		public Entry(String key) {
        this.key = key;
		}
		
		public String getKey() {
        return this.key;
      }
      
      public abstract String getString();
      public abstract Entries getBlock();
	}
	
	private static class ItemEntry extends Entry {
      private final String entry;
      
      public ItemEntry(String key, String entry) {
        super(key);
        this.entry = entry;
      }
      
      @Override
      public String getString() {
        return entry;
      }
      
      @Override
      public Entries getBlock() {
        return null;
      }
	}
	
	private static class BlockEntry extends Entry {
      private final Entries entries;
        
      public BlockEntry(String key, Entries entries) {
        super(key);
        this.entries = entries;
      }
      
      @Override
      public String getString() {
        return null;
      }
      
      @Override
      public Entries getBlock() {
        return this.entries;
      }
	}
	
	private static class Entries extends ArrayList<Entry> {
      public void put(String key, String entry) {
        add(new MpdPreference.ItemEntry(key, entry));
      }
        
      public void put(String key, Entries entries) {
        add(new MpdPreference.BlockEntry(key, entries));
      }
	}
	
	private static boolean write(Context context, Entries entries) {
      boolean success = false;
      StringBuilder conf = new StringBuilder();

      Iterator<MpdPreference.Entry> it = entries.iterator();
      
      while (it.hasNext()) {
         MpdPreference.Entry entry = it.next();
         conf.append(entry.getKey());
         if (entry.getString() != null) {
            conf.append(" \"").append(entry.getString()).append("\"\n");
         } else {
            conf.append(" {\n");
            Entries blockEntries = entry.getBlock();
            Iterator<MpdPreference.Entry> blockIt = blockEntries.iterator();
            while (blockIt.hasNext()) {
               MpdPreference.Entry blockEntry = blockIt.next();
               if (blockEntry.getString() == null) {
                        return false;
               }
               conf.append(" ").append(blockEntry.getKey())
                        .append(" \"").append(blockEntry.getString()).append("\"\n");
               blockIt.remove();
            }
            conf.append("}\n");
         }
         it.remove();
      }
      
      try {           
            File file = new File(Environment.getExternalStorageDirectory(), CONF_FILE);
            
            Log.d(TAG, "Save file to " + file.getPath());
            
            FileOutputStream os = new FileOutputStream(file);
            if(os != null) {
              OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
              osw.write(conf.toString());
              osw.flush();
              osw.close();           
            }
            
            success = true;
      } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.toString());
      } catch (IOException e) {
            Log.d(TAG, "Can not read file: " + e.toString());
      }

      return success;
	}
	
	public static void createFileConfig(Context context, AppSettings settings) {
		Entries entries = new Entries();
		
		String musicDirectory = !settings.mMusicPath.isEmpty() ? settings.mMusicPath : DEFAULT_MUSIC_DIRECTORY;
		String playlistDirectory = !settings.mPlaylistPath.isEmpty() ? settings.mPlaylistPath : DEFAULT_MUSIC_DIRECTORY;		
		String slesPlugin = "sles";
		String httpdPlugin = "httpd";
		
		boolean useMixer = false;
		
		entries.put("music_directory", musicDirectory);
		entries.put("playlist_directory", playlistDirectory);
		entries.put("bind_to_address", "any");
		
		Entries audioOutputBlock = new Entries();
		audioOutputBlock.put("type", slesPlugin);
		audioOutputBlock.put("name", slesPlugin);
		if (!useMixer)
        audioOutputBlock.put("mixer_type", "none");
		entries.put("audio_output", audioOutputBlock);
		
		if (settings.useHttpdPlugin) {
			Entries httpdOutputBlock = new Entries();
			httpdOutputBlock.put("type", httpdPlugin);
			httpdOutputBlock.put("name", "My HTTP Stream");
			if(!settings.mHostname.isEmpty())
				httpdOutputBlock.put("bind_to_address", settings.mHostname);
			httpdOutputBlock.put("port", String.valueOf(settings.mPort));
			httpdOutputBlock.put("bitrate", "128");
			httpdOutputBlock.put("format", "44100:16:1");	
			entries.put("audio_output", httpdOutputBlock);
		}
		
		Entries inputBlock = new Entries();
		inputBlock.put("plugin", "curl");
		entries.put("input", inputBlock);
		
		Log.d(TAG, "Preparing for recording");		
		
		write(context, entries);
	}
}