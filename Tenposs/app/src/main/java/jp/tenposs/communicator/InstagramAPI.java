package jp.tenposs.communicator;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.tenposs.tenposs.R;
import jp.tenposs.utils.SharedPreferencesHelper;

/**
 * Created by ambient on 9/26/16.
 */

public class InstagramAPI {
    public interface OAuthDialogListener {
        void onComplete(String accessToken);

        void onError(String error);
    }

    private InstagramSession mSession;
    private InstagramDialog mDialog;
    private OAuthAuthenticationListener mListener;
    private ProgressDialog mProgress;
    private String mAuthUrl;
    private String mTokenUrl;
    private String mAccessToken;
    private Context mCtx;
    private String mClientId;
    private String mClientSecret;
    boolean mAuthorized = false;

    public static int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;

    /**
     * Callback url, as set in 'Manage OAuth Costumers' page
     * (https://developer.github.com/)
     */

    public static String mCallbackUrl = "";
    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String API_URL = "https://api.instagram.com/v1";

    private static final String TAG = "InstagramAPI";

    public InstagramAPI(Context context,
                        String clientId,
                        String clientSecret,
                        String callbackUrl) {
        mClientId = clientId;
        mClientSecret = clientSecret;
        mCtx = context;
        mSession = new InstagramSession(context);
        mAccessToken = mSession.getAccessToken();
        mCallbackUrl = callbackUrl;
        mTokenUrl = TOKEN_URL + "?client_id=" + clientId + "&client_secret="
                + clientSecret + "&redirect_uri=" + mCallbackUrl + "&grant_type=authorization_code";
        mAuthUrl = AUTH_URL + "?client_id=" + clientId + "&redirect_uri="
                + mCallbackUrl + "&response_type=code&display=touch&scope=likes+comments+relationships";
        OAuthDialogListener listener = new OAuthDialogListener() {
            @Override
            public void onComplete(String code) {
                getAccessToken(code);
            }

            @Override
            public void onError(String error) {
                mListener.onFail("Authorization failed");
            }
        };

        mDialog = new InstagramDialog(context, mAuthUrl, listener);
        mProgress = new ProgressDialog(context);
        mProgress.setCancelable(false);
    }

