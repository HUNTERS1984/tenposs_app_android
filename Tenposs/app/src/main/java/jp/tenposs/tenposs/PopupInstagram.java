package jp.tenposs.tenposs;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.Serializable;

/**
 * Created by ambient on 9/22/16.
 */

public class PopupInstagram implements DialogInterface.OnClickListener {

    public interface PopupListener {
        void onPopupDismiss(int which, Serializable extras);
    }

    final static String authUrlString = "https://api.instagram.com/oauth/authorize/";
    final static String tokenUrlString = "https://api.instagram.com/oauth/access_token/";

    // ADD YOUR CLIENT ID AND SECRET HERE
    final static String clientID = "8a2eb09ac42d4ad3bdbd61b3c6742a33";
    final static String clientSecret = "586b1c5041134bc7946894b8da702f4b";

    // YOU NEED A BAD URL HERE - THIS NEEDS TO MATCH YOUR URL SET UP FOR YOUR
// INSTAGRAM APP
    final static String redirectUri = "https://ten-po.com/";
    final static String scope = "basic";

    protected AlertDialog alert;
    protected AlertDialog.Builder alertBuilder;
    protected Context mContext;
    protected View contentView;

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
        alertBuilder.setView(mWebView);
        alertBuilder.setCancelable(false);
        //contentView.setBackgroundColor(Color.argb(0, 0, 0, 0));

        alertBuilder.setPositiveButton(mContext.getString(R.string.cancel), this);
        String url = authUrlString +
                "?client_id=" + clientID +
                "&redirect_uri=" + redirectUri +
                "&scope=" + scope +
                "&response_type=token&display=touch";

        this.mWebView.setWebChromeClient(
                new WebChromeClient() {
                    @Override
                    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                        System.out.println(consoleMessage.message());
                        return super.onConsoleMessage(consoleMessage);
                    }
                }
        );
        this.mWebView.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
            mListener.onPopupDismiss(which, null);
        }
    }
}
