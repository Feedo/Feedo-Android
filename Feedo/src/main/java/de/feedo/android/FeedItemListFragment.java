package de.feedo.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        long feedId = getArguments().getLong(ARGUMENT_KEY_FEED_ID);
        Log.i("feedo", "FeedId is " + feedId);
        mFeed = Feed.load(Feed.class, feedId);
        refreshFeedItems();
        this.getListView().setOnItemClickListener(feedItemClickListener);
    }

    public void refreshFeedItems() {
        FeedItem[] feedItemArray = new FeedItem[mFeed.items().size()];

        for(int i = 0; i < feedItemArray.length; i++)
            feedItemArray[i] = mFeed.items().get(i);
        this.setListAdapter(new FeedItemAdapter(getActivity(), feedItemArray));
    }

    private AdapterView.OnItemClickListener feedItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getActivity(), FeedItemActivity.class);
            intent.putExtra(FeedItemActivity.EXTRAS_KEY_FEED_ITEM_ID, mFeed.items().get(i).getId());
            startActivity(intent);
        }
    };
}
