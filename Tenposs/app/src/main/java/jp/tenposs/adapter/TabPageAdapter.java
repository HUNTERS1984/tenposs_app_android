package jp.tenposs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by ambient on 11/9/16.
 */

public class TabPageAdapter extends FragmentStatePagerAdapter {

    TabPageDataSource mDataSource;

    public TabPageAdapter(FragmentManager fm, TabPageDataSource data) {
        super(fm);
        this.mDataSource = data;
    }

    @Override
    public Fragment getItem(int position) {
        return mDataSource.getItem(position);
    }

    @Override
    public int getCount() {
        return mDataSource.getCount();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        /*if (position == 0){
            return "";
        }*/
        return mDataSource.getItemTitle(position);
    }
}
