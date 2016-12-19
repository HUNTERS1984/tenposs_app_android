package jp.tenposs.tenposs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 9/22/16.
 */

public class PopupInstagram implements DialogInterface.OnClickListener {
    public interface OAuthListener {
        void onComplete(String accessToken);

        void onError(String error);
    }

    OAuthListener mOAuthListenner;
    private static String TAG = "PopupInstagram";

    public interface PopupListener {
        void onPopupDismiss(int which, Serializable extras);
    }


    final static String authUrlString = "https://api.instagram.com/oauth/authorize/";
    final static String tokenUrlString = "https://api.instagram.com/oauth/access_token/";

    final static String clientID = "cd9f614f85f44238ace18045a51c44d1";
    final static String clientSecret = "d839149848c04447bd379ce8bff4d890";
    final static String redirectUri = "https://www.facebook.com/hunters5inc/?notif_t=page_admin&notif_id=1461120763662795";

    final static String scope = "basic";
    final static String TOKEN_TEMPLATE = "#access_token=";

    protected AlertDialog alert;
    protected AlertDialog.Builder alertBuilder;
    protected Context mContext;
    protected View contentView;

    String mExtras = null;

    WebView mWebView;
    PopupListener mListener;

    public PopupInstagram(Context context) {
        this.mContext = context;
    }

    public void show(PopupListener listener) {
        mListener = listener;
        //alertBuilder = new AlertDialog.Builder(this.mContext, R.style.NormalDialog);
        alertBuilder = new AlertDialog.Builder(this.mContext);

        //contentView = LayoutInflater.from(this.mContext).inflate(R.layout.popup_instagram, null);
        //mWebView = (WebView) contentView.findViewById(R.id.web_view);
        mWebView = new WebView(mContext);
//        alertBuilder.setView(contentView);

        LinearLayout wrapper = new LinearLayout(mContext);
        EditText keyboardHack = new EditText(mContext);

        keyboardHack.setVisibility(View.GONE);


        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(mWebView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        wrapper.addView(keyboardHack, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        alertBuilder.setView(wrapper);
        alertBuilder.setCancelable(false);
        //contentView.setBackgroundColor(Color.argb(0, 0, 0, 0));

        alertBuilder.setPositiveButton(mContext.getString(R.string.cancel), this);
        String url = authUrlString +
                "?client_id=" + clientID +
                "&redirect_uri=" + Utils.urlEncode(redirectUri) +
                "&scope=" + scope +
                "&response_type=token&display=touch";

        this.mWebView.setWebChromeClient(
                new WebChromeClient() {
                    @Override
                    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                        Log.i(TAG, consoleMessage.message());
                        return super.onConsoleMessage(consoleMessage);
                    }
                }
        );
        this.mWebView.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {

                        Log.i(TAG, "Loading " + url);

                        if (url.contains(TOKEN_TEMPLATE) == true) {
                            //TODO:get access_token here
                            processRedirectUrl(url);

                            return true;
                        }
                        return false;
                    }
                }
        );

        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.loadUrl(url);

