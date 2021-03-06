package jp.tenposs.tenposs;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;

import java.io.Serializable;

/**
 * Created by ambient on 7/26/16.
 */
public class MainApplication extends Application {
    static Context mContext = null;
    static FragmentManager mFragmentManager = null;

    private Bundle applicationBundle;


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

    public void putParcelable(String key, Parcelable value) {
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


    public static void setSupportFragmentManager(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    public static FragmentManager getSupportFragmentManager() {
        return mFragmentManager;
    }
}
