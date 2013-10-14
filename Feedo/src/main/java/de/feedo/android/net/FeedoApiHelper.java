package de.feedo.android.net;

import android.content.Context;

import com.activeandroid.query.Select;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import de.feedo.android.model.Feed;
import de.feedo.android.model.FeedItem;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
public class FeedoApiHelper {
    public static void testFeedoUrlAndUserdata(String url, String username, String password, AsyncHttpResponseHandler h) {
        FeedoRestClient.setRootUrl(url);
        FeedoRestClient.setUserData(username, password);
        FeedoRestClient.get("api/info", null, h);
    }

    public static void updateFeedItems(AsyncHttpResponseHandler h) {
        FeedoRestClient.get("api/update_feeds", null, h);
    }

    public static void getFeeds(JsonHttpResponseHandler h) {
        FeedoRestClient.get("api/feeds", null, h);
    }

    public static void getFeedItems(Feed feed, JsonHttpResponseHandler h) {
        FeedoRestClient.get("api/feeds/" + feed.serverId + "/items", null, h);
    }

    public static void setFeedItemRead(FeedItem fi, JsonHttpResponseHandler h) {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("read", fi.read);
            StringEntity entity = new StringEntity(jsonParams.toString());
            FeedoRestClient.put("api/feeds/" + fi.feed.serverId + "/items/" + fi.serverId, entity, "appilcation/json", h);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void putFeedItemsFromJsonToFeed(Context ctx, Feed f, JSONArray response) {
        for(int i = 0; i < response.length(); i++) {
            try {
                JSONObject o = (JSONObject) response.get(i);

                int id = o.getInt("id");
                String author = o.getString("author");
                String content = o.getString("content");
                String image = o.getString("image");
                String itemGuid = o.getString("item_guid");
                String link = o.getString("link");
                String published = o.getString("published");
                boolean read = o.getBoolean("read");
                String summary = o.getString("summary");
                String title = o.getString("title");

                FeedItem fi = new Select().from(FeedItem.class).where("serverId = ?", id).executeSingle();

                if(fi == null) {
                    fi = new FeedItem();
                }

                fi.serverId = id;
                fi.author = author;
                fi.content = content;
                fi.image = image;
                fi.itemGuid = itemGuid;
                fi.link = link;
                fi.published = published;
                fi.read = read;
                fi.summary = summary;
                fi.title = title;
                fi.feed = f;

                fi.save();
                f.items().add(fi);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        f.save();
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

                Feed f = new Select().from(Feed.class).where("serverId = ?", id).executeSingle();
                if(f == null) {
                    f = new Feed();
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
