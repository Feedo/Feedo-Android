package de.feedo.android.net;

import java.util.List;

import de.feedo.android.model.Feed;
import de.feedo.android.model.FeedItem;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by Jan-Henrik on 15.10.13.
 */
public interface FeedoService {
    @GET("/feeds")
    void listFeeds(Callback<List<Feed>> cb);

    @POST("/feeds")
    void createFeed(@Body Feed feed, Callback<Feed> cb);

    @DELETE("/feeds/{id}")
    void deleteFeed(@Path("id") int feedId, Callback<Feed> cb);

    @PUT("/feeds/{id}")
    void updateFeed(Feed feed, Callback<Feed> cb);

    @GET("/feeds/{id}/items")
    void listFeedItems(@Path("id") int feedId, Callback<List<FeedItem>> cb);

    @PUT("/feeds/{feedId}/items/{itemId}")
    void updateFeedItem(@Path("feedId") int feedId, @Path("itemId") int feedItemId, @Body FeedItem feedItem, Callback<FeedItem> cb);

}
