package jp.tenposs.tenposs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import jp.tenposs.datamodel.AppSettings;
import jp.tenposs.datamodel.HomeScreenItem;
import jp.tenposs.datamodel.HomeObject;

/**
 * Created by ambient on 7/26/16.
 */
public abstract class AbstractFragment extends Fragment {

    public interface MainActivityListener {
        void updateNavigationBar(ToolbarSettings toolbarSettings);
        void updateMenuItems(HomeObject homeObject);
        void showScreen(HomeScreenItem screenItem);
    }

    public enum ScreenDataStatus {
        ScreenDataStatusUnload,
        ScreenDataStatusLoading,
        ScreenDataStatusLoaded;

        public static ScreenDataStatus fromInt(int item) {
            for (ScreenDataStatus type : ScreenDataStatus.values()) {
                if (type.ordinal() == item) {
                    return type;
                }
            }
            return ScreenDataStatusUnload;
        }
    }

    public class ToolbarSettings {

        public static final int LEFT_MENU_BUTTON = 1;
        public static final int LEFT_BACK_BUTTON = 2;

        public String toolbarTitle;
        public String toolbarIcon;
        public int toolbarType;
        public AppSettings.Settings settings;
        public AppSettings.Settings titleSettings;
    }

    public static String SCREEN_DATA = "SCREEN_DATA";
    public static String SCREEN_DATA_STATUS = "SCREEN_DATA_STATUS";

    public ToolbarSettings toolbarSettings;
    protected ScreenDataStatus screenDataStatus = ScreenDataStatus.ScreenDataStatusUnload;
    protected SharedPreferences appPreferences;
    protected MainActivityListener activityListener;

    protected ViewGroup fragmentContent;

    protected abstract void customClose();

    protected abstract void customToolbarInit();

    protected abstract void reloadScreenData();

    protected abstract void previewScreenData();

    protected abstract View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    abstract void loadSavedInstanceState(@Nullable Bundle savedInstanceState);


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCREEN_DATA_STATUS, this.screenDataStatus.ordinal());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA_STATUS) == true) {
                this.screenDataStatus = ScreenDataStatus.fromInt(savedInstanceState.getInt(SCREEN_DATA_STATUS));
            }
        }
        loadSavedInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupVariables();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupVariables();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA_STATUS) == true) {
                this.screenDataStatus = ScreenDataStatus.fromInt(savedInstanceState.getInt(SCREEN_DATA_STATUS));
            }
        }
        loadSavedInstanceState(savedInstanceState);
        customToolbarInit();
        View view = onCustomCreateView(inflater, container, savedInstanceState);
        fragmentContent = (ViewGroup) view.findViewById(R.id.fragment_content);
        return view;
    }

    void setupVariables() {

        /*showGrid = getResources().getBoolean(R.bool.showGrid);
        isLandScape = getResources().getBoolean(R.bool.isLandscape);
        numberOfColumn = getResources().getInteger(R.integer.numberOfColumn);*/

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

    protected ArrayList<String> getSearchHistory() {
        Set<String> history = this.appPreferences.getStringSet("search_history", null);
        if (history == null) {
            history = new TreeSet<>();
        }
        ArrayList<String> historyList = new ArrayList<>(history);

        return historyList;
    }

    protected void addSearchHistory(String query) {
        SharedPreferences.Editor editor = this.appPreferences.edit();

        Set<String> history = this.appPreferences.getStringSet("search_history", null);

        if (history == null) {
            history = new TreeSet<>();
        }

        history.add(query);

        editor.putStringSet("search_history", history);
        editor.commit();
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
        /*getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();*/
    }
}
