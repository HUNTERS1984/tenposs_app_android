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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.adapter.AbstractRecyclerAdapter;
import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.GridSpanSizeLookup;
import jp.tenposs.adapter.MarginDecoration;
import jp.tenposs.adapter.RecyclerDataSource;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.communicator.StaffCategoryCommunicator;
import jp.tenposs.communicator.StaffInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.StaffCategoryInfo;
import jp.tenposs.datamodel.StaffInfo;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.utils.FontIcon;
import jp.tenposs.utils.Utils;


/**
 * Created by ambient on 7/27/16.
 */
public class FragmentStaff extends AbstractFragment implements View.OnClickListener, RecyclerDataSource, OnCommonItemClickListener {

    ProgressBar mLoadingIndicator;
    LinearLayout mSubToolbar;
    ImageButton mPreviousButton;
    TextView mTitleLabel;
    ImageButton mNextButton;
    TextView mNoDataLabel;
    RecyclerView mRecyclerView;
    AbstractRecyclerAdapter mRecyclerAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    StaffCategoryInfo.Response mScreenData;
    StaffInfo.Response mCurrentItem;
    StaffCategoryInfo.StaffCategory mCurrentStaffCat;
    int mCurrentStaffCatIndex;


    ArrayList<Bundle> mAllItems;

    /**
     * Fragment Override
     */

//    private FragmentStaff() {
//
//    }
//
//    public static FragmentStaff newInstance(String title, int storeId) {
//        FragmentStaff fragment = new FragmentStaff();
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
        mToolbarSettings.toolbarTitle = getString(R.string.staff);
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
        mAllItems = null;
        mScreenData = null;
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
        loadStaffCatData();
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;

        setRefreshing(false);

