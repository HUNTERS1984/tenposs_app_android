package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by ambient on 7/29/16.
 */
public class FragmentUserPrivacy extends FragmentWebView {

//    public static FragmentUserPrivacy newInstance(@NonNull String url) {
//        FragmentUserPrivacy gm = new FragmentUserPrivacy();
//        Bundle b = new Bundle();
//        b.putString(SCREEN_URL, url);
//        gm.setArguments(b);
//        return gm;
//    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.user_privacy);
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
        super.loadSavedInstanceState(savedInstanceState);
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        super.customSaveInstanceState(outState);
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return true;
    }

}
