package de.feedo.android.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.feedo.android.FeedsActivity;
import de.feedo.android.util.ObscuredSharedPreferences;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
public class FeedoApiHelper {
    private static SharedPreferences userDataPreferences;

    private static AsyncHttpClient client = new AsyncHttpClient();
    ;
    public static String rootUrl;
    public static String username;
    public static String password;

    public static void loadUserData(Context c) {
        userDataPreferences = new ObscuredSharedPreferences(
                c, c.getSharedPreferences(FeedsActivity.PREFERENCES_USERDATA_NAME, Context.MODE_PRIVATE));

        if (!userDataPreferences.getString(FeedsActivity.PREFERENCES_KEY_USERNAME, "").isEmpty()) {
            String username = userDataPreferences.getString(FeedsActivity.PREFERENCES_KEY_USERNAME, "");
            String password = userDataPreferences.getString(FeedsActivity.PREFERENCES_KEY_PASSWORD, "");
            FeedoApiHelper.username = username;
            FeedoApiHelper.password = password;
            client.setBasicAuth(username, password);
            rootUrl = userDataPreferences.getString(FeedsActivity.PREFERENCES_KEY_URL, "");
        }
    }

    public static void setRootUrl(String rootUrl) {
        FeedoApiHelper.rootUrl = rootUrl;
    }

    public static void setUserData(String username, String password) {
        FeedoApiHelper.username = username;
        FeedoApiHelper.password = password;
        client.setBasicAuth(username, password);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
        Log.i("feedo", "Getting " + getAbsoluteUrl(url));
    }

    public static void testFeedoUrlAndUserdata(String url, String username, String password, AsyncHttpResponseHandler h) {
        FeedoApiHelper.setRootUrl(url);
        FeedoApiHelper.setUserData(username, password);
        FeedoApiHelper.get("api/info", null, h);
    }


    public static String getAbsoluteUrl(String relativeUrl) {
        return rootUrl + "/" + relativeUrl;
    }

    public static void updateFeedItems(final FeedUpdateListener listener) {
        URI uri = URI.create(getAbsoluteUrl("api/update_feeds"));
        if (uri.getPort() == -1) {
            try {
                uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), 80, uri.getPath(), uri.getQuery(), uri.getFragment());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
        list.add(new BasicNameValuePair("Authorization", "Basic " + getAuthHeaderString(FeedoApiHelper.username, FeedoApiHelper.password)));

        SSEClient c = new SSEClient(uri, new SSEClient.Listener() {
            @Override
            public void onConnect() {
                Log.i("feedo", "connected!");
            }

            @Override
            public void onMessage(String message, String data) {
                if (message.equals("feed_updated")) {
                    try {
                        JSONObject o = new JSONObject(data);
                        int progress = o.getInt("progress");
                        int total = o.getInt("total");
                        listener.onProgress(progress, total);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (message.equals("updating_finished")) {
                    listener.onFinished();
                }

            }

            @Override
            public void onDisconnect(int code, String reason) {
                Log.i("feedo", "disconnect!" + code + reason);

            }

            @Override
            public void onError(Exception error) {
                Log.i("feedo", "error!", error);

            }
        }, list);
        c.connect();

    }

    public static interface FeedUpdateListener {
        void onProgress(int progress, int total);

        void onFinished();
    }

    private static String getAuthHeaderString(String username, String password) {
        return (Base64.encodeToString((username + ":" + password).getBytes(), Base64.DEFAULT));
    }

    public static FeedoService getFeedoService() {
        RestAdapter restAdapter = new RestAdapter.Builder().setServer(FeedoApiHelper.rootUrl + "/api/")
                .setConverter(new GsonConverter(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        requestFacade.addHeader("Authorization", "Basic " + getAuthHeaderString(FeedoApiHelper.username, FeedoApiHelper.password));
                    }
                }).build();

        return restAdapter.create(FeedoService.class);
    }
}
