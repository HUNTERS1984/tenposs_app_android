package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import jp.tenposs.utils.ThemifyIcon;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    ImageButton toggleMenuButton;
    ImageButton backButton;

    TextView navTitleLabel;
    TextView navSubtitleLabel;
    ImageButton searchButton;

    DrawerLayout drawerLayout;

    boolean needUpdateNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        //      this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        ///toggle.syncState();
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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //return true;
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    AbstractFragment getTopFragment() {
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
        return topFragment;
    }

    Fragment getFragmentForTag(String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }


    private void updateNavigationBar(AbstractFragment.ToolbarSettings toolbarSettings) {

    }


    void showFragmentHome(){

    }
    void showFragmentMenu(){

    }
    void showFragmentNews(){

    }
}
