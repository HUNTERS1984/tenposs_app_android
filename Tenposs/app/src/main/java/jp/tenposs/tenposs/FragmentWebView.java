package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import jp.tenposs.datamodel.ScreenDataStatus;

/**
 * Created by ambient on 9/28/16.
 */

public abstract class FragmentWebView extends AbstractFragment {
    ProgressBar mLoadingProgress;
    WebView mWebView;
    String mUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        this.mWebView.loadUrl(mUrl);
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_web_view, null);
        this.mLoadingProgress = (ProgressBar) root.findViewById(R.id.loading_progress);
        this.mWebView = (WebView) root.findViewById(R.id.web_view);
        this.mWebView.measure(100, 100);
        this.mWebView.getSettings().setUseWideViewPort(true);
        this.mWebView.getSettings().setLoadWithOverviewMode(true);
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                return false;
            }
        });
        this.mWebView.setWebChromeClient(
                new WebChromeClient() {
                    @Override
                    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                        System.out.println(consoleMessage.message());
                        return super.onConsoleMessage(consoleMessage);
                    }

                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        if (newProgress < 100) {
                            mLoadingProgress.setVisibility(View.VISIBLE);
                            System.out.println("Loading " + newProgress + "%");
                            mLoadingProgress.setProgress(newProgress);
                        } else {
                            mLoadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        return root;
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }
}
