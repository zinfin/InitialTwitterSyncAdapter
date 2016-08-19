package com.bignerdranch.android.initialtwittersyncadapter.model;

import com.bignerdranch.android.initialtwittersyncadapter.contentprovider.DatabaseContract;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sand8529 on 8/17/16.
 */
public class TweetSearchResponse {
  @SerializedName("statuses")
  private List<Tweet> mTweetList;

  public List<Tweet> getTweetList(){
    return mTweetList;
  }
}
