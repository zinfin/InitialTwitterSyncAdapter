package com.bignerdranch.android.initialtwittersyncadapter.web;

import com.bignerdranch.android.initialtwittersyncadapter.model.TweetSearchResponse;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by sand8529 on 8/17/16.
 */
public interface TweetInterface {
  @GET("/search/tweets.json")
  TweetSearchResponse searchTweets(@Query("q") String query);
}
