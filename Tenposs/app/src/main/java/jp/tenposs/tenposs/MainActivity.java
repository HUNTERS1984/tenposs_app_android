package jp.tenposs.tenposs;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import jp.tenposs.datamodel.HomeScreenItem;
import jp.tenposs.datamodel.HomeObject;
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

        toggleMenuButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getApplication().getAssets(),
                "ti-menu",
                28,
                Color.argb(0, 0, 0, 0),
                Color.parseColor("#00CECB")
        ));
        toggleMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });


        this.fragmentManager = getSupportFragmentManager();
        this.fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (needUpdateNavigationBar == true) {
                    needUpdateNavigationBar = false;
                    AbstractFragment topFragment = getTopFragment();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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


    @Override
    public void updateMenuItems(HomeObject homeObject) {
        if (leftMenuView != null) {
            leftMenuView.updateMenuItems(homeObject);
        }
    }

    void loadAppSettings() {

    }

    void saveAppSettings() {

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

    @Override
    public void updateNavigationBar(AbstractFragment.ToolbarSettings toolbarSettings) {

        if (toolbarSettings.toolbarType == 1) {
            toggleMenuButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.INVISIBLE);
            toggleMenuButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getApplication().getAssets(),
                    toolbarSettings.toolbarIcon,
                    28,
                    Color.argb(0, 0, 0, 0),
                    toolbarSettings.settings.getColor()
            ));

        } else {
            toggleMenuButton.setVisibility(View.INVISIBLE);
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
        if (position == -1) {
            showSignIn();
        }
    }

    void showSignIn() {
        FragmentSignin fragmentSignin = new FragmentSignin();
        showFragment(fragmentSignin, FragmentSignin.class.getCanonicalName());
    }
}
