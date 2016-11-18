package jp.tenposs.tenposs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

import io.fabric.sdk.android.Fabric;
import jp.tenposs.communicator.AppInfoCommunicator;
import jp.tenposs.communicator.SignOutCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.communicator.UserInfoCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.datamodel.SignOutInfo;
import jp.tenposs.datamodel.SocialSigninInfo;
import jp.tenposs.datamodel.TopInfo;
import jp.tenposs.datamodel.UserInfo;
import jp.tenposs.utils.MyTimer;
import jp.tenposs.utils.MyTimerFireListener;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.LeftMenuView;

public class MainActivity extends AppCompatActivity
        implements
        AbstractFragment.MainActivityListener,
        LeftMenuView.OnLeftMenuItemClickListener {

    FragmentManager mFragmentManager;
    FragmentHome mFragmentHome;

    DrawerLayout mDrawerLayout;
    LeftMenuView mLeftMenuView;
    FrameLayout mContentContainer;

    CallbackManager mCallbackManager;

    protected SharedPreferences mAppPreferences;
    protected HashMap<String, String> mSessionValues;


    private boolean serviceNotStart = true;
    MyTimer mTimerShowShareApp = null;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_main);

        MainApplication.setContext(this.getApplicationContext());

        this.mAppPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE);

        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.mLeftMenuView = (LeftMenuView) findViewById(R.id.left_menu_view);
        this.mContentContainer = (FrameLayout) findViewById(R.id.content_container);

        this.mFragmentManager = getSupportFragmentManager();
        this.mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment top = getTopFragment();
                if (top != null) {
                    top.setUserVisibleHint(true);
                }
            }
        });

        MainApplication.setSupportFragmentManager(this.mFragmentManager);
        this.mLeftMenuView.setOnItemClickListener(this);

        if (Utils.isRealDevice(this) == false) {
            Utils.showAlert(this,
                    getString(R.string.warning),
                    getString(R.string.msg_cannot_regist_on_this_device),
                    getString(R.string.close),
                    null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            exitActivity();
                        }
                    });
        } else {
            checkNetworkConnection();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            AbstractFragment topFragment = getTopFragment();
            if (topFragment != null) {
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    if (topFragment.mToolbarSettings.toolbarType == AbstractFragment.ToolbarSettings.LEFT_MENU_BUTTON &&
                            this.mDrawerLayout.getDrawerLockMode(Gravity.LEFT) == DrawerLayout.LOCK_MODE_UNLOCKED) {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                } else {

                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, e);
    }

    @Override
    public void onBackPressed() {
        Utils.hideKeyboard(this, null);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        } else if (mFragmentManager.getBackStackEntryCount() > 0) {
            /**
             * Get Top fragment
             */
            AbstractFragment topFragment = null;
            try {
                int size = mFragmentManager.getBackStackEntryCount();
                FragmentManager.BackStackEntry backStackEntry = this.mFragmentManager.getBackStackEntryAt(size - 1);
                topFragment = (AbstractFragment) this.mFragmentManager.findFragmentByTag(backStackEntry.getName());

            } catch (Exception ex) {

            }

            if (topFragment != null) {
                if (topFragment.mToolbarSettings.toolbarType == AbstractFragment.ToolbarSettings.LEFT_MENU_BUTTON) {
                    /**
                     * Do something
                     */
                    Utils.showAlert(this,
                            getString(R.string.info),
                            getString(R.string.msg_exit_confirm),
                            getString(R.string.yes),
                            getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE: {
                                            exitActivity();
                                        }
                                        break;

                                        case DialogInterface.BUTTON_NEGATIVE: {
                                        }
                                        break;
                                    }
                                }
                            });
                } else if (topFragment instanceof FragmentSignIn && mFragmentHome == null) {
                    mFragmentManager.popBackStackImmediate();
                    showHomeScreen();
                } else if (topFragment.canCloseByBackpressed() == true) {
                    /**
                     * Back and update NavigationBar
                     */
                    topFragment.close();

//                    mFragmentManager.popBackStackImmediate();
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void updateAppInfo(AppInfo.Response appInfo, int storeId) {
        AppData.sharedInstance().mAppInfo = appInfo;
        AppData.sharedInstance().mStoreId = storeId;

        String userProfileStr = Utils.getPrefString(getApplicationContext(), Key.UserProfile);
        SignInInfo.User userProfile = null;
        if (userProfileStr.length() > 0) {
            try {
                userProfile = (SignInInfo.User) CommonObject.fromJSONString(userProfileStr, SignInInfo.User.class, null);
            } catch (Exception ignored) {
            }
        }
        mLeftMenuView.updateMenu(AppData.sharedInstance().mAppInfo.data.app_setting, AppData.sharedInstance().mAppInfo.data.side_menu, userProfile);
    }

    @Override
    public void updateUserInfo(SignInInfo.User userProfile) {
        if (mLeftMenuView != null) {
            mLeftMenuView.updateMenu(AppData.sharedInstance().mAppInfo.data.app_setting, AppData.sharedInstance().mAppInfo.data.side_menu, userProfile);
        }
    }

    @Override
    public void showScreen(int screenId, Serializable extras) {
        switch (screenId) {


            //2
            case AbstractFragment.MENU_SCREEN: {
                showMenuScreen(AppData.sharedInstance().mStoreId);
            }
            break;

            case AbstractFragment.ITEM_SCREEN: {
                showItemDetailScreen(extras);
            }
            break;

            case AbstractFragment.ITEM_PURCHASE_SCREEN: {
                showItemPurchaseScreen(extras);
            }
            break;

            //3
            case AbstractFragment.NEWS_SCREEN: {
                showNewsScreen(AppData.sharedInstance().mStoreId);
            }
            break;

            case AbstractFragment.NEWS_DETAILS_SCREEN: {
                showNewsDetailScreen(extras);
            }
            break;

            //4
            case AbstractFragment.RESERVE_SCREEN: {
                showReserveScreen(extras);
            }
            break;

            //5
            case AbstractFragment.PHOTO_SCREEN: {
                showPhotoScreen(AppData.sharedInstance().mStoreId);
            }
            break;

            case AbstractFragment.PHOTO_ITEM_SCREEN: {
                showPhotoPreviewScreen(extras);
            }
            break;

            case AbstractFragment.HOME_SCREEN: {
                showHomeScreen();
            }
            break;

            case AbstractFragment.CHAT_SCREEN: {
                if (isSignedIn() == true) {
                    showChatScreen(AppData.sharedInstance().mStoreId);
                } else {
                    Utils.showAlert(this,
                            getString(R.string.info),
                            getString(R.string.msg_not_sign_in),
                            getString(R.string.close),
                            null,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                }
            }
            break;


            case AbstractFragment.COUPON_SCREEN: {
                showCouponScreen(AppData.sharedInstance().mStoreId);

            }
            break;

            case AbstractFragment.COUPON_DETAIL_SCREEN: {
                showCouponDetailScreen(extras);
            }
            break;

            case AbstractFragment.SETTING_SCREEN: {
                showSettingScreen(AppData.sharedInstance().mStoreId);
            }
            break;

            case AbstractFragment.PROFILE_SCREEN: {
                showProfileScreen();
            }
            break;

            case AbstractFragment.CHANGE_DEVICE_SCREEN: {
                showChangeDeviceScreen();
            }
            break;

            case AbstractFragment.COMPANY_INFO_SCREEN: {
                showCompanyInfo();
            }
            break;

            case AbstractFragment.USER_PRIVACY_SCREEN: {
                showUserPrivacy();
            }
            break;


            case AbstractFragment.SIGN_IN_SCREEN: {
                showSignInScreen(true);
            }
            break;

            case AbstractFragment.SIGN_IN_EMAIL_SCREEN: {
                showSignInEmailScreen();
            }
            break;

            case AbstractFragment.SIGN_UP_SCREEN: {
                showSignUpScreen();
            }
            break;

            case AbstractFragment.SIGN_UP_NEXT_SCREEN: {
                showSignUpNextScreen(extras);
            }
            break;

            case AbstractFragment.SIGN_OUT_SCREEN: {
                performSignOut();
            }
            break;


            case AbstractFragment.STAFF_SCREEN: {
                showStaffScreen(AppData.sharedInstance().mStoreId);
            }
            break;

            case AbstractFragment.STAFF_DETAIL_SCREEN: {
                showStaffDetailScreen(extras);
            }
            break;

            case AbstractFragment.MY_PAGE_SCREEN: {
                showMyPageScreen();
            }
            break;

            default: {
                Assert.assertFalse("Should never be here ;(", false);
            }
            break;
        }
    }


    @Override
    public AppInfo.Response.ResponseData getAppInfo() {
        return AppData.sharedInstance().mAppInfo.data;
    }

    @Override
    public CallbackManager getCallbackManager() {
        return mCallbackManager;
    }

    @Override
    public void toggleMenu() {
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        } else {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public FragmentManager getFM() {
        return this.mFragmentManager;
    }

    @Override
    public void setDrawerLockMode(int mode) {
        this.mDrawerLayout.setDrawerLockMode(mode);
    }

    @Override
    public void onClick(int position, Bundle params) {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        if (position == -1) {
            int screenId = params.getInt(AbstractFragment.SCREEN_DATA);
            showScreen(screenId, null);
        } else if (position == -2) {
            //sign out???
        } else {
            AppInfo.SideMenu menuItem = (AppInfo.SideMenu) params.getSerializable(Key.RequestObject);
            int screenId = menuItem.id;
            showScreen(screenId, null);
        }
    }

    void exitActivity() {
        MainActivity.this.finish();
    }

    AbstractFragment getTopFragment() {
        AbstractFragment topFragment = null;
        try {
            int size = mFragmentManager.getBackStackEntryCount();
            FragmentManager.BackStackEntry backStackEntry = this.mFragmentManager.getBackStackEntryAt(size - 1);
            topFragment = (AbstractFragment) this.mFragmentManager.findFragmentByTag(backStackEntry.getName());
        } catch (Exception ex) {

        }
        return topFragment;
    }

    Fragment getFragmentForTag(String tag) {
        return mFragmentManager.findFragmentByTag(tag);
    }

    @Override
    public void showFragment(AbstractFragment fragment, String fragmentTag, boolean animated) {
        if (fragment.isAdded()) {
            Log.d("Fragment " + fragmentTag, "IS ADDED!!!");
            if (mFragmentManager.popBackStackImmediate(fragmentTag, 0)) {
                Log.e("Fragment", "IS ADDED AND POPPED!!!");
            } else {
                Log.e("Fragment", "IS ADDED BUT NOT POPPED!!!");
            }
        } else {
            Log.d("Fragment " + fragmentTag, "IS NOT ADDED!!!");
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            if (animated == true) {
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            }
            ft.add(mContentContainer.getId(), fragment, fragmentTag);
            ft.addToBackStack(fragmentTag);
            ft.commit();
        }
    }

    @Override
    public String getSessionValue(String key, String valueDefault) {
        if (this.mSessionValues == null) {
            return valueDefault;
        } else {
            return this.mSessionValues.get(key);
        }
    }

    @Override
    public void setSessionValue(String key, String value) {
        try {

            if (this.mSessionValues == null) {
                this.mSessionValues = new HashMap<>();
            }
            if (value != null) {
                this.mSessionValues.put(key, value);
            } else {
                this.mSessionValues.remove(key);
            }
        } catch (Exception ignored) {

        }
    }

    void showHomeScreen() {
        if (isSignedIn() && serviceNotStart) {
            startService(new Intent(this, TenpossInstanceIDService.class));
            startService(new Intent(this, TenpossMessagingService.class));
            serviceNotStart = false;
        }
        if (this.mFragmentHome == null) {
            this.mFragmentHome = new FragmentHome();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.APP_DATA, AppData.sharedInstance().mAppInfo);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, AppData.sharedInstance().mStoreId);
            mFragmentHome.setArguments(b);

            startAlarmShareApp();
        }
        showFragment(this.mFragmentHome, FragmentHome.class.getCanonicalName(), false);
    }

    void showSignInScreen(boolean showToolbar) {
        //clear token and user profile

        //Local
        Utils.setPrefString(getApplicationContext(), Key.TokenKey, "");
        Utils.setPrefString(getApplicationContext(), Key.UserProfile, "");

        //Facebook
        LoginManager.getInstance().logOut();

        FragmentSignIn fragmentSignIn = (FragmentSignIn) getFragmentForTag(FragmentSignIn.class.getCanonicalName());
        if (fragmentSignIn == null) {
            fragmentSignIn = FragmentSignIn.newInstance(showToolbar, AppData.sharedInstance().mAppInfo.data.name);
        }
        showFragment(fragmentSignIn, FragmentSignIn.class.getCanonicalName(), true);
    }

    void showSignInEmailScreen() {
        FragmentSignInEmail fragmentSignInEmail = (FragmentSignInEmail) getFragmentForTag(FragmentSignInEmail.class.getCanonicalName());
        if (fragmentSignInEmail == null) {
            fragmentSignInEmail = FragmentSignInEmail.newInstance(null);
        }
        showFragment(fragmentSignInEmail, FragmentSignIn.class.getCanonicalName(), true);
    }

    void showSignUpScreen() {
        FragmentSignUp fragmentSignUp = (FragmentSignUp) getFragmentForTag(FragmentSignUp.class.getCanonicalName());
        if (fragmentSignUp == null) {
            fragmentSignUp = FragmentSignUp.newInstance(null);
        }
        showFragment(fragmentSignUp, FragmentSignUp.class.getCanonicalName(), true);
    }

    void showSignUpNextScreen(Serializable extras) {
        FragmentSignUpNext fragmentSignUpNext = (FragmentSignUpNext) getFragmentForTag(FragmentSignUpNext.class.getCanonicalName());
        if (fragmentSignUpNext == null) {
            fragmentSignUpNext = FragmentSignUpNext.newInstance(extras);

        }
        showFragment(fragmentSignUpNext, FragmentSignUpNext.class.getCanonicalName(), true);
    }

    void showMenuScreen(int storeId) {
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            RestaurantFragmentMenuContainer fragmentMenu = (RestaurantFragmentMenuContainer) getFragmentForTag(RestaurantFragmentMenuContainer.class.getCanonicalName());
            if (fragmentMenu == null) {
                AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.data.getSideMenu(AbstractFragment.MENU_SCREEN);
                fragmentMenu = RestaurantFragmentMenuContainer.newInstance(menu.name, storeId);
            }
            showFragment(fragmentMenu, RestaurantFragmentMenuContainer.class.getCanonicalName(), true);

        } else {
            FragmentMenu fragmentMenu = (FragmentMenu) getFragmentForTag(FragmentMenu.class.getCanonicalName());
            if (fragmentMenu == null) {
                AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.data.getSideMenu(AbstractFragment.MENU_SCREEN);
                fragmentMenu = FragmentMenu.newInstance(menu.name, storeId);
            }
            showFragment(fragmentMenu, FragmentMenu.class.getCanonicalName(), true);
        }
    }


    void showItemDetailScreen(Serializable extras) {
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            RestaurantFragmentItemDetail restaurantFragmentItemDetail = (RestaurantFragmentItemDetail) getFragmentForTag(RestaurantFragmentItemDetail.class.getCanonicalName());
            if (restaurantFragmentItemDetail == null) {
                restaurantFragmentItemDetail = RestaurantFragmentItemDetail.newInstance(extras);
            }
            showFragment(restaurantFragmentItemDetail, RestaurantFragmentItemDetail.class.getCanonicalName(), true);
        } else {
            FragmentProduct fragmentProduct = (FragmentProduct) getFragmentForTag(FragmentProduct.class.getCanonicalName());
            if (fragmentProduct == null) {
                fragmentProduct = FragmentProduct.newInstance(extras);
            }
            showFragment(fragmentProduct, FragmentProduct.class.getCanonicalName(), true);
        }
    }

    void showItemPurchaseScreen(Serializable extras) {
        FragmentPurchase fragmentPurchase = (FragmentPurchase) getFragmentForTag(FragmentPurchase.class.getCanonicalName());
        if (fragmentPurchase == null) {
            fragmentPurchase = FragmentPurchase.newInstance(extras);
        }
        showFragment(fragmentPurchase, FragmentPurchase.class.getCanonicalName(), true);
    }


    void showReserveScreen(Serializable extras) {
        FragmentReserve fragmentReserve = (FragmentReserve) getFragmentForTag(FragmentReserve.class.getCanonicalName());
        if (fragmentReserve != null) {
            //Pop to
//            Bundle b = new Bundle();
//            AppInfo.SideMenu menu = mAppInfo.data.getSideMenu(AbstractFragment.RESERVE_SCREEN);
//            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
//            fragmentReserve.setArguments(b);

        } else {
            fragmentReserve = FragmentReserve.newInstance((TopInfo.Contact) extras);
        }
        showFragment(fragmentReserve, FragmentReserve.class.getCanonicalName(), true);
    }

    void showNewsScreen(int storeId) {
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            RestaurantFragmentNews fragmentNews = (RestaurantFragmentNews) getFragmentForTag(RestaurantFragmentNews.class.getCanonicalName());
            if (fragmentNews == null) {
                AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.data.getSideMenu(AbstractFragment.NEWS_SCREEN);
                fragmentNews = RestaurantFragmentNews.newInstance(menu.name, storeId);

            }
            showFragment(fragmentNews, FragmentNews.class.getCanonicalName(), true);
        } else {
            FragmentNews fragmentNews = (FragmentNews) getFragmentForTag(FragmentNews.class.getCanonicalName());
            if (fragmentNews == null) {
                AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.data.getSideMenu(AbstractFragment.NEWS_SCREEN);
                fragmentNews = FragmentNews.newInstance(menu.name, storeId);

            }
            showFragment(fragmentNews, FragmentNews.class.getCanonicalName(), true);
        }
    }

    void showNewsDetailScreen(Serializable extras) {
        FragmentNewsDetail fragmentNewsDetail = (FragmentNewsDetail) getFragmentForTag(FragmentNewsDetail.class.getCanonicalName());
        if (fragmentNewsDetail == null) {
            fragmentNewsDetail = new FragmentNewsDetail();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentNewsDetail.setArguments(b);
        }
        showFragment(fragmentNewsDetail, FragmentNewsDetail.class.getCanonicalName(), true);
    }

    void showPhotoScreen(int storeId) {
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            RestaurantFragmentPhotoGallery fragmentPhotoGallery = (RestaurantFragmentPhotoGallery) getFragmentForTag(RestaurantFragmentPhotoGallery.class.getCanonicalName());
            if (fragmentPhotoGallery == null) {
                AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.data.getSideMenu(AbstractFragment.PHOTO_SCREEN);
                fragmentPhotoGallery = RestaurantFragmentPhotoGallery.newInstance(menu.name, storeId);

            }
            showFragment(fragmentPhotoGallery, RestaurantFragmentPhotoGallery.class.getCanonicalName(), true);
        } else {
            FragmentPhotoGallery fragmentPhotoGallery = (FragmentPhotoGallery) getFragmentForTag(FragmentPhotoGallery.class.getCanonicalName());
            if (fragmentPhotoGallery == null) {
                AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.data.getSideMenu(AbstractFragment.PHOTO_SCREEN);
                fragmentPhotoGallery = FragmentPhotoGallery.newInstance(menu.name, storeId);

            }
            showFragment(fragmentPhotoGallery, FragmentPhotoGallery.class.getCanonicalName(), true);
        }
    }

    void showPhotoPreviewScreen(Serializable extras) {
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            RestaurantPopupPhotoPreview photoPreview = new RestaurantPopupPhotoPreview(this);
            photoPreview.setData(extras);
            photoPreview.show();
        } else {
            PopupPhotoPreview photoPreview = new PopupPhotoPreview(this);
            photoPreview.setData(extras);
            photoPreview.show();
        }
    }

    void showCouponScreen(int storeId) {
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            RestaurantFragmentCoupon restaurantFragmentCoupon = (RestaurantFragmentCoupon) getFragmentForTag(RestaurantFragmentCoupon.class.getCanonicalName());
            if (restaurantFragmentCoupon == null) {
                AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.data.getSideMenu(AbstractFragment.COUPON_SCREEN);
                restaurantFragmentCoupon = RestaurantFragmentCoupon.newInstance(menu.name, storeId);
            }
            showFragment(restaurantFragmentCoupon, RestaurantFragmentCoupon.class.getCanonicalName(), true);

        } else {
            FragmentCoupon fragmentCoupon = (FragmentCoupon) getFragmentForTag(FragmentCoupon.class.getCanonicalName());
            if (fragmentCoupon == null) {
                AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.data.getSideMenu(AbstractFragment.COUPON_SCREEN);
                fragmentCoupon = FragmentCoupon.newInstance(menu.name, storeId);
            }
            showFragment(fragmentCoupon, FragmentCoupon.class.getCanonicalName(), true);
        }
    }

    void showCouponDetailScreen(Serializable extras) {
        FragmentCouponDetail fragmentCouponDetail = (FragmentCouponDetail) getFragmentForTag(FragmentCouponDetail.class.getCanonicalName());
        if (fragmentCouponDetail == null) {
            fragmentCouponDetail = FragmentCouponDetail.newInstance(extras);
        }
        showFragment(fragmentCouponDetail, FragmentCouponDetail.class.getCanonicalName(), true);
    }

    void showChatScreen(int storeId) {
        FragmentChat fragmentChat = (FragmentChat) getFragmentForTag(FragmentChat.class.getCanonicalName());
        if (fragmentChat == null) {
            AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.data.getSideMenu(AbstractFragment.CHAT_SCREEN);
            fragmentChat = FragmentChat.newInstance(menu.name, storeId);
        }
        showFragment(fragmentChat, FragmentChat.class.getCanonicalName(), true);
    }

    void showSettingScreen(int storeId) {
        FragmentSetting fragmentSetting = (FragmentSetting) getFragmentForTag(FragmentSetting.class.getCanonicalName());
        if (fragmentSetting == null) {
            AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.data.getSideMenu(AbstractFragment.SETTING_SCREEN);
            fragmentSetting = FragmentSetting.newInstance(menu.name, storeId);
        }
        showFragment(fragmentSetting, FragmentSetting.class.getCanonicalName(), true);
    }

    void showProfileScreen() {
        FragmentEditProfile fragmentEditProfile = (FragmentEditProfile) getFragmentForTag(FragmentEditProfile.class.getCanonicalName());
        if (fragmentEditProfile == null) {
            fragmentEditProfile = FragmentEditProfile.newInstance(AppData.sharedInstance().mStoreId);
        }
        showFragment(fragmentEditProfile, FragmentEditProfile.class.getCanonicalName(), true);
    }

    void showChangeDeviceScreen() {
        FragmentChangeDevice fragmentChangeDevice = (FragmentChangeDevice) getFragmentForTag(FragmentChangeDevice.class.getCanonicalName());
        if (fragmentChangeDevice == null) {
            fragmentChangeDevice = FragmentChangeDevice.newInstance(null);
        }
        showFragment(fragmentChangeDevice, FragmentChangeDevice.class.getCanonicalName(), true);
    }

    void showCompanyInfo() {
        FragmentCompanyInfo fragmentCompanyInfo = (FragmentCompanyInfo) getFragmentForTag(FragmentCompanyInfo.class.getCanonicalName());
        if (fragmentCompanyInfo == null) {
            fragmentCompanyInfo = FragmentCompanyInfo.newInstance(this.getAppInfo().app_setting.company_info);
        }
        showFragment(fragmentCompanyInfo, FragmentCompanyInfo.class.getCanonicalName(), true);
    }

    void showUserPrivacy() {
        FragmentUserPrivacy fragmentUserPrivacy = (FragmentUserPrivacy) getFragmentForTag(FragmentUserPrivacy.class.getCanonicalName());
        if (fragmentUserPrivacy == null) {
            fragmentUserPrivacy = FragmentUserPrivacy.newInstance(this.getAppInfo().app_setting.user_privacy);
        }
        showFragment(fragmentUserPrivacy, FragmentUserPrivacy.class.getCanonicalName(), true);
    }

    void showStaffScreen(int storeId) {
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            RestaurantFragmentStaff fragmentStaff = (RestaurantFragmentStaff) getFragmentForTag(RestaurantFragmentStaff.class.getCanonicalName());
            if (fragmentStaff == null) {
                AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.data.getSideMenu(AbstractFragment.STAFF_SCREEN);
                fragmentStaff = RestaurantFragmentStaff.newInstance(menu.name, storeId);
            }

            showFragment(fragmentStaff, FragmentStaff.class.getCanonicalName(), true);
        } else {
            FragmentStaff fragmentStaff = (FragmentStaff) getFragmentForTag(FragmentStaff.class.getCanonicalName());
            if (fragmentStaff == null) {
                AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.data.getSideMenu(AbstractFragment.STAFF_SCREEN);
                fragmentStaff = FragmentStaff.newInstance(menu.name, storeId);
            }

            showFragment(fragmentStaff, FragmentStaff.class.getCanonicalName(), true);
        }
    }

    void showStaffDetailScreen(Serializable extras) {
        FragmentStaffDetail fragmentStaffDetail = (FragmentStaffDetail) getFragmentForTag(FragmentStaffDetail.class.getCanonicalName());
        if (fragmentStaffDetail == null) {
            fragmentStaffDetail = FragmentStaffDetail.newInstance(extras);
        }

        showFragment(fragmentStaffDetail, FragmentStaffDetail.class.getCanonicalName(), true);
    }

    void showMyPageScreen() {
        FragmentMyPage fragmentMyPage = (FragmentMyPage) getFragmentForTag(FragmentMyPage.class.getCanonicalName());
        if (fragmentMyPage == null) {
            fragmentMyPage = FragmentMyPage.newInstance(null);
        }

        showFragment(fragmentMyPage, FragmentMyPage.class.getCanonicalName(), true);
    }

    int wifiEnable() {
        int ret = 0;
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            // wifi is enabled
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = null;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            } else {
                Network[] networks = connManager.getAllNetworks();
                for (Network network : networks) {
                    NetworkInfo networkInfo = connManager.getNetworkInfo(network);
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        wifi = networkInfo;
                        break;
                    }
                }
            }

            if (wifi != null && wifi.isConnected()) {
                ret = 1;
            }
        } else {
            ret = -1;
        }
        return ret;
    }

    // 0 : not enable
    // 1 : enable
    int mobileNetworkEnable() {
        int ret = 0;
        try {
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile = null;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            } else {
                Network[] networks = connManager.getAllNetworks();
                for (Network network : networks) {
                    NetworkInfo networkInfo = connManager.getNetworkInfo(network);
                    if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        mobile = networkInfo;
                        break;
                    }
                }
            }

            if (mobile != null && mobile.isConnected()) {
                ret = 1;
            }
        } catch (Exception ignored) {
        }
        return ret;
    }


    void checkNetworkConnection() {
        int wifiState = wifiEnable();
        int mobileState = mobileNetworkEnable();

        if (wifiState == 1 || mobileState == 1) {
            startup();

        } else {
            Utils.hideProgress();
            Utils.showAlert(this,
                    getString(R.string.info),
                    getString(R.string.msg_connection_required),
                    getString(R.string.setting), getString(R.string.exit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE: {
                                    showWifiSettings();
                                }
                                break;
                                case DialogInterface.BUTTON_NEGATIVE: {
                                    exitActivity();
                                }
                                break;
                            }
                        }
                    }
            );
        }
    }

    void showWifiSettings() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivityForResult(intent, AbstractFragment.WIFI_SETTINGS);
    }

    void startup() {
        //Facebook initialize
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getApplication());
        mCallbackManager = CallbackManager.Factory.create();

        //Twitter initialize
        TwitterAuthConfig authConfig = new TwitterAuthConfig(SocialSigninInfo.TWITTER_CONSUMER_KEY,
                SocialSigninInfo.TWITTER_CONSUMER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig));

        //Firebase initialize
