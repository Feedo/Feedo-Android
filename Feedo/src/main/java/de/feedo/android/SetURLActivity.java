package de.feedo.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import butterknife.InjectView;
import butterknife.Views;
import de.feedo.android.net.FeedoApiHelper;

/**
 * Activity which displays a login screen to the user.
 */
public class SetURLActivity extends Activity {
    /**
     * The default url to populate the email field with.
     */
    public static final String EXTRA_URL = "de.feedo.android.login.extra.URL";
    public static final String EXTRA_USERNAME = "de.feedo.android.login.extra.USERNAME";
    public static final String EXTRA_PASSWORD = "de.feedo.android.login.extra.PASSWORD";

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

        mUsername = getIntent().getStringExtra(EXTRA_USERNAME);
        mUsernameView.setText(mUsername);

        mPassword = getIntent().getStringExtra(EXTRA_PASSWORD);
        mPasswordView.setText(mPassword);

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
        getMenuInflater().inflate(R.menu.feed_list, menu);
        return true;
    }

    /**
     * Attempts to sign in or register the instance specified by the form.
     * If there are form errors (invalid url, missing fields, etc.), the
     * errors are presented and no actual test-attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mURLView.setError(null);
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mURL = mURLView.getText().toString();
        if(mURL != null && !mURL.startsWith("http")) {
            mURL = "http://" + mURL;
        }

        mURL = mURL.trim();

        mUsername = mUsernameView.getText().toString().trim();
        mPassword = mPasswordView.getText().toString().trim();

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
            mLoginStatusMessageView.setText(R.string.login_progress_attempting_login);
            showProgress(true);
        }

        FeedoApiHelper.testFeedoUrlAndUserdata(mURL, mUsername, mPassword, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                if(response.equalsIgnoreCase("FEEDO")) {
                    Intent intent = SetURLActivity.this.getIntent();
                    intent.putExtra(EXTRA_URL, mURL);
                    if(!mUsername.isEmpty() && !mPassword.isEmpty()) {
                        intent.putExtra(EXTRA_USERNAME, mUsername);
                        intent.putExtra(EXTRA_PASSWORD, mPassword);
                    }
                    SetURLActivity.this.setResult(RESULT_OK, intent);
                    finish();
                } else {
                    mURLView.setError(getString(R.string.error_invalid_url));
                    mURLView.requestFocus();
                }
            }

            @Override
            public void onFailure(Throwable e, String errorResponse) {
                mURLView.setError(getString(R.string.error_invalid_url));
                mURLView.requestFocus();
            }

            @Override
            public void onFinish() {
                showProgress(false);
            }
        });
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
}
