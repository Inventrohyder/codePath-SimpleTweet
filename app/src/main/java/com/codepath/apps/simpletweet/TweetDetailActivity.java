package com.codepath.apps.simpletweet;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.simpletweet.models.Tweet;

import org.parceler.Parcels;

import java.util.Objects;

public class TweetDetailActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    ImageView mIvProfileImage;
    TextView mTvBody;
    TextView mTvScreenName;
    TextView mTvCreatedAt;
    TextView mTvName;
    ImageView ivMedia;
    VideoView videoMedia;
    ImageView playIcon;
    View overlayImage;
    private ProgressBar progressBar;
    private Tweet mTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mTweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        setupMedia();
    }

    private void setupMedia() {
        switch (mTweet.mediaType) {
            case Tweet.MEDIA_TYPE_VIDEO:
                setupVideoType();
                break;
            case Tweet.MEDIA_TYPE_PHOTO:
                setupPhotoType();
                break;
            case Tweet.MEDIA_TYPE_NONE:
            default:
                setupNoneType();
        }
    }

    private void setupVideoType() {
        setupPhotoType();
        videoMedia = findViewById(R.id.videoMedia);
        playIcon = findViewById(R.id.playIcon);
        progressBar = findViewById(R.id.progressBar);
        overlayImage = findViewById(R.id.overlay_image);

        playIcon.setVisibility(View.VISIBLE);
        overlayImage.setVisibility(View.VISIBLE);
        videoMedia.setVisibility(View.VISIBLE);

        videoMedia.setVideoURI(Uri.parse(mTweet.videoUrl));

        final int position = getIntent().getIntExtra("progress", 0);
        final boolean isPlaying = getIntent().getBooleanExtra("isPlaying", false);

        View.OnClickListener playListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Video");
                if (videoMedia.isPlaying()) {
                    pause();
                } else {
                    play();
                }
            }
        };

        videoMedia.setOnClickListener(playListener);
        playIcon.setOnClickListener(playListener);
        ivMedia.setOnClickListener(playListener);

        videoMedia.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                progressBar.setVisibility(View.INVISIBLE);

                if (isPlaying) {
                    videoMedia.seekTo(position);
                    play();
                }
            }
        });

        videoMedia.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                reset();
            }
        });
    }

    private void reset() {
        playIcon.setVisibility(View.VISIBLE);
        ivMedia.setVisibility(View.VISIBLE);
        overlayImage.setVisibility(View.VISIBLE);
    }

    private void pause() {
        videoMedia.pause();
        playIcon.setVisibility(View.VISIBLE);
        overlayImage.setVisibility(View.VISIBLE);
    }

    private void play() {
        videoMedia.start();
        playIcon.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        ivMedia.setVisibility(View.INVISIBLE);
        overlayImage.setVisibility(View.INVISIBLE);
    }

    private void setupPhotoType() {
        setupNoneType();
        ivMedia = findViewById(R.id.ivEmbeddedImage);
        ivMedia.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(mTweet.mediaUrl)
                .transform(new RoundedCorners(25))
                .into(ivMedia);
    }

    private void setupNoneType() {
        mIvProfileImage = findViewById(R.id.ivProfileImage);
        mTvBody = findViewById(R.id.tvBody);
        mTvScreenName = findViewById(R.id.tvScreenName);
        mTvName = findViewById(R.id.tvName);
        mTvCreatedAt = findViewById(R.id.tvCreatedAt);

        mTvBody.setText(Objects.requireNonNull(mTweet).body);
        mTvName.setText(mTweet.user.name);
        mTvScreenName.setText(getString(R.string.user_name, mTweet.user.screenName));
        mTvCreatedAt.setText(mTweet.createdAt);
        Glide.with(this)
                .load(mTweet.user.profileImageUrl)
                .transform(new RoundedCorners(25))
                .into(mIvProfileImage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}