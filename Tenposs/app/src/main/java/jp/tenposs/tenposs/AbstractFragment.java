package jp.tenposs.tenposs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.utils.FlatIcon;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 7/26/16.
 */
public abstract class AbstractFragment extends Fragment {

    public interface MainActivityListener {
        void updateAppInfo(AppInfo.Response appInfo, int storeId);

        void updateSideMenuItems(ArrayList<AppInfo.SideMenu> menuInfo, boolean isSignedIn);

        void updateUserInfo(SignInInfo.Profile profile);

        void showScreen(int menuId, Serializable extras);

        AppInfo.Response.ResponseData getAppInfo();

        CallbackManager getCallbackManager();

        void toggleMenu();

        FragmentManager getFM();

        void setDrawerLockMode(int mode);
    }

    public final static int PROFILE_SECTION = -2;


    public final static int HOME_SCREEN = 6;

    public final static int MENU_SCREEN = 2;
    public final static int ITEM_SCREEN = 201;

    public final static int NEWS_SCREEN = 3;
    public final static int NEWS_DETAILS_SCREEN = 301;

    public final static int RESERVE_SCREEN = 4;

    public final static int PHOTO_SCREEN = 5;
    public final static int PHOTO_ITEM_SCREEN = 501;

    public final static int CHAT_SCREEN = 7;

    public final static int STAFF_SCREEN = 8;
    public final static int STAFF_DETAIL_SCREEN = 801;

    public final static int COUPON_SCREEN = 9;
    public final static int COUPON_DETAIL_SCREEN = 901;

    public final static int SETTING_SCREEN = 10;
    public final static int PROFILE_SCREEN = 1001;


    public final static int SIGN_IN_SCREEN = 11;
    public final static int SIGN_IN_EMAIL_SCREEN = 1101;
    public final static int SIGN_UP_SCREEN = 1102;

    public final static int PURCHASE_SCREEN = 12;
    public final static int COMPANY_INFO_SCREEN = 14;
    public final static int USER_PRIVACY_SCREEN = 15;

    public final static int SIGN_OUT_SCREEN = 16;

    public class ToolbarSettings {

        static final int LEFT_MENU_BUTTON = 1;
        static final int LEFT_BACK_BUTTON = 2;

        public String toolbarTitle;
        public String toolbarLeftIcon;
        public String toolbarRightIcon;
        public int toolbarType;
        AppInfo.AppSetting appSetting;

        public int getToolbarIconColor() {
            if (appSetting != null) {
                return appSetting.getToolbarIconColor();
            } else {
                return Color.parseColor("#14c8c8");
            }
        }

        public int getToolbarTitleColor() {
            if (appSetting != null) {
                return appSetting.getToolbarTitleColor();
            } else {
                return Color.parseColor("#505a5e");
            }
        }

        public int getToolbarBackgroundColor() {
            if (appSetting != null) {
                return appSetting.getToolbarTitleColor();
            } else {
                return Color.WHITE;
            }
        }

        public int getMenuBackgroundColor() {
            if (appSetting != null) {
                return appSetting.getMenuBackgroundColor();
            } else {
                return Color.parseColor("#2F455B");
            }
        }

        public int getMenuIconColor() {
            if (appSetting != null) {
                return appSetting.getMenuIconColor();
            } else {
                return Color.WHITE;
            }
        }

        public int getMenuTitleColor() {
            if (appSetting != null) {
                return appSetting.getMenuTitleColor();
            } else {
                return Color.WHITE;
            }
        }

        public String getToolBarTitleFont() {
            if (appSetting != null) {
                return appSetting.getToolBarTitleFont();
            } else {
                return "fonts/Arial.ttf";
            }
        }
    }

    public static String SCREEN_DATA = "SCREEN_DATA";
    public static String SCREEN_TITLE = "SCREEN_TITLE";
    public static String SCREEN_PAGE_ITEMS = "SCREEN_PAGE_ITEMS";

    public static String SCREEN_DATA_PAGE_INDEX = "SCREEN_DATA_PAGE_INDEX";
    public static String SCREEN_DATA_PAGE_SIZE = "SCREEN_DATA_PAGE_SIZE";
    public static String SCREEN_DATA_PAGE_DATA = "SCREEN_DATA_PAGE_DATA";

    public static String APP_DATA_STORE_ID = "APP_DATA_STORE_ID";
    public static String SCREEN_DATA_STATUS = "SCREEN_DATA_STATUS";

    protected int thumbImageSize = 320;
    protected int fullImageSize = 1024;

    protected int spanCount = 1;
    protected int spanLargeItems = 1;
    protected int spanSmallItems = 1;
    protected String screenTitle = "";
    public ToolbarSettings toolbarSettings;
    protected ScreenDataStatus screenDataStatus = ScreenDataStatus.ScreenDataStatusUnload;
    protected SharedPreferences appPreferences;
    protected MainActivityListener activityListener;
    List<RecyclerItemWrapper> screenDataItems = new ArrayList<>();
    boolean screenToolBarHidden = true;

    protected ViewGroup fragmentContent;
    Toolbar toolbar;
    ImageButton leftToolbarButton;
    TextView titleToolbarLabel;
    ImageButton rightToolbarButton;

    protected ProgressDialog progressDialog;

    protected abstract void customClose();

    protected abstract void customToolbarInit();

    protected abstract void reloadScreenData();

    protected abstract void previewScreenData();

    protected abstract View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract void customResume();

