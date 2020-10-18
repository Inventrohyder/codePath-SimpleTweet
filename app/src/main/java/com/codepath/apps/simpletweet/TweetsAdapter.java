package com.codepath.apps.simpletweet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.simpletweet.models.Tweet;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();

    Context mContext;
    List<Tweet> mTweets;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        mContext = context;
        mTweets = tweets;
    }

    @Override
    public int getItemViewType(int position) {
        return mTweets.get(position).mediaType;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        TweetsAdapter.ViewHolder viewHolder;

        switch (viewType) {
            case Tweet.MEDIA_TYPE_PHOTO:
                View viewPhoto = layoutInflater.inflate(R.layout.item_tweet_photo, parent, false);
                viewHolder = new ViewHolderPhoto(viewPhoto);
                break;
            case Tweet.MEDIA_TYPE_VIDEO:
                View viewVideo = layoutInflater.inflate(R.layout.item_tweet_video, parent, false);
                viewHolder = new ViewHolderVideo(viewVideo);
                break;
            case Tweet.MEDIA_TYPE_NONE:
            default:
                View view = layoutInflater.inflate(R.layout.item_tweet, parent, false);
                viewHolder = new ViewHolder(view);
        }
        return viewHolder;
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = mTweets.get(position);

        // Bind the tweet data into the VH
        switch (holder.getItemViewType()) {
            case Tweet.MEDIA_TYPE_PHOTO:
                ViewHolderPhoto viewHolderPhoto = (ViewHolderPhoto) holder;
                viewHolderPhoto.bind(tweet);
                break;
            case Tweet.MEDIA_TYPE_VIDEO:
                ViewHolderVideo viewHolderVideo = (ViewHolderVideo) holder;
                viewHolderVideo.bind(tweet, position);
                break;
            case Tweet.MEDIA_TYPE_NONE:
            default:
                holder.bind(tweet);
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> tweets) {
        mTweets.addAll(tweets);
        notifyDataSetChanged();
    }

    // Define a ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {

        View container;
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvTimestamp;
        TextView tvName;
        List<Pair<View, String>> mSharedElements;
        Intent mIntent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvName = itemView.findViewById(R.id.tvName);

            Pair<View, String>[] sharedElements = new Pair[]{
                    Pair.create((View) ivProfileImage, ivProfileImage.getTransitionName()),
                    Pair.create((View) tvName, tvName.getTransitionName()),
                    Pair.create((View) tvScreenName, tvScreenName.getTransitionName()),
                    Pair.create((View) tvTimestamp, tvTimestamp.getTransitionName()),
                    Pair.create((View) tvBody, tvBody.getTransitionName())
            };

            mSharedElements = new ArrayList<>();
            mSharedElements.addAll(Arrays.asList(sharedElements));

            mIntent = new Intent(mContext, TweetDetailActivity.class);
        }


        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(mContext.getString(R.string.user_name, tweet.user.screenName));
            tvName.setText(tweet.user.name);
            tvTimestamp.setText(tweet.getFormattedTimestamp());
            Glide.with(mContext)
                    .load(tweet.user.profileImageUrl)
                    .transform(new RoundedCorners(25))
                    .into(ivProfileImage);
            View.OnClickListener detailClickListener = setOnClickListener(tweet);
            container.setOnClickListener(detailClickListener);
            tvBody.setOnClickListener(detailClickListener);
        }

        @NotNull
        public View.OnClickListener setOnClickListener(final Tweet tweet) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mIntent.putExtra("tweet", Parcels.wrap(tweet));

                    Pair<View, String>[] sharedElements = mSharedElements.toArray(new Pair[0]);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, sharedElements);
                    mContext.startActivity(mIntent, options.toBundle());
                }
            };
        }
    }

    public class ViewHolderPhoto extends ViewHolder {

        ImageView ivMedia;

        public ViewHolderPhoto(@NonNull View itemView) {
            super(itemView);
            ivMedia = itemView.findViewById(R.id.ivEmbeddedImage);
        }

        @Override
        public void bind(Tweet tweet) {
            mSharedElements.add(
                    Pair.create((View) ivMedia, ivMedia.getTransitionName())
            );
            Glide.with(mContext)
                    .load(tweet.mediaUrl)
                    .transform(new RoundedCorners(25))
                    .into(ivMedia);
            super.bind(tweet);
        }
    }

    public class ViewHolderVideo extends ViewHolder {

        private final ProgressBar progressBar;
        VideoView videoMedia;
        ImageView thumbnail;
        ImageView playIcon;
        View overlayImage;
        private int mPosition;

        public ViewHolderVideo(@NonNull View itemView) {
            super(itemView);
            videoMedia = itemView.findViewById(R.id.videoMedia);
            thumbnail = itemView.findViewById(R.id.ivEmbeddedImage);
            playIcon = itemView.findViewById(R.id.playIcon);
            progressBar = itemView.findViewById(R.id.progressBar);
            overlayImage = itemView.findViewById(R.id.overlay_image);
        }

        @NotNull
        @Override
        public View.OnClickListener setOnClickListener(final Tweet tweet) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reset();
                    Pair<View, String>[] sharedElements = mSharedElements.toArray(new Pair[0]);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, sharedElements);
                    mIntent.putExtra("tweet", Parcels.wrap(tweet));
                    mIntent.putExtra("isPlaying", videoMedia.isPlaying());
                    videoMedia.pause();
                    mIntent.putExtra("progress", videoMedia.getCurrentPosition());
                    mIntent.putExtra("position", mPosition);
                    mContext.startActivity(mIntent, options.toBundle());
                }
            };
        }

        public void bind(Tweet tweet, int position) {
            mSharedElements.add(
                    Pair.create((View) videoMedia, videoMedia.getTransitionName())
            );

            mPosition = position;

            reset();


            Glide.with(mContext)
                    .load(tweet.mediaUrl)
                    .into(thumbnail);

            videoMedia.setVideoURI(Uri.parse(tweet.videoUrl));

            View.OnClickListener playListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: Video");
                    if (videoMedia.isPlaying()) {
                        videoMedia.pause();
                        playIcon.setVisibility(View.VISIBLE);
                        overlayImage.setVisibility(View.VISIBLE);
                    } else {
                        videoMedia.start();
                        playIcon.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        thumbnail.setVisibility(View.INVISIBLE);
                        overlayImage.setVisibility(View.INVISIBLE);
                    }
                }
            };
            videoMedia.setOnClickListener(playListener);
            playIcon.setOnClickListener(playListener);
            thumbnail.setOnClickListener(playListener);

            videoMedia.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

            videoMedia.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    reset();
                }
            });

            super.bind(tweet);
        }

        private void reset() {
            playIcon.setVisibility(View.VISIBLE);
            thumbnail.setVisibility(View.VISIBLE);
            overlayImage.setVisibility(View.VISIBLE);
        }
    }
}
