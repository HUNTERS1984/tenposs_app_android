package jp.tenposs.tenposs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
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

    AppInfo.Response appInfo;
    int storeId;
    ArrayList<AppInfo.SideMenu> sideMenuInfo;


    FragmentManager fragmentManager;
    FragmentHome fragmentHome;
    Fragment lastTopFragment;

    //ImageButton toggleMenuButton;
    //ImageButton backButton;

    //ImageButton closeButton;

    //TextView navTitleLabel;
    //ImageButton signOutButton;

    //Toolbar toolbar;
    DrawerLayout drawerLayout;
    LeftMenuView leftMenuView;
    FrameLayout contentContainer;

    CallbackManager callbackManager;

    protected SharedPreferences appPreferences;

    protected ProgressDialog progressDialog;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTopFragmentNav();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MainApplication.setContext(this.getApplicationContext());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Facebook initialize
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getApplication());
        callbackManager = CallbackManager.Factory.create();

        //Twitter initialize
        TwitterAuthConfig authConfig = new TwitterAuthConfig(SocialSigninInfo.TWITTER_CONSUMER_KEY,
                SocialSigninInfo.TWITTER_CONSUMER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig));

        //Firebase initialize
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            Log.i("demo", token);
        } else {
            Log.i("demo", "fail " + token);
        }

        this.appPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE);

        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.leftMenuView = (LeftMenuView) findViewById(R.id.left_menu_view);
        this.contentContainer = (FrameLayout) findViewById(R.id.content_container);

        this.fragmentManager = getSupportFragmentManager();
        this.leftMenuView.setOnItemClickListener(this);

        this.fragmentHome = (FragmentHome) getFragmentForTag(FragmentHome.class.getCanonicalName());
        if (this.fragmentHome == null) {
            getUserDetail(false);
        } else {
            showHomeScreen();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onKeyDown(keyCode, e);
    }

    @Override
    public void onBackPressed() {
        Utils.hideKeyboard(this, null);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        } else if (fragmentManager.getBackStackEntryCount() > 0) {

            /**
             * Get Top fragment
             */
            AbstractFragment topFragment = null;
            try {
                int size = fragmentManager.getBackStackEntryCount();
                FragmentManager.BackStackEntry backStackEntry = this.fragmentManager.getBackStackEntryAt(size - 1);
                topFragment = (AbstractFragment) this.fragmentManager.findFragmentByTag(backStackEntry.getName());

            } catch (Exception ex) {

            }

            if (topFragment == fragmentHome) {
                //if (topFragment.toolbarSettings.toolbarType == AbstractFragment.ToolbarSettings.LEFT_MENU_BUTTON) {
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
            } else if (topFragment instanceof FragmentSignIn && fragmentHome == null) {
                //TODO: Exit or Skip
                fragmentManager.popBackStackImmediate();
                showHomeScreen();
            } else {
                /**
                 * Back and update NavigationBar
                 */
                fragmentManager.popBackStackImmediate();
            }
        } else

        {
            super.onBackPressed();
        }

    }

    @Override
    public void updateAppInfo(AppInfo.Response appInfo, int storeId) {
        this.appInfo = appInfo;
        this.storeId = storeId;
        updateTopFragmentNav();
    }

    @Override
    public void updateSideMenuItems(ArrayList<AppInfo.SideMenu> menuInfo, boolean isSignedIn) {
        this.sideMenuInfo = menuInfo;
        if (leftMenuView != null && this.sideMenuInfo != null) {
            leftMenuView.updateMenuItems(this.appInfo.data.app_setting, menuInfo, isSignedIn);
        }
        String profileStr = getKeyString(Key.Profile);
        String userProfile = getKeyString(Key.UserProfile);
        SignInInfo.Profile profile = null;
        if (userProfile.length() > 0) {
            try {
                UserInfo.User user = (UserInfo.User) CommonObject.fromJSONString(userProfile, UserInfo.User.class, null);
                profile = user.profile;
            } catch (Exception ignored) {

            }
        } else if (profileStr.length() > 0) {
            profile = (SignInInfo.Profile) CommonObject.fromJSONString(profileStr, SignInInfo.Profile.class, null);
        }

        leftMenuView.updateUserInfo(profile);
    }

    @Override
    public void updateUserInfo(SignInInfo.Profile profile) {
        if (leftMenuView != null) {
            leftMenuView.updateUserInfo(profile);
        }

        if (this.sideMenuInfo != null) {
            updateSideMenuItems(this.sideMenuInfo, profile != null);
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
                showMenuScreen(storeId);
            }
            break;

            case AbstractFragment.ITEM_SCREEN: {
                showItemDetail(extras);
            }
            break;

            case AbstractFragment.RESERVE_SCREEN: {
                showReserveScreen(extras);
            }
            break;

            case AbstractFragment.NEWS_SCREEN: {
                showNewsScreen(storeId);
            }
            break;

            case AbstractFragment.NEWS_DETAILS_SCREEN: {
                showNewsDetailScreen(extras);
            }
            break;

            case AbstractFragment.PHOTO_SCREEN: {
                showPhotoScreen(storeId);
            }
            break;

            case AbstractFragment.PHOTO_ITEM_SCREEN: {
                showPhotoPreviewScreen(extras);
            }
            break;

            case AbstractFragment.COUPON_SCREEN: {
                showCouponScreen(storeId);

            }
            break;

            case AbstractFragment.COUPON_DETAIL_SCREEN: {
                showCouponDetailScreen(extras);
            }
            break;

            case AbstractFragment.CHAT_SCREEN: {
                showChatScreen(storeId);

            }
            break;

            case AbstractFragment.SETTING_SCREEN: {
                showSettingScreen(storeId);
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
                showStaffScreen(storeId);
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
        return this.appInfo.data;
    }

    @Override
    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    @Override
    public void toggleMenu() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(Gravity.LEFT);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public FragmentManager getFM() {
        return this.fragmentManager;
    }

    @Override
    public void setDrawerLockMode(int mode) {
        this.drawerLayout.setDrawerLockMode(mode);
    }

    /*@Override
    public void updateNavigationBar(AbstractFragment.ToolbarSettings toolbarSettings) {
        try {
            if (toolbarSettings.toolbarType == AbstractFragment.ToolbarSettings.LEFT_MENU_BUTTON) {
                toggleMenuButton.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.GONE);
                toggleMenuButton.setImageBitmap(FlatIcon.fromFlatIcon(getApplication().getAssets(),
                        toolbarSettings.toolbarLeftIcon,
                        40,
                        Color.argb(0, 0, 0, 0),
                        toolbarSettings.appSetting.getToolbarIconColor()
                ));

            } else {
                toggleMenuButton.setVisibility(View.GONE);
                backButton.setVisibility(View.VISIBLE);
                backButton.setImageBitmap(FlatIcon.fromFlatIcon(getApplication().getAssets(),
                        toolbarSettings.toolbarLeftIcon,
                        40,
                        Color.argb(0, 0, 0, 0),
                        toolbarSettings.appSetting.getToolbarIconColor()
                ));
            }

            signOutButton.setImageBitmap(FlatIcon.fromFlatIcon(getApplication().getAssets(),
                    "flaticon-sign-out",
                    40,
                    Color.argb(0, 0, 0, 0),
                    toolbarSettings.appSetting.getToolbarIconColor()
            ));

            navTitleLabel.setText(toolbarSettings.toolbarTitle);
            navTitleLabel.setTextColor(toolbarSettings.appSetting.getToolbarTitleColor());
            try {
                Typeface type = Typeface.createFromAsset(getAssets(), toolbarSettings.appSetting.getToolBarTitleFont());
                navTitleLabel.setTypeface(type);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ignored) {

        }
    }*/

    @Override
    public void onClick(int position, Bundle params) {
        drawerLayout.closeDrawer(Gravity.LEFT);
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

    private void updateTopFragmentNav() {
        AbstractFragment topFragment = getTopFragment();
        if (topFragment != null) {
            //updateNavigationBar(topFragment.toolbarSettings);
        }
    }


    AbstractFragment getTopFragment() {
        AbstractFragment topFragment = null;
        try {
            int size = fragmentManager.getBackStackEntryCount();
            FragmentManager.BackStackEntry backStackEntry = this.fragmentManager.getBackStackEntryAt(size - 1);
            topFragment = (AbstractFragment) this.fragmentManager.findFragmentByTag(backStackEntry.getName());
        } catch (Exception ex) {

        }
        return topFragment;
    }

    Fragment getFragmentForTag(String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }

    void showFragment(AbstractFragment fragment, String fragmentTag, boolean animated, boolean showToolbar, boolean showsignOutButton) {
        if (fragment.isAdded()) {
            Log.d("Fragment " + fragmentTag, "IS ADDED!!!");
            if (fragmentManager.popBackStackImmediate(fragmentTag, 0)) {
                Log.e("Fragment", "IS ADDED AND POPPED!!!");
            } else {
                Log.e("Fragment", "IS ADDED BUT NOT POPPED!!!");
            }
        } else {
            Log.d("Fragment " + fragmentTag, "IS NOT ADDED!!!");
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (animated == true) {
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            }
            ft.add(contentContainer.getId(), fragment, fragmentTag);
            ft.addToBackStack(fragmentTag);
            ft.commit();
        }

        lastTopFragment = fragment;

        //updateNavigationBar(((AbstractFragment) fragment).toolbarSettings);

        if (showToolbar == true) {
            //toolbar.setVisibility(View.VISIBLE);
            if (showsignOutButton == true) {
                //signOutButton.setVisibility(View.VISIBLE);
            } else {
                //signOutButton.setVisibility(View.INVISIBLE);
            }
        } else {
            //toolbar.setVisibility(View.GONE);
            //signOutButton.setVisibility(View.INVISIBLE);
        }
    }

    void showHomeScreen() {
        if (this.fragmentHome == null) {
            this.fragmentHome = new FragmentHome();
        }
        showFragment(this.fragmentHome, FragmentHome.class.getCanonicalName(), false, true, false);
    }

    void showSignInScreen(boolean showToolbar) {
        //clear token and user profile

        //Local
        setKeyString(Key.TokenKey, "");
        setKeyString(Key.UserProfile, "");
        setKeyString(Key.Profile, "");

        //Facebook
        LoginManager.getInstance().logOut();

        FragmentSignIn fragmentSignIn = (FragmentSignIn) getFragmentForTag(FragmentSignIn.class.getCanonicalName());
        if (fragmentSignIn == null) {
            fragmentSignIn = FragmentSignIn.newInstance(showToolbar);
        }
        showFragment(fragmentSignIn, FragmentSignIn.class.getCanonicalName(), true, showToolbar, false);
    }

    void showSignInEmailScreen() {
        FragmentSignInEmail fragmentSignInEmail = (FragmentSignInEmail) getFragmentForTag(FragmentSignInEmail.class.getCanonicalName());
        if (fragmentSignInEmail == null) {
            fragmentSignInEmail = new FragmentSignInEmail();
        }
        showFragment(fragmentSignInEmail, FragmentSignIn.class.getCanonicalName(), true, true, false);
    }

    void showSignUpScreen() {
        FragmentSignUp fragmentSignUp = (FragmentSignUp) getFragmentForTag(FragmentSignUp.class.getCanonicalName());
        if (fragmentSignUp == null) {
            fragmentSignUp = new FragmentSignUp();
        }
        showFragment(fragmentSignUp, FragmentSignUp.class.getCanonicalName(), true, (this.fragmentHome != null), false);
    }

    void showMenuScreen(int storeId) {
        FragmentMenu fragmentMenu = (FragmentMenu) getFragmentForTag(FragmentMenu.class.getCanonicalName());
        if (fragmentMenu == null) {
            fragmentMenu = new FragmentMenu();
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = appInfo.data.getSideMenu(AbstractFragment.MENU_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentMenu.setArguments(b);
        }
        showFragment(fragmentMenu, FragmentMenu.class.getCanonicalName(), true, true, false);
    }


    void showItemDetail(Serializable extras) {
        FragmentProduct fragmentProduct = (FragmentProduct) getFragmentForTag(FragmentProduct.class.getCanonicalName());
        if (fragmentProduct == null) {
            fragmentProduct = new FragmentProduct();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentProduct.setArguments(b);
        }
        showFragment(fragmentProduct, FragmentProduct.class.getCanonicalName(), true, true, false);
    }

    void showReserveScreen(Serializable extras) {
        FragmentReserve fragmentReserve = (FragmentReserve) getFragmentForTag(FragmentReserve.class.getCanonicalName());
        if (fragmentReserve != null) {
            //Pop to
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = appInfo.data.getSideMenu(AbstractFragment.RESERVE_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            fragmentReserve.setArguments(b);
        }
        fragmentReserve = FragmentReserve.newInstance((TopInfo.Contact) extras);
        showFragment(fragmentReserve, FragmentReserve.class.getCanonicalName(), true, true, false);
    }

    void showNewsScreen(int storeId) {
        FragmentNews fragmentNews = (FragmentNews) getFragmentForTag(FragmentNews.class.getCanonicalName());
        if (fragmentNews == null) {
            fragmentNews = new FragmentNews();
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = appInfo.data.getSideMenu(AbstractFragment.NEWS_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentNews.setArguments(b);
        }
        showFragment(fragmentNews, FragmentNews.class.getCanonicalName(), true, true, false);
    }

    void showNewsDetailScreen(Serializable extras) {
        FragmentNewsDetail fragmentNewsDetail = (FragmentNewsDetail) getFragmentForTag(FragmentNewsDetail.class.getCanonicalName());
        if (fragmentNewsDetail == null) {
            fragmentNewsDetail = new FragmentNewsDetail();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentNewsDetail.setArguments(b);
        }
        showFragment(fragmentNewsDetail, FragmentNewsDetail.class.getCanonicalName(), true, true, false);
    }

    void showPhotoScreen(int storeId) {
        FragmentPhotoGallery fragmentPhotoGallery = (FragmentPhotoGallery) getFragmentForTag(FragmentPhotoGallery.class.getCanonicalName());
        if (fragmentPhotoGallery == null) {
            fragmentPhotoGallery = new FragmentPhotoGallery();
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = appInfo.data.getSideMenu(AbstractFragment.PHOTO_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentPhotoGallery.setArguments(b);
        }
        showFragment(fragmentPhotoGallery, FragmentPhotoGallery.class.getCanonicalName(), true, true, false);
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
            AppInfo.SideMenu menu = appInfo.data.getSideMenu(AbstractFragment.COUPON_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentCoupon.setArguments(b);
        }

        showFragment(fragmentCoupon, FragmentCoupon.class.getCanonicalName(), true, true, false);
    }

    void showCouponDetailScreen(Serializable extras) {
        FragmentCouponDetail fragmentCouponDetail = (FragmentCouponDetail) getFragmentForTag(FragmentCouponDetail.class.getCanonicalName());
        if (fragmentCouponDetail == null) {
            fragmentCouponDetail = new FragmentCouponDetail();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentCouponDetail.setArguments(b);
        }
        showFragment(fragmentCouponDetail, FragmentCouponDetail.class.getCanonicalName(), true, true, false);
    }

    void showChatScreen(int storeId) {
        FragmentChat fragmentChat = (FragmentChat) getFragmentForTag(FragmentChat.class.getCanonicalName());
        if (fragmentChat == null) {
            fragmentChat = new FragmentChat();
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = appInfo.data.getSideMenu(AbstractFragment.CHAT_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentChat.setArguments(b);
        }
        showFragment(fragmentChat, FragmentChat.class.getCanonicalName(), true, true, false);
    }

    void showSettingScreen(int storeId) {
        FragmentSetting fragmentSetting = (FragmentSetting) getFragmentForTag(FragmentSetting.class.getCanonicalName());
        if (fragmentSetting == null) {
            fragmentSetting = new FragmentSetting();
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = appInfo.data.getSideMenu(AbstractFragment.SETTING_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentSetting.setArguments(b);
        }
        showFragment(fragmentSetting, FragmentSetting.class.getCanonicalName(), true, true, false);
    }

    void showProfileScreen() {
        FragmentEditProfile fragmentEditProfile = (FragmentEditProfile) getFragmentForTag(FragmentEditProfile.class.getCanonicalName());
        if (fragmentEditProfile == null) {
            fragmentEditProfile = new FragmentEditProfile();
            Bundle b = new Bundle();
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentEditProfile.setArguments(b);
        }
        showFragment(fragmentEditProfile, FragmentEditProfile.class.getCanonicalName(), true, true, true);
    }

    void showCompanyInfo() {
        FragmentCompanyInfo fragmentCompanyInfo = (FragmentCompanyInfo) getFragmentForTag(FragmentCompanyInfo.class.getCanonicalName());
        if (fragmentCompanyInfo == null) {
            fragmentCompanyInfo = new FragmentCompanyInfo();
        }
        showFragment(fragmentCompanyInfo, FragmentCompanyInfo.class.getCanonicalName(), true, true, true);
    }

    void showUserPrivacy() {
        FragmentUserPrivacy fragmentUserPrivacy = (FragmentUserPrivacy) getFragmentForTag(FragmentUserPrivacy.class.getCanonicalName());
        if (fragmentUserPrivacy == null) {
            fragmentUserPrivacy = new FragmentUserPrivacy();
        }
        showFragment(fragmentUserPrivacy, FragmentUserPrivacy.class.getCanonicalName(), true, true, true);
    }

    void showStaffScreen(int storeId) {
        FragmentStaff fragmentStaff = (FragmentStaff) getFragmentForTag(FragmentStaff.class.getCanonicalName());
        if (fragmentStaff == null) {
            fragmentStaff = new FragmentStaff();
            Bundle b = new Bundle();
            AppInfo.SideMenu menu = appInfo.data.getSideMenu(AbstractFragment.STAFF_SCREEN);
            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            fragmentStaff.setArguments(b);
        }

        showFragment(fragmentStaff, FragmentStaff.class.getCanonicalName(), true, true, false);
    }

    void showStaffDetailScreen(Serializable extras) {
        FragmentStaffDetail fragmentStaffDetail = (FragmentStaffDetail) getFragmentForTag(FragmentStaffDetail.class.getCanonicalName());
        if (fragmentStaffDetail == null) {
            fragmentStaffDetail = new FragmentStaffDetail();
            Bundle b = new Bundle();
//            AppInfo.SideMenu menu = appInfo.data.getSideMenu(AbstractFragment.STAFF_SCREEN);
//            b.putString(AbstractFragment.SCREEN_TITLE, menu.name);
//            b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentStaffDetail.setArguments(b);
        }

        showFragment(fragmentStaffDetail, FragmentStaffDetail.class.getCanonicalName(), true, true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getFragmentForTag(FragmentSignIn.class.getCanonicalName());
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    protected String getKeyString(String key) {
        if (this.appPreferences == null) {
            this.appPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        }
        return this.appPreferences.getString(key, "");
    }

    protected boolean setKeyString(String key, String value) {
        if (this.appPreferences == null) {
            this.appPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        }
        boolean ret;
        SharedPreferences.Editor editor = this.appPreferences.edit();
        editor.putString(key, value);
        ret = editor.commit();
        return ret;
    }

    protected void errorWithMessage(Bundle response, String message) {
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
                                                        setKeyString(Key.Profile, "");

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
                                                        updateSideMenuItems(MainActivity.this.sideMenuInfo, false);
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

    public void getUserDetail(boolean skip) {
        if (skip == true) {
            showHomeScreen();
        } else {
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
                                int result = responseParams.getInt(Key.ResponseResult);
                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                    if (resultApi == CommonResponse.ResultSuccess) {
                                        //Update User profile

                                        UserInfo.Response data = (UserInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                        setKeyString(Key.UserProfile, CommonObject.toJSONString(data.data.user, UserInfo.User.class));

                                        showHomeScreen();
                                    } else {
                                        showSignInScreen(false);
                                    }
                                } else {
                                    showSignInScreen(false);
                                }
                            }
                        });
                communicator.execute(params);
            } else {
                showSignInScreen(false);
            }
        }
    }

    protected void showProgress(String message) {
        if (this.progressDialog != null)
            this.progressDialog.dismiss();
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage(message);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progressDialog.setProgress(0);
        this.progressDialog.setMax(20);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    protected void changeProgress(String message) {
        if (this.progressDialog == null)
            showProgress(message);
        else
            this.progressDialog.setMessage(message);
    }

    protected void hideProgress() {
        try {
            if (this.progressDialog != null)
                this.progressDialog.dismiss();
            this.progressDialog = null;
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
