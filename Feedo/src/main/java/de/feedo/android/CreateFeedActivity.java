package de.feedo.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;
import de.feedo.android.model.Feed;
import de.feedo.android.net.FeedoApiHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateFeedActivity extends ActionBarActivity {

    @InjectView(R.id.add_feed_feed_url)
    EditText mUrlEditText;

    @InjectView(R.id.add_feed_add_button)
    Button mAddFeedButton;

    @InjectView(R.id.add_feed_status)
    View mAddFeedStatusView;

    @InjectView(R.id.add_feed_form)
    View mAddFeedFromView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_feed);

        Views.inject(this);

        // Show the Up button in the action bar.
        setupActionBar();
    }

    @OnClick(R.id.add_feed_add_button) public void submit() {
        mUrlEditText.setError(null);
        String url = mUrlEditText.getText().toString();
        showProgress(true);
        if(!url.startsWith("http"))
            url = "http://" + url;

        Feed feed = new Feed();
        feed.file_url = url;

        FeedoApiHelper.getFeedoService().createFeed(feed, new Callback<Feed>() {
            @Override
            public void success(Feed feed, Response response) {
                feed.save();
                NavUtils.navigateUpFromSameTask(CreateFeedActivity.this);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showProgress(false);
                Response r = retrofitError.getResponse();
                if(r != null) {
                    switch(r.getStatus()) {
                        case (400): // No URL or invalid
                            mUrlEditText.setError("This URL is not a valid RSS/ATOM Feed!");
                            break;
                        case (409): // Conflict
                            mUrlEditText.setError("You already added this Feed!");

                            break;
                    }
                }
            }
        });

    }


    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_feed, menu);
        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAddFeedStatusView.setVisibility(View.VISIBLE);
            mAddFeedStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAddFeedStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mAddFeedFromView.setVisibility(View.VISIBLE);
            mAddFeedFromView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAddFeedFromView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mAddFeedStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAddFeedFromView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
