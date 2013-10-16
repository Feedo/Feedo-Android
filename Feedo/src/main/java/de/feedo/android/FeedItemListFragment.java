package de.feedo.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.Collections;
import java.util.List;

import de.feedo.android.model.Feed;
import de.feedo.android.model.FeedItem;
import de.feedo.android.model.adapters.FeedItemAdapter;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
public class FeedItemListFragment extends ListFragment implements uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener  {
    public static final String ARGUMENT_KEY_FEED_ID = "feed_id";

    private long mFeedId;
    private Feed mFeed;
    private Handler mHandler;
    private FeedItemAdapter mFeedItemAdapter;
    private List<FeedItem> mFeedItems;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((FeedsActivity) getActivity()).mPullToRefreshAttacher.addRefreshableView(this.getListView(), this);

        mFeedId = getArguments().getLong(ARGUMENT_KEY_FEED_ID);

        Log.i("feedo", "FeedId is " + mFeedId);
        mHandler = new Handler();

        onRefreshStarted(view);

        this.getListView().setOnItemClickListener(feedItemClickListener);
    }

    public void refreshFeedItems() {
        Log.i("feedo", getActivity().toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                mFeedItems = mFeed.items();
                Collections.sort(mFeedItems);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mFeedItems.size() > 0) {
                            if(FeedItemListFragment.this.mFeedItemAdapter == null) {
                                FeedItemListFragment.this.mFeedItemAdapter = new FeedItemAdapter(getActivity(), mFeedItems);
                                FeedItemListFragment.this.setListAdapter(mFeedItemAdapter);
                            } else {
                                FeedItemListFragment.this.mFeedItemAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

            }
        }).start();

    }

    @Override
    public void onResume() {
        super.onResume();
        this.getListView().invalidateViews();
    }

    public void onRefreshStarted(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFeed = Feed.load(Feed.class, mFeedId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshFeedItems();
                    }
                });
                mFeed.loadFeedItems(FeedItemListFragment.this.getActivity(), new Feed.FeedItemsUpdatedListener() {
                    @Override
                    public void updatingFinished(Feed feed) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                refreshFeedItems();
                                ((FeedsActivity) getActivity()).mPullToRefreshAttacher.setRefreshComplete();
                            }
                        });
                    }
                });
            }
        }).start();
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
