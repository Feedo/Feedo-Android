package de.feedo.android;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.feedo.android.model.Feed;
import de.feedo.android.model.FeedItem;
import de.feedo.android.model.FeedItemAdapter;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
public class FeedItemListFragment extends ListFragment {
    public static final String ARGUMENT_KEY_FEED_ID = "feed_id";

    private Feed mFeed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        long feedId = getArguments().getLong(ARGUMENT_KEY_FEED_ID);
        Log.i("feedo", "FeedId is " + feedId);
        mFeed = Feed.load(Feed.class, feedId);
        refreshFeedItems();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void refreshFeedItems() {
        FeedItem[] feedItemArray = new FeedItem[mFeed.items().size()];

        for(int i = 0; i < feedItemArray.length; i++)
            feedItemArray[i] = mFeed.items().get(i);
        this.setListAdapter(new FeedItemAdapter(getActivity(), feedItemArray));
    }
}
