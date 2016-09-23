package jp.tenposs.tenposs;

import android.app.ProgressDialog;
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
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import junit.framework.Assert;

import java.io.Serializable;

import io.fabric.sdk.android.Fabric;
import jp.tenposs.communicator.AppInfoCommunicator;
import jp.tenposs.communicator.SignOutCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.communicator.UserInfoCommunicator;
import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.datamodel.SignOutInfo;
import jp.tenposs.datamodel.SocialSigninInfo;
import jp.tenposs.datamodel.TopInfo;
import jp.tenposs.datamodel.UserInfo;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.LeftMenuView;

public class MainActivity extends AppCompatActivity
        implements
        AbstractFragment.MainActivityListener,
        LeftMenuView.OnLeftMenuItemClickListener {


    AppInfo.Response mAppInfo;
    int mStoreId;

    FragmentManager mFragmentManager;
    FragmentHome mFragmentHome;

    //ImageButton toggleMenuButton;
    //ImageButton backButton;

    //ImageButton closeButton;

    //TextView navTitleLabel;
    //ImageButton signOutButton;

    //Toolbar mToolbar;
    DrawerLayout mDrawerLayout;
    LeftMenuView mLeftMenuView;
    FrameLayout mContentContainer;

    CallbackManager mCallbackManager;

    protected SharedPreferences mAppPreferences;

    protected ProgressDialog mProgressDialog;
    private boolean mSignedIn;

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
        setContentView(R.layout.activity_main);

        MainApplication.setContext(this.getApplicationContext());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
        this.mLeftMenuView.setOnItemClickListener(this);

        checkNetworkConnection();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            AbstractFragment topFragment = getTopFragment();
            if (topFragment != null) {
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    if (topFragment.mToolbarSettings.toolbarType == AbstractFragment.ToolbarSettings.LEFT_MENU_BUTTON) {
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
        this.mAppInfo = appInfo;
        this.mStoreId = storeId;

        String userProfileStr = getKeyString(Key.UserProfile);
        SignInInfo.User userProfile = null;
        if (userProfileStr.length() > 0) {
            try {
                userProfile = (SignInInfo.User) CommonObject.fromJSONString(userProfileStr, SignInInfo.User.class, null);
            } catch (Exception ignored) {
            }
        }
        mLeftMenuView.updateMenu(this.mAppInfo.data.app_setting, this.mAppInfo.data.side_menu, userProfile);
    }

    @Override
    public void updateUserInfo(SignInInfo.User userProfile) {
        if (mLeftMenuView != null) {
            mLeftMenuView.updateMenu(this.mAppInfo.data.app_setting, this.mAppInfo.data.side_menu, userProfile);
        }
    }

    @Override
    public void showScreen(int screenId, Serializable extras) {
        switch (screenId) {
            case AbstractFragment.HOME_SCREEN: {
                showHomeScreen();
            }
            break;

            case AbstractFragment.MENU_SCREEN: {
                showMenuScreen(this.mStoreId);
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

            case AbstractFragment.RESERVE_SCREEN: {
                showReserveScreen(extras);
            }
            break;

            case AbstractFragment.NEWS_SCREEN: {
                showNewsScreen(this.mStoreId);
            }
            break;

            case AbstractFragment.NEWS_DETAILS_SCREEN: {
                showNewsDetailScreen(extras);
            }
            break;

            case AbstractFragment.PHOTO_SCREEN: {
                showPhotoScreen(this.mStoreId);
            }
            break;

            case AbstractFragment.PHOTO_ITEM_SCREEN: {
                showPhotoPreviewScreen(extras);
            }
            break;

            case AbstractFragment.COUPON_SCREEN: {
                showCouponScreen(this.mStoreId);

            }
            break;

            case AbstractFragment.COUPON_DETAIL_SCREEN: {
                showCouponDetailScreen(extras);
            }
            break;

            case AbstractFragment.CHAT_SCREEN: {
                if (isSignedIn() == true) {
                    showChatScreen(this.mStoreId);
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

            case AbstractFragment.SETTING_SCREEN: {
                showSettingScreen(this.mStoreId);
            }
            break;

            case AbstractFragment.PROFILE_SCREEN: {
                showProfileScreen();
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

            case AbstractFragment.SIGN_OUT_SCREEN: {
                performSignOut();
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

            case AbstractFragment.STAFF_SCREEN: {
                showStaffScreen(this.mStoreId);
            }
            break;

            case AbstractFragment.STAFF_DETAIL_SCREEN: {
                showStaffDetailScreen(extras);
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
        return this.mAppInfo.data;
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

    void showHomeScreen() {
        if (this.mFragmentHome == null) {
            this.mFragmentHome = new FragmentHome();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.APP_DATA, this.mAppInfo);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, this.mStoreId);
            mFragmentHome.setArguments(b);
        }
        showFragment(this.mFragmentHome, FragmentHome.class.getCanonicalName(), false);
    }

    void showSignInScreen(boolean showToolbar) {
        //clear token and user profile

        //Local
        setKeyString(Key.TokenKey, "");
        setKeyString(Key.UserProfile, "");

        //Facebook
        LoginManager.getInstance().logOut();

        FragmentSignIn fragmentSignIn = (FragmentSignIn) getFragmentForTag(FragmentSignIn.class.getCanonicalName());
        if (fragmentSignIn == null) {
            fragmentSignIn = FragmentSignIn.newInstance(showToolbar, this.mAppInfo.data.name);
        }
        showFragment(fragmentSignIn, FragmentSignIn.class.getCanonicalName(), true);
    }

    void showSignInEmailScreen() {
        FragmentSignInEmail fragmentSignInEmail = (FragmentSignInEmail) getFragmentForTag(FragmentSignInEmail.class.getCanonicalName());
        if (fragmentSignInEmail == null) {
            fragmentSignInEmail = new FragmentSignInEmail();
        }
        showFragment(fragmentSignInEmail, FragmentSignIn.class.getCanonicalName(), true);
    }

    void showSignUpScreen() {
        FragmentSignUp fragmentSignUp = (FragmentSignUp) getFragmentForTag(FragmentSignUp.class.getCanonicalName());
        if (fragmentSignUp == null) {
            fragmentSignUp = new FragmentSignUp();
        }
        showFragment(fragmentSignUp, FragmentSignUp.class.getCanonicalName(), true);
    }

    void showMenuScreen(int storeId) {
        FragmentMenu fragmentMenu = (FragmentMenu) getFragmentForTag(FragmentMenu.class.getCanonicalName());
        if (fragmentMenu == null) {
            fragmentMenu = new FragmentMenu();
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = mAppInfo.data.getSideMenu(AbstractFragment.MENU_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentMenu.setArguments(b);
        }
        showFragment(fragmentMenu, FragmentMenu.class.getCanonicalName(), true);
    }


    void showItemDetailScreen(Serializable extras) {
        FragmentProduct fragmentProduct = (FragmentProduct) getFragmentForTag(FragmentProduct.class.getCanonicalName());
        if (fragmentProduct == null) {
            fragmentProduct = new FragmentProduct();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentProduct.setArguments(b);
        }
        showFragment(fragmentProduct, FragmentProduct.class.getCanonicalName(), true);
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
        FragmentNews fragmentNews = (FragmentNews) getFragmentForTag(FragmentNews.class.getCanonicalName());
        if (fragmentNews == null) {
            fragmentNews = new FragmentNews();
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = mAppInfo.data.getSideMenu(AbstractFragment.NEWS_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentNews.setArguments(b);
        }
        showFragment(fragmentNews, FragmentNews.class.getCanonicalName(), true);
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
        FragmentPhotoGallery fragmentPhotoGallery = (FragmentPhotoGallery) getFragmentForTag(FragmentPhotoGallery.class.getCanonicalName());
        if (fragmentPhotoGallery == null) {
            fragmentPhotoGallery = new FragmentPhotoGallery();
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = mAppInfo.data.getSideMenu(AbstractFragment.PHOTO_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentPhotoGallery.setArguments(b);
        }
        showFragment(fragmentPhotoGallery, FragmentPhotoGallery.class.getCanonicalName(), true);
    }

    void showPhotoPreviewScreen(Serializable extras) {
        PopupPhotoPreview photoPreview = new PopupPhotoPreview(this);
        photoPreview.setData(extras);
        photoPreview.show();
    }

    void showCouponScreen(int storeId) {
        FragmentCoupon fragmentCoupon = (FragmentCoupon) getFragmentForTag(FragmentCoupon.class.getCanonicalName());
        if (fragmentCoupon == null) {
            fragmentCoupon = new FragmentCoupon();
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = mAppInfo.data.getSideMenu(AbstractFragment.COUPON_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentCoupon.setArguments(b);
        }

        showFragment(fragmentCoupon, FragmentCoupon.class.getCanonicalName(), true);
    }

    void showCouponDetailScreen(Serializable extras) {
        FragmentCouponDetail fragmentCouponDetail = (FragmentCouponDetail) getFragmentForTag(FragmentCouponDetail.class.getCanonicalName());
        if (fragmentCouponDetail == null) {
            fragmentCouponDetail = new FragmentCouponDetail();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentCouponDetail.setArguments(b);
        }
        showFragment(fragmentCouponDetail, FragmentCouponDetail.class.getCanonicalName(), true);
    }

    void showChatScreen(int storeId) {
        FragmentChat fragmentChat = (FragmentChat) getFragmentForTag(FragmentChat.class.getCanonicalName());
        if (fragmentChat == null) {
            fragmentChat = new FragmentChat();
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = mAppInfo.data.getSideMenu(AbstractFragment.CHAT_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentChat.setArguments(b);
        }
        showFragment(fragmentChat, FragmentChat.class.getCanonicalName(), true);
    }

    void showSettingScreen(int storeId) {
        FragmentSetting fragmentSetting = (FragmentSetting) getFragmentForTag(FragmentSetting.class.getCanonicalName());
        if (fragmentSetting == null) {
            fragmentSetting = new FragmentSetting();
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = mAppInfo.data.getSideMenu(AbstractFragment.SETTING_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentSetting.setArguments(b);
        }
        showFragment(fragmentSetting, FragmentSetting.class.getCanonicalName(), true);
    }

    void showProfileScreen() {
        FragmentEditProfile fragmentEditProfile = (FragmentEditProfile) getFragmentForTag(FragmentEditProfile.class.getCanonicalName());
        if (fragmentEditProfile == null) {
            fragmentEditProfile = new FragmentEditProfile();
            Bundle b = new Bundle();
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, this.mStoreId);
            fragmentEditProfile.setArguments(b);
        }
        showFragment(fragmentEditProfile, FragmentEditProfile.class.getCanonicalName(), true);
    }

    void showCompanyInfo() {
        FragmentCompanyInfo fragmentCompanyInfo = (FragmentCompanyInfo) getFragmentForTag(FragmentCompanyInfo.class.getCanonicalName());
        if (fragmentCompanyInfo == null) {
            fragmentCompanyInfo = new FragmentCompanyInfo();
        }
        showFragment(fragmentCompanyInfo, FragmentCompanyInfo.class.getCanonicalName(), true);
    }

    void showUserPrivacy() {
        FragmentUserPrivacy fragmentUserPrivacy = (FragmentUserPrivacy) getFragmentForTag(FragmentUserPrivacy.class.getCanonicalName());
        if (fragmentUserPrivacy == null) {
            fragmentUserPrivacy = new FragmentUserPrivacy();
        }
        showFragment(fragmentUserPrivacy, FragmentUserPrivacy.class.getCanonicalName(), true);
    }

    void showStaffScreen(int storeId) {
        FragmentStaff fragmentStaff = (FragmentStaff) getFragmentForTag(FragmentStaff.class.getCanonicalName());
        if (fragmentStaff == null) {
            fragmentStaff = new FragmentStaff();
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = mAppInfo.data.getSideMenu(AbstractFragment.STAFF_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentStaff.setArguments(b);
        }

        showFragment(fragmentStaff, FragmentStaff.class.getCanonicalName(), true);
    }

    void showStaffDetailScreen(Serializable extras) {
        FragmentStaffDetail fragmentStaffDetail = (FragmentStaffDetail) getFragmentForTag(FragmentStaffDetail.class.getCanonicalName());
        if (fragmentStaffDetail == null) {
            fragmentStaffDetail = new FragmentStaffDetail();
            Bundle b = new Bundle();
//            AppInfo.SideMenu menu = mAppInfo.data.getSideMenu(AbstractFragment.STAFF_SCREEN);
//            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
//            b.putInt(AbstractFragment.APP_DATA_STORE_ID, mStoreId);
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentStaffDetail.setArguments(b);
        }

        showFragment(fragmentStaffDetail, FragmentStaffDetail.class.getCanonicalName(), true);
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
            hideProgress();
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
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            setKeyString(Key.FireBaseTokenKey, token);
        } else {
            setKeyString(Key.FireBaseTokenKey, "");
        }

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
        }
    }

    protected String getKeyString(String key) {
        if (this.mAppPreferences == null) {
            this.mAppPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        }
        return this.mAppPreferences.getString(key, "");
    }

    protected boolean setKeyString(String key, String value) {
        if (this.mAppPreferences == null) {
            this.mAppPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        }
        boolean ret;
        SharedPreferences.Editor editor = this.mAppPreferences.edit();
        editor.putString(key, value);
        ret = editor.commit();
        return ret;
    }

    protected void errorWithMessage(Bundle response, String message) {
        hideProgress();
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
                                                hideProgress();
                                                int result = responseParams.getInt(Key.ResponseResult);
                                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                                    if (resultApi == CommonResponse.ResultSuccess ||
                                                            resultApi == CommonResponse.ResultErrorInvalidToken) {
                                                        //clear token and user profile
                                                        setKeyString(Key.TokenKey, "");
                                                        setKeyString(Key.UserProfile, "");

                                                        try {
                                                            //Facebook
                                                            LoginManager.getInstance().logOut();
                                                        } catch (Exception ex) {
                                                            ex.printStackTrace();
                                                        }

                                                        try {
                                                            //Twitter
                                                            CookieSyncManager.createInstance(MainActivity.this);
                                                            CookieManager cookieManager = CookieManager.getInstance();
                                                            cookieManager.removeSessionCookie();
                                                            Twitter.getSessionManager().clearActiveSession();
                                                            Twitter.logOut();
                                                        } catch (Exception ex) {
                                                            ex.printStackTrace();
                                                        }

                                                        updateUserInfo(null);
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
                                request.token = getKeyString(Key.TokenKey);
                                params.putSerializable(Key.RequestObject, request);
                                showProgress(getString(R.string.msg_signing_out));
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
        showProgress(getString(R.string.msg_starting_up));
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
                                MainActivity.this.mAppInfo = (AppInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                if (MainActivity.this.mAppInfo.data != null && MainActivity.this.mAppInfo.data.stores.size() > 0) {
                                    MainActivity.this.mStoreId = MainActivity.this.mAppInfo.data.stores.get(0).id;
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
        String token = getKeyString(Key.TokenKey);
        if (token.length() > 0) {
            Bundle params = new Bundle();
            UserInfo.Request request = new UserInfo.Request();
            request.token = token;
            params.putSerializable(Key.RequestObject, request);

            UserInfoCommunicator communicator = new UserInfoCommunicator(
                    new TenpossCommunicator.TenpossCommunicatorListener() {
                        @Override
                        public void completed(TenpossCommunicator request, Bundle responseParams) {
                            hideProgress();
                            int result = responseParams.getInt(Key.ResponseResult);
                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                if (resultApi == CommonResponse.ResultSuccess) {
                                    //Update User profile

                                    UserInfo.Response data = (UserInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                    setKeyString(Key.UserProfile, CommonObject.toJSONString(data.data.user, SignInInfo.User.class));

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
            hideProgress();
            showSignInScreen(false);
        }
    }

    protected void showProgress(String message) {
        if (this.mProgressDialog != null)
            this.mProgressDialog.dismiss();
        this.mProgressDialog = new ProgressDialog(this);
        this.mProgressDialog.setMessage(message);
        this.mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.mProgressDialog.setProgress(0);
        this.mProgressDialog.setMax(20);
        this.mProgressDialog.setCancelable(false);
        this.mProgressDialog.show();
    }

    protected void changeProgress(String message) {
        if (this.mProgressDialog == null)
            showProgress(message);
        else
            this.mProgressDialog.setMessage(message);
    }

    protected void hideProgress() {
        try {
            if (this.mProgressDialog != null)
                this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        } catch (Exception ignored) {
        }
    }

    public boolean isSignedIn() {
        String token = getKeyString(Key.TokenKey);
        String userProfile = getKeyString(Key.UserProfile);
        if (token.length() > 0 && userProfile.length() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
