package de.feedo.android.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
@Table(name = "FeedItem")
public class FeedItem extends Model {
    @Column(name = "title")
    public String title;
    @Column(name = "content")
    public String content;
    @Column(name = "summary")
    public String summary;
    @Column(name = "image")
    public String image;
    @Column(name = "published")
    public String published;
    @Column(name = "link")
    public String link;
    @Column(name = "author")
    public String author;
    @Column(name = "itemGuid")
    public String itemGuid;
    @Column(name = "read")
    public boolean read;
    @Column(name = "serverId")
    public int serverId;
    @Column(name = "feed")
    public Feed feed;

}
