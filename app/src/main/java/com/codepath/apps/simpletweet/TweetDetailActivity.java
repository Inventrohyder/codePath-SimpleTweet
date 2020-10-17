package com.codepath.apps.simpletweet;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.simpletweet.models.Tweet;

import org.parceler.Parcels;

import java.util.Objects;

public class TweetDetailActivity extends AppCompatActivity {

    ImageView mIvProfileImage;
    TextView mTvBody;
    TextView mTvScreenName;
    TextView mTvCreatedAt;
    TextView mTvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        mIvProfileImage = findViewById(R.id.ivProfileImage);
        mTvBody = findViewById(R.id.tvBody);
        mTvScreenName = findViewById(R.id.tvScreenName);
        mTvName = findViewById(R.id.tvName);
        mTvCreatedAt = findViewById(R.id.tvCreatedAt);

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        mTvBody.setText(Objects.requireNonNull(tweet).body);
        mTvName.setText(tweet.user.name);
        mTvScreenName.setText(getString(R.string.user_name, tweet.user.screenName));
        mTvCreatedAt.setText(tweet.createdAt);
        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .transform(new RoundedCorners(25))
                .into(mIvProfileImage);
    }
}