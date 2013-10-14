package de.feedo.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.InjectView;
import butterknife.Views;
import de.feedo.android.model.Feed;
import de.feedo.android.net.FeedoApiHelper;
import de.feedo.android.net.FeedoRestClient;
import de.feedo.android.util.ObscuredSharedPreferences;

/**
 * Created by jhbruhn on 30.06.13.
 */
public class FeedsActivity extends Activity {
    public static final String PREFERENCES_USERDATA_NAME = "UserData";

    public static final String PREFERENCES_KEY_URL = "url";
    public static final String PREFERENCES_KEY_USERNAME = "username";
    public static final String PREFERENCES_KEY_PASSWORD = "password";

    private static final int REQUEST_URL = 0x00042beef;

    private SharedPreferences userDataPreferences;

    @InjectView(R.id.feed_list)
    ListView mDrawerListView;

    @InjectView(R.id.feed_item_list_frame)
    FrameLayout mFeedItemListFrame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        FeedoRestClient.loadUserData(this);

        Views.inject(this);

        userDataPreferences = new ObscuredSharedPreferences(
                this, this.getSharedPreferences(PREFERENCES_USERDATA_NAME, Context.MODE_PRIVATE));

        if(userDataPreferences.getString(PREFERENCES_KEY_URL, "").isEmpty()) {
            this.startActivityForResult(new Intent(this, SetURLActivity.class), REQUEST_URL);
        } else {
            loadFeedsFromServer();
        }
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
                for(int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject o = (JSONObject) response.get(i);
                        long id = o.getLong("id");
                        String description = o.getString("description");
                        String faviconUrl = o.getString("favicon_url");
                        String fileUrl = o.getString("file_url");
                        String link = o.getString("link");
                        String title = o.getString("title");
                        boolean hasUnread = o.getBoolean("has_unread");

                        if(Feed.findById(Feed.class, id) == null) {
                            new Feed(FeedsActivity.this, description, title, fileUrl, link, faviconUrl, hasUnread, id).save();
                        } else {
                            Feed f = Feed.findById(Feed.class, id);
                            f.description = description;
                            f.title = title;
                            f.fileUrl = fileUrl;
                            f.link = link;
                            f.faviconUrl = faviconUrl;
                            f.hasUnread = hasUnread;
                            f.save();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

            FeedoRestClient.loadUserData(this);

            loadFeedsFromServer();
        }
    }
}
