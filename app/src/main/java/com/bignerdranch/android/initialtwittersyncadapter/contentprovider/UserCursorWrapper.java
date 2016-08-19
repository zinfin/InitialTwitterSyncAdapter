package com.bignerdranch.android.initialtwittersyncadapter.contentprovider;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.provider.ContactsContract;
import com.bignerdranch.android.initialtwittersyncadapter.model.User;

/**
 * Created by sand8529 on 8/17/16.
 */
public class UserCursorWrapper extends CursorWrapper {
  /**
   * Creates a cursor wrapper.
   *
   * @param cursor The underlying cursor to wrap.
   */
  public UserCursorWrapper(Cursor cursor) {
    super(cursor);
  }
  public User getUser(){
    String serverId = getString(getColumnIndex(DatabaseContract.User.SERVER_ID));
    String screenName = getString(getColumnIndex(DatabaseContract.User.SCREEN_NAME));
    String photUrl = getString(getColumnIndex(DatabaseContract.User.PHOTO_URL));

    return new User(serverId, screenName, photUrl);
  }
}
