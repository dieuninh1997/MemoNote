package com.ttdn.memonote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by ttdn1 on 9/29/2017.
 */

public class AppSettings {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public AppSettings(Context context) {
        preferences = context.getSharedPreferences("memo_note",
                Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    //note
    public final static String NOTE_BG_COLOR = "NOTE_BG_COLOR";

    public void setNoteBgColor(int color) {
        Log.d("AppSetting", "-SetColor: " + color);
        editor.putInt(NOTE_BG_COLOR, color);
        editor.commit();
    }

    public int getNoteBgColor() {
        return preferences.getInt(NOTE_BG_COLOR, Color.YELLOW);
    }

    //sort
    public final static String OLDEST_SORT = "OLDEST_SORT";

    public void setOldestSort(boolean value) {
        editor.putBoolean(OLDEST_SORT, value);
        editor.apply();
    }

    public boolean getOldestSort() {
        return preferences.getBoolean(OLDEST_SORT, false);
    }

    //layout manager grid
    public final static String IS_GRID = "IS_GRID";

    public void setIsGrid(boolean value) {
        editor.putBoolean(IS_GRID, value);
        editor.apply();
    }

    public boolean getIsGrid() {
        return preferences.getBoolean(IS_GRID, true);
    }

    //theme dark
    public final static String DARK_THEME = "DARK_THEME";

    public void setDarkTheme(boolean value) {
        editor.putBoolean(DARK_THEME, value);
        editor.apply();
    }

    public boolean getDarkTheme() {
        return preferences.getBoolean(DARK_THEME, false);
    }

    //recorder
    public final static String pathDefault =
            Environment.getExternalStorageDirectory()
                    + File.separator + "MemoNote";

    private static final String FILE_NAME_FORMAT = "FILE_NAME_FORMAT";
    public final static String AUDIO_DIRECTORY = "AUDIO_DIRECTORY";


    public void setAudioDirectory(String path) {
        editor.putString(AUDIO_DIRECTORY, path);
        editor.commit();
    }

    public String getAudioDirectory() {
        return preferences.getString(AUDIO_DIRECTORY, pathDefault);
    }

    public void setFileNameFormat(String format) {
        editor.putString(FILE_NAME_FORMAT, format);
        editor.apply();
    }

    public String getFileNameFormat() {
        return preferences.getString(FILE_NAME_FORMAT, "yyyy_MM_dd_HH_mm_ss");
    }

}
