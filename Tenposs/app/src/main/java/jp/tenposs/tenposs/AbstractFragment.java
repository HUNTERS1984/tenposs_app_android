package jp.tenposs.tenposs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

/**
 * Created by ambient on 7/26/16.
 */
public abstract class AbstractFragment extends Fragment {

    public interface MainActivityListener {
        void updateNavigationBar(ToolbarSettings toolbarSettings);

        void updateAppInfo(AppInfo.Response appInfo, int storeId);

        void updateSideMenuItems(ArrayList<AppInfo.SideMenu> menus);

        void updateUserInfo(SignInInfo.Response userInfo);

        void showScreen(int menuId, Serializable extras);

        AppInfo.Response.ResponseData getAppInfo();

        CallbackManager getCallbackManager();

    }

    public final static int HOME_SCREEN = 1;
    public final static int MENU_SCREEN = 2;
    public final static int ITEM_SCREEN = 201;

    public final static int RESERVE_SCREEN = 3;

    public final static int NEWS_SCREEN = 4;
    public final static int NEWS_DETAILS_SCREEN = 401;

    public final static int PHOTO_SCREEN = 5;
    public final static int PHOTO_ITEM_SCREEN = 501;

    public final static int STAFF_SCREEN = 6;
    public final static int COUPON_SCREEN = 7;
    public final static int CHAT_SCREEN = 8;

    public final static int SETTING_SCREEN = 9;
    public final static int PROFILE_SCREEN = 901;


    public final static int SIGNIN_SCREEN = 10;
    public final static int SIGNUP_SCREEN = 101;
    public final static int SIGNIN_EMAIL_SCREEN = 102;

    public final static int PURCHASE_SCREEN = 11;

    public class ToolbarSettings {

        static final int LEFT_MENU_BUTTON = 1;
        static final int LEFT_BACK_BUTTON = 2;

        public String toolbarTitle;
        public String toolbarIcon;
        public int toolbarType;
        AppInfo.AppSetting appSetting;
    }

    public static String SCREEN_DATA = "SCREEN_DATA";
    public static String SCREEN_DATA_STATUS = "SCREEN_DATA_STATUS";

    protected int spanCount = 1;
    public ToolbarSettings toolbarSettings;
    protected ScreenDataStatus screenDataStatus = ScreenDataStatus.ScreenDataStatusUnload;
    protected SharedPreferences appPreferences;
    protected MainActivityListener activityListener;

    protected ViewGroup fragmentContent;

    List<RecyclerItemWrapper> screenDataItems = new ArrayList<>();

    protected abstract void customClose();

    protected abstract void customToolbarInit();

    protected abstract void reloadScreenData();

    protected abstract void previewScreenData();

    protected abstract View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract void customResume();

    abstract void loadSavedInstanceState(@NonNull Bundle savedInstanceState);

    abstract void setRefreshing(boolean refreshing);


    /**
     * Fragment Life cycles
     */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("Fragment Life Cycle onAttach " + this.getClass().getCanonicalName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupVariables();
        System.out.println("Fragment Life Cycle onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("Fragment Life Cycle onCreateView");
        if (savedInstanceState == null) {
            savedInstanceState = getArguments();
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA_STATUS)) {
                this.screenDataStatus = ScreenDataStatus.fromInt(savedInstanceState.getInt(SCREEN_DATA_STATUS));
            }
            loadSavedInstanceState(savedInstanceState);
        }
        toolbarSettings = new ToolbarSettings();
        try {
            toolbarSettings.appSetting = activityListener.getAppInfo().app_setting;
        } catch (Exception ignored) {

        }

        customToolbarInit();
        View view = onCustomCreateView(inflater, container, savedInstanceState);
        fragmentContent = (ViewGroup) view.findViewById(R.id.fragment_content);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("Fragment Life Cycle onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("Fragment Life Cycle onActivityCreated");
        setupVariables();
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("Fragment Life Cycle onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        customResume();
        System.out.println("Fragment Life Cycle onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("Fragment Life Cycle onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Fragment Life Cycle onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("Fragment Life Cycle onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Fragment Life Cycle onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("Fragment Life Cycle onDetach " + this.getClass().getCanonicalName());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCREEN_DATA_STATUS, this.screenDataStatus.ordinal());
    }

    @Override
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
    }

    void setupVariables() {
        spanCount = 6;
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
                    activityListener.updateNavigationBar(toolbarSettings);
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
        customClose();
        getActivity().getSupportFragmentManager().popBackStack();
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
}
