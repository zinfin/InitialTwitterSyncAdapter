package com.bignerdranch.android.initialtwittersyncadapter.contentprovider;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.provider.ContactsContract;
import com.bignerdranch.android.initialtwittersyncadapter.model.Tweet;

/**
 * Created by sand8529 on 8/17/16.
 */
public class TweetCursorWrapper extends CursorWrapper {
  /**
   * Creates a cursor wrapper.
   *
   * @param cursor The underlying cursor to wrap.
   */
  public TweetCursorWrapper(Cursor cursor) {
    super(cursor);
  }

  public Tweet getTweet(){
    String serverId = getString(getColumnIndex(DatabaseContract.Tweet.SERVER_ID));
    String text = getString(getColumnIndex(DatabaseContract.Tweet.TEXT));
    int favCount = getInt(getColumnIndex(DatabaseContract.Tweet.FAVORITE_COUNT));
    int reCount = getInt(getColumnIndex(DatabaseContract.Tweet.RETWEET_COUNT));
    String userId = getString(getColumnIndex(DatabaseContract.Tweet.USER_ID));
    return new Tweet(serverId,text,favCount,reCount,userId);

  }
}
