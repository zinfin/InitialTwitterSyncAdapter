package com.bignerdranch.android.initialtwittersyncadapter.model;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by sand8529 on 8/17/16.
 */
public class PreferenceStore {
  private static final String GCM_TOKEN_KEY = "com.bignerdranch.android.twittersyncadapter.GCM_TOKEN";
  private static PreferenceStore sPreferenceStore;
  private Context mConext;

  public static PreferenceStore get(Context context){
    if (sPreferenceStore == null){
      sPreferenceStore = new PreferenceStore(context);
    }
    return sPreferenceStore;
  }

  private PreferenceStore(Context context){
    mConext = context.getApplicationContext();
  }

  public String getGcmToken(){
    return PreferenceManager.getDefaultSharedPreferences(mConext).getString(GCM_TOKEN_KEY,null);
  }
  public void setGcmToken(String token){
    PreferenceManager.getDefaultSharedPreferences(mConext).edit().putString(GCM_TOKEN_KEY,token).apply();
  }
}
