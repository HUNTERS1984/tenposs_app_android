package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import jp.tenposs.datamodel.ScreenDataStatus;

/**
 * Created by ambient on 9/22/16.
 */

public class FragmentInstagram extends AbstractFragment {

    final static String authUrlString = "https://api.instagram.com/oauth/authorize/";
    final static String tokenUrlString = "https://api.instagram.com/oauth/access_token/";

    // ADD YOUR CLIENT ID AND SECRET HERE
    final static String clientID = "8a2eb09ac42d4ad3bdbd61b3c6742a33";
    final static String clientSecret = "586b1c5041134bc7946894b8da702f4b";

    // YOU NEED A BAD URL HERE - THIS NEEDS TO MATCH YOUR URL SET UP FOR YOUR
// INSTAGRAM APP
    final static String redirectUri = "https://ten-po.com/";
    WebView mWebView;

//    private FragmentInstagram() {
//
//    }
//
//    public static FragmentInstagram newInstance(Serializable extras) {
//        FragmentInstagram fragment = new FragmentInstagram();
//        return fragment;
//    }


    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {

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
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    protected void customResume() {

    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return false;
    }
}
