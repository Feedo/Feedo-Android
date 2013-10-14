package de.feedo.android.net;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.feedo.android.model.Feed;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
public class FeedoApiHelper {
    public static void testFeedoUrlAndUserdata(String url, String username, String password, AsyncHttpResponseHandler h) {
        FeedoRestClient.setRootUrl(url);
        FeedoRestClient.setUserData(username, password);
        FeedoRestClient.get("api/info", null, h);
    }

    public static void getFeeds(JsonHttpResponseHandler h) {
        FeedoRestClient.get("api/feeds", null, h);
    }

    public static void saveFeedsFromJsonToDB(Context ctx, JSONArray response) {
        for(int i = 0; i < response.length(); i++) {
            try {
                JSONObject o = (JSONObject) response.get(i);
                long id = o.getLong("id");
                String description = o.getString("description");
                String faviconUrl = o.getString("favicon_url");
                String fileUrl = o.getString("file_url");
                String link = o.getString("link");
                String title = o.getString("title");
                boolean hasUnread = o.getBoolean("has_unread");

                if(Feed.findById(Feed.class, id) == null) {
                    new Feed(ctx, description, title, fileUrl, link, faviconUrl, hasUnread, id).save();
                } else {
                    Feed f = Feed.findById(Feed.class, id);
                    f.description = description;
                    f.title = title;
                    f.fileUrl = fileUrl;
                    f.link = link;
                    f.faviconUrl = faviconUrl;
                    f.hasUnread = hasUnread;
                    f.save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
