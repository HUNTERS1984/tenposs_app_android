package jp.tenposs.tenposs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import io.fabric.sdk.android.Fabric;
import jp.tenposs.communicator.AppInfoCommunicator;
import jp.tenposs.communicator.RefreshTokenCommunicator;
import jp.tenposs.communicator.SetPushKeyCommunicator;
import jp.tenposs.communicator.SignOutCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.communicator.UserInfoCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.RefreshTokenInfo;
import jp.tenposs.datamodel.SetPushKeyInfo;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.datamodel.SocialSignInInfo;
import jp.tenposs.datamodel.UserInfo;
import jp.tenposs.utils.MyTimer;
import jp.tenposs.utils.MyTimerFireListener;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.LeftMenuView;

public class MainActivity extends AppCompatActivity
        implements
        AbstractFragment.MainActivityListener,
        LeftMenuView.OnLeftMenuItemClickListener {

    static final String FIRST_FRAGMENT = "FIRST_FRAGMENT";
    static final String ALARM_STATUS = "ALARM_STATUS";
    static final String SESSION_VALUES = "SESSION_VALUES";
    public static final int CALL_PHONE_REQUEST = 61284;
    FragmentManager mFragmentManager;
    AbstractFragment mFragmentFirst;

    DrawerLayout mDrawerLayout;
    LeftMenuView mLeftMenuView;
    FrameLayout mContentContainer;

    CallbackManager mCallbackManager;

    protected HashMap<String, String> mSessionValues;

    private int alarmStatus = -1;
    MyTimer mTimerShowShareApp = null;
    private String TAG = "MainActivity";

    private void onLoadSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(ALARM_STATUS)) {
            this.alarmStatus = savedInstanceState.getInt(ALARM_STATUS);
        }
        if (savedInstanceState.containsKey(SESSION_VALUES)) {
            this.mSessionValues = (HashMap<String, String>) savedInstanceState.getSerializable(SESSION_VALUES);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ALARM_STATUS, alarmStatus);
        if (this.mSessionValues != null) {
            outState.putSerializable(SESSION_VALUES, this.mSessionValues);
        }
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

        if (savedInstanceState != null) {
            onLoadSavedInstanceState(savedInstanceState);
        }

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
                } else if (topFragment instanceof FragmentSignIn && mFragmentFirst == null) {
                    mFragmentManager.popBackStackImmediate();
                    showFirstFragment();

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

    public void updateAppInfo(AppInfo.Response appInfo, int storeId) {
        AppData.sharedInstance().mAppInfo = appInfo;
        AppData.sharedInstance().mStoreId = storeId;

//        String userProfileStr = Utils.getPrefString(getApplicationContext(), Key.UserProfile);
//        SignInInfo.User userProfile = null;
//        if (userProfileStr.length() > 0) {
//            try {
//                userProfile = (SignInInfo.User) CommonObject.fromJSONString(userProfileStr, SignInInfo.User.class, null);
//            } catch (Exception ignored) {
//            }
//        }
        mLeftMenuView.updateMenu(AppData.sharedInstance().mAppInfo.getAppSetting(), AppData.sharedInstance().mAppInfo.getSideMenu(), null);
    }

    @Override
    public void updateUserInfo(UserInfo.User userProfile) {
        startAlarmShareApp();
        if (userProfile == null) {
            setSessionValue(Key.PushSettings, null);
            Utils.setPrefString(MainApplication.getContext(), Key.TokenKey, "");
            Utils.setPrefString(MainApplication.getContext(), Key.UserProfile, "");
        }
        if (mLeftMenuView != null) {
            mLeftMenuView.updateMenu(AppData.sharedInstance().mAppInfo.getAppSetting(), AppData.sharedInstance().mAppInfo.getSideMenu(), userProfile);
        }
    }

    @Override
    public AbstractFragment showScreen(int screenId, Serializable extras, String fragmentTag, boolean showFromSideMenu) {
        switch (screenId) {
            //2
            case AbstractFragment.MENU_SCREEN: {
                return showMenuScreen(AppData.sharedInstance().mStoreId, fragmentTag, showFromSideMenu);
            }

            case AbstractFragment.ITEM_SCREEN: {
                return showItemDetailScreen(extras);
            }

            case AbstractFragment.ITEM_PURCHASE_SCREEN: {
                return showItemPurchaseScreen(extras);
            }

            //3
            case AbstractFragment.NEWS_SCREEN: {
                return showNewsScreen(AppData.sharedInstance().mStoreId, fragmentTag, showFromSideMenu);
            }

            case AbstractFragment.NEWS_DETAILS_SCREEN: {
                return showNewsDetailScreen(extras);
            }

            //4
            case AbstractFragment.RESERVE_SCREEN: {
                return showReserveScreen(extras, fragmentTag, showFromSideMenu);
            }

            //5
            case AbstractFragment.PHOTO_SCREEN: {
                return showPhotoScreen(AppData.sharedInstance().mStoreId, fragmentTag, showFromSideMenu);
            }

            case AbstractFragment.PHOTO_ITEM_SCREEN: {
                showPhotoPreviewScreen(extras);
                return null;
            }

            case AbstractFragment.TOP_SCREEN: {
                return showTopScreen(fragmentTag, showFromSideMenu);
            }

            case AbstractFragment.CHAT_SCREEN: {
                return showChatScreen(AppData.sharedInstance().mStoreId, fragmentTag, showFromSideMenu);
            }

            case AbstractFragment.COUPON_SCREEN: {
                return showCouponScreen(AppData.sharedInstance().mStoreId, fragmentTag, showFromSideMenu);
            }

            case AbstractFragment.COUPON_DETAIL_SCREEN: {
                return showCouponDetailScreen(extras);
            }

            case AbstractFragment.SETTING_SCREEN: {
                return showSettingScreen(AppData.sharedInstance().mStoreId, fragmentTag, showFromSideMenu);
            }

            case AbstractFragment.PROFILE_SCREEN: {
                return showProfileScreen(showFromSideMenu);
            }

            case AbstractFragment.CHANGE_DEVICE_SCREEN: {
                return showChangeDeviceScreen();
            }

            case AbstractFragment.COMPANY_INFO_SCREEN: {
                return showCompanyInfo();
            }

            case AbstractFragment.USER_PRIVACY_SCREEN: {
                return showUserPrivacy();
            }

            case AbstractFragment.SIGN_IN_SCREEN: {
                return showSignInScreen(true);
            }

            case AbstractFragment.SIGN_IN_EMAIL_SCREEN: {
                return showSignInEmailScreen();
            }

            case AbstractFragment.SIGN_UP_SCREEN: {
                return showSignUpScreen();
            }

            case AbstractFragment.SIGN_UP_NEXT_SCREEN: {
                return showSignUpNextScreen(extras);
            }

            case AbstractFragment.SIGN_OUT_SCREEN: {
                performSignOut();
                return null;
            }

            case AbstractFragment.STAFF_SCREEN: {
                return showStaffScreen(AppData.sharedInstance().mStoreId, fragmentTag, showFromSideMenu);
            }

            case AbstractFragment.STAFF_DETAIL_SCREEN: {
                return showStaffDetailScreen(extras);
            }

            case AbstractFragment.MY_PAGE_SCREEN: {
                return showMyPageScreen(showFromSideMenu);
            }

            default: {
                Assert.assertFalse("Should never be here ;(", false);
                return null;
            }
        }
    }


    @Override
    public AppInfo.Response.ApplicationInfo getAppInfo() {
        return AppData.sharedInstance().mAppInfo.getAppInfo();
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
            showScreen(screenId, null, null, true);

            String token = FirebaseInstanceId.getInstance().getToken();
            if (token != null) {
                Utils.log(TAG, token);
                Utils.setPrefString(getApplicationContext(), Key.FireBaseTokenKey, token);
            } else {
                Utils.setPrefString(getApplicationContext(), Key.FireBaseTokenKey, "");
            }
        } else {
            AppInfo.SideMenu menuItem = (AppInfo.SideMenu) params.getSerializable(Key.RequestObject);
            int screenId = menuItem.id;
            String fragmentTag = null;
            AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.getAppInfo().getSideMenuAtIndex(0);
            if (screenId == menu.id) {
                fragmentTag = FIRST_FRAGMENT;
            }
            showScreen(screenId, null, fragmentTag, true);
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

    @Override
    public void showFirstFragment() {
        startAlarmShareApp();

        if (this.mFragmentFirst == null) {
            AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.getAppInfo().getSideMenuAtIndex(0);

            if(isSignedIn() == false &&
                    (menu.id == AbstractFragment.CHAT_SCREEN ||
                            menu.id == AbstractFragment.SETTING_SCREEN )){
                this.mFragmentFirst = showScreen(AbstractFragment.TOP_SCREEN, null, null, true);
            }else {
                this.mFragmentFirst = showScreen(menu.id, null, FIRST_FRAGMENT, true);
            }
        } else {
            showFragment(this.mFragmentFirst, FIRST_FRAGMENT, false);
        }
    }

    @Override
    public void stopServices() {
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
    }

    @Override
    public void logoutBecauseExpired() {
        stopServices();
        updateUserInfo(null);
        showFirstFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            //showAlert("Please select 'Allow' button in order to use this feature.", "OK", null, AlertTag.AlertCommon.ordinal());
        } else {
            if (requestCode == CALL_PHONE_REQUEST) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Utils.getPrefString(this, Key.PHONE_NUMBER)));
                this.startActivity(intent);
            }
        }
    }

    AbstractFragment showTopScreen(String fragmentTag, boolean showFromSideMenu) {
        if (fragmentTag == null) {
            fragmentTag = FragmentTop.class.getCanonicalName() + "_" + showFromSideMenu;
        }
        FragmentTop fragmentTop = (FragmentTop) getFragmentForTag(fragmentTag);
        if (fragmentTop == null) {
            fragmentTop = new FragmentTop();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.APP_DATA, AppData.sharedInstance().mAppInfo);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, AppData.sharedInstance().mStoreId);
            b.putBoolean(AbstractFragment.SCREEN_DATA_SHOW_FROM_SIDE_MENU, showFromSideMenu);
            fragmentTop.setArguments(b);
        }
        showFragment(fragmentTop, fragmentTag, false);
        return fragmentTop;
    }

    AbstractFragment showSignInScreen(boolean showToolbar) {
        //clear token and user profile
        //Local
        Utils.setPrefString(getApplicationContext(), Key.TokenKey, "");
        Utils.setPrefString(getApplicationContext(), Key.UserProfile, "");

        //Facebook
        LoginManager.getInstance().logOut();

        FragmentSignIn fragmentSignIn = (FragmentSignIn) getFragmentForTag(FragmentSignIn.class.getCanonicalName());
        if (fragmentSignIn == null) {
            fragmentSignIn = new FragmentSignIn();
            Bundle bundle = new Bundle();
            bundle.putBoolean(AbstractFragment.SCREEN_TYPE, showToolbar);
            bundle.putString(AbstractFragment.SCREEN_TITLE, AppData.sharedInstance().mAppInfo.getAppSetting().getTitle());
            fragmentSignIn.setArguments(bundle);
        }
        showFragment(fragmentSignIn, FragmentSignIn.class.getCanonicalName(), true);
        return fragmentSignIn;
    }

    AbstractFragment showSignInEmailScreen() {
        FragmentSignInEmail fragmentSignInEmail = (FragmentSignInEmail) getFragmentForTag(FragmentSignInEmail.class.getCanonicalName());
        if (fragmentSignInEmail == null) {
            fragmentSignInEmail = new FragmentSignInEmail();
        }
        showFragment(fragmentSignInEmail, FragmentSignIn.class.getCanonicalName(), true);
        return fragmentSignInEmail;
    }

    AbstractFragment showSignUpScreen() {
        FragmentSignUp fragmentSignUp = (FragmentSignUp) getFragmentForTag(FragmentSignUp.class.getCanonicalName());
        if (fragmentSignUp == null) {
            fragmentSignUp = new FragmentSignUp();
        }
        showFragment(fragmentSignUp, FragmentSignUp.class.getCanonicalName(), true);
        return fragmentSignUp;
    }

    AbstractFragment showSignUpNextScreen(Serializable extras) {
        FragmentSignUpNext fragmentSignUpNext = (FragmentSignUpNext) getFragmentForTag(FragmentSignUpNext.class.getCanonicalName());
        if (fragmentSignUpNext == null) {
            fragmentSignUpNext = new FragmentSignUpNext();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentSignUpNext.setArguments(b);
        }
        showFragment(fragmentSignUpNext, FragmentSignUpNext.class.getCanonicalName(), true);
        return fragmentSignUpNext;
    }

    AbstractFragment showMenuScreen(int storeId, String fragmentTag, boolean showFromSideMenu) {
        AbstractFragment fragmentMenu;
        AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.getSideMenuById(AbstractFragment.MENU_SCREEN);
        Bundle b = new Bundle();
        if (menu != null && menu.name != null) {
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
        } else {
            b.putString(AbstractFragment.SCREEN_TITLE, getString(R.string.menu));
        }

        b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
        b.putBoolean(AbstractFragment.SCREEN_DATA_SHOW_FROM_SIDE_MENU, showFromSideMenu);

        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            if (fragmentTag == null) {
                fragmentTag = RestaurantFragmentMenuContainer.class.getCanonicalName() + "_" + showFromSideMenu;
            }
            fragmentMenu = (RestaurantFragmentMenuContainer) getFragmentForTag(fragmentTag);
            if (fragmentMenu == null) {
                fragmentMenu = new RestaurantFragmentMenuContainer();
                fragmentMenu.setArguments(b);
            }
        } else {
            if (fragmentTag == null) {
                fragmentTag = FragmentMenu.class.getCanonicalName() + "_" + showFromSideMenu;
            }
            fragmentMenu = (FragmentMenu) getFragmentForTag(fragmentTag);
            if (fragmentMenu == null) {
                fragmentMenu = new FragmentMenu();
                fragmentMenu.setArguments(b);
            }
        }

        showFragment(fragmentMenu, fragmentTag, true);
        return fragmentMenu;
    }

    AbstractFragment showItemDetailScreen(Serializable extras) {
        AbstractFragment fragmentMenuItem;
        String fragmentTag;
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            fragmentMenuItem = (RestaurantFragmentItemDetail) getFragmentForTag(RestaurantFragmentItemDetail.class.getCanonicalName());
            if (fragmentMenuItem == null) {
                fragmentMenuItem = RestaurantFragmentItemDetail.newInstance(extras);
            }
            fragmentTag = RestaurantFragmentItemDetail.class.getCanonicalName();

        } else {
            fragmentMenuItem = (FragmentMenuItem) getFragmentForTag(FragmentMenuItem.class.getCanonicalName());
            if (fragmentMenuItem == null) {
                fragmentMenuItem = FragmentMenuItem.newInstance(extras);
            }
            fragmentTag = FragmentMenuItem.class.getCanonicalName();
        }
        showFragment(fragmentMenuItem, fragmentTag, true);
        return fragmentMenuItem;
    }

    AbstractFragment showItemPurchaseScreen(Serializable extras) {
        FragmentPurchase fragmentPurchase = (FragmentPurchase) getFragmentForTag(FragmentPurchase.class.getCanonicalName());
        if (fragmentPurchase == null) {
            fragmentPurchase = new FragmentPurchase();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentPurchase.setArguments(b);
        }
        showFragment(fragmentPurchase, FragmentPurchase.class.getCanonicalName(), true);
        return fragmentPurchase;
    }

    AbstractFragment showReserveScreen(Serializable extras, String fragmentTag, boolean showFromSideMenu) {
        if (fragmentTag == null) {
            fragmentTag = FragmentReserve.class.getCanonicalName() + "_" + showFromSideMenu;
        }
        FragmentReserve fragmentReserve = (FragmentReserve) getFragmentForTag(fragmentTag);
        if (fragmentReserve == null) {
            fragmentReserve = new FragmentReserve();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.STORE_INFO, extras);
            b.putBoolean(AbstractFragment.SCREEN_DATA_SHOW_FROM_SIDE_MENU, showFromSideMenu);
            fragmentReserve.setArguments(b);
        }
        showFragment(fragmentReserve, fragmentTag, true);
        return fragmentReserve;
    }

    AbstractFragment showNewsScreen(int storeId, String fragmentTag, boolean showFromSideMenu) {
        AbstractFragment fragmentNews;
        AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.getSideMenuById(AbstractFragment.NEWS_SCREEN);
        Bundle b = new Bundle();
        if (menu != null && menu.name != null) {
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
        } else {
            b.putString(AbstractFragment.SCREEN_TITLE, getString(R.string.news));
        }
        b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
        b.putBoolean(AbstractFragment.SCREEN_DATA_SHOW_FROM_SIDE_MENU, showFromSideMenu);
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            if (fragmentTag == null) {
                fragmentTag = RestaurantFragmentNews.class.getCanonicalName() + "_" + showFromSideMenu;
            }
            fragmentNews = (RestaurantFragmentNews) getFragmentForTag(fragmentTag);
            if (fragmentNews == null) {
                fragmentNews = new RestaurantFragmentNews();
                fragmentNews.setArguments(b);
            }
        } else {
            if (fragmentTag == null) {
                fragmentTag = FragmentNews.class.getCanonicalName() + "_" + showFromSideMenu;
            }
            fragmentNews = (FragmentNews) getFragmentForTag(fragmentTag);
            if (fragmentNews == null) {
                fragmentNews = new FragmentNews();
                fragmentNews.setArguments(b);
            }
        }
        showFragment(fragmentNews, fragmentTag, true);
        return fragmentNews;
    }

    AbstractFragment showNewsDetailScreen(Serializable extras) {
        FragmentNewsDetail fragmentNewsDetail = (FragmentNewsDetail) getFragmentForTag(FragmentNewsDetail.class.getCanonicalName());
        if (fragmentNewsDetail == null) {
            fragmentNewsDetail = new FragmentNewsDetail();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentNewsDetail.setArguments(b);
        }
        showFragment(fragmentNewsDetail, FragmentNewsDetail.class.getCanonicalName(), true);
        return fragmentNewsDetail;
    }

    AbstractFragment showPhotoScreen(int storeId, String fragmentTag, boolean showFromSideMenu) {
        AbstractFragment fragmentPhotoGallery;
        AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.getSideMenuById(AbstractFragment.PHOTO_SCREEN);
        Bundle b = new Bundle();

        if (menu != null && menu.name != null) {
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
        } else {
            b.putString(AbstractFragment.SCREEN_TITLE, getString(R.string.photo_gallery));
        }
        b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
        b.putBoolean(AbstractFragment.SCREEN_DATA_SHOW_FROM_SIDE_MENU, showFromSideMenu);
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            if (fragmentTag == null) {
                fragmentTag = RestaurantFragmentPhotoGallery.class.getCanonicalName() + "_" + showFromSideMenu;
            }
            fragmentPhotoGallery = (RestaurantFragmentPhotoGallery) getFragmentForTag(fragmentTag);
            if (fragmentPhotoGallery == null) {
                fragmentPhotoGallery = new RestaurantFragmentPhotoGallery();
                fragmentPhotoGallery.setArguments(b);
            }
        } else {
            if (fragmentTag == null) {
                fragmentTag = FragmentPhotoGallery.class.getCanonicalName() + "_" + showFromSideMenu;
            }
            fragmentPhotoGallery = (FragmentPhotoGallery) getFragmentForTag(fragmentTag);
            if (fragmentPhotoGallery == null) {
                fragmentPhotoGallery = new FragmentPhotoGallery();
                fragmentPhotoGallery.setArguments(b);
            }
        }

        showFragment(fragmentPhotoGallery, fragmentTag, true);
        return fragmentPhotoGallery;
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

    AbstractFragment showCouponScreen(int storeId, String fragmentTag, boolean showFromSideMenu) {
        AbstractFragment fragmentCoupon;
        AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.getSideMenuById(AbstractFragment.COUPON_SCREEN);
        String title;
        if (menu != null && menu.name != null) {
            title = menu.name;
        } else {
            title = getString(R.string.coupon);
        }
        Bundle b = new Bundle();
        b.putString(AbstractFragment.SCREEN_TITLE, title);
        b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
        b.putBoolean(AbstractFragment.SCREEN_DATA_SHOW_FROM_SIDE_MENU, showFromSideMenu);
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            if (fragmentTag == null) {
                fragmentTag = RestaurantFragmentCoupon.class.getCanonicalName() + "_" + showFromSideMenu;
            }
            fragmentCoupon = (RestaurantFragmentCoupon) getFragmentForTag(fragmentTag);
            if (fragmentCoupon == null) {
                fragmentCoupon = new RestaurantFragmentCoupon();
                fragmentCoupon.setArguments(b);
            }
        } else {
            if (fragmentTag == null) {
                fragmentTag = FragmentCoupon.class.getCanonicalName() + "_" + showFromSideMenu;
            }
            fragmentCoupon = (FragmentCoupon) getFragmentForTag(fragmentTag);
            if (fragmentCoupon == null) {
                fragmentCoupon = new FragmentCoupon();
                fragmentCoupon.setArguments(b);
            }
        }
        showFragment(fragmentCoupon, fragmentTag, true);
        return fragmentCoupon;
    }

    AbstractFragment showCouponDetailScreen(Serializable extras) {
        FragmentCouponDetail fragmentCouponDetail = (FragmentCouponDetail) getFragmentForTag(FragmentCouponDetail.class.getCanonicalName());
        if (fragmentCouponDetail == null) {
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentCouponDetail = new FragmentCouponDetail();
            fragmentCouponDetail.setArguments(b);
        }
        showFragment(fragmentCouponDetail, FragmentCouponDetail.class.getCanonicalName(), true);
        return fragmentCouponDetail;
    }

    AbstractFragment showChatScreen(int storeId, String fragmentTag, boolean showFromSideMenu) {
        if (fragmentTag == null) {
            fragmentTag = FragmentChat.class.getCanonicalName() + "_" + showFromSideMenu;
        }
        FragmentChat fragmentChat = (FragmentChat) getFragmentForTag(fragmentTag);
        AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.getSideMenuById(AbstractFragment.CHAT_SCREEN);

        String title;
        if (menu != null && menu.name != null) {
            title = menu.name;
        } else {
            title = getString(R.string.chat);
        }
        if (fragmentChat == null) {
            Bundle b = new Bundle();
            b.putString(AbstractFragment.SCREEN_TITLE, title);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            b.putBoolean(AbstractFragment.SCREEN_DATA_SHOW_FROM_SIDE_MENU, showFromSideMenu);
            fragmentChat = new FragmentChat();
            fragmentChat.setArguments(b);
        }
        showFragment(fragmentChat, fragmentTag, true);
        return fragmentChat;
    }

    AbstractFragment showSettingScreen(int storeId, String fragmentTag, boolean showFromSideMenu) {
        if (fragmentTag == null) {
            fragmentTag = FragmentSetting.class.getCanonicalName() + "_" + showFromSideMenu;
        }
        FragmentSetting fragmentSetting = (FragmentSetting) getFragmentForTag(fragmentTag);
        AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.getSideMenuById(AbstractFragment.SETTING_SCREEN);
        String title;
        if (menu != null && menu.name != null) {
            title = menu.name;
        } else {
            title = getString(R.string.chat);
        }
        if (fragmentSetting == null) {
            Bundle b = new Bundle();
            b.putString(AbstractFragment.SCREEN_TITLE, title);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            b.putBoolean(AbstractFragment.SCREEN_DATA_SHOW_FROM_SIDE_MENU, showFromSideMenu);
            fragmentSetting = new FragmentSetting();
            fragmentSetting.setArguments(b);
        }
        showFragment(fragmentSetting, fragmentTag, true);
        return fragmentSetting;
    }

    AbstractFragment showProfileScreen(boolean showFromSideMenu) {
        FragmentEditProfile fragmentEditProfile = (FragmentEditProfile) getFragmentForTag(FragmentEditProfile.class.getCanonicalName());
        if (fragmentEditProfile == null) {
            Bundle b = new Bundle();
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, AppData.sharedInstance().mStoreId);
            b.putBoolean(AbstractFragment.SCREEN_DATA_SHOW_FROM_SIDE_MENU, showFromSideMenu);
            fragmentEditProfile = new FragmentEditProfile();
            fragmentEditProfile.setArguments(b);
        }
        showFragment(fragmentEditProfile, FragmentEditProfile.class.getCanonicalName() + "_" + showFromSideMenu, true);
        return fragmentEditProfile;
    }

    AbstractFragment showChangeDeviceScreen() {
        FragmentChangeDevice fragmentChangeDevice = (FragmentChangeDevice) getFragmentForTag(FragmentChangeDevice.class.getCanonicalName());
        if (fragmentChangeDevice == null) {
            fragmentChangeDevice = new FragmentChangeDevice();
        }
        showFragment(fragmentChangeDevice, FragmentChangeDevice.class.getCanonicalName(), true);
        return fragmentChangeDevice;
    }

    AbstractFragment showCompanyInfo() {
        FragmentCompanyInfo fragmentCompanyInfo = (FragmentCompanyInfo) getFragmentForTag(FragmentCompanyInfo.class.getCanonicalName());
        if (fragmentCompanyInfo == null) {
            Bundle b = new Bundle();
            b.putString(AbstractFragment.SCREEN_URL, this.getAppInfo().app_setting.company_info);
            fragmentCompanyInfo = new FragmentCompanyInfo();
            fragmentCompanyInfo.setArguments(b);
        }
        showFragment(fragmentCompanyInfo, FragmentCompanyInfo.class.getCanonicalName(), true);
        return fragmentCompanyInfo;
    }

    AbstractFragment showUserPrivacy() {
        FragmentUserPrivacy fragmentUserPrivacy = (FragmentUserPrivacy) getFragmentForTag(FragmentUserPrivacy.class.getCanonicalName());
        if (fragmentUserPrivacy == null) {
            Bundle b = new Bundle();
            b.putString(AbstractFragment.SCREEN_URL, this.getAppInfo().app_setting.user_privacy);
            fragmentUserPrivacy = new FragmentUserPrivacy();//.newInstance(this.getAppInfo().app_setting.user_privacy);
            fragmentUserPrivacy.setArguments(b);
        }
        showFragment(fragmentUserPrivacy, FragmentUserPrivacy.class.getCanonicalName(), true);
        return fragmentUserPrivacy;
    }

    AbstractFragment showStaffScreen(int storeId, String fragmentTag, boolean showFromSideMenu) {
        AbstractFragment fragmentStaff;
        AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.getSideMenuById(AbstractFragment.STAFF_SCREEN);
        String title;
        if (menu != null && menu.name != null) {
            title = menu.name;
        } else {
            title = getString(R.string.chat);
        }
        Bundle b = new Bundle();
        b.putString(AbstractFragment.SCREEN_TITLE, title);
        b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
        b.putBoolean(AbstractFragment.SCREEN_DATA_SHOW_FROM_SIDE_MENU, showFromSideMenu);

        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            if (fragmentTag == null) {
                fragmentTag = RestaurantFragmentStaff.class.getCanonicalName() + "_" + showFromSideMenu;
            }
            fragmentStaff = (RestaurantFragmentStaff) getFragmentForTag(fragmentTag);
            if (fragmentStaff == null) {
                fragmentStaff = new RestaurantFragmentStaff();
                fragmentStaff.setArguments(b);
            }
        } else {
            if (fragmentTag == null) {
                fragmentTag = FragmentStaff.class.getCanonicalName() + "_" + showFromSideMenu;
            }
            fragmentStaff = (FragmentStaff) getFragmentForTag(fragmentTag);
            if (fragmentStaff == null) {
                fragmentStaff = new FragmentStaff();
                fragmentStaff.setArguments(b);
            }
        }
        showFragment(fragmentStaff, fragmentTag, true);
        return fragmentStaff;
    }

    AbstractFragment showStaffDetailScreen(Serializable extras) {
        FragmentStaffDetail fragmentStaffDetail = (FragmentStaffDetail) getFragmentForTag(FragmentStaffDetail.class.getCanonicalName());
        if (fragmentStaffDetail == null) {
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentStaffDetail = new FragmentStaffDetail();
            fragmentStaffDetail.setArguments(b);
        }
        showFragment(fragmentStaffDetail, FragmentStaffDetail.class.getCanonicalName(), true);
        return fragmentStaffDetail;
    }

    AbstractFragment showMyPageScreen(boolean showFromSideMenu) {
        FragmentMyPage fragmentMyPage = (FragmentMyPage) getFragmentForTag(FragmentMyPage.class.getCanonicalName());
        if (fragmentMyPage == null) {
            Bundle b = new Bundle();
            b.putBoolean(AbstractFragment.SCREEN_DATA_SHOW_FROM_SIDE_MENU, showFromSideMenu);
            fragmentMyPage = new FragmentMyPage();
            fragmentMyPage.setArguments(b);
        }
        showFragment(fragmentMyPage, FragmentMyPage.class.getCanonicalName() + "_" + showFromSideMenu, true);
        return fragmentMyPage;
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
        TwitterAuthConfig authConfig = new TwitterAuthConfig(SocialSignInInfo.TWITTER_CONSUMER_KEY,
                SocialSignInInfo.TWITTER_CONSUMER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig));

        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            Utils.log(TAG, token);
            Utils.setPrefString(getApplicationContext(), Key.FireBaseTokenKey, token);
        } else {
            Utils.setPrefString(getApplicationContext(), Key.FireBaseTokenKey, "");
        }

        this.mFragmentFirst = (AbstractFragment) getFragmentForTag(FIRST_FRAGMENT);
        if (this.mFragmentFirst == null) {
            loadAppInfo();
        } else {
            showFirstFragment();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

                                                        showFirstFragment();
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
                                params.putString(Key.TokenKey, Utils.getPrefString(getApplicationContext(), Key.TokenKey));
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

                                ArrayList<AppInfo.Store> stores = AppData.sharedInstance().mAppInfo.getStores();
                                if (stores.size() > 0) {
                                    AppData.sharedInstance().mStoreId = stores.get(0).id;
                                    updateAppInfo(AppData.sharedInstance().mAppInfo, AppData.sharedInstance().mStoreId);
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

    public void refreshTokenThenTryAgain(final AbstractFragment.TenpossCallback callback) {
        RefreshTokenInfo.Request request = new RefreshTokenInfo.Request();
        Bundle params = new Bundle();
        request.access_refresh_token_href = Utils.getPrefString(getApplicationContext(), Key.RefreshTokenHref);
        params.putSerializable(Key.RequestObject, request);
        new RefreshTokenCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                Utils.hideProgress();
                int result = responseParams.getInt(Key.ResponseResult);
                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                    if (resultApi == CommonResponse.ResultSuccess) {
                        SignInInfo.Response response = (SignInInfo.Response) responseParams.get(Key.ResponseObject);
                        Utils.setPrefString(getApplicationContext(), Key.TokenKey, response.data.token);
                        Utils.setPrefString(getApplicationContext(), Key.RefreshToken, response.data.refresh_token);
                        Utils.setPrefString(getApplicationContext(), Key.RefreshTokenHref, response.data.access_refresh_token_href);
                        callback.onSuccess(responseParams);

                    } else {
                        callback.onFailed(responseParams);
                    }
                } else {
                    callback.onFailed(responseParams);
                }
            }
        }).execute(params);
    }

    public void getUserDetail() {
        final String token = Utils.getPrefString(getApplicationContext(), Key.TokenKey);
        if (token.length() > 0) {
            Bundle params = new Bundle();
            UserInfo.Request request = new UserInfo.Request();
            request.token = token;
            params.putSerializable(Key.RequestObject, request);
            params.putString(Key.TokenKey, token);
            UserInfoCommunicator communicator = new UserInfoCommunicator(
                    new TenpossCommunicator.TenpossCommunicatorListener() {
                        @Override
                        public void completed(TenpossCommunicator request, Bundle responseParams) {
                            int result = responseParams.getInt(Key.ResponseResult);
                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                if (resultApi == CommonResponse.ResultSuccess) {
                                    Utils.hideProgress();
                                    //Update User profile
                                    UserInfo.Response data = (UserInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                    Utils.setPrefString(getApplicationContext(), Key.UserProfile, CommonObject.toJSONString(data.data.user, UserInfo.User.class));
                                    AppData.sharedInstance().mUserInfo = data.data.user;
                                    mLeftMenuView.updateMenu(
                                            AppData.sharedInstance().mAppInfo.getAppSetting(),
                                            AppData.sharedInstance().mAppInfo.getSideMenu(),
                                            AppData.sharedInstance().mUserInfo);
                                    showFirstFragment();
                                    setPushKey(token);

                                } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                    //refresh token then try again
                                    refreshTokenThenTryAgain(new AbstractFragment.TenpossCallback() {
                                        @Override
                                        public void onSuccess(Bundle params) {
                                            getUserDetail();
                                        }

                                        @Override
                                        public void onFailed(Bundle params) {
                                            Utils.hideProgress();
                                            showSignInScreen(false);
                                        }
                                    });

                                } else {
                                    Utils.hideProgress();
                                    showSignInScreen(false);
                                }
                            } else if (result == TenpossCommunicator.CommunicationCode.ConnectionTimedOut.ordinal()) {
                                Utils.hideProgress();
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
                                Utils.hideProgress();
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
        if (isSignedIn() == true && alarmStatus == -1) {
            Random r = new Random();
            int i1 = r.nextInt(80 - 65) + 10;

            Log.i("MainActivity", "Will show Share popup after " + i1 + " secods");
            alarmStatus = 1;
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

    private void setPushKey(String token) {
        String firebaseToken = Utils.getPrefString(getApplicationContext(), Key.FireBaseTokenKey);
        if (firebaseToken != null && firebaseToken.length() > 0) {
            Bundle params = new Bundle();
            SetPushKeyInfo.Request request = new SetPushKeyInfo.Request();
            request.token = token;
            request.key = firebaseToken;
            params.putString(Key.TokenKey, Utils.getPrefString(getApplicationContext(), Key.TokenKey));
            params.putSerializable(Key.RequestObject, request);

            Utils.log(TAG, "token : " + token);
            SetPushKeyCommunicator communicator = new SetPushKeyCommunicator(
                    new TenpossCommunicator.TenpossCommunicatorListener() {
                        @Override
                        public void completed(TenpossCommunicator request, Bundle responseParams) {
                            int result = responseParams.getInt(Key.ResponseResult);
                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                if (resultApi == CommonResponse.ResultSuccess) {
                                    //Update User profile
                                    //TODO: SetPushKeyInfo.Response response = (SetPushKeyInfo.Response) responseParams.getSerializable(Key.ResponseObject);
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
            communicator.execute(params);
        }
    }
}