    private void getAccessToken(final String code) {
        mProgress.setMessage("Getting access token ...");
        mProgress.show();

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Getting access token");
                int what = WHAT_FINALIZE;
                try {
                    URL url = new URL(TOKEN_URL);
                    Log.i(TAG, "Opening Token URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write("client_id=" + mClientId +
                            "&client_secret=" + mClientSecret +
                            "&grant_type=authorization_code" +
                            "&redirect_uri=" + mCallbackUrl +
                            "&code=" + code);
                    writer.flush();
                    String response = streamToString(urlConnection.getInputStream());
                    Log.i(TAG, "response " + response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                    mAccessToken = jsonObj.getString("access_token");
                    Log.i(TAG, "Got access token: " + mAccessToken);
                    String id = jsonObj.getJSONObject("user").getString("id");
                    String user = jsonObj.getJSONObject("user").getString("username");
                    String name = jsonObj.getJSONObject("user").getString("full_name");
                    mAuthorized = true;
                    mSession.storeAccessToken(mAccessToken, id, user, name);
                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
            }
        }.start();
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_ERROR) {
                mProgress.dismiss();
                if (msg.arg1 == 1) {
                    mListener.onFail("Failed to get access token");
                } else if (msg.arg1 == 2) {
                    mListener.onFail("Failed to get user information");
                }
            } else {
                mProgress.dismiss();
                mListener.onSuccess();
            }
        }
    };

    public boolean hasAccessToken() {
        return (mAccessToken == null) ? false : true;
    }

    public void setListener(OAuthAuthenticationListener listener) {
        mListener = listener;
    }


    public String getAccessToken() {
        return mAccessToken;
    }

    public String getUserName() {
        return mSession.getUsername();
    }

    public String getId() {
        return mSession.getId();
    }

    public String getName() {
        return mSession.getName();
    }

    public void authorize() {
        mAuthorized = false;
        mDialog.show();
    }

    private String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

    public void resetAccessToken() {
        if (mAccessToken != null) {
            mSession.resetAccessToken();
            mAccessToken = null;
        }
    }

    public interface OAuthAuthenticationListener {
        void onSuccess();

        void onFail(String error);
    }

    public class InstagramSession {

        private SharedPreferences sharedPref;
        private SharedPreferences.Editor editor;

        private static final String API_USERNAME = "instagram_username";
        private static final String API_ID = "instagram_id";
        private static final String API_NAME = "instagram_name";
        private static final String API_ACCESS_TOKEN = "instagram_access_token";

        public InstagramSession(Context context) {
            sharedPref = SharedPreferencesHelper.getSharedPreferences(context);
            editor = sharedPref.edit();
        }

        /**
         * @param accessToken
         * @param username
         */
        public void storeAccessToken(String accessToken, String id, String username, String name) {
            editor.putString(API_ID, id);
            editor.putString(API_NAME, name);
            editor.putString(API_ACCESS_TOKEN, accessToken);
            editor.putString(API_USERNAME, username);
            editor.commit();
        }

        public void storeAccessToken(String accessToken) {
            editor.putString(API_ACCESS_TOKEN, accessToken);
            editor.commit();
        }

        /**
         * Reset access token and user name
         */
        public void resetAccessToken() {
            editor.putString(API_ID, null);
            editor.putString(API_NAME, null);
            editor.putString(API_ACCESS_TOKEN, null);
            editor.putString(API_USERNAME, null);
            editor.commit();
        }

        /**
         * Get user name
         *
         * @return User name
         */
        public String getUsername() {
            return sharedPref.getString(API_USERNAME, null);
        }

        /**
         * @return
         */
        public String getId() {
            return sharedPref.getString(API_ID, null);
        }

        /**
         * @return
         */
        public String getName() {
            return sharedPref.getString(API_NAME, null);
        }

        /**
         * Get access token
         *
         * @return Access token
         */
        public String getAccessToken() {
            return sharedPref.getString(API_ACCESS_TOKEN, null);
        }
    }

    public class InstagramDialog extends Dialog {
        final float[] DIMENSIONS_LANDSCAPE = {460, 260};
        final float[] DIMENSIONS_PORTRAIT = {280, 420};
        final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        static final int MARGIN = 4;
        static final int PADDING = 2;
        private String mUrl;
        private OAuthDialogListener mListener;
        private ProgressDialog mSpinner;
        private WebView mWebView;
        private LinearLayout mContent;
        private static final String TAG = "Instagram-WebView";

        public InstagramDialog(Context context, String url,
                               OAuthDialogListener listener) {
            super(context);
            mUrl = url;
            mListener = listener;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mSpinner = new ProgressDialog(getContext());
            mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mSpinner.setMessage(mCtx.getString(R.string.msg_loading));
            mContent = new LinearLayout(getContext());
            mContent.setOrientation(LinearLayout.VERTICAL);
            setUpWebView();
//            Display display = getWindow().getWindowManager().getDefaultDisplay();
//            final float scale = getContext().getResources().getDisplayMetrics().density;
//            float[] dimensions = (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;
            //addContentView(mContent, new FrameLayout.LayoutParams(
            //      (int) (dimensions[0] * scale + 0.5f),
            //    (int) (dimensions[1] * scale + 0.5f)));
            addContentView(mContent, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));

            CookieManager cookieManager = CookieManager.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.removeAllCookies(null);

            } else {
                CookieSyncManager.createInstance(getContext());
                cookieManager.removeAllCookie();
            }
        }

        private void setUpWebView() {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            mWebView = new WebView(getContext());
            mWebView.setVerticalScrollBarEnabled(false);
            mWebView.setHorizontalScrollBarEnabled(false);
            mWebView.setWebViewClient(new OAuthWebViewClient());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadUrl(mUrl);
            mWebView.setLayoutParams(FILL);
            mContent.addView(mWebView);
        }

        private class OAuthWebViewClient extends WebViewClient {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "Redirecting URL " + url);
                //if (url.startsWith(InstagramAPI.mCallbackUrl)) {
                if (url.contains("&code=") == true && mAuthorized == false) {
                    String urls[] = url.split("=");

                    mListener.onComplete(urls[urls.length - 1]);
                    InstagramDialog.this.dismiss();
                    return true;
                }
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d(TAG, "Page error: " + description);
                super.onReceivedError(view, errorCode, description, failingUrl);
                mListener.onError(description);
                InstagramDialog.this.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "Loading URL: " + url);
                super.onPageStarted(view, url, favicon);
                mSpinner.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished URL: " + url);
                mSpinner.dismiss();
            }
        }

    }
}
