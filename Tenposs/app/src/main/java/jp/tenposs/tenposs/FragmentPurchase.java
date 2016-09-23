package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.Serializable;
import java.util.Locale;

import jp.tenposs.datamodel.ItemsInfo;
import jp.tenposs.datamodel.ScreenDataStatus;

/**
 * Created by ambient on 8/4/16.
 */
public class FragmentPurchase extends AbstractFragment {

    ProgressBar mLoadingProgress;
    WebView mWebView;
    ItemsInfo.Item mScreenData;

    public static FragmentPurchase newInstance(Serializable extras) {
        FragmentPurchase gm = new FragmentPurchase();
        Bundle b = new Bundle();
        b.putSerializable(SCREEN_DATA, extras);
        gm.setArguments(b);
        return gm;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.purchase);
        mToolbarSettings.toolbarLeftIcon = "flaticon-back";
        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void clearScreenData() {

    }


    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        try {
            String strUrl = mScreenData.item_link;
            String strTemp = mScreenData.item_link.toLowerCase(Locale.US);

            if (strTemp.contains("http://") == false && strTemp.contains("https://") == false)
                strUrl = "http://" + strUrl;

            this.mWebView.loadUrl(strUrl);
        } catch (Exception ignored) {

        }
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
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (ItemsInfo.Item) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putSerializable(SCREEN_DATA, this.mScreenData);

    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return true;
    }

}
