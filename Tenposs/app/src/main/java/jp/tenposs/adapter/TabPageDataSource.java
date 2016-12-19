package jp.tenposs.adapter;

import android.support.v4.app.Fragment;

/**
 * Created by ambient on 11/9/16.
 */

public interface TabPageDataSource {
    int getCount();

    Fragment getItem(int position);

    String getItemTitle(int position);
}
