package jp.tenposs.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ambient on 12/19/16.
 */
public final class SharedPreferencesHelper {
    public static final String PREFERENCE_NAME = "tenposs.jp";

    private SharedPreferencesHelper() {
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
    }

    public static void save(Context context, String key, String value) {
        SharedPreferences shared = getSharedPreferences(context);
        SharedPreferences.Editor edit = shared.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static String get(Context context, String key) {
        SharedPreferences shared = getSharedPreferences(context);
        return shared.getString(key, "");
    }
}