package de.feedo.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ListView;

import butterknife.InjectView;
import butterknife.Views;
import de.feedo.android.net.FeedoRestClient;
import de.feedo.android.util.ObscuredSharedPreferences;

/**
 * Created by jhbruhn on 30.06.13.
 */
public class FeedsActivity extends Activity {
    public static final String PREFERENCES_USERDATA_NAME = "UserData";

    public static final String PREFERENCES_KEY_URL = "url";
    public static final String PREFERENCES_KEY_USERNAME = "url";
    public static final String PREFERENCES_KEY_PASSWORD = "url";

    private static final int REQUEST_URL = 0x00042beef;

    private SharedPreferences userDataPreferences;

    @InjectView(R.id.feed_list)
    ListView mDrawerListView;

    @InjectView(R.id.feed_item_list_frame)
    FrameLayout mFeedItemListFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FeedoRestClient.loadUserData(this);

        Views.inject(this);

        userDataPreferences = new ObscuredSharedPreferences(
                this, this.getSharedPreferences(PREFERENCES_USERDATA_NAME, Context.MODE_PRIVATE));

        if(userDataPreferences.getString(PREFERENCES_KEY_URL, "").isEmpty()) {
            this.startActivityForResult(new Intent(this, SetURLActivity.class), REQUEST_URL);
        }
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
        }
    }
}
