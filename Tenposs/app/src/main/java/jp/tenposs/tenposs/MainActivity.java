package jp.tenposs.tenposs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.datamodel.TopInfo;
import jp.tenposs.utils.ThemifyIcon;
import jp.tenposs.view.LeftMenuView;

public class MainActivity extends AppCompatActivity
        implements
        AbstractFragment.MainActivityListener,
        LeftMenuView.OnLeftMenuItemClickListener {

    AppInfo.Response appInfo;
    int storeId;
    AppInfo.Response.ResponseData storeInfo;

    ArrayList<AppInfo.SideMenu> sideMenuInfo;

    SignInInfo.Response userInfo;

    FragmentManager fragmentManager;
    FragmentHome fragmentHome;
    FragmentSetting fragmentSetting;
    Fragment lastTopFragment;

    ImageButton toggleMenuButton;
    ImageButton backButton;

    TextView navTitleLabel;
    ImageButton searchButton;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    LeftMenuView leftMenuView;
    FrameLayout contentContainer;

    CallbackManager callbackManager;


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

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getApplication());

        callbackManager = CallbackManager.Factory.create();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toggleMenuButton = (ImageButton) findViewById(R.id.toggle_menu_button);
        backButton = (ImageButton) findViewById(R.id.back_button);

        navTitleLabel = (TextView) findViewById(R.id.nav_title_label);
        searchButton = (ImageButton) findViewById(R.id.search_button);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftMenuView = (LeftMenuView) findViewById(R.id.left_menu_view);
        contentContainer = (FrameLayout) findViewById(R.id.content_container);

        toggleMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.onBackPressed();
            }
        });

        this.fragmentManager = getSupportFragmentManager();
        this.fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                AbstractFragment topFragment = getTopFragment();
                if ((topFragment instanceof FragmentSignIn) == false &&
                        (lastTopFragment instanceof FragmentSignIn) &&
                        (MainActivity.this.fragmentHome == null)) {
//
//                }
//                if (MainActivity.this.fragmentHome == null) {
                    MainActivity.this.fragmentHome = new FragmentHome();
                    showFragment(MainActivity.this.fragmentHome, FragmentHome.class.getCanonicalName(), false, true);
                } else {
//                    AbstractFragment topFragment = getTopFragment();
                    if (topFragment != null) {
                        updateNavigationBar(topFragment.toolbarSettings);
                    }
                }
            }
        });

        leftMenuView.setOnItemClickListener(this);


        this.fragmentHome = (FragmentHome) getFragmentForTag(FragmentHome.class.getCanonicalName());
        if (this.fragmentHome == null) {
            showSignInScreen(false);
        } else {
            this.fragmentHome = new FragmentHome();
            showFragment(this.fragmentHome, FragmentHome.class.getCanonicalName(), false, true);
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
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
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
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();

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
//        this.storeInfo = this.appInfo.get(this.storeId);
    }

    @Override
    public void updateSideMenuItems(ArrayList<AppInfo.SideMenu> menuInfo) {
        this.sideMenuInfo = menuInfo;
        if (leftMenuView != null && this.sideMenuInfo != null) {
            leftMenuView.updateMenuItems(this.appInfo.data.app_setting, menuInfo);
        }
    }

    @Override
    public void updateUserInfo(SignInInfo.Response userInfo) {
        this.userInfo = userInfo;
        if (leftMenuView != null) {
            leftMenuView.updateUserInfo(userInfo);
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
                showMenuScreen();
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
                showNewsScreen();
            }
            break;

            case AbstractFragment.NEWS_DETAILS_SCREEN: {
                showNewsDetailScreen(extras);
            }
            break;

            case AbstractFragment.PHOTO_SCREEN: {
                showPhotoScreen();
            }
            break;

            case AbstractFragment.PHOTO_ITEM_SCREEN: {
                showPhotoPreviewScreen(extras);
            }
            break;

            case AbstractFragment.COUPON_SCREEN: {
                showCouponScreen();

            }
            break;

            case AbstractFragment.CHAT_SCREEN: {
                showChatScreen();

            }
            break;

            case AbstractFragment.SETTING_SCREEN: {
                showSettingScreen();
            }
            break;

            case AbstractFragment.SIGNIN_SCREEN: {
                showSignInScreen(true);
            }
            break;

            case AbstractFragment.SIGNUP_SCREEN: {
                showSignUpScreen();
            }
            break;

            case AbstractFragment.SIGNIN_EMAIL_SCREEN: {

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
    public void updateNavigationBar(AbstractFragment.ToolbarSettings toolbarSettings) {
        try {
            if (toolbarSettings.toolbarType == AbstractFragment.ToolbarSettings.LEFT_MENU_BUTTON) {
                toggleMenuButton.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.GONE);
                toggleMenuButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getApplication().getAssets(),
                        toolbarSettings.toolbarIcon,
                        40,
                        Color.argb(0, 0, 0, 0),
                        toolbarSettings.appSetting.getToolbarIconColor()
                ));

            } else {
                toggleMenuButton.setVisibility(View.GONE);
                backButton.setVisibility(View.VISIBLE);
                backButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getApplication().getAssets(),
                        toolbarSettings.toolbarIcon,
                        40,
                        Color.argb(0, 0, 0, 0),
                        toolbarSettings.appSetting.getToolbarIconColor()
                ));
            }

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

    }


    @Override
    public void onClick(int position, Bundle params) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        if (position == -1) {
            int screenId = params.getInt(AbstractFragment.SCREEN_DATA);
            showScreen(screenId, null);
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
            updateNavigationBar(topFragment.toolbarSettings);
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

    void showFragment(AbstractFragment fragment, String fragmentTag, boolean animated, boolean showToolbar) {
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

        updateNavigationBar(((AbstractFragment) fragment).toolbarSettings);

        if (showToolbar == true) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    void showHomeScreen() {
        if (this.fragmentHome == null) {
            Assert.assertFalse("Should never be here ;(", false);
            this.fragmentHome = new FragmentHome();
        }
        showFragment(this.fragmentHome, FragmentHome.class.getCanonicalName(), false, true);
    }

    void showSignInScreen(boolean showToolbar) {
        FragmentSignIn fragmentSignIn = (FragmentSignIn) getFragmentForTag(FragmentSignIn.class.getCanonicalName());
        if (fragmentSignIn == null) {
            fragmentSignIn = new FragmentSignIn();
        }
        showFragment(fragmentSignIn, FragmentSignIn.class.getCanonicalName(), true, showToolbar);
    }

    void showSignUpScreen() {
        FragmentSignUp fragmentSignUp = (FragmentSignUp) getFragmentForTag(FragmentSignUp.class.getCanonicalName());
        if (fragmentSignUp == null) {
            fragmentSignUp = new FragmentSignUp();
        }
        showFragment(fragmentSignUp, FragmentSignUp.class.getCanonicalName(), true, true);
    }

    void showMenuScreen() {
        FragmentMenu fragmentMenu = (FragmentMenu) getFragmentForTag(FragmentMenu.class.getCanonicalName());
        if (fragmentMenu == null) {
            fragmentMenu = new FragmentMenu();
            Bundle b = new Bundle();
            fragmentMenu.setArguments(b);
        }
        showFragment(fragmentMenu, FragmentMenu.class.getCanonicalName(), true, true);
    }


    private void showItemDetail(Serializable extras) {
        FragmentProduct fragmentProduct = (FragmentProduct) getFragmentForTag(FragmentProduct.class.getCanonicalName());
        if (fragmentProduct == null) {
            fragmentProduct = new FragmentProduct();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentProduct.setArguments(b);
        }
        showFragment(fragmentProduct, FragmentProduct.class.getCanonicalName(), true, true);
    }

    void showReserveScreen(Serializable extras) {
        FragmentReserve fragmentReserve = (FragmentReserve) getFragmentForTag(FragmentReserve.class.getCanonicalName());
        if (fragmentReserve != null) {
            //Pop to
        }
        fragmentReserve = FragmentReserve.newInstance((TopInfo.Contact) extras);
        showFragment(fragmentReserve, FragmentReserve.class.getCanonicalName(), true, true);
    }

    public void showNewsScreen() {
        FragmentNews fragmentNews = (FragmentNews) getFragmentForTag(FragmentNews.class.getCanonicalName());
        if (fragmentNews == null) {
            fragmentNews = new FragmentNews();
        }
        showFragment(fragmentNews, FragmentNews.class.getCanonicalName(), true, true);
    }

    public void showNewsDetailScreen(Serializable extras) {
        FragmentNewsDetail fragmentNewsDetail = (FragmentNewsDetail) getFragmentForTag(FragmentNewsDetail.class.getCanonicalName());
        if (fragmentNewsDetail == null) {
            fragmentNewsDetail = new FragmentNewsDetail();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
            fragmentNewsDetail.setArguments(b);
        }
        showFragment(fragmentNewsDetail, FragmentNewsDetail.class.getCanonicalName(), true, true);
    }

    void showPhotoScreen() {
        FragmentPhotoGallery fragmentPhotoGallery = (FragmentPhotoGallery) getFragmentForTag(FragmentPhotoGallery.class.getCanonicalName());
        if (fragmentPhotoGallery == null) {
            fragmentPhotoGallery = new FragmentPhotoGallery();
        }
        showFragment(fragmentPhotoGallery, FragmentPhotoGallery.class.getCanonicalName(), true, true);
    }

    void showPhotoPreviewScreen(Serializable extras) {
        PopupPhotoPreview photoPreview = new PopupPhotoPreview(this);
        photoPreview.setData(extras);
        photoPreview.show();
    }

    void showCouponScreen() {
        FragmentCoupon fragmentCoupon = (FragmentCoupon) getFragmentForTag(FragmentCoupon.class.getCanonicalName());
        if (fragmentCoupon == null) {
            fragmentCoupon = new FragmentCoupon();
        }
        showFragment(fragmentCoupon, FragmentCoupon.class.getCanonicalName(), true, true);
    }

    void showChatScreen() {
        FragmentChat fragmentChat = (FragmentChat) getFragmentForTag(FragmentChat.class.getCanonicalName());
        if (fragmentChat == null) {
            fragmentChat = new FragmentChat();
        }
        showFragment(fragmentChat, FragmentChat.class.getCanonicalName(), true, true);
    }

    void showSettingScreen() {
        FragmentSetting fragmentSetting = (FragmentSetting) getFragmentForTag(FragmentSetting.class.getCanonicalName());
        if (fragmentSetting == null) {
            fragmentSetting = new FragmentSetting();
        }
        showFragment(fragmentSetting, FragmentSetting.class.getCanonicalName(), true, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
