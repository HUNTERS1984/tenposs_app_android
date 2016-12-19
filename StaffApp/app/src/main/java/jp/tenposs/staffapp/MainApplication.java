package jp.tenposs.staffapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ambient on 7/26/16.
 */
public class MainApplication extends Application implements Application.ActivityLifecycleCallbacks {
    static Context mContext = null;

    private Bundle applicationBundle;

    private static boolean isInterestingActivityVisible;


    public MainApplication() {
        super();
        applicationBundle = new Bundle();
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }

    public void putSerializable(String key, Serializable value) {
        applicationBundle.putSerializable(key, value);
    }

    public void putParceable(String key, Parcelable value) {
        applicationBundle.putParcelable(key, value);
    }

    public Serializable getSerializable(String key) {
        return applicationBundle.getSerializable(key);
    }

    public Parcelable getParcelable(String key) {
        return applicationBundle.getParcelable(key);
    }

    public boolean containsKey(String key) {
        return applicationBundle.containsKey(key);
    }

    public void remove(String key) {
        applicationBundle.remove(key);
    }

    public static boolean isInterestingActivityVisible() {
        return isInterestingActivityVisible;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        isInterestingActivityVisible = true;

    }

    @Override
    public void onActivityPaused(Activity activity) {
        isInterestingActivityVisible = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        isInterestingActivityVisible = false;

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
