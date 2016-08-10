package jp.tenposs.tenposs;

import android.content.DialogInterface;
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
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import junit.framework.Assert;

import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.LoginInfo;
import jp.tenposs.datamodel.SideMenuInfo;
import jp.tenposs.utils.ThemifyIcon;
import jp.tenposs.view.LeftMenuView;

public class MainActivity extends AppCompatActivity
        implements
        AbstractFragment.MainActivityListener,
        LeftMenuView.OnLeftMenuItemClickListener {

    public final static long HOME_SCREEN = 0;
    public final static long MENU_SCREEN = 1;
    public final static long RESERVE_SCREEN = 2;
    public final static long NEWS_SCREEN = 3;
    public final static long PHOTO_SCREEN = 4;
    public final static long COUPON_SCREEN = 5;
    public final static long CHAT_SCREEN = 6;
    public final static long SETTING_SCREEN = 7;

    AppInfo.Response appInfo;
    int storeId;
    AppInfo.Response.ResponseData storeInfo;

    SideMenuInfo.Response sideMenuInfo;

    LoginInfo.Response userInfo;

    FragmentManager fragmentManager;
    FragmentHome fragmentHome;

    ImageButton toggleMenuButton;
    ImageButton backButton;

    TextView navTitleLabel;
    TextView navSubtitleLabel;
    ImageButton searchButton;

    DrawerLayout drawerLayout;
    LeftMenuView leftMenuView;
    FrameLayout contentContainer;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AbstractFragment topFragment = getTopFragment();
        if (topFragment != null) {
            updateNavigationBar(topFragment.toolbarSettings);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainApplication.setContext(this.getApplicationContext());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        toggleMenuButton = (ImageButton) findViewById(R.id.toggle_menu_button);
        backButton = (ImageButton) findViewById(R.id.back_button);

        navTitleLabel = (TextView) findViewById(R.id.nav_title_label);
        navSubtitleLabel = (TextView) findViewById(R.id.nav_subtitle_label);
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
                if (topFragment != null) {
                    updateNavigationBar(topFragment.toolbarSettings);
                }
            }
        });

        leftMenuView.setOnItemClickListener(this);

        this.fragmentHome = (FragmentHome) getFragmentForTag(FragmentHome.class.getCanonicalName());
        if (this.fragmentHome == null) {
            this.fragmentHome = new FragmentHome();
            showFragment(this.fragmentHome, FragmentHome.class.getCanonicalName(), false);
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
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void updateAppInfo(AppInfo.Response appInfo, int storeId) {
        this.appInfo = appInfo;
        this.storeId = storeId;
        this.storeInfo = this.appInfo.get(this.storeId);
    }

    @Override
    public void updateSideMenuItems(SideMenuInfo.Response menuInfo) {
        this.sideMenuInfo = menuInfo;
        if (leftMenuView != null && this.sideMenuInfo != null) {
            leftMenuView.updateMenuItems(menuInfo);
        }
    }

    @Override
    public void updateUserInfo(LoginInfo.Response userInfo) {
        this.userInfo = userInfo;
        if (leftMenuView != null) {
            leftMenuView.updateUserInfo(userInfo);
        }
    }

    @Override
    public void updateNavigationBar(AbstractFragment.ToolbarSettings toolbarSettings) {
        if (toolbarSettings.toolbarType == AbstractFragment.ToolbarSettings.LEFT_MENU_BUTTON) {
            toggleMenuButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.GONE);
            toggleMenuButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getApplication().getAssets(),
                    toolbarSettings.toolbarIcon,
                    40,
                    Color.argb(0, 0, 0, 0),
                    toolbarSettings.settings.getColor()
            ));

        } else {
            toggleMenuButton.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            backButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getApplication().getAssets(),
                    toolbarSettings.toolbarIcon,
                    40,
                    Color.argb(0, 0, 0, 0),
                    toolbarSettings.settings.getColor()
            ));
        }

        navTitleLabel.setText(toolbarSettings.toolbarTitle);
        navTitleLabel.setTextColor(toolbarSettings.titleSettings.getColor());
        try {
            Typeface type = Typeface.createFromAsset(getAssets(), toolbarSettings.settings.getFont());
            navTitleLabel.setTypeface(type);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void showScreen(SideMenuInfo.Response.ResponseData.Menu menuItem) {

    }

    @Override
    public void onClick(int position, Bundle params) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        if (position == -1) {
            showSignInScreen();
        } else {
            SideMenuInfo.Response.ResponseData.Menu menuItem = (SideMenuInfo.Response.ResponseData.Menu) params.getSerializable(Key.RequestObject);
            int menuId = menuItem.id;
            if (menuId == HOME_SCREEN) {
                showHomeScreen();

            } else if (menuId == MENU_SCREEN) {
                showMenuScreen();

            } else if (menuId == RESERVE_SCREEN) {
                showReserveScreen();

            } else if (menuId == NEWS_SCREEN) {
                showNewsScreen();

            } else if (menuId == PHOTO_SCREEN) {
                showPhotoScreen();

            } else if (menuId == COUPON_SCREEN) {
                showCouponScreen();

            } else if (menuId == CHAT_SCREEN) {
                showChatScreen();

            } else if (menuId == SETTING_SCREEN) {
                showSettingScreen();

            }
        }
    }

    void exitActivity() {
        MainActivity.this.finish();
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

    void showFragment(AbstractFragment fragment, String fragmentTag, boolean animated) {
        if (fragment.isAdded()) {
            Log.d("Fragment " + fragmentTag, "IS ADDED!!!");
            if (fragmentManager.popBackStackImmediate(fragmentTag, 0)) {
                Log.e("Fragment", "IS ADDED AND POPPED!!!");
            } else {
                Log.e("Fragment", "IS ADDED BUT NOT POPPED!!!");
            }
            updateNavigationBar(((AbstractFragment) fragment).toolbarSettings);
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
    }

    void showHomeScreen() {
        if (this.fragmentHome == null) {
            Assert.assertFalse("Should never be here ;(", false);
            this.fragmentHome = new FragmentHome();
        }
        showFragment(this.fragmentHome, FragmentHome.class.getCanonicalName(), false);
    }

    void showSignInScreen() {
        FragmentSignin fragmentSignin = (FragmentSignin) getFragmentForTag(FragmentSignin.class.getCanonicalName());
        if (fragmentSignin == null) {
            fragmentSignin = new FragmentSignin();
        }
        showFragment(fragmentSignin, FragmentSignin.class.getCanonicalName(), true);
    }

    void showMenuScreen() {
        FragmentMenu fragmentMenu = (FragmentMenu) getFragmentForTag(FragmentMenu.class.getCanonicalName());
        if (fragmentMenu == null) {
            fragmentMenu = new FragmentMenu();
            Bundle b = new Bundle();
            b.putSerializable(AbstractFragment.SCREEN_DATA, this.storeInfo.menus);
            fragmentMenu.setArguments(b);
        }
        showFragment(fragmentMenu, FragmentMenu.class.getCanonicalName(), true);
    }

    void showReserveScreen() {
        FragmentReserve fragmentReserve = (FragmentReserve) getFragmentForTag(FragmentReserve.class.getCanonicalName());
        if (fragmentReserve == null) {
            fragmentReserve = new FragmentReserve();
        }
        showFragment(fragmentReserve, FragmentReserve.class.getCanonicalName(), true);
    }

    void showNewsScreen() {
        FragmentNews fragmentNews = (FragmentNews) getFragmentForTag(FragmentNews.class.getCanonicalName());
        if (fragmentNews == null) {
            fragmentNews = new FragmentNews();
        }
        showFragment(fragmentNews, FragmentNews.class.getCanonicalName(), true);
    }

    void showPhotoScreen() {
        FragmentPhotoGallery fragmentPhotoGallery = (FragmentPhotoGallery) getFragmentForTag(FragmentPhotoGallery.class.getCanonicalName());
        if (fragmentPhotoGallery == null) {
            fragmentPhotoGallery = new FragmentPhotoGallery();
        }
        showFragment(fragmentPhotoGallery, FragmentPhotoGallery.class.getCanonicalName(), true);
    }

    void showCouponScreen() {
        FragmentCoupon fragmentCoupon = (FragmentCoupon) getFragmentForTag(FragmentCoupon.class.getCanonicalName());
        if (fragmentCoupon == null) {
            fragmentCoupon = new FragmentCoupon();
        }
        showFragment(fragmentCoupon, FragmentCoupon.class.getCanonicalName(), true);
    }

    void showChatScreen() {
        FragmentChat fragmentChat = (FragmentChat) getFragmentForTag(FragmentChat.class.getCanonicalName());
        if (fragmentChat == null) {
            fragmentChat = new FragmentChat();
        }
        showFragment(fragmentChat, FragmentChat.class.getCanonicalName(), true);
    }

    void showSettingScreen() {
        FragmentSetting fragmentSetting = (FragmentSetting) getFragmentForTag(FragmentSetting.class.getCanonicalName());
        if (fragmentSetting == null) {
            fragmentSetting = new FragmentSetting();
        }
        showFragment(fragmentSetting, FragmentSetting.class.getCanonicalName(), true);
    }
}
