package de.feedo.android.net;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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
                int id = o.getInt("id");
                String description = o.getString("description");
                String faviconUrl = o.getString("favicon_url");
                if(faviconUrl.startsWith("img"))
                    faviconUrl = FeedoRestClient.getAbsoluteUrl(faviconUrl);
                String fileUrl = o.getString("file_url");
                String link = o.getString("link");
                String title = o.getString("title");
                boolean hasUnread = o.getBoolean("has_unread");

                List<Feed> feeds =  Feed.find(Feed.class, "server_id = ?", Integer.toString(id));
                Feed f = null;
                if(feeds.size() == 1)
                    f = feeds.get(0);
                if(f == null) {
                    f = new Feed(ctx);
                }
                f.serverId = id;
                f.description = description;
                f.title = title;
                f.fileUrl = fileUrl;
                f.link = link;
                f.faviconUrl = faviconUrl;
                f.hasUnread = hasUnread;
                f.save();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
