package de.feedo.android.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;

import de.feedo.android.util.DateParser;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
@Table(name = "FeedItem")
public class FeedItem extends Model implements Comparable<FeedItem> {
    @Column(name = "title")
    @Expose
    public String title;

    @Column(name = "content")
    @Expose
    public String content;

    @Column(name = "summary")
    @Expose
    public String summary;

    @Column(name = "image")
    @Expose
    public String image;

    @Column(name = "published")
    @Expose
    public String published;

    @Column(name = "link")
    @Expose
    public String link;

    @Column(name = "author")
    @Expose
    public String author;

    @Column(name = "itemGuid")
    @Expose
    public String itemGuid;

    @Column(name = "read")
    @Expose
    public boolean read;

    @Column(name = "serverId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    @Expose
    @SerializedName("id")
    public int serverId;

    @Column(name = "feed")
    public Feed feed;

    @Override
    public int compareTo(FeedItem feedItem) {
        try {
            return DateParser.parse(feedItem.published).compareTo(DateParser.parse(this.published));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
