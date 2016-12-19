package jp.tenposs.staffapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.utils.FontIcon;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 7/26/16.
 */
public abstract class AbstractFragment extends Fragment {

    public static int NavIconSize = 22;

    public static int CatIconSize = 16;

    public interface MainActivityListener {
        void showScreen(int menuId, Serializable extras);

        void toggleMenu();

        FragmentManager getFM();

        void setDrawerLockMode(int mode);

        void showFragment(AbstractFragment fragment, String fragmentTag, boolean animated);

        void setSessionValue(String key, String value);

        String getSessionValue(String key, String valueDefault);
    }

    public static final int WIFI_SETTINGS = 0xDAD0;
    public static final int CAPTURE_IMAGE_REQUEST = 0xDAD1;
    public static final int CAPTURE_IMAGE_INTENT = 0xDAD2;
    public static final int CROP_IMAGE_INTENT = 0xDAD3;

    public final static int SIGN_IN_SCREEN = 1;
    public final static int COUPON_REQUEST_SCREEN = 2;
    public final static int QR_SCANNER_SCREEN = 3;

    public final static int PROFILE_SECTION = -2;


    public final static int HOME_SCREEN = 6;

    public final static int MENU_SCREEN = 2;
    public final static int ITEM_SCREEN = 201;
    public final static int ITEM_PURCHASE_SCREEN = 202;


    public final static int NEWS_SCREEN = 3;
    public final static int NEWS_DETAILS_SCREEN = 301;

    public final static int RESERVE_SCREEN = 4;

    public final static int PHOTO_SCREEN = 5;
    public final static int PHOTO_ITEM_SCREEN = 501;

    public final static int CHAT_SCREEN = 7;

    public final static int STAFF_SCREEN = 8;
    public final static int STAFF_DETAIL_SCREEN = 801;
    public final static int STAFF_UNKNOWN_SCREEN = 802;

    public final static int COUPON_SCREEN = 9;
//    public final static int COUPON_DETAIL_SCREEN = 901;

    public final static int SETTING_SCREEN = 10;
    public final static int PROFILE_SCREEN = 1001;
    public final static int ISSUE_INFO_SCREEN = 1002;
    public final static int COMPANY_INFO_SCREEN = 1003;
    public final static int USER_PRIVACY_SCREEN = 1004;


    public final static int SIGN_IN_EMAIL_SCREEN = 1101;
    public final static int SIGN_UP_SCREEN = 1102;


    public final static int SIGN_OUT_SCREEN = 16;
    public final static int MY_PAGE_SCREEN = 17;


    public final static int DEFAULT_RECORD_PER_PAGE = 48;

    protected String Tag = this.getClass().getSimpleName();

    public class ToolbarSettings {

        static final int LEFT_MENU_BUTTON = 1;
        static final int LEFT_BACK_BUTTON = 2;

        public String toolbarTitle;
        public String toolbarLeftIcon;
        public String toolbarRightIcon;
        public int toolbarType;
//        AppInfo.AppSetting appSetting;
//
//        public int getToolbarIconColor() {
//            if (appSetting != null) {
//                return appSetting.getToolbarIconColor();
//            } else {
//                return Color.parseColor("#14c8c8");
//            }
//        }
//
//        public int getToolbarTitleColor() {
//            if (appSetting != null) {
//                return appSetting.getToolbarTitleColor();
//            } else {
//                return Color.parseColor("#505a5e");
//            }
//        }
//
//        public int getToolbarBackgroundColor() {
//            if (appSetting != null) {
//                return appSetting.getToolbarBackgroundColor();
//            } else {
//                return Color.WHITE;
//            }
//        }

//        public int getMenuIconColor() {
//            if (appSetting != null) {
//                return appSetting.getMenuIconColor();
//            } else {
//                return Color.WHITE;
//            }
//        }

//        public int getMenuBackgroundColor() {
//            if (appSetting != null) {
//                return appSetting.getMenuBackgroundColor();
//            } else {
//                return Color.parseColor("#2F455B");
//            }
//        }
//
//        public String getMenuItemTitleFontSize() {
//            if (appSetting != null) {
//                return appSetting.getMenuItemTitleFontSize();
//            } else {
//                return "";
//            }
//        }
//
//        public String getToolbarTitleFontSize() {
//            if (appSetting != null) {
//                return appSetting.getToolbarTitleFontSize();
//            } else {
//                return "";
//            }
//        }
//
//        public int getMenuItemTitleColor() {
//            if (appSetting != null) {
//                return appSetting.getMenuItemTitleColor();
//            } else {
//                return Color.WHITE;
//            }
//        }
//
//        public String getMenuItemTitleFont() {
//            if (appSetting != null) {
//                return appSetting.getMenuItemTitleFont();
//            } else {
//                return "Arial";
//            }
//        }
//
//        public String getToolBarTitleFont() {
//            if (appSetting != null) {
//                return appSetting.getToolBarTitleFont();
//            } else {
//                return "Arial";
//            }
//        }

    }

