package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by ambient on 7/29/16.
 */
public class FragmentCompanyInfo extends FragmentWebView {

    public static FragmentCompanyInfo newInstance(@NonNull String url) {
        FragmentCompanyInfo gm = new FragmentCompanyInfo();
        Bundle b = new Bundle();
        b.putString(SCREEN_DATA, url);
        gm.setArguments(b);
        return gm;
    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.company_info);
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
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mUrl = savedInstanceState.getString(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return true;
    }

}
