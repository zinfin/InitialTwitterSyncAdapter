package com.bignerdranch.android.initialtwittersyncadapter.controller;


import android.accounts.*;
import android.content.ContentResolver;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.initialtwittersyncadapter.R;
import com.bignerdranch.android.initialtwittersyncadapter.account.Authenticator;
import com.bignerdranch.android.initialtwittersyncadapter.contentprovider.DatabaseContract;
import com.bignerdranch.android.initialtwittersyncadapter.contentprovider.TweetCursorWrapper;
import com.bignerdranch.android.initialtwittersyncadapter.contentprovider.UserCursorWrapper;
import com.bignerdranch.android.initialtwittersyncadapter.model.PreferenceStore;
import com.bignerdranch.android.initialtwittersyncadapter.model.Tweet;
import com.bignerdranch.android.initialtwittersyncadapter.model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TweetListFragment extends Fragment {

    private static final String TAG = "TweetListFragment";
    private String mAccessToken;
    private Account mAccount;
    private RecyclerView mRecyclerView;
    private TweetAdapter mTweetAdapter;
    private boolean mSyncingPeriodically;
    private static final String SENDER_ID = "827650385095";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);
        mRecyclerView = (RecyclerView)
                view.findViewById(R.id.fragment_tweet_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTweetAdapter = new TweetAdapter(new ArrayList<Tweet>());
        mRecyclerView.setAdapter(mTweetAdapter);
        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        fetchAccessToken();
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mSyncingPeriodically){
            ContentResolver.removePeriodicSync(mAccount, DatabaseContract.AUTHORITY, Bundle.EMPTY);
        }

    }

    private void fetchAccessToken() {
        AccountManager accountManager = AccountManager.get(getActivity());
        mAccount = new Account(Authenticator.ACCOUNT_NAME, Authenticator.ACCOUNT_TYPE);
        accountManager.getAuthToken(
            mAccount,
            Authenticator.AUTH_ACCOUNT_TYPE,
            null,
            getActivity(),
            new AccountManagerCallback<Bundle>() {
                @Override public void run(AccountManagerFuture<Bundle> future) {
                    initRecyclerView();
                    initGcm();
                    getActivity().getContentResolver().registerContentObserver(
                        DatabaseContract.Tweet.CONTENT_URI, true, mContentObserver
                    );
                }
            },null);
    }

    private void initGcm() {
        PreferenceStore prefStore = PreferenceStore.get(getActivity());
        String currentToken = prefStore.getGcmToken();
        if(currentToken == null){
            new GcmRegistrationTask().execute();
        }else{
            Log.d(TAG, "Have token " + currentToken);
        }

    }

    private class GcmRegistrationTask extends AsyncTask<Void, Void, String>{

        @Override protected String doInBackground(Void... params) {
            if(getActivity() == null){
                return null;
            }
            int googleApiAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
            if (googleApiAvailable != ConnectionResult.SUCCESS){
                Log.e(TAG, "Play services not available, cannot register for Google Cloud Messaging");
                return null;
            }
            InstanceID instanceID = InstanceID.getInstance(getActivity());
            try{
                String token = instanceID.getToken(SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.d(TAG, "Got token from GCM " + token);
                return token;
            }catch(IOException io){
                Log.e(TAG, "Failed to get token from InstanceID ", io);
                return null;
            }

        }
        @Override
        protected void onPostExecute(String token){
            if (token == null){
                setUpPeriodicSync();
            }else{
                PreferenceStore.get(getActivity()).setGcmToken(token);
                Log.d(TAG, "Token = " + token);
            }

        }
    }
    private void setUpPeriodicSync(){
        mSyncingPeriodically = true;
        ContentResolver.setIsSyncable(mAccount, DatabaseContract.AUTHORITY,1);
        ContentResolver.setSyncAutomatically(mAccount, DatabaseContract.AUTHORITY, true);
        ContentResolver.addPeriodicSync(mAccount, DatabaseContract.AUTHORITY, Bundle.EMPTY, 30);
    }

    private void initRecyclerView(){
        if (!isAdded()){
            return;
        }
        List<Tweet> tweetList = getTweetList();
        mTweetAdapter.setTweetList(tweetList);
    }
    private HashMap<String, User> getUserMap(){
        Cursor userCursor = getActivity().getContentResolver().query(
            DatabaseContract.User.CONTENT_URI, null,null,null,null);
        UserCursorWrapper wrapper = new UserCursorWrapper(userCursor);

        HashMap<String, User> userMap = new HashMap<>();
        User user;
        wrapper.moveToFirst();
        while (!wrapper.isAfterLast()) {
            user = wrapper.getUser();
            userMap.put(user.getServerId(), user);
            wrapper.moveToNext();
        }
        userCursor.close();;
        return userMap;
    }
    private List<Tweet> getTweetList(){
        HashMap<String,User> userHashMap = getUserMap();
        Cursor tweetCursor = getActivity().getContentResolver().query(
            DatabaseContract.Tweet.CONTENT_URI, null, null,null,null);
        TweetCursorWrapper wrapper = new TweetCursorWrapper(tweetCursor);
        wrapper.moveToFirst();
        Tweet tweet;
        User tweetUser;
        List<Tweet> tweetList = new ArrayList<>();
        while (!wrapper.isAfterLast()) {

            tweet = wrapper.getTweet();
            tweetUser = userHashMap.get(tweet.getUserId());
            tweet.setUser(tweetUser);
            tweetList.add(tweet);
            wrapper.moveToNext();
        }
        tweetCursor.close();
        return tweetList;
    }

    private class TweetAdapter extends RecyclerView.Adapter<TweetHolder> {
        private List<Tweet> mTweetList;

        public TweetAdapter(List<Tweet> tweetList) {
            mTweetList = tweetList;
        }

        public void setTweetList(List<Tweet> tweetList) {
            mTweetList = tweetList;
            notifyDataSetChanged();
        }

        @Override
        public TweetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_tweet, parent, false);
            return new TweetHolder(view);
        }

        @Override
        public void onBindViewHolder(TweetHolder holder, int position) {
            Tweet tweet = mTweetList.get(position);
            holder.bindTweet(tweet);
        }

        @Override
        public int getItemCount() {
            return mTweetList.size();
        }
    }

    private class TweetHolder extends RecyclerView.ViewHolder {
        private ImageView mProfileImageView;
        private TextView mTweetTextView;
        private TextView mScreenNameTextView;

        public TweetHolder(View itemView) {
            super(itemView);
            mProfileImageView = (ImageView) itemView
                    .findViewById(R.id.list_item_tweet_user_profile_image);
            mTweetTextView = (TextView) itemView
                    .findViewById(R.id.list_item_tweet_tweet_text_view);
            mScreenNameTextView = (TextView) itemView
                    .findViewById(R.id.list_item_tweet_user_screen_name_text_view);
        }

        public void bindTweet(Tweet tweet) {
            mTweetTextView.setText(tweet.getText());
            if (tweet.getUser() != null) {
                mScreenNameTextView.setText(tweet.getUser().getScreenName());
                Glide.with(getActivity())
                        .load(tweet.getUser().getPhotoUrl()).into(mProfileImageView);
            }
        }
    }

    private ContentObserver mContentObserver = new ContentObserver(new android.os.Handler()){
        @Override
        public void onChange(boolean selfChange){
            super.onChange(selfChange);
            initRecyclerView();
        }
    };
}
