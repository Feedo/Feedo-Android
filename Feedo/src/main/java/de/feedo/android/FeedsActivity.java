package de.feedo.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;

import java.util.Collections;
import java.util.List;

import butterknife.InjectView;
import butterknife.Views;
import de.feedo.android.model.Feed;
import de.feedo.android.model.FeedAdapter;
import de.feedo.android.net.FeedoApiHelper;
import de.feedo.android.util.ObscuredSharedPreferences;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jhbruhn on 30.06.13.
 */
public class FeedsActivity extends ActionBarActivity implements uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener {
    public static final String PREFERENCES_USERDATA_NAME = "UserData";

    public static final String PREFERENCES_KEY_URL = "url";
    public static final String PREFERENCES_KEY_USERNAME = "username";
    public static final String PREFERENCES_KEY_PASSWORD = "password";

    private static final int REQUEST_URL = 0xbeef;

    private SharedPreferences userDataPreferences;

    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    @InjectView(R.id.feed_list)
    ListView mDrawerListView;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private List<Feed> mFeeds;

    private Handler mHandler;

    public uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher mPullToRefreshAttacher;

    private boolean isRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        supportRequestWindowFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_feeds);

        Views.inject(this);

        mPullToRefreshAttacher = uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.get(this);

        mPullToRefreshAttacher.addRefreshableView(mDrawerListView, this);

        mHandler = new Handler();

        mTitle = mDrawerTitle = getTitle();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);

        mDrawerListView.setBackgroundColor(getResources().getColor(android.R.color.background_light));
        mDrawerListView.setOnItemClickListener(feedItemClicklistener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FeedoApiHelper.loadUserData(this);


        userDataPreferences = new ObscuredSharedPreferences(
                this, this.getSharedPreferences(PREFERENCES_USERDATA_NAME, Context.MODE_PRIVATE));

        if(userDataPreferences.getString(PREFERENCES_KEY_URL, "").isEmpty()) {
            this.startActivityForResult(new Intent(this, SetURLActivity.class), REQUEST_URL);
        } else {
            loadFeedsFromServer();
        }

        refreshFeedList();

        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerListView);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start, menu);


        return super.onCreateOptionsMenu(menu);
    }

    private void refreshEverything() {
        FeedsActivity.this.setSupportProgressBarVisibility(true);
        FeedoApiHelper.updateFeedItems(new FeedoApiHelper.FeedUpdateListener() {
            @Override
            public void onProgress(final int progress, final int total) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        FeedsActivity.this.setSupportProgress((int) ((float) progress / total) * 1000);
                    }
                });
            }

            @Override
            public void onFinished() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        FeedsActivity.this.setSupportProgressBarVisibility(false);
                        isRefreshing = false;
                    }
                });
            }
        });

    }

    public void onRefreshStarted(View view) {
        loadFeedsFromServer();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_refresh:
                if(!isRefreshing) {
                    refreshEverything();
                    isRefreshing = true;
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshFeedList() {
        mFeeds = new Select().from(Feed.class).execute();
        Collections.sort(mFeeds);
        Log.i("feed", mFeeds.toString());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mFeeds.size() > 0) {
                    mDrawerListView.setAdapter(new FeedAdapter(FeedsActivity.this, mFeeds));
                }
                mPullToRefreshAttacher.setRefreshComplete();
            }
        });
    }

    private void loadFeedsFromServer() {
        FeedoApiHelper.getFeedoService().listFeeds(new Callback<List<Feed>>() {
            @Override
            public void success(final List<Feed> feeds, Response response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (Feed f : feeds) {
                            f.save();
                        }

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                FeedsActivity.this.refreshFeedList();
                            }
                        });
                    }
                }).start();

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                try {
                    Log.e("feed", "retrofit error!", retrofitError);
                    Log.e("feedo", "StatusCode: " + retrofitError.getResponse().getStatus() + ", Reason: " + retrofitError.getResponse().getReason());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setLoading(boolean loading) {
        setProgressBarIndeterminateVisibility(loading);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_URL) {
            Bundle e = data.getExtras();
            SharedPreferences.Editor ed = userDataPreferences.edit();
            ed.putString(PREFERENCES_KEY_URL, e.getString(SetURLActivity.EXTRA_URL));
            if(e.containsKey(SetURLActivity.EXTRA_USERNAME) && e.containsKey(SetURLActivity.EXTRA_PASSWORD)) {
                ed.putString(PREFERENCES_KEY_USERNAME, e.getString(SetURLActivity.EXTRA_USERNAME, ""));
                ed.putString(PREFERENCES_KEY_PASSWORD, e.getString(SetURLActivity.EXTRA_PASSWORD, ""));
            }
            ed.commit();

            FeedoApiHelper.loadUserData(this);

            loadFeedsFromServer();
        }
    }

    private AdapterView.OnItemClickListener feedItemClicklistener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
            android.support.v4.app.FragmentManager m = FeedsActivity.this.getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction t = m.beginTransaction();

            FeedItemListFragment f = new FeedItemListFragment();
            Bundle args = new Bundle();
            args.putLong(FeedItemListFragment.ARGUMENT_KEY_FEED_ID, mFeeds.get(i).getId());
            f.setArguments(args);
            t.replace(R.id.feed_item_list_frame, f);
            t.commit();

            mTitle = mFeeds.get(i).title;
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    };
}
