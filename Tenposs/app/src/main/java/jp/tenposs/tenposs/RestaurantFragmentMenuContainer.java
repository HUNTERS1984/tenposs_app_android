package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import jp.tenposs.adapter.TabPageAdapter;
import jp.tenposs.adapter.TabPageDataSource;
import jp.tenposs.communicator.MenuInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.MenuInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.view.NonSwipeableViewPager;
import jp.tenposs.view.PagerSlidingTabStrip;


/**
 * Created by ambient on 7/27/16.
 */
public class RestaurantFragmentMenuContainer extends AbstractFragment implements TabPageDataSource {

    ProgressBar mLoadingIndicator;

    MenuInfo.Response mScreenData;


    TabPageAdapter mTabPageAdapter;
    NonSwipeableViewPager mViewPager;
    PagerSlidingTabStrip mTabLayout;
    TextView mNoDataLabel;

    /**
     * Fragment Override
     */

//    public static RestaurantFragmentMenuContainer newInstance(String title, int storeId) {
//        RestaurantFragmentMenuContainer fragment = new RestaurantFragmentMenuContainer();
//        Bundle b = new Bundle();
//        b.putString(AbstractFragment.SCREEN_TITLE, title);
//        b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
//        fragment.setArguments(b);
//        return fragment;
//    }
    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.menu);
        if (this.mShowFromSideMenu == true) {
            mToolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
            mToolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
        } else {
            mToolbarSettings.toolbarLeftIcon = "flaticon-back";
            mToolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
        }
    }

    @Override
    protected void clearScreenData() {
        mScreenData = null;
        this.mScreenDataItems = new ArrayList<>();
    }

    @Override
    protected void reloadScreenData() {
        if (this.mScreenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        loadMenuData();
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        setRefreshing(false);

        setRefreshing(false);

        if (mScreenData != null &&
                mScreenData.data != null &&
                mScreenData.data.menus != null &&
                mScreenData.data.menus.size() > 0) {
            if (mTabPageAdapter == null) {
                mTabPageAdapter = new TabPageAdapter(getChildFragmentManager(), this);
                this.mViewPager.setAdapter(mTabPageAdapter);
                this.mTabLayout.setBackgroundColor(Color.WHITE);
                this.mTabLayout.shouldExpand = false;
                this.mTabLayout.setViewPager(this.mViewPager);
            } else {
                mTabPageAdapter.notifyDataSetChanged();
            }
        } else {
//No data
            this.mNoDataLabel.setVisibility(View.VISIBLE);
            this.mTabLayout.setVisibility(View.GONE);
            this.mViewPager.setVisibility(View.GONE);
        }

        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.restaurant_fragment_recycler_view_tabs, null);

        this.mLoadingIndicator = (ProgressBar) root.findViewById(R.id.loading_indicator);
        this.mViewPager = (NonSwipeableViewPager) root.findViewById(R.id.tab_page_container);
        this.mNoDataLabel = (TextView) root.findViewById(R.id.no_data_label);
        this.mTabLayout = (PagerSlidingTabStrip) root.findViewById(R.id.tabs);
        return root;
    }

    @Override
    protected void customResume() {
        if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            loadMenuData();

        } else if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //just waiting

        } else {
            //reload screen, this case application return from background or from other activity
            previewScreenData();
        }
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(APP_DATA_STORE_ID)) {
            this.mStoreId = savedInstanceState.getInt(APP_DATA_STORE_ID);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (MenuInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putInt(APP_DATA_STORE_ID, this.mStoreId);
        if (this.mScreenData != null) {
            outState.putSerializable(SCREEN_DATA, this.mScreenData);
        }
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return true;
    }

    void loadMenuData() {
        MenuInfo.Request requestParams = new MenuInfo.Request();
        requestParams.store_id = this.mStoreId;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        MenuInfoCommunicator communicator = new MenuInfoCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        mLoadingStatus = LOADING_STATUS_UNKNOWN;
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                mScreenData = (MenuInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                previewScreenData();
                            } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                refreshToken(new TenpossCallback() {
                                    @Override
                                    public void onSuccess(Bundle params) {
                                        loadMenuData();
                                    }

                                    @Override
                                    public void onFailed(Bundle params) {
                                        //Logout, then do something
                                        mActivityListener.logoutBecauseExpired();
                                    }
                                });
                            } else {
                                String strMessage = responseParams.getString(Key.ResponseMessage);
                                errorWithMessage(responseParams, strMessage, null);
                            }
                        } else {
                            String strMessage = responseParams.getString(Key.ResponseMessage);
                            errorWithMessage(responseParams, strMessage, null);
                        }
                    }
                });
        communicator.execute(params);
    }

    @Override
    public int getCount() {
        if (mScreenData != null &&
                mScreenData.data != null &&
                mScreenData.data.menus != null &&
                mScreenData.data.menus.size() > 0) {
            return this.mScreenData.data.menus.size();
        } else {
            return 0;
        }
    }


    @Override
    public Fragment getItem(int position) {
        if (mScreenData != null &&
                mScreenData.data != null &&
                mScreenData.data.menus != null &&
                mScreenData.data.menus.size() > 0) {
            MenuInfo.Menu menu = this.mScreenData.data.menus.get(position);
            return RestaurantFragmentMenu.newInstance(menu);
        } else {
            return null;
        }
    }

    @Override
    public String getItemTitle(int position) {
        if (mScreenData != null &&
                mScreenData.data != null &&
                mScreenData.data.menus != null &&
                mScreenData.data.menus.size() > 0) {
            MenuInfo.Menu menu = this.mScreenData.data.menus.get(position);
            return menu.name;
        } else {
            return "";
        }
    }
}
