package de.feedo.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
    private Handler mHandler;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final long feedId = getArguments().getLong(ARGUMENT_KEY_FEED_ID);
        Log.i("feedo", "FeedId is " + feedId);
        mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFeed = Feed.load(Feed.class, feedId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshFeedItems();
                    }
                });
            }
        }).start();

        this.getListView().setOnItemClickListener(feedItemClickListener);
    }

    public void refreshFeedItems() {
        this.setListAdapter(new FeedItemAdapter(getActivity(), mFeed.items()));
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
