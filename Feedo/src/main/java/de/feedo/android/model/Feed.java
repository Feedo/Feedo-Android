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
    public int serverId;

    public Feed(Context ctx){
        super(ctx);
    }

}
