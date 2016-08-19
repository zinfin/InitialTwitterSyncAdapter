package com.bignerdranch.android.initialtwittersyncadapter.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by sand8529 on 8/17/16.
 */
public class SyncService extends Service {
  @Override public IBinder onBind(Intent intent) {
    return new SyncAdapter(this, true).getSyncAdapterBinder();
  }
}
