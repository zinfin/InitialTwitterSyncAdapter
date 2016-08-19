package com.bignerdranch.android.initialtwittersyncadapter.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by sand8529 on 8/17/16.
 */
public class AuthenticatorService extends Service {
  private Authenticator mAuthenticator;

  public AuthenticatorService(){
    mAuthenticator = new Authenticator(this);
  }
  @Nullable @Override public IBinder onBind(Intent intent) {
    return mAuthenticator.getIBinder();
  }
}
