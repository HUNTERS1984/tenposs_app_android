package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.SignInInfo;

/**
 * Created by ambient on 8/4/16.
 */
public class FragmentChat extends AbstractFragment {
    WebView mWebView;

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.chat);
        mToolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
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
        updateToolbar();
        String userProfile = getKeyString(Key.UserProfile);
        SignInInfo.User user = (SignInInfo.User) CommonObject.fromJSONString(userProfile, SignInInfo.User.class, null);
        this.mWebView.setWebChromeClient(new WebChromeClient() {
                                            @Override
                                            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                                                return super.onConsoleMessage(consoleMessage);
                                            }
                                        }
        );
        this.mWebView.setWebViewClient(new WebViewClient() {
                                          @Override
                                          public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                              return false;
                                          }
                                      }
        );

        this.mWebView.getSettings().setJavaScriptEnabled(true);
        String url = TenpossCommunicator.WEB_ADDRESS + "/chat/screen/" + user.profile.app_user_id;
        this.mWebView.loadUrl(url);
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_web_view, null);
        this.mWebView = (WebView) mRoot.findViewById(R.id.web_view);
        return mRoot;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(APP_DATA_STORE_ID)) {
            this.mStoreId = savedInstanceState.getInt(APP_DATA_STORE_ID);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putInt(APP_DATA_STORE_ID, this.mStoreId);
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return false;
    }
}