        if (mScreenData != null &&
                mScreenData.data != null &&
                mScreenData.data.staff_categories != null &&
                mScreenData.data.staff_categories.size() > 0) {
            this.mSubToolbar.setVisibility(View.VISIBLE);
            updateNavigation();
            enableControls(true);
            mScreenDataItems = new ArrayList<>();

            for (StaffInfo.Staff staff : mCurrentItem.data.staffs) {
                staff.staff_categories.name = this.mCurrentStaffCat.name;
                Bundle extras = new Bundle();
                extras.putInt(RecyclerItemWrapper.ITEM_ID, staff.id);
                extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.STAFF_DETAIL_SCREEN);
                extras.putString(RecyclerItemWrapper.ITEM_IMAGE, staff.getImageUrl());
                extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, staff);
                mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeGridStaff, mSpanCount / mSpanSmallItems, extras));
            }

            mTitleLabel.setText(mCurrentStaffCat.name);
            if (this.mRecyclerAdapter == null) {
                GridLayoutManager manager = new GridLayoutManager(getActivity(), mSpanCount);//);
                this.mRecyclerAdapter = new CommonAdapter(getActivity(), this, this);
                manager.setSpanSizeLookup(new GridSpanSizeLookup(mRecyclerAdapter));
                this.mRecyclerView.setLayoutManager(manager);
                this.mRecyclerView.addItemDecoration(new MarginDecoration(getActivity(), R.dimen.common_item_spacing));
                this.mRecyclerView.setAdapter(mRecyclerAdapter);

                this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            int lastPos = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                            if (lastPos != -1) {
                                if (lastPos == getItemCount() - 1 && getItemCount() < mCurrentItem.data.total_staffs) {
                                    if (mLoadingStatus == LOADING_STATUS_UNKNOWN) {
                                        mLoadingIndicator.setVisibility(View.VISIBLE);
                                        mLoadingStatus = LOADING_STATUS_MORE;
                                        loadStaffCatItem(mCurrentStaffCatIndex);
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
        } else {
            this.mSubToolbar.setVisibility(View.GONE);
            this.mNoDataLabel.setVisibility(View.VISIBLE);
        }
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recycler_view_paging_refresh, null);

        this.mLoadingIndicator = (ProgressBar) root.findViewById(R.id.loading_indicator);
        this.mSubToolbar = (LinearLayout) root.findViewById(R.id.sub_toolbar);
        this.mPreviousButton = (ImageButton) root.findViewById(R.id.previous_button);
        this.mTitleLabel = (TextView) root.findViewById(R.id.title_label);
        this.mNextButton = (ImageButton) root.findViewById(R.id.next_button);
        this.mNoDataLabel = (TextView) root.findViewById(R.id.no_data_label);
        this.mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        this.mPreviousButton.setOnClickListener(this);
        this.mNextButton.setOnClickListener(this);

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
                    mCurrentStaffCatIndex = 0;
                    loadStaffCatData();
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
            this.mScreenData = (StaffCategoryInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
        }
        if (savedInstanceState.containsKey(SCREEN_PAGE_ITEMS)) {
            mAllItems = (ArrayList<Bundle>) savedInstanceState.getSerializable(SCREEN_PAGE_ITEMS);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA_PAGE_DATA)) {
            this.mCurrentItem = (StaffInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA_PAGE_DATA);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA_ITEM_INDEX)) {
            this.mCurrentStaffCatIndex = savedInstanceState.getInt(SCREEN_DATA_ITEM_INDEX);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putInt(APP_DATA_STORE_ID, this.mStoreId);
        if (this.mScreenData != null) {
            outState.putSerializable(SCREEN_DATA, this.mScreenData);
        }
        if (this.mAllItems != null) {
            outState.putSerializable(SCREEN_PAGE_ITEMS, this.mAllItems);
        }
        if (this.mCurrentItem != null) {
            outState.putSerializable(SCREEN_DATA_PAGE_DATA, this.mCurrentItem);
        }
        outState.putInt(SCREEN_DATA_ITEM_INDEX, this.mCurrentStaffCatIndex);
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

    void loadStaffCatData() {
        enableControls(false);
        StaffCategoryInfo.Request requestParams = new StaffCategoryInfo.Request();
        requestParams.store_id = this.mStoreId;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        StaffCategoryCommunicator communicator = new StaffCategoryCommunicator(
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
                                mScreenData = (StaffCategoryInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                mAllItems = new ArrayList<>();
                                if (mScreenData != null &&
                                        mScreenData.data != null &&
                                        mScreenData.data.staff_categories != null &&
                                        mScreenData.data.staff_categories.size() > 0) {
                                    for (int i = 0; i < mScreenData.data.staff_categories.size(); i++) {
                                        Bundle staffCategory = new Bundle();
                                        staffCategory.putInt(SCREEN_DATA_PAGE_INDEX, 1);
                                        staffCategory.putInt(SCREEN_DATA_PAGE_SIZE, DEFAULT_RECORD_PER_PAGE);
                                        mAllItems.add(staffCategory);
                                    }
                                    loadStaffCatItem(mCurrentStaffCatIndex);
                                } else {
                                    previewScreenData();
                                }
                            } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                refreshToken(new TenpossCallback() {
                                    @Override
                                    public void onSuccess(Bundle params) {
                                        loadStaffCatData();
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

    void loadStaffCatItem(final int menuIndex) {
        try {
            mCurrentStaffCat = mScreenData.data.staff_categories.get(menuIndex);
            Bundle staffCategory = mAllItems.get(menuIndex);
            if (staffCategory.containsKey(SCREEN_DATA_PAGE_DATA) && this.mLoadingStatus == LOADING_STATUS_UNKNOWN) {
                this.mCurrentItem = (StaffInfo.Response) staffCategory.getSerializable(SCREEN_DATA_PAGE_DATA);
                previewScreenData();
            } else {
                enableControls(false);
                StaffInfo.Request requestParams = new StaffInfo.Request();
                requestParams.category_id = mCurrentStaffCat.id;
                requestParams.pageindex = staffCategory.getInt(SCREEN_DATA_PAGE_INDEX);
                requestParams.pagesize = staffCategory.getInt(SCREEN_DATA_PAGE_SIZE);

                Bundle params = new Bundle();
                params.putSerializable(Key.RequestObject, requestParams);
                params.putParcelable(Key.RequestData, staffCategory);
                StaffInfoCommunicator communicator = new StaffInfoCommunicator(
                        new TenpossCommunicator.TenpossCommunicatorListener() {
                            @Override
                            public void completed(TenpossCommunicator request, Bundle responseParams) {
                                if (isAdded() == false) {
                                    return;
                                }
                                mLoadingStatus = LOADING_STATUS_UNKNOWN;
                                mLoadingIndicator.setVisibility(View.INVISIBLE);
                                int result = responseParams.getInt(Key.ResponseResult);
                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                    if (resultApi == CommonResponse.ResultSuccess) {
                                        StaffInfo.Response newPage = (StaffInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                        Bundle staffCategory = responseParams.getParcelable(Key.RequestData);

                                        if (newPage.data.staffs.size() > 0) {
                                            int pageindex = staffCategory.getInt(SCREEN_DATA_PAGE_INDEX);
                                            pageindex++;
                                            staffCategory.putInt(SCREEN_DATA_PAGE_INDEX, pageindex);
                                        }

                                        //Append
                                        mCurrentItem = (StaffInfo.Response) staffCategory.getSerializable(SCREEN_DATA_PAGE_DATA);
                                        if (mCurrentItem == null) {
                                            mCurrentItem = newPage;
                                        } else {
                                            mCurrentItem.data.staffs.addAll(newPage.data.staffs);
                                        }
                                        staffCategory.putSerializable(SCREEN_DATA_PAGE_DATA, mCurrentItem);

                                        previewScreenData();
                                    } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                        refreshToken(new TenpossCallback() {
                                            @Override
                                            public void onSuccess(Bundle params) {
                                                loadStaffCatItem(menuIndex);
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
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    boolean hasPrevious() {
        return (mCurrentStaffCatIndex > 0);
    }

    boolean hasNext() {
        return (mCurrentStaffCatIndex < (this.mScreenData.data.staff_categories.size() - 1));
    }

    void previous() {
        if (hasPrevious() == false) {
            return;
        }
        try {
            if (mCurrentStaffCatIndex > 0) {
                mCurrentStaffCatIndex--;
            }
        } catch (Exception ignored) {

        }
        loadStaffCatItem(mCurrentStaffCatIndex);
    }

    void next() {
        if (hasNext() == false) {
            return;
        }
        try {
            if (mCurrentStaffCatIndex < this.mScreenData.data.staff_categories.size() - 1) {
                mCurrentStaffCatIndex++;
            }
        } catch (Exception ignored) {

        }
        this.mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
                loadStaffCatItem(mCurrentStaffCatIndex);
            }
        });
    }

    void updateNavigation() {
        int previousButtonColor = Color.rgb(128, 128, 128);
        if (hasPrevious() == true) {
            previousButtonColor = this.mToolbarSettings.getToolbarIconColor();
            if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                previousButtonColor = Utils.getColor(getContext(), R.color.restaurant_toolbar_icon_color);
            }
        }


        int nextButtonColor = Color.rgb(128, 128, 128);
        if (hasNext() == true) {
            nextButtonColor = this.mToolbarSettings.getToolbarIconColor();
            if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                nextButtonColor = Utils.getColor(getContext(), R.color.restaurant_toolbar_icon_color);
            }
        }

        mPreviousButton.setImageBitmap(FontIcon.imageForFontIdentifier(getContext().getAssets(),
                "flaticon-back",
                Utils.CatIconSize,
                Color.argb(0, 0, 0, 0),
                previousButtonColor,
                FontIcon.FLATICON
        ));

        mNextButton.setImageBitmap(FontIcon.imageForFontIdentifier(getContext().getAssets(),
                "flaticon-next",
                Utils.CatIconSize,
                Color.argb(0, 0, 0, 0),
                nextButtonColor,
                FontIcon.FLATICON
        ));
    }

    @Override
    public void onClick(View v) {
        if (v == mPreviousButton) {
            previous();

        } else if (v == mNextButton) {
            next();
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
            case RecyclerItemTypeGridStaff: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.mActivityListener.showScreen(screenId, extras, null, false);
            }
            break;

            default: {
                Assert.assertFalse("" + item.itemType, false);
            }
            break;
        }
    }

    void enableControls(boolean enable) {
        mPreviousButton.setEnabled(enable);
        mNextButton.setEnabled(enable);
    }

}