    public static String SCREEN_DATA = "SCREEN_DATA";
    public static String SCREEN_TITLE = "SCREEN_TITLE";
    public static String SCREEN_PAGE_ITEMS = "SCREEN_PAGE_ITEMS";
    public static String SCREEN_DATA_ITEM_INDEX = "SCREEN_DATA_ITEM_INDEX";

    public static String SCREEN_DATA_PAGE_INDEX = "SCREEN_DATA_PAGE_INDEX";
    public static String SCREEN_DATA_PAGE_SIZE = "SCREEN_DATA_PAGE_SIZE";
    public static String SCREEN_DATA_PAGE_DATA = "SCREEN_DATA_PAGE_DATA";

    public static String APP_DATA = "APP_DATA";
    public static String APP_DATA_STORE_ID = "APP_DATA_STORE_ID";
    public static String SCREEN_DATA_STATUS = "SCREEN_DATA_STATUS";


    final static int LOADING_STATUS_UNKNOWN = -1;
    final static int LOADING_STATUS_REFRESH = 0;
    final static int LOADING_STATUS_MORE = 1;

    protected int mThumbImageSize = 320;
    protected int mFullImageSize = 1024;

    protected int mSpanCount = 1;
    protected int mSpanLargeItems = 1;
    protected int mSpanSmallItems = 1;

    int mStoreId;
    int mPageIndex = 1;
    int mPageSize = DEFAULT_RECORD_PER_PAGE;
    int mLoadingStatus = LOADING_STATUS_UNKNOWN;

    protected String mScreenTitle = "";
    public ToolbarSettings mToolbarSettings;
    protected ScreenDataStatus mScreenDataStatus = ScreenDataStatus.ScreenDataStatusUnload;
    protected SharedPreferences mAppPreferences;
    protected MainActivityListener mActivityListener;
    protected MainApplication mApplication;
    List<RecyclerItemWrapper> mScreenDataItems = new ArrayList<>();
    boolean mScreenToolBarHidden = true;

    protected ViewGroup mFragmentContent;
    Toolbar mToolbar;
    ImageButton mLeftToolbarButton;
    TextView mTitleToolbarLabel;
    ImageButton mRightToolbarButton;

    protected ProgressDialog mProgressDialog;

    protected abstract boolean customClose();

    protected abstract void customToolbarInit();

    protected abstract void clearScreenData();

    protected abstract void reloadScreenData();

    protected abstract void previewScreenData();

    protected abstract View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract void customResume();

    abstract void loadSavedInstanceState(@NonNull Bundle savedInstanceState);

    abstract void customSaveInstanceState(Bundle outState);

    abstract void setRefreshing(boolean refreshing);

    abstract boolean canCloseByBackpressed();