    abstract void loadSavedInstanceState(@NonNull Bundle savedInstanceState);

    abstract void customSaveInstanceState(Bundle outState);

    abstract void setRefreshing(boolean refreshing);


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
        this.spanCount = getResources().getInteger(R.integer.span_count);
        this.spanLargeItems = getResources().getInteger(R.integer.span_large_items);
        this.spanSmallItems = getResources().getInteger(R.integer.span_small_items);


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
        toolbarSettings = new ToolbarSettings();
        try {
            toolbarSettings.appSetting = activityListener.getAppInfo().app_setting;
        } catch (Exception ignored) {

        }

        customToolbarInit();

        if (screenTitle.length() > 0) {
            toolbarSettings.toolbarTitle = screenTitle;
        }

        View view = onCustomCreateView(inflater, container, savedInstanceState);
        this.toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        this.leftToolbarButton = (ImageButton) view.findViewById(R.id.left_toolbar_button);
        this.titleToolbarLabel = (TextView) view.findViewById(R.id.title_toolbar_label);
        this.rightToolbarButton = (ImageButton) view.findViewById(R.id.right_toolbar_button);

        this.leftToolbarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbarSettings.toolbarType == ToolbarSettings.LEFT_MENU_BUTTON) {
                    activityListener.toggleMenu();
                } else {
                    close();
                }
            }
        });

        fragmentContent = (ViewGroup) view.findViewById(R.id.fragment_content);

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
        topFragment.updateToolbar();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCREEN_DATA_STATUS, this.screenDataStatus.ordinal());
        customSaveInstanceState(outState);
    }


    /*@Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState == null) {
            savedInstanceState = getArguments();
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA_STATUS)) {
                this.screenDataStatus = ScreenDataStatus.fromInt(savedInstanceState.getInt(SCREEN_DATA_STATUS));
            }
            loadSavedInstanceState(savedInstanceState);
        }
    }*/

    void setupVariables() {
        if (this.activityListener == null) {
            this.activityListener = (MainActivityListener) getActivity();
        }
        if (this.appPreferences == null) {
            this.appPreferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        }
    }

    protected String getKeyString(String key) {
        setupVariables();
        return this.appPreferences.getString(key, "");
    }

    protected boolean setKeyString(String key, String value) {
        setupVariables();
        boolean ret;
        SharedPreferences.Editor editor = this.appPreferences.edit();
        editor.putString(key, value);
        ret = editor.commit();
        return ret;
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim == 0)
            return null;

        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);

        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                System.out.println("Animation started.");
                if (toolbarSettings != null) {
                    //activityListener.updateNavigationBar(toolbarSettings);
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

    protected void close() {
        try {
            Utils.hideKeyboard(this.getActivity(), null);
            customClose();
            getActivity().getSupportFragmentManager().popBackStack();

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

    protected void showProgress(String message) {
        if (this.progressDialog != null)
            this.progressDialog.dismiss();
        this.progressDialog = new ProgressDialog(this.getContext());
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

    private void restoreSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA_STATUS)) {
                this.screenDataStatus = ScreenDataStatus.fromInt(savedInstanceState.getInt(SCREEN_DATA_STATUS));
            }
            if (savedInstanceState.containsKey(SCREEN_TITLE)) {
                this.screenTitle = savedInstanceState.getString(SCREEN_TITLE);
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
            FragmentManager fm = activityListener.getFM();
            int size = fm.getBackStackEntryCount();
            FragmentManager.BackStackEntry backStackEntry = fm.getBackStackEntryAt(size - 1);
            topFragment = (AbstractFragment) fm.findFragmentByTag(backStackEntry.getName());
        } catch (Exception ex) {

        }
        return topFragment;
    }

    protected void updateToolbar() {
        try {
            if (this.leftToolbarButton != null) {
                this.leftToolbarButton.setImageBitmap(FlatIcon.fromFlatIcon(getActivity().getAssets(),
                        this.toolbarSettings.toolbarLeftIcon,
                        40,
                        Color.argb(0, 0, 0, 0),
                        this.toolbarSettings.getToolbarIconColor()
                ));
            }
            if (this.rightToolbarButton != null) {
                this.rightToolbarButton.setImageBitmap(FlatIcon.fromFlatIcon(getActivity().getAssets(),
                        this.toolbarSettings.toolbarRightIcon,
                        40,
                        Color.argb(0, 0, 0, 0),
                        this.toolbarSettings.getToolbarIconColor()
                ));
            }

            if (this.titleToolbarLabel != null) {
                this.titleToolbarLabel.setText(toolbarSettings.toolbarTitle);
                this.titleToolbarLabel.setTextColor(toolbarSettings.getToolbarTitleColor());
                try {
                    Typeface type = Typeface.createFromAsset(getActivity().getAssets(),
                            toolbarSettings.getToolBarTitleFont());
                    this.titleToolbarLabel.setTypeface(type);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (this.screenToolBarHidden == true) {
                this.toolbar.setVisibility(View.VISIBLE);
            } else {
                this.toolbar.setVisibility(View.GONE);
            }

            if (this.toolbarSettings.toolbarType == ToolbarSettings.LEFT_MENU_BUTTON) {
                this.activityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            } else {
                this.activityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        } catch (Exception ignored) {

        }
    }

    boolean isSignedIn() {
        String token = getKeyString(Key.TokenKey);
        String userProfile = getKeyString(Key.UserProfile);
        if (token.length() > 0 && userProfile.length() > 0) {
            return true;
        } else {
            return false;
        }
    }
}