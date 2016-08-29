package jp.tenposs.tenposs;

import android.app.Application;
import android.content.Context;

/**
 * Created by ambient on 7/26/16.
 */
public class MainApplication extends Application {
    static Context mContext = null;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}
