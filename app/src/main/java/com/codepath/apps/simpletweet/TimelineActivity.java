package com.codepath.apps.simpletweet;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.simpletweet.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    RecyclerView mRvTweets;
    List<Tweet> mTweets;
    TweetsAdapter mTweetsAdapter;
    private TwitterClient mClient;

    SwipeRefreshLayout mSwipecontainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mClient = TwitterApp.getRestClient(this);

        mSwipecontainer = findViewById(R.id.swipeContainer);
        // Configure the refreshing colors
        mSwipecontainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipecontainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh: ");
                populateHomeTimeline();
            }
        });

        // Find the RecyclerView
        mRvTweets = findViewById(R.id.rvTweets);
        // Initialize the list of tweets and adapter
        mTweets = new ArrayList<>();
        mTweetsAdapter = new TweetsAdapter(this, mTweets);
        // Recycler View setup: layout manager and the adapter
        mRvTweets.setLayoutManager(new LinearLayoutManager(this));
        mRvTweets.setAdapter(mTweetsAdapter);
        populateHomeTimeline();
    }

    private void populateHomeTimeline() {
        mClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess: " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    mTweetsAdapter.clear();
                    mTweetsAdapter.addAll(Tweet.fromJsonArray(jsonArray));
                    mSwipecontainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "onSuccess: ", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure: ", throwable);
                mSwipecontainer.setRefreshing(false);
            }
        });
    }
}