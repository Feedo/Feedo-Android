package de.feedo.android.model;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.feedo.android.net.FeedoApiHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
@Table(name = "Feeds")
public class Feed extends Model implements Comparable<Feed> {
    @Column(name = "description")
    @Expose
    public String description;

    @Column(name = "title")
    @Expose
    public String title;

    @Column(name = "fileUrl")
    @Expose
    public String file_url;

    @Column(name = "link")
    @Expose
    public String link;

    @Column(name = "faviconUrl")
    @Expose
    public String favicon_url;

    @Column(name = "hasUnread")
    @Expose
    public boolean has_unread;

    @Column(name = "serverId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    @SerializedName("id")
    @Expose
    public int serverId;

    public List<FeedItem> items() {
        return getMany(FeedItem.class, "Feed");
    }

    public void loadFeedItems(final Context ctx, final FeedItemsUpdatedListener l) {
        FeedoApiHelper.getFeedoService().listFeedItems(this.serverId, new Callback<List<FeedItem>>() {
            @Override
            public void success(List<FeedItem> feedItems, Response response) {
                Feed.this.items().addAll(feedItems);

                for(FeedItem feedItem : feedItems) {
                    feedItem.feed = Feed.this;

                    feedItem.save();
                }
                Feed.this.save();
                l.updatingFinished(Feed.this);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    @Override
    public int compareTo(Feed feed) {
        return this.title.trim().compareTo(feed.title.trim());
    }

    public interface FeedItemsUpdatedListener {
        public void updatingFinished(Feed feed);
    }

    public void put(Feed f) {
        this.description = f.description;
        this.title = f.title;
        this.file_url = f.file_url;
        this.link = f.link;
        this.favicon_url = f.favicon_url;
        this.has_unread = f.has_unread;
        this.serverId = f.serverId;
    }

}
