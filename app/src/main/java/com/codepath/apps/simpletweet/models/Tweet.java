package com.codepath.apps.simpletweet.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import android.util.Log;

import com.codepath.apps.simpletweet.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Tweet {

    public static final String TAG = "Tweet";

    public static final int MEDIA_TYPE_NONE = -1;
    public static final int MEDIA_TYPE_PHOTO = 0;
    public static final int MEDIA_TYPE_VIDEO = 1;

    @ColumnInfo
    @PrimaryKey
    public long id;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public long userId;

    @Ignore
    public User user;


    public String mediaUrl;
    public String videoUrl;
    public int mediaType = MEDIA_TYPE_NONE;

    // empty constructor needed by the Parceler library
    public Tweet() {
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.id = jsonObject.getLong("id");
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.userId = tweet.user.id;
        try {
            JSONObject mediaObject = jsonObject.getJSONObject("extended_entities")
                    .getJSONArray("media")
                    .getJSONObject(0);
            String type = mediaObject.getString("type");

            tweet.mediaUrl = mediaObject.getString("media_url_https");
            switch (type) {
                case "photo":
                    tweet.mediaType = MEDIA_TYPE_PHOTO;
                    tweet.videoUrl = null;
                    break;
                case "video":
                    tweet.mediaType = MEDIA_TYPE_VIDEO;
                    tweet.videoUrl = mediaObject.getJSONObject("video_info")
                            .getJSONArray("variants")
                            .getJSONObject(0)
                            .getString("url");
                    break;
            }
            Log.i(TAG, "fromJson: media present");
        } catch (JSONException e) {
            Log.e(TAG, "fromJson: media absent", e);
            tweet.videoUrl = null;
            tweet.mediaUrl = null;
            tweet.mediaType = MEDIA_TYPE_NONE;
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getFormattedTimestamp() {
        return TimeFormatter.getTimeDifference(createdAt);
    }

}
