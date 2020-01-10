package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Preferences {

    Context appContext;

    public void setBoolean(String key, boolean value) {
        SharedPreferences pref = appContext.getSharedPreferences("prefer", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        SharedPreferences pref = appContext.getSharedPreferences("prefer", MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    public void setInt(String key, int value) {
        SharedPreferences pref = appContext.getSharedPreferences("prefer", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value).apply();
    }

    public int getInt(String key) {
        SharedPreferences pref = appContext.getSharedPreferences("prefer", MODE_PRIVATE);
        return pref.getInt(key, 0);
    }



    public void setString(String key, String value) {
        SharedPreferences pref = appContext.getSharedPreferences("prefer", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value).apply();

    }

    public String getString(String key) {
        SharedPreferences pref = appContext.getSharedPreferences("prefer", MODE_PRIVATE);
        return pref.getString(key, "");
    }



    public Preferences(Context appContext) {
        this.appContext = appContext;
    }


}
