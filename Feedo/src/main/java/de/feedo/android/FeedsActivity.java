package de.feedo.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ListView;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by jhbruhn on 30.06.13.
 */
public class FeedsActivity extends Activity {

    @InjectView(R.id.feed_list)
    ListView mDrawerListView;

    @InjectView(R.id.feed_item_list_frame)
    FrameLayout mFeedItemListFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Views.inject(this);


    }
}
