package de.feedo.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class SetURLActivity extends Activity {
    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_URL = "de.feedo.android.login.extra.URL";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private TestURLTask mAuthTask = null;

    // Values for email and password at the time of the login attempt.
    private String mURL;
    private String mUsername;
    private String mPassword;

    // UI references.
    @InjectView(R.id.feedo_url)
    EditText mURLView;

    @InjectView(R.id.feedo_username)
    EditText mUsernameView;

    @InjectView(R.id.feedo_password)
    EditText mPasswordView;

    @InjectView(R.id.login_form)
    View mLoginFormView;

    @InjectView(R.id.login_status)
    View mLoginStatusView;

    @InjectView(R.id.login_status_message)
    TextView mLoginStatusMessageView;

    @InjectView(R.id.sign_in_button)
    Button mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_url);

        Views.inject(this);

        // Set up the login form.
        mURL = getIntent().getStringExtra(EXTRA_URL);
        mURLView.setText(mURL);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    /**
     * Attempts to sign in or register the instance specified by the form.
     * If there are form errors (invalid url, missing fields, etc.), the
     * errors are presented and no actual test-attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mURLView.setError(null);
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mURL = mURLView.getText().toString();
        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid url.
        if (TextUtils.isEmpty(mURL)) {
            mURLView.setError(getString(R.string.error_field_required));
            focusView = mURLView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_loading_feeds);
            showProgress(true);
            mAuthTask = new TestURLTask();
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class TestURLTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            AndroidHttpClient client = AndroidHttpClient.newInstance("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36");

            try {
                HttpGet request = new HttpGet(mURL);
                //TODO: use Username and Password if supplied.
                client.execute(request);
            } catch (IOException e) {
                Log.e("feedo", "Login failed.", e);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //TODO: Save the URL, Username and the Password (but encrypt the Password!)
                finish();
            } else {
                mURLView.setError(getString(R.string.error_invalid_url));
                mURLView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
