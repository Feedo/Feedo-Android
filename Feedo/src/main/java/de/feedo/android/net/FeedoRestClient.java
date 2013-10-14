package de.feedo.android.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;

import de.feedo.android.FeedsActivity;
import de.feedo.android.util.ObscuredSharedPreferences;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
public class FeedoRestClient {
    private static SharedPreferences userDataPreferences;

    private static AsyncHttpClient client = new AsyncHttpClient();;
    private static String rootUrl;
    private static Context context;

    public static void loadUserData(Context c) {
        userDataPreferences = new ObscuredSharedPreferences(
                c, c.getSharedPreferences(FeedsActivity.PREFERENCES_USERDATA_NAME, Context.MODE_PRIVATE));

        if(!userDataPreferences.getString(FeedsActivity.PREFERENCES_KEY_USERNAME, "").isEmpty()) {
            String username = userDataPreferences.getString(FeedsActivity.PREFERENCES_KEY_USERNAME, "");
            String password = userDataPreferences.getString(FeedsActivity.PREFERENCES_KEY_PASSWORD, "");
            client.setBasicAuth(username, password);
            rootUrl = userDataPreferences.getString(FeedsActivity.PREFERENCES_KEY_URL, "");
        }

        FeedoRestClient.context = c;
    }

    public static void setRootUrl(String rootUrl) {
        FeedoRestClient.rootUrl = rootUrl;
    }

    public static void setUserData(String username, String password) {
        client.setBasicAuth(username, password);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
        Log.i("feedo", "Getting " + getAbsoluteUrl(url));
    }

    public static void post(String url, HttpEntity httpEntity, String contentType, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), httpEntity, contentType, responseHandler);
    }

    public static void put(String url, HttpEntity httpEntity, String contentType, AsyncHttpResponseHandler responseHandler) {
        client.put(context, getAbsoluteUrl(url), httpEntity, contentType, responseHandler);
        Log.i("feedo", "Putting " + getAbsoluteUrl(url));

    }

    public static String getAbsoluteUrl(String relativeUrl) {
        return rootUrl + "/" + relativeUrl;
    }
}