        alert = alertBuilder.show();
        //alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mListener != null) {
            mListener.onPopupDismiss(which, mExtras);
        }
    }

    void processRedirectUrl(String url) {
        int pos = url.indexOf(TOKEN_TEMPLATE);

        if (pos == -1) {
            //show error
        } else {

            String token = url.substring(pos + TOKEN_TEMPLATE.length());

            mExtras = token;

            if (mListener != null) {
                mListener.onPopupDismiss(DialogInterface.BUTTON_POSITIVE, mExtras);
            }
            alert.dismiss();
        }
    }

    public static class InstagramSession {
        private SharedPreferences sharedPref;
        private SharedPreferences.Editor editor;

        private static final String SHARED = "Instagram_Preferences";
        private static final String API_USERNAME = "username";
        private static final String API_ID = "id";
        private static final String API_NAME = "name";
        private static final String API_ACCESS_TOKEN = "access_token";

        public InstagramSession(Context context) {
            sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
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

//    public static class InstagramApp {
//        private InstagramSession mSession;
//        //        private InstagramDialog mDialog;
//        private OAuthAuthenticationListener mListener;
//        //        private ProgressDialog mProgress;
//        private String mAuthUrl;
//        private String mTokenUrl;
//        private String mAccessToken;
//        private Context mCtx;
//        private String mClientId;
//        private String mClientSecret;
//
//        private static int WHAT_FINALIZE = 0;
//        private static int WHAT_ERROR = 1;
//        private static int WHAT_FETCH_INFO = 2;
//
//        /**
//         * Callback url, as set in 'Manage OAuth Costumers' page
//         * (https://developer.github.com/)
//         */
//
//        public static String mCallbackUrl = "";
//        private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
//        private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
//        private static final String API_URL = "https://api.instagram.com/v1";
//
//        private static final String TAG = "InstagramAPI";
//
//
//        public InstagramApp(Context context, String clientId, String clientSecret,
//                            String callbackUrl) {
//            mClientId = clientId;
//            mClientSecret = clientSecret;
//            mCtx = context;
//            mSession = new InstagramSession(context);
//            mAccessToken = mSession.getAccessToken();
//            mCallbackUrl = callbackUrl;
//            mTokenUrl = TOKEN_URL + "?client_id=" + clientId + "&client_secret="
//                    + clientSecret + "&redirect_uri=" + mCallbackUrl + "&grant_type=authorization_code";
//            mAuthUrl = AUTH_URL + "?client_id=" + clientId + "&redirect_uri="
//                    + mCallbackUrl + "&response_type=code&display=touch&scope=likes+comments+relationships";
//            OAuthListener listener = new OAuthListener() {
//                @Override
//                public void onComplete(String code) {
//                    getAccessToken(code);
//                }
//
//                @Override
//                public void onError(String error) {
//                    mListener.onFail("Authorization failed");
//                }
//            };
//
////            mDialog = new InstagramDialog(context, mAuthUrl, listener);
////            mProgress = new ProgressDialog(context);
////            mProgress.setCancelable(false);
//        }
//
//        private void getAccessToken(final String code) {
////            mProgress.setMessage("Getting access token ...");
////            mProgress.show();
//
//            new Thread() {
//                @Override
//                public void run() {
////                    Log.i(TAG, "Getting access token");
//                    int what = WHAT_FETCH_INFO;
//                    try {
//                        URL url = new URL(TOKEN_URL);
////URL url = new URL(mTokenUrl + "&code=" + code);
////                        Log.i(TAG, "Opening Token URL " + url.toString());
//                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                        urlConnection.setRequestMethod("POST");
//                        urlConnection.setDoInput(true);
//                        urlConnection.setDoOutput(true);
////urlConnection.connect();
//                        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
//                        writer.write("client_id=" + mClientId +
//                                "&client_secret=" + mClientSecret +
//                                "&grant_type=authorization_code" +
//                                "&redirect_uri=" + mCallbackUrl +
//                                "&code=" + code);
//                        writer.flush();
//                        String response = streamToString(urlConnection.getInputStream());
////                        Log.i(TAG, "response " + response);
//                        JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
//                        mAccessToken = jsonObj.getString("access_token");
////                        Log.i(TAG, "Got access token: " + mAccessToken);
//                        String id = jsonObj.getJSONObject("user").getString("id");
//                        String user = jsonObj.getJSONObject("user").getString("username");
//                        String name = jsonObj.getJSONObject("user").getString("full_name");
//                        mSession.storeAccessToken(mAccessToken, id, user, name);
//                    } catch (Exception ex) {
//                        what = WHAT_ERROR;
//                        ex.printStackTrace();
//                    }
//
//                    mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
//                }
//            }.start();
//        }
//
//        private void fetchUserName() {
////            mProgress.setMessage("Finalizing ...");
//            new Thread() {
//
//                @Override
//                public void run() {
////                    Log.i(TAG, "Fetching user info");
//                    int what = WHAT_FINALIZE;
//                    try {
//                        URL url = new URL(API_URL + "/users/" + mSession.getId() + "/?access_token=" + mAccessToken);
//
////                        Log.d(TAG, "Opening URL " + url.toString());
//                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                        urlConnection.setRequestMethod("GET");
//                        urlConnection.setDoInput(true);
//                        urlConnection.connect();
//                        String response = streamToString(urlConnection.getInputStream());
//                        Log.i(TAG, response);
//                        JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
//                        String name = jsonObj.getJSONObject("data").getString("full_name");
//                        String bio = jsonObj.getJSONObject("data").getString("bio");
////                        Log.i(TAG, "Got name: " + name + ", bio [" + bio + "]");
//                    } catch (Exception ex) {
//                        what = WHAT_ERROR;
//                        ex.printStackTrace();
//                    }
//
//                    mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
//                }
//            }.start();
//        }
//
//
//        private Handler mHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == WHAT_ERROR) {
////                    mProgress.dismiss();
//                    if (msg.arg1 == 1) {
//                        mListener.onFail("Failed to get access token");
//                    } else if (msg.arg1 == 2) {
//                        mListener.onFail("Failed to get user information");
//                    }
//                } else if (msg.what == WHAT_FETCH_INFO) {
//                    fetchUserName();
//                } else {
////                    mProgress.dismiss();
//                    mListener.onSuccess();
//                }
//            }
//        };
//
//        public boolean hasAccessToken() {
//            return (mAccessToken == null) ? false : true;
//        }
//
//        public void setListener(OAuthAuthenticationListener listener) {
//            mListener = listener;
//        }
//
//        public String getUserName() {
//            return mSession.getUsername();
//        }
//
//        public String getId() {
//            return mSession.getId();
//        }
//
//        public String getName() {
//            return mSession.getName();
//        }
//
//        public void authorize() {
////Intent webAuthIntent = new Intent(Intent.ACTION_VIEW);
//            //webAuthIntent.setData(Uri.parse(AUTH_URL));
//            //mCtx.startActivity(webAuthIntent);
////            mDialog.show();
//        }
//
//        private String streamToString(InputStream is) throws IOException {
//            String str = "";
//
//            if (is != null) {
//                StringBuilder sb = new StringBuilder();
//                String line;
//
//                try {
//                    BufferedReader reader = new BufferedReader(
//                            new InputStreamReader(is));
//
//                    while ((line = reader.readLine()) != null) {
//                        sb.append(line);
//                    }
//
//                    reader.close();
//                } finally {
//                    is.close();
//                }
//
//                str = sb.toString();
//            }
//
//            return str;
//        }
//
//        public void resetAccessToken() {
//            if (mAccessToken != null) {
//                mSession.resetAccessToken();
//                mAccessToken = null;
//            }
//        }
//
//        public interface OAuthAuthenticationListener {
//            public abstract void onSuccess();
//
//            public abstract void onFail(String error);
//        }
//    }
}
