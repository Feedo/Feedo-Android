package de.feedo.android;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import butterknife.InjectView;
import butterknife.Views;
import de.feedo.android.model.FeedItem;
import de.feedo.android.net.FeedoApiHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FeedItemActivity extends ActionBarActivity {
    public static final String EXTRAS_KEY_FEED_ITEM_ID = "feed_item_id";

    @InjectView(R.id.feed_item_detail_webview)
    WebView mWebView;

    private FeedItem mFeedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_item);

        Views.inject(this);

        mFeedItem = FeedItem.load(FeedItem.class, getIntent().getLongExtra(EXTRAS_KEY_FEED_ITEM_ID, 0));

        // Show the Up button in the action bar.
        setupActionBar();
        setupUI();


        mFeedItem.read = true;
        mFeedItem.save();

        FeedoApiHelper.getFeedoService().updateFeedItem(mFeedItem.feed.serverId, mFeedItem.serverId, mFeedItem, new Callback<FeedItem>() {
            @Override
            public void success(FeedItem feedItem, Response response) {
                FeedItemActivity.this.mFeedItem = feedItem;
                mFeedItem.save();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("feedo", "Error updating FeedItem", retrofitError);
            }
        });


    }

    private void setupUI() {
        String content = mFeedItem.content;
        if(content.isEmpty())
            content = mFeedItem.summary;
        String html = "<html>" +
                "<head>" +
                "<style>img{\n" +
                "  width: auto;\n" +
                "  max-width: 100%;\n" +
                "  height: auto;\n" +
                "  max-height: 100%;\n" +
                "}</style>" +
                "</head>" +
                "<body>"
                + content + "</body>" +
                "</html>";
        String mime = "text/html";
        String encoding = "utf-8";

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadDataWithBaseURL(mFeedItem.link, html, mime, encoding, null);


        this.setTitle(mFeedItem.title);
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
