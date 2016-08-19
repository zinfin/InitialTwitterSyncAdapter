package com.bignerdranch.android.initialtwittersyncadapter.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;
import com.bignerdranch.android.initialtwittersyncadapter.account.Authenticator;
import com.bignerdranch.android.initialtwittersyncadapter.contentprovider.DatabaseContract;
import com.bignerdranch.android.initialtwittersyncadapter.controller.AuthenticationActivity;
import com.bignerdranch.android.initialtwittersyncadapter.model.Tweet;
import com.bignerdranch.android.initialtwittersyncadapter.model.TweetSearchResponse;
import com.bignerdranch.android.initialtwittersyncadapter.model.User;
import com.bignerdranch.android.initialtwittersyncadapter.web.AuthorizationInterceptor;
import com.bignerdranch.android.initialtwittersyncadapter.web.TweetInterface;
import com.squareup.okhttp.OkHttpClient;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

import java.util.List;

/**
 * Created by sand8529 on 8/17/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
  private String mAccessTokenSecret;
  private String mAccessToken;
  private static final String  TWITTER_ENDPOINT = "https://api.twitter.com/1.1";
  private static final String QUERY = "android";

  public SyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
    AccountManager accountManager = AccountManager.get(context);
    Account account = new Account(
        Authenticator.ACCOUNT_NAME, Authenticator.ACCOUNT_TYPE);
    mAccessTokenSecret =accountManager.getUserData(account, AuthenticationActivity.OAUTH_TOKEN_SECRET_KEY);
    mAccessToken = accountManager.peekAuthToken(account, Authenticator.AUTH_ACCOUNT_TYPE);

  }

  @Override public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
      SyncResult syncResult) {
    List<Tweet> tweets = fetchTweets();
    Log.i("****SYNC8******", "Got some tweets");
    insertTweetData(tweets);

  }

  private void insertTweetData(List<Tweet> tweets) {
    User user;
    for (Tweet tweet : tweets){
      user = tweet.getUser();
      getContext().getContentResolver().insert(DatabaseContract.User.CONTENT_URI, user.getConentValues());
      getContext().getContentResolver().insert(DatabaseContract.Tweet.CONTENT_URI, tweet.getConentValues());
    }
  }

  private List<Tweet> fetchTweets() {
    OkHttpClient client = new OkHttpClient();
    client.interceptors().add(new AuthorizationInterceptor());

    RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint(TWITTER_ENDPOINT)
        .setClient(new OkClient(client))
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .build();
    TweetInterface tweetInterface = restAdapter.create(TweetInterface.class);
    TweetSearchResponse tweetResponse = tweetInterface.searchTweets(QUERY);
    return tweetResponse.getTweetList();
  }
}
