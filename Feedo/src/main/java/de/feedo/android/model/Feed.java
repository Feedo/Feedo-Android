package de.feedo.android.model;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import de.feedo.android.net.FeedoApiHelper;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
@Table(name = "Feeds")
public class Feed extends Model {
    @Column(name = "description")
    public String description;
    @Column(name = "title")
    public String title;
    @Column(name = "fileUrl")
    public String fileUrl;
    @Column(name = "link")
    public String link;
    @Column(name = "faviconUrl")
    public String faviconUrl;
    @Column(name = "hasUnread")
    public boolean hasUnread;
    @Column(name = "serverId")
    public int serverId;

    public List<FeedItem> items() {
        return getMany(FeedItem.class, "Feed");
    }

    public void loadFeedItems(final Context ctx) {
        FeedoApiHelper.getFeedItems(this, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(JSONArray response) {
                FeedoApiHelper.putFeedItemsFromJsonToFeed(ctx, Feed.this, response);
            }
        });
    }

}
