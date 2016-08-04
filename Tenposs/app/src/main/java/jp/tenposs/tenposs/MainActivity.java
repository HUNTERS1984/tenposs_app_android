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
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import junit.framework.Assert;

import jp.tenposs.datamodel.HomeObject;
import jp.tenposs.datamodel.HomeScreenItem;
import jp.tenposs.datamodel.Key;
import jp.tenposs.utils.ThemifyIcon;
import jp.tenposs.view.LeftMenuView;

public class MainActivity extends AppCompatActivity
        implements
        AbstractFragment.MainActivityListener,
        LeftMenuView.OnLeftMenuItemClickListener {

    static final String HOME_DATA = "HOME_DATA";
    FragmentManager fragmentManager;
    boolean needUpdateNavigationBar;
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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


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

        this.fragmentHome = (FragmentHome) getFragmentForTag(FragmentHome.class.getCanonicalName());
        if (this.fragmentHome == null) {
            this.fragmentHome = new FragmentHome();
            showFragment(this.fragmentHome, FragmentHome.class.getCanonicalName());
        } else {
            updateMenuItems(fragmentHome.screenData);
        }

        leftMenuView.setOnItemClickListener(this);
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
                            case DialogInterface.BUTTON_POSITIVE:
                                exitActivity();


                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
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
    public void updateMenuItems(HomeObject homeObject) {
        if (leftMenuView != null) {
            leftMenuView.updateMenuItems(homeObject);
        }
    }

    @Override
    public void updateNavigationBar(AbstractFragment.ToolbarSettings toolbarSettings) {

        if (toolbarSettings.toolbarType == AbstractFragment.ToolbarSettings.LEFT_MENU_BUTTON) {
            toggleMenuButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.GONE);
            toggleMenuButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getApplication().getAssets(),
                    toolbarSettings.toolbarIcon,
                    28,
                    Color.argb(0, 0, 0, 0),
                    toolbarSettings.settings.getColor()
            ));

        } else {
            toggleMenuButton.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            backButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getApplication().getAssets(),
                    toolbarSettings.toolbarIcon,
                    28,
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
    public void showScreen(HomeScreenItem screenItem) {

    }

    @Override
    public void onClick(int position, Bundle params) {
        drawerLayout.closeDrawer(Gravity.LEFT);


        if (position == -1) {
            showSignInScreen();
        } else {
            HomeScreenItem item = (HomeScreenItem) params.getSerializable(Key.RequestObject);
            long menuId = item.itemId;
            if (menuId == HomeScreenItem.HOME_SCREEN) {
                showHomeScreen();

            } else if (menuId == HomeScreenItem.MENU_SCREEN) {
                showMenuScreen();

            } else if (menuId == HomeScreenItem.RESERVE_SCREEN) {
                showReserveScreen();

            } else if (menuId == HomeScreenItem.NEWS_SCREEN) {
                showNewsScreen();

            } else if (menuId == HomeScreenItem.PHOTO_SCREEN) {
                showPhotoScreen();

            } else if (menuId == HomeScreenItem.COUPON_SCREEN) {
                showCouponScreen();

            } else if (menuId == HomeScreenItem.CHAT_SCREEN) {
                showChatScreen();

            } else if (menuId == HomeScreenItem.SETTING_SCREEN) {
                showSettingScreen();

            }
        }
    }

    void exitActivity() {
        MainActivity.this.finish();
    }

    void loadAppSettings() {

    }

    void saveAppSettings() {

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

    void showFragment(AbstractFragment fragment, String fragmentTag) {
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
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
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
        showFragment(this.fragmentHome, FragmentHome.class.getCanonicalName());
    }

    void showSignInScreen() {
        FragmentSignin fragmentSignin = (FragmentSignin) getFragmentForTag(FragmentSignin.class.getCanonicalName());
        if (fragmentSignin == null) {
            fragmentSignin = new FragmentSignin();
        }
        showFragment(fragmentSignin, FragmentSignin.class.getCanonicalName());
    }

    void showMenuScreen() {
        FragmentMenu fragmentMenu = (FragmentMenu) getFragmentForTag(FragmentMenu.class.getCanonicalName());
        if (fragmentMenu == null) {
            fragmentMenu = new FragmentMenu();
        }
        showFragment(fragmentMenu, FragmentMenu.class.getCanonicalName());
    }

    void showReserveScreen() {
        FragmentReserve fragmentReserve = (FragmentReserve) getFragmentForTag(FragmentReserve.class.getCanonicalName());
        if (fragmentReserve == null) {
            fragmentReserve = new FragmentReserve();
        }
        showFragment(fragmentReserve, FragmentReserve.class.getCanonicalName());
    }

    void showNewsScreen() {
        FragmentNews fragmentNews = (FragmentNews) getFragmentForTag(FragmentNews.class.getCanonicalName());
        if (fragmentNews == null) {
            fragmentNews = new FragmentNews();
        }
        showFragment(fragmentNews, FragmentNews.class.getCanonicalName());
    }

    void showPhotoScreen() {
        FragmentPhotoGallery fragmentPhotoGallery = (FragmentPhotoGallery) getFragmentForTag(FragmentPhotoGallery.class.getCanonicalName());
        if (fragmentPhotoGallery == null) {
            fragmentPhotoGallery = new FragmentPhotoGallery();
        }
        showFragment(fragmentPhotoGallery, FragmentPhotoGallery.class.getCanonicalName());
    }

    void showCouponScreen() {
        FragmentCoupon fragmentCoupon = (FragmentCoupon) getFragmentForTag(FragmentCoupon.class.getCanonicalName());
        if (fragmentCoupon == null) {
            fragmentCoupon = new FragmentCoupon();
        }
        showFragment(fragmentCoupon, FragmentCoupon.class.getCanonicalName());
    }

    void showChatScreen() {
        FragmentChat fragmentChat = (FragmentChat) getFragmentForTag(FragmentChat.class.getCanonicalName());
        if (fragmentChat == null) {
            fragmentChat = new FragmentChat();
        }
        showFragment(fragmentChat, FragmentChat.class.getCanonicalName());
    }

    void showSettingScreen() {
        FragmentSetting fragmentSetting = (FragmentSetting) getFragmentForTag(FragmentSetting.class.getCanonicalName());
        if (fragmentSetting == null) {
            fragmentSetting = new FragmentSetting();
        }
        showFragment(fragmentSetting, FragmentSetting.class.getCanonicalName());
    }

}
