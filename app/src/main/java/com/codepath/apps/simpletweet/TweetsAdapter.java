package com.codepath.apps.simpletweet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.simpletweet.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context mContext;
    List<Tweet> mTweets;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        mContext = context;
        mTweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = mTweets.get(position);
        // Bind the tweet with the ViewHolder
        holder.bind(tweet);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvName = itemView.findViewById(R.id.tvName);
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
            View.OnClickListener detailClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, TweetDetailActivity.class);
                    intent.putExtra("tweet", Parcels.wrap(tweet));

                    Pair<View, String> p1 = Pair.create((View) ivProfileImage, ivProfileImage.getTransitionName());
                    Pair<View, String> p2 = Pair.create((View) tvName, tvName.getTransitionName());
                    Pair<View, String> p3 = Pair.create((View) tvScreenName, tvScreenName.getTransitionName());
                    Pair<View, String> p4 = Pair.create((View) tvTimestamp, tvTimestamp.getTransitionName());
                    Pair<View, String> p5 = Pair.create((View) tvBody, tvBody.getTransitionName());

                    //noinspection unchecked
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, p1, p2, p3, p4, p5);
                    mContext.startActivity(intent, options.toBundle());
                }
            };
            container.setOnClickListener(detailClickListener);
            tvBody.setOnClickListener(detailClickListener);
        }
    }
}
