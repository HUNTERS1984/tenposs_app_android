package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.adapter.AbstractRecyclerAdapter;
import jp.tenposs.adapter.GridSpanSizeLookup;
import jp.tenposs.adapter.MarginDecoration;
import jp.tenposs.adapter.RecyclerDataSource;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.adapter.RestaurantAdapter;
import jp.tenposs.communicator.StaffInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.StaffInfo;
import jp.tenposs.listener.OnCommonItemClickListener;


/**
 * Created by ambient on 7/27/16.
 */
public class RestaurantFragmentStaff
        extends AbstractFragment
        implements RecyclerDataSource, OnCommonItemClickListener {

    TextView mNoDataLabel;
    RecyclerView mRecyclerView;
    AbstractRecyclerAdapter mRecyclerAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    StaffInfo.Response mScreenData;

    /**
     * Fragment Override
     */

    private RestaurantFragmentStaff() {

    }

    public static RestaurantFragmentStaff newInstance(String title, int storeId) {
        RestaurantFragmentStaff fragment = new RestaurantFragmentStaff();
        Bundle b = new Bundle();
        b.putString(AbstractFragment.SCREEN_TITLE, title);
        b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.staff);
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate && this.mFirstScreen == false) {
            mToolbarSettings.toolbarLeftIcon = "flaticon-back";
            mToolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
        } else {
            mToolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
            mToolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
        }
    }

    @Override
    protected void clearScreenData() {
        this.mScreenDataItems = new ArrayList<>();
        if (this.mRecyclerAdapter != null) {
            this.mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void reloadScreenData() {
        if (this.mScreenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        loadStaffCatItem();
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        setRefreshing(false);

        mScreenDataItems = new ArrayList<>();

        int rowIndex = 0;
        int itemSpanCount = 0;
        for (StaffInfo.Staff staff : mScreenData.data.staffs) {
            Bundle extras = new Bundle();
            extras.putInt(RecyclerItemWrapper.ITEM_ID, staff.id);
            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.STAFF_DETAIL_SCREEN);
            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, staff.getImageUrl());
            extras.putString(RecyclerItemWrapper.ITEM_TITLE, staff.getTitle());
            extras.putString(RecyclerItemWrapper.ITEM_PRICE, staff.getDescription());
            extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, staff);

            extras.putInt(RecyclerItemWrapper.ITEM_ROW, rowIndex);
            itemSpanCount += this.mSpanCount / this.mSpanSmallItems;
            if (itemSpanCount == this.mSpanCount) {
                rowIndex++;
                itemSpanCount = 0;
            }

            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeRestaurantGridStaff, mSpanCount / mSpanSmallItems, extras));
        }

        if (this.mRecyclerAdapter == null) {
            GridLayoutManager manager = new GridLayoutManager(getActivity(), mSpanCount);//);
            this.mRecyclerAdapter = new RestaurantAdapter(getActivity(), this, this);
            manager.setSpanSizeLookup(new GridSpanSizeLookup(mRecyclerAdapter));
            this.mRecyclerView.setLayoutManager(manager);
            this.mRecyclerView.addItemDecoration(new MarginDecoration(getActivity(), R.dimen.restaurant_item_spacing));
            this.mRecyclerView.setAdapter(mRecyclerAdapter);

            this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int lastPos = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                        if (lastPos != -1) {
                            if (lastPos == getItemCount() - 1 && getItemCount() < mScreenData.data.total_staffs) {
                                if (mLoadingStatus == LOADING_STATUS_UNKNOWN) {
                                    mLoadingStatus = LOADING_STATUS_MORE;
                                    loadStaffCatItem();
                                }
                            }
                        }
                    }
                }
            });
        } else {
            this.mRecyclerAdapter.notifyDataSetChanged();
        }
        if (this.mScreenDataItems.size() == 0) {
            this.mNoDataLabel.setVisibility(View.VISIBLE);
        } else {
            this.mNoDataLabel.setVisibility(View.GONE);
        }
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recycler_view_refresh, null);

        this.mNoDataLabel = (TextView) root.findViewById(R.id.no_data_label);
        this.mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);

        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            this.mRecyclerView.setBackgroundColor(Color.rgb(243, 243, 243));
        } else {
            this.mRecyclerView.setBackgroundColor(Color.WHITE);
        }
        this.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mLoadingStatus == LOADING_STATUS_UNKNOWN) {
                    mScreenDataStatus = ScreenDataStatus.ScreenDataStatusUnload;
                    mLoadingStatus = LOADING_STATUS_REFRESH;
                    setRefreshing(true);
                    clearScreenData();
                    reloadScreenData();
                } else {
                    setRefreshing(false);
                }
            }
        });

        return root;
    }

    @Override
    protected void customResume() {
        if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            this.mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    setRefreshing(true);
                    loadStaffCatItem();
                }
            });

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
            this.mScreenData = (StaffInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
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
        this.mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    boolean canCloseByBackpressed() {
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            return true;
        } else {
            return false;
        }
    }

    /*void loadStaffCatData() {
        enableControls(false);
        StaffCategoryInfo.Request requestParams = new StaffCategoryInfo.Request();
        requestParams.store_id = this.mStoreId;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        StaffCategoryCommunicator communicator = new StaffCategoryCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        if(isAdded() == false){
                            return;
                        }
                        mLoadingStatus = LOADING_STATUS_UNKNOWN;
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                mScreenData = (StaffCategoryInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                mAllItems = new ArrayList<>();
                                if (mScreenData.data.staff_categories.size() > 0) {
                                    for (int i = 0; i < mScreenData.data.staff_categories.size(); i++) {
                                        Bundle staffCategory = new Bundle();
                                        staffCategory.putInt(SCREEN_DATA_PAGE_INDEX, 1);
                                        staffCategory.putInt(SCREEN_DATA_PAGE_SIZE, DEFAULT_RECORD_PER_PAGE);
                                        mAllItems.add(staffCategory);
                                    }
                                    loadStaffCatItem(mCurrentStaffCatIndex);
                                } else {
                                    mSubToolbar.setVisibility(View.GONE);
                                }
                            } else {
                                String strMessage = responseParams.getString(Key.ResponseMessage);
                                errorWithMessage(responseParams, strMessage);
                            }
                        } else {
                            String strMessage = responseParams.getString(Key.ResponseMessage);
                            errorWithMessage(responseParams, strMessage);
                        }
                    }
                });
        communicator.execute(params);
    }*/

    void loadStaffCatItem() {
        try {
            StaffInfo.Request requestParams = new StaffInfo.Request();
            requestParams.category_id = 0;
            requestParams.pageindex = this.mPageIndex;
            requestParams.pagesize = this.mPageSize;

            Bundle params = new Bundle();
            params.putSerializable(Key.RequestObject, requestParams);
            StaffInfoCommunicator communicator = new StaffInfoCommunicator(
                    new TenpossCommunicator.TenpossCommunicatorListener() {
                        @Override
                        public void completed(TenpossCommunicator request, Bundle responseParams) {
                            if (isAdded() == false) {
                                return;
                            }
                            mLoadingStatus = LOADING_STATUS_UNKNOWN;
                            int result = responseParams.getInt(Key.ResponseResult);
                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                if (resultApi == CommonResponse.ResultSuccess) {
                                    StaffInfo.Response newPage = (StaffInfo.Response) responseParams.getSerializable(Key.ResponseObject);

                                    if (newPage.data.staffs.size() > 0) {
                                        mPageIndex++;
                                    }

                                    //Append
                                    if (mScreenData == null) {
                                        mScreenData = newPage;
                                    } else {
                                        mScreenData.data.staffs.addAll(newPage.data.staffs);
                                    }
                                    previewScreenData();
                                } else {
                                    String strMessage = responseParams.getString(Key.ResponseMessage);
                                    errorWithMessage(responseParams, strMessage);
                                }
                            } else {
                                String strMessage = responseParams.getString(Key.ResponseMessage);
                                errorWithMessage(responseParams, strMessage);
                            }
                        }
                    });
            communicator.execute(params);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mScreenDataItems.size();
    }

    @Override
    public RecyclerItemWrapper getItemData(int position) {
        return mScreenDataItems.get(position);
    }

    @Override
    public void onCommonItemClick(int position, Bundle extraData) {
        RecyclerItemWrapper item = getItemData(position);
        switch (item.itemType) {
            case RecyclerItemTypeRestaurantGridStaff: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.mActivityListener.showScreen(screenId, extras, null);
            }
            break;

            default: {
                Assert.assertFalse("" + item.itemType, false);
            }
            break;
        }
    }
}
