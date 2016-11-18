package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;

import jp.tenposs.datamodel.ItemsInfo;
import jp.tenposs.datamodel.ScreenDataStatus;

/**
 * Created by ambient on 8/4/16.
 */
public class FragmentPurchase extends FragmentWebView {
    ItemsInfo.Item mScreenData;

    public static FragmentPurchase newInstance(Serializable extras) {
        FragmentPurchase gm = new FragmentPurchase();
        Bundle b = new Bundle();
        b.putSerializable(SCREEN_DATA, extras);
        gm.setArguments(b);
        return gm;
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
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        super.loadSavedInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (ItemsInfo.Item) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        super.customSaveInstanceState(outState);
        if (mScreenData != null) {
            if (this.mScreenData != null) {
                outState.putSerializable(SCREEN_DATA, this.mScreenData);
            }
        }
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return true;
    }

}