    /**
     * Fragment Life cycles
     */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("Fragment Life Cycle onAttach " + this.getClass().getSimpleName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.mSpanCount = getResources().getInteger(R.integer.span_count);
//        this.mSpanLargeItems = getResources().getInteger(R.integer.span_large_items);
//        this.mSpanSmallItems = getResources().getInteger(R.integer.span_small_items);


        setRetainInstance(true);

        setupVariables();
        System.out.println("Fragment Life Cycle onCreate " + this.getClass().getSimpleName());

        if (savedInstanceState == null) {
            savedInstanceState = getArguments();
        }
        restoreSavedInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("Fragment Life Cycle onCreateView " + this.getClass().getSimpleName());

        restoreSavedInstanceState(savedInstanceState);
        mToolbarSettings = new ToolbarSettings();
        try {
//            mToolbarSettings.appSetting = mActivityListener.getAppInfo().app_setting;
        } catch (Exception ignored) {

        }

        customToolbarInit();

        if (mScreenTitle.length() > 0) {
            mToolbarSettings.toolbarTitle = mScreenTitle;
        }

        View view = onCustomCreateView(inflater, container, savedInstanceState);
        this.mToolbar = (Toolbar) view.findViewById(R.id.toolbar);

        this.mLeftToolbarButton = (ImageButton) view.findViewById(R.id.left_toolbar_button);
        this.mTitleToolbarLabel = (TextView) view.findViewById(R.id.title_toolbar_label);
        this.mRightToolbarButton = (ImageButton) view.findViewById(R.id.right_toolbar_button);

        if (this.mLeftToolbarButton != null) {
            this.mLeftToolbarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mToolbarSettings.toolbarType == ToolbarSettings.LEFT_MENU_BUTTON) {
                        if (mScreenDataStatus == ScreenDataStatus.ScreenDataStatusLoaded) {
                            mActivityListener.toggleMenu();
                        }
                    } else {
                        close();
                    }
                }
            });
        }

        mFragmentContent = (ViewGroup) view.findViewById(R.id.fragment_content);


        mFragmentContent.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setUserVisibleHint(true);
                            }
                        }, 200);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mFragmentContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            mFragmentContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                        }
                    }
                });
        updateToolbar();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("Fragment Life Cycle onViewCreated " + this.getClass().getSimpleName());

        restoreSavedInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("Fragment Life Cycle onActivityCreated " + this.getClass().getSimpleName());

        restoreSavedInstanceState(savedInstanceState);
        setupVariables();
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("Fragment Life Cycle onStart " + this.getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        customResume();
        updateToolbar();
        System.out.println("Fragment Life Cycle onResume " + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("Fragment Life Cycle onPause " + this.getClass().getSimpleName());
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Fragment Life Cycle onStop " + this.getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("Fragment Life Cycle onDestroyView " + this.getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Fragment Life Cycle onDestroy " + this.getClass().getSimpleName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("Fragment Life Cycle onDetach " + this.getClass().getSimpleName());

        //updateToolbar
        AbstractFragment topFragment = getTopFragment();
        if (topFragment != null) {
            topFragment.updateToolbar();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCREEN_DATA_STATUS, this.mScreenDataStatus.ordinal());
        customSaveInstanceState(outState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        System.out.println(this.getClass().getSimpleName() + " VisibleToUser " + hidden);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //
            this.updateToolbar();
            System.out.println(this.getClass().getSimpleName() + " VisibleToUser");
        }
    }

    /*@Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState == null) {
            savedInstanceState = getArguments();
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA_STATUS)) {
                this.mScreenDataStatus = ScreenDataStatus.fromInt(savedInstanceState.getInt(SCREEN_DATA_STATUS));
            }
            loadSavedInstanceState(savedInstanceState);
        }
    }*/

    void setupVariables() {
        if (this.mActivityListener == null) {
            this.mActivityListener = (MainActivityListener) getActivity();
            this.mApplication = (MainApplication) getActivity().getApplication();
        }
        if (this.mAppPreferences == null) {
            this.mAppPreferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        }
    }

//    protected String getPrefString(String key) {
//        setupVariables();
//        return this.mAppPreferences.getString(key, "");
//    }
//
//    protected boolean setPref(String key, String value) {
//        setupVariables();
//        boolean ret;
//        SharedPreferences.Editor editor = this.mAppPreferences.edit();
//        editor.putString(key, value);
//        ret = editor.commit();
//        return ret;
//    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim == 0)
            return null;

        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);

        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                System.out.println("Animation started.");
                if (mToolbarSettings != null) {
                    //mActivityListener.updateNavigationBar(mToolbarSettings);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                System.out.println("Animation repeating.");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                System.out.println("Animation ended.");
            }
        });

        return anim;
    }

    protected void hideKeyboard() {
        Utils.hideKeyboard(this.getActivity(), null);
    }

    protected void close() {
        try {
            hideKeyboard();
            if (customClose() == false) {
                getActivity().getSupportFragmentManager().popBackStack();
            }

        } catch (Exception ignored) {

        }
    }

    protected void errorWithMessage(Bundle response, String message) {
        setRefreshing(false);
        if (response != null) {
            String msg = response.getString(Key.ResponseMessage);
            Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }


    private void restoreSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA_STATUS)) {
                this.mScreenDataStatus = ScreenDataStatus.fromInt(savedInstanceState.getInt(SCREEN_DATA_STATUS));
            }
            if (savedInstanceState.containsKey(SCREEN_TITLE)) {
                this.mScreenTitle = savedInstanceState.getString(SCREEN_TITLE);
            }
            loadSavedInstanceState(savedInstanceState);
        }
    }

    public static String getMenuIconName(int menuId) {
        switch (menuId) {
            case HOME_SCREEN: {
                return "flaticon-home";
            }
            case MENU_SCREEN: {
                return "flaticon-menu";
            }
            case NEWS_SCREEN: {
                return "flaticon-news";
            }
            case RESERVE_SCREEN: {
                return "flaticon-reserve";
            }
            case PHOTO_SCREEN: {
                return "flaticon-photo-gallery";
            }
            case CHAT_SCREEN: {
                return "flaticon-chat";
            }
            case STAFF_SCREEN: {
                return "flaticon-staff";
            }
            case COUPON_SCREEN: {
                return "flaticon-coupon";
            }
            case SETTING_SCREEN: {
                return "flaticon-settings";
            }
            case SIGN_OUT_SCREEN: {
                return "flaticon-sign-out";
            }
            default:
                return "";
        }
    }

    AbstractFragment getTopFragment() {
        AbstractFragment topFragment = null;
        try {
            FragmentManager fm = mActivityListener.getFM();
            int size = fm.getBackStackEntryCount();
            FragmentManager.BackStackEntry backStackEntry = fm.getBackStackEntryAt(size - 1);
            topFragment = (AbstractFragment) fm.findFragmentByTag(backStackEntry.getName());
        } catch (Exception ex) {

        }
        return topFragment;
    }

    protected void updateToolbar() {
        try {
            //this.mToolbar.setBackgroundColor(this.mToolbarSettings.getToolbarBackgroundColor());
            this.mToolbar.setBackgroundColor(Color.WHITE);
            if (this.mLeftToolbarButton != null) {
                this.mLeftToolbarButton.setImageBitmap(FontIcon.imageForFontIdentifier(
                        getActivity().getAssets(),
                        this.mToolbarSettings.toolbarLeftIcon,
                        NavIconSize,
                        Color.argb(0, 0, 0, 0),
                        //this.mToolbarSettings.getToolbarIconColor()
                        Color.argb(255, 20, 200, 200),
                        FontIcon.FLATICON
                ));
            }
            if (this.mRightToolbarButton != null) {
                this.mRightToolbarButton.setImageBitmap(FontIcon.imageForFontIdentifier(getActivity().getAssets(),
                        this.mToolbarSettings.toolbarRightIcon,
                        NavIconSize,
                        Color.argb(0, 0, 0, 0),
                        //this.mToolbarSettings.getToolbarIconColor()
                        Color.argb(255, 20, 200, 200),
                        FontIcon.FLATICON
                ));
            }

            if (this.mTitleToolbarLabel != null) {
                this.mTitleToolbarLabel.setText(mToolbarSettings.toolbarTitle);
//                try {
//                    Utils.setTextApperance(getContext(), this.mTitleToolbarLabel, mToolbarSettings.getToolbarTitleFontSize());
//                    Typeface type = Utils.getTypeFaceForFont(getActivity(), mToolbarSettings.getToolBarTitleFont());
//                    this.mTitleToolbarLabel.setTypeface(type);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
                //this.mTitleToolbarLabel.setTextColor(mToolbarSettings.getToolbarTitleColor());
                this.mTitleToolbarLabel.setTextColor(Color.BLACK);
            }

            if (this.mToolbarSettings.toolbarType == ToolbarSettings.LEFT_MENU_BUTTON) {
                if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusLoaded) {
                    this.mActivityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else {
                    this.mActivityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }

            } else {
                this.mActivityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            if (this.mScreenToolBarHidden == true) {
                this.mToolbar.setVisibility(View.VISIBLE);
            } else {
                this.mActivityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                this.mToolbar.setVisibility(View.GONE);
            }

        } catch (Exception ignored) {

        }
    }

    boolean isSignedIn() {
        String token = Utils.getPrefString(this.getContext(), Key.TokenKey);
        String userProfile = Utils.getPrefString(this.getContext(), Key.UserProfile);
        if (token.length() > 0 && userProfile.length() > 0) {
            return true;
        } else {
            return false;
        }
    }
}