package jp.tenposs.tenposs;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import jp.tenposs.datamodel.AppSettings;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.UserObject;

/**
 * Created by ambient on 7/29/16.
 */
public class FragmentReserve extends AbstractFragment {
    WebView webView;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings = new ToolbarSettings();
        toolbarSettings.toolbarTitle = "Reserve";
        toolbarSettings.toolbarIcon = "ti-angle-left";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;

        toolbarSettings.settings = new AppSettings.Settings();
        toolbarSettings.settings.fontColor = "#00CECB";

        toolbarSettings.titleSettings = new AppSettings.Settings();
        toolbarSettings.titleSettings.fontColor = "#000000";
        toolbarSettings.titleSettings.fontSize = 20;
    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {

    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_reserve, null);
        this.webView = (WebView) mRoot.findViewById(R.id.web_view);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                return false;
            }
        });
        return mRoot;
    }

    @Override
    void loadSavedInstanceState(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
//            this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
//        } else {
//            previewScreenData();
//        }
        this.webView.loadUrl("http://tabelog.com");
    }
}
