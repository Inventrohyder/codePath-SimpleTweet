package com.codepath.apps.simpletweet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.simpletweet.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.Objects;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    private static final int MAX_TWEET_LENGTH = 280;
    private final String TAG = getClass().getSimpleName();
    TextInputLayout mTiCompose;
    Button mBtnTweet;
    private TwitterClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        mClient = TwitterApp.getRestClient(this);

        mTiCompose = findViewById(R.id.tiCompose);
        mBtnTweet = findViewById(R.id.btnTweet);

        mTiCompose.setHelperText(getString(R.string.tweet_char_len, 0, MAX_TWEET_LENGTH));

        Objects.requireNonNull(mTiCompose.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > MAX_TWEET_LENGTH) {
                    // ERROR
                    mTiCompose.setError(getString(R.string.tweet_char_len, editable.length(), MAX_TWEET_LENGTH));
                    mBtnTweet.setEnabled(false);
                } else {
                    // OKAY
                    mTiCompose.setHelperText(getString(R.string.tweet_char_len, editable.length(), MAX_TWEET_LENGTH));
                    mBtnTweet.setEnabled(true);
                }
            }
        });


        // Set click listener on button
        mBtnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = mTiCompose.getEditText().getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();

                // Make an API call to Twitter to publish the tweet
                mClient.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess: to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "onSuccess: Published tweet says: " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            // set the result code and bundle data for response
                            setResult(RESULT_OK, intent);
                            // close the activity and pass data to the parent activity
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure: to publish tweet", throwable);
                    }
                });
            }
        });
    }
}