package com.bignerdranch.android.initialtwittersyncadapter.model;

import android.content.ContentValues;
import com.bignerdranch.android.initialtwittersyncadapter.contentprovider.DatabaseContract;
import com.google.gson.annotations.SerializedName;

public class Tweet {

    @SerializedName("id_str")
    private String mServerId;
    private int mId;
    @SerializedName("text")
    private String mText;
    @SerializedName("favorite_count")
    private int mFavoriteCount;
    @SerializedName("retweet_count")
    private int mRetweetCount;
    @SerializedName("user")
    private User mUser;
    private String mUserId;

    public Tweet(String serverId, String text, int favoriteCount,
                 int retweetCount, User user) {
        mServerId = serverId;
        mText = text;
        mFavoriteCount = favoriteCount;
        mRetweetCount = retweetCount;
        mUser = user;
    }

    public Tweet(String serverId, String text, int favoriteCount,
                 int retweetCount, String userId) {
        mServerId = serverId;
        mText = text;
        mFavoriteCount = favoriteCount;
        mRetweetCount = retweetCount;
        mUserId = userId;
    }

    public String getText() {
        return mText;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public String getUserId() {
        return mUserId;
    }

    public ContentValues getConentValues(){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.Tweet.SERVER_ID,mServerId);
        cv.put(DatabaseContract.Tweet.TEXT , mText);
        cv.put(DatabaseContract.Tweet.FAVORITE_COUNT, mFavoriteCount);
        cv.put(DatabaseContract.Tweet.RETWEET_COUNT, mRetweetCount);
        cv.put(DatabaseContract.Tweet.USER_ID, mUserId);
        return cv;
    }
}
