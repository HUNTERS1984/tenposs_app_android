package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.UserInfo;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/4/16.
 */
public class FragmentChat extends FragmentWebView {

//    private FragmentChat() {
//
//    }
//
//    public static FragmentChat newInstance(String title, int storeId) {
//        FragmentChat fragment = new FragmentChat();
//        Bundle b = new Bundle();
//        b.putString(AbstractFragment.SCREEN_TITLE, title);
//        b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
//        fragment.setArguments(b);
//        return fragment;
//    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.chat);

        if (this.mShowFromSideMenu == true) {
            mToolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
            mToolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
        } else {
            mToolbarSettings.toolbarLeftIcon = "flaticon-back";
            mToolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
        }
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
        String userProfile = Utils.getPrefString(getContext(), Key.UserProfile);
        UserInfo.User user = (UserInfo.User) CommonObject.fromJSONString(userProfile, UserInfo.User.class, null);
        String url = TenpossCommunicator.WEB_ADDRESS + "/chat/screen/" + user.id;
        this.mWebView.loadUrl(url);
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        super.loadSavedInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(APP_DATA_STORE_ID)) {
            this.mStoreId = savedInstanceState.getInt(APP_DATA_STORE_ID);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        super.customSaveInstanceState(outState);
        outState.putInt(APP_DATA_STORE_ID, this.mStoreId);
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            return true;
        } else {
            return false;
        }
    }
}
