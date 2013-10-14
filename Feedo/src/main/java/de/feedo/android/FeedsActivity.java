package de.feedo.android;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.InjectView;
import butterknife.Views;
import de.feedo.android.model.Feed;
import de.feedo.android.model.FeedAdapter;
import de.feedo.android.net.FeedoApiHelper;
import de.feedo.android.net.FeedoRestClient;
import de.feedo.android.util.ObscuredSharedPreferences;

/**
 * Created by jhbruhn on 30.06.13.
 */
public class FeedsActivity extends ActionBarActivity {
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

    @InjectView(R.id.feed_item_list_frame)
    FrameLayout mFeedItemListFrame;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private List<Feed> mFeeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_feeds);

        Views.inject(this);

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

        FeedoRestClient.loadUserData(this);


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
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private void refreshFeedList() {
        mFeeds = new Select().from(Feed.class).execute();
        Feed[] feedArray = new Feed[mFeeds.size()];

        for(int i = 0; i < feedArray.length; i++)
            feedArray[i] = mFeeds.get(i);

        Log.i("feedo", "Yo! " + feedArray.length + " Feeds! (also "+mFeeds.size()+")");
        mDrawerListView.setAdapter(new FeedAdapter(this, feedArray));

    }

    private void loadFeedsFromServer() {
        FeedoApiHelper.getFeeds(new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                setLoading(true);
            }

            @Override
            public void onFinish() {
                setLoading(false);
            }

            @Override
            public void onSuccess(JSONArray response) {
                FeedoApiHelper.saveFeedsFromJsonToDB(FeedsActivity.this, response);
                refreshFeedList();
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

            FeedoRestClient.loadUserData(this);

            loadFeedsFromServer();
        }
    }

    private AdapterView.OnItemClickListener feedItemClicklistener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            FeedsActivity.this.mFeeds.get(i).loadFeedItems(FeedsActivity.this);

            android.support.v4.app.FragmentManager m = FeedsActivity.this.getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction t = m.beginTransaction();

            FeedItemListFragment f = new FeedItemListFragment();
            Bundle args = new Bundle();
            args.putLong(FeedItemListFragment.ARGUMENT_KEY_FEED_ID, mFeeds.get(i).getId());
            f.setArguments(args);
            t.replace(R.id.feed_item_list_frame, f);
            t.commit();
        }
    };
}