//        if (!FirebaseApp.getApps(this).isEmpty())
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        FirebaseMessaging.getInstance().subscribeToTopic("DEMO");
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        FirebaseMessaging.getInstance().subscribeToTopic("test");
//        String token = FirebaseInstanceId.getInstance().getToken();
//        if (token != null) {
//            Utils.setPrefString(getApplicationContext(), Key.FireBaseTokenKey, token);
//        } else {
//            Utils.setPrefString(getApplicationContext(), Key.FireBaseTokenKey, "");
//        }

        this.mFragmentHome = (FragmentHome) getFragmentForTag(FragmentHome.class.getCanonicalName());
        if (this.mFragmentHome == null) {
            loadAppInfo();
        } else {
            showHomeScreen();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AbstractFragment.WIFI_SETTINGS) {
            checkNetworkConnection();

        } else if (requestCode == AbstractFragment.CAPTURE_IMAGE_REQUEST ||
                requestCode == AbstractFragment.CAPTURE_IMAGE_INTENT) {
            FragmentEditProfile fragmentEditProfile = (FragmentEditProfile) getFragmentForTag(FragmentEditProfile.class.getCanonicalName());
            if (fragmentEditProfile != null) {
                fragmentEditProfile.onActivityResult(requestCode, resultCode, data);
            }

        } else {
            Fragment fragment = getFragmentForTag(FragmentSignIn.class.getCanonicalName());
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
            Fragment fragmentEditProfile = getFragmentForTag(FragmentEditProfile.class.getCanonicalName());
            if (fragmentEditProfile != null) {
                fragmentEditProfile.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    protected void errorWithMessage(Bundle response, String message) {
        Utils.hideProgress();
        if (response != null) {
            String msg = response.getString(Key.ResponseMessage);
            Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    void performSignOut() {
        Utils.showAlert(this,
                getString(R.string.info),
                getString(R.string.msg_sign_out_confirm),
                getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: {
                                SignOutCommunicator communicator = new SignOutCommunicator(
                                        new TenpossCommunicator.TenpossCommunicatorListener() {
                                            @Override
                                            public void completed(TenpossCommunicator request, Bundle responseParams) {
                                                Utils.hideProgress();
                                                int result = responseParams.getInt(Key.ResponseResult);
                                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                                    if (resultApi == CommonResponse.ResultSuccess ||
                                                            resultApi == CommonResponse.ResultErrorInvalidToken) {
                                                        //clear token and user profile
                                                        setSessionValue(Key.PushSettings, null);
                                                        Utils.setPrefString(getApplicationContext(), Key.TokenKey, "");
                                                        Utils.setPrefString(getApplicationContext(), Key.UserProfile, "");

                                                        serviceNotStart = true;
                                                        //TODO stop service

                                                        try {
                                                            //Facebook
                                                            LoginManager.getInstance().logOut();
                                                        } catch (Exception ex) {
                                                            ex.printStackTrace();
                                                        }

                                                        try {
                                                            //Twitter
                                                            CookieManager cookieManager = CookieManager.getInstance();
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                cookieManager.removeSessionCookies(null);
                                                            } else {
                                                                CookieSyncManager.createInstance(MainActivity.this);
                                                                cookieManager.removeSessionCookie();
                                                            }
                                                            Twitter.getSessionManager().clearActiveSession();
                                                            Twitter.logOut();
                                                        } catch (Exception ex) {
                                                            ex.printStackTrace();
                                                        }

                                                        updateUserInfo(null);

                                                        showHomeScreen();
                                                    } else {
                                                        String strMessage = responseParams.getString(Key.ResponseMessage);
                                                        errorWithMessage(responseParams, strMessage);
                                                    }
                                                } else {
                                                    String strMessage = responseParams.getString(Key.ResponseMessage);
                                                    errorWithMessage(responseParams, strMessage);
                                                }
                                            }
                                        });
                                Bundle params = new Bundle();
                                SignOutInfo.Request request = new SignOutInfo.Request();
                                request.token = Utils.getPrefString(getApplicationContext(), Key.TokenKey);
                                params.putSerializable(Key.RequestObject, request);
                                Utils.showProgress(MainActivity.this, getString(R.string.msg_signing_out));
                                communicator.execute(params);
                            }
                            break;

                            case DialogInterface.BUTTON_NEGATIVE: {
                                //Do nothing
                            }
                            break;
                        }
                    }
                });
    }

    void loadAppInfo() {
        Utils.showProgress(MainActivity.this, getString(R.string.msg_starting_up));
        Bundle params = new Bundle();
        AppInfo.Request requestParams = new AppInfo.Request();

        params.putSerializable(Key.RequestObject, requestParams);
        AppInfoCommunicator communicator = new AppInfoCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                AppData.sharedInstance().mAppInfo = (AppInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                if (AppData.sharedInstance().mAppInfo.data != null && AppData.sharedInstance().mAppInfo.data.stores.size() > 0) {
                                    AppData.sharedInstance().mStoreId = AppData.sharedInstance().mAppInfo.data.stores.get(0).id;
                                    getUserDetail();
                                } else {
                                    String strMessage = "Invalid response data!";
                                    errorWithMessage(responseParams, strMessage);
                                }
                            } else {
                                String strMessage = responseParams.getString(Key.ResponseMessage);
                                errorWithMessage(responseParams, strMessage);
                            }
                        } else if (result == TenpossCommunicator.CommunicationCode.ConnectionTimedOut.ordinal()) {
                            Utils.showAlert(MainActivity.this,
                                    getString(R.string.warning),
                                    getString(R.string.msg_connection_timedout_try_again),
                                    getString(R.string.retry),
                                    getString(R.string.exit),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE: {
                                                    loadAppInfo();
                                                }
                                                break;

                                                case DialogInterface.BUTTON_NEGATIVE: {
                                                    exitActivity();
                                                }
                                                break;
                                            }
                                        }
                                    }
                            );

                        } else {
                            showSignInScreen(false);
                        }
                    }
                });
        communicator.execute(params);
    }

    public void getUserDetail() {
        String token = Utils.getPrefString(getApplicationContext(), Key.TokenKey);
        if (token.length() > 0) {
            Bundle params = new Bundle();
            UserInfo.Request request = new UserInfo.Request();
            request.token = token;
            params.putSerializable(Key.RequestObject, request);

            UserInfoCommunicator communicator = new UserInfoCommunicator(
                    new TenpossCommunicator.TenpossCommunicatorListener() {
                        @Override
                        public void completed(TenpossCommunicator request, Bundle responseParams) {
                            Utils.hideProgress();
                            int result = responseParams.getInt(Key.ResponseResult);
                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                if (resultApi == CommonResponse.ResultSuccess) {
                                    //Update User profile

                                    UserInfo.Response data = (UserInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                    Utils.setPrefString(getApplicationContext(), Key.UserProfile, CommonObject.toJSONString(data.data.user, SignInInfo.User.class));

                                    showHomeScreen();

                                } else {
                                    showSignInScreen(false);
                                }
                            } else if (result == TenpossCommunicator.CommunicationCode.ConnectionTimedOut.ordinal()) {
                                Utils.showAlert(MainActivity.this,
                                        getString(R.string.warning),
                                        getString(R.string.msg_connection_timedout_try_again),
                                        getString(R.string.retry),
                                        getString(R.string.exit),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE: {
                                                        getUserDetail();
                                                    }
                                                    break;

                                                    case DialogInterface.BUTTON_NEGATIVE: {
                                                        exitActivity();
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                );

                            } else {
                                showSignInScreen(false);
                            }
                        }
                    });
            communicator.execute(params);
        } else {
            Utils.hideProgress();
            showSignInScreen(false);
        }
    }


    public boolean isSignedIn() {
        String token = Utils.getPrefString(getApplicationContext(), Key.TokenKey);
        String userProfile = Utils.getPrefString(getApplicationContext(), Key.UserProfile);
        if (token.length() > 0 && userProfile.length() > 0) {
            return true;
        } else {
            return false;
        }
    }


    void startAlarmShareApp() {
        if (isSignedIn() == true) {
            Random r = new Random();
            int i1 = r.nextInt(80 - 65) + 10;

            Log.i("MainActivity", "Will show Share popup after " + i1 + " secods");

            mTimerShowShareApp = MyTimer.timerWithTimeInterval(i1, false, new MyTimerFireListener() {
                @Override
                protected void timerFire(MyTimer timer) {
                    AbstractFragment topFragment = getTopFragment();
                    if (topFragment != null) {
                        mTimerShowShareApp.invalidate();
                        mTimerShowShareApp = null;
                        if (topFragment.showShareAppPopup() == false) {
                            startAlarmShareApp();
                        }
                    }
                }
            });
            mTimerShowShareApp.scheduleNow(i1 * 1000);
        }
    }
}
