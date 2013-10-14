package de.feedo.android.model;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
public class Feed extends SugarRecord<Feed> {
    public String description;
    public String title;
    public String fileUrl;
    public String link;
    public String faviconUrl;
    public boolean hasUnread;

    public Feed(Context ctx){
        super(ctx);
    }

    public Feed(Context ctx, String description, String title, String fileUrl, String link, String faviconUrl, boolean hasUnread, long id) {
        super(ctx);
        this.setId(id);
        this.description = description;
        this.title = title;
        this.fileUrl = fileUrl;
        this.link = link;
        this.faviconUrl = faviconUrl;
        this.hasUnread = hasUnread;
    }
}
