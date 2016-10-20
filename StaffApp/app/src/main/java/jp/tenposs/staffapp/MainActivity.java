package jp.tenposs.staffapp;

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
import android.widget.FrameLayout;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.HashMap;

import jp.tenposs.datamodel.Key;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.LeftMenuView;

public class MainActivity extends AppCompatActivity implements LeftMenuView.OnLeftMenuItemClickListener, AbstractFragment.MainActivityListener {

    private SharedPreferences mAppPreferences;
    private DrawerLayout mDrawerLayout;
    private LeftMenuView mLeftMenuView;
    private FrameLayout mContentContainer;
    private FragmentManager mFragmentManager;

    FragmentCouponRequest mFragmentRequest;

    boolean mSignedIn = false;
    private ProgressDialog mProgressDialog;
    protected HashMap<String, String> mSessionValues;

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
                } else if (topFragment.canCloseByBackpressed() == true) {
                    /**
                     * Back and update NavigationBar
                     */
                    topFragment.close();
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(int position, Bundle params) {

    }

    @Override
    public void showScreen(int menuId, Serializable extras) {

        switch (menuId) {
            case AbstractFragment.SIGN_IN_SCREEN: {
                showSignInScreen();
            }
            break;

            case AbstractFragment.COUPON_REQUEST_SCREEN: {
                showRequestScreen();
            }
            break;

            case AbstractFragment.COUPON_DETAIL_SCREEN: {
                showCouponDetailScreen(extras);
            }
            break;

            case AbstractFragment.QR_SCANNER_SCREEN: {
                showQrScannerScreen();
            }
            break;

            default: {
                Assert.assertFalse("Should never be here ;(", false);
            }
        }
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
    public String getSessionValue(String key, String valueDefault) {
        if (this.mSessionValues == null) {
            return valueDefault;
        } else {
            return this.mSessionValues.get(key);
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
                    getString(R.string.setting), getString(R.string.exit),
                    new DialogInterface.OnClickListener() {
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
        if (this.mSignedIn == false) {
            showScreen(AbstractFragment.SIGN_IN_SCREEN, null);
        } else {
            showScreen(AbstractFragment.COUPON_REQUEST_SCREEN, null);
        }
        //Firebase initialize
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            Utils.setPrefString(getApplicationContext(), Key.FireBaseTokenKey, token);
        } else {
            Utils.setPrefString(getApplicationContext(), Key.FireBaseTokenKey, "");
        }

    }

    void showSignInScreen() {
        FragmentSignIn fragmentSignIn = (FragmentSignIn) getFragmentForTag(FragmentSignIn.class.getCanonicalName());
        if (fragmentSignIn == null) {
            fragmentSignIn = new FragmentSignIn();
        }
        showFragment(fragmentSignIn, FragmentSignIn.class.getCanonicalName(), false);
    }

    void showRequestScreen() {
        FragmentCouponRequest fragmentCouponRequest = (FragmentCouponRequest) getFragmentForTag(FragmentCouponRequest.class.getCanonicalName());
        if (fragmentCouponRequest == null) {
            fragmentCouponRequest = new FragmentCouponRequest();
        }
        showFragment(fragmentCouponRequest, FragmentCouponRequest.class.getCanonicalName(), true);
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

    void showQrScannerScreen() {
        FragmentQrScanner fragmentQrScanner = (FragmentQrScanner) getFragmentForTag(FragmentQrScanner.class.getCanonicalName());
        if (fragmentQrScanner == null) {
            fragmentQrScanner = new FragmentQrScanner();
        }
        showFragment(fragmentQrScanner, FragmentQrScanner.class.getCanonicalName(), true);
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
}
