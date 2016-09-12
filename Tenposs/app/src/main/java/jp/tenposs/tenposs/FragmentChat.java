package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by ambient on 8/4/16.
 */
public class FragmentChat extends AbstractFragment {
    int storeId;
    WebView webView;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = getString(R.string.chat);
        toolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_chat, null);
        this.webView = (WebView) mRoot.findViewById(R.id.web_view);
        return mRoot;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            //this.screenData = (TopInfo.Response.ResponseData) savedInstanceState.getSerializable(SCREEN_DATA);
        }
        if (savedInstanceState.containsKey(APP_DATA_STORE_ID)) {
            this.storeId = savedInstanceState.getInt(APP_DATA_STORE_ID);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {

    }
}
