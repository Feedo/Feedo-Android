package de.feedo.android.net;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

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
}
