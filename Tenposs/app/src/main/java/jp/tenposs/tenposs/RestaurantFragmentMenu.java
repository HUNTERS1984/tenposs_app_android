package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
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
import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.GridSpanSizeLookup;
import jp.tenposs.adapter.MarginDecoration;
import jp.tenposs.adapter.RecyclerDataSource;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.adapter.RestaurantAdapter;
import jp.tenposs.communicator.ItemCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.ItemsInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.MenuInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;


/**
 * Created by ambient on 7/27/16.
 */
public class RestaurantFragmentMenu
        extends AbstractFragment
        implements RecyclerDataSource, OnCommonItemClickListener {

    AppBarLayout mAppBar;
    TextView mNoDataLabel;
    RecyclerView mRecyclerView;
    AbstractRecyclerAdapter mRecyclerAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    ItemsInfo.Response mCurrentItem;
    MenuInfo.Menu mScreenData;

    ArrayList<Bundle> mAllItems;

    /**
     * Fragment Override
     */

    private RestaurantFragmentMenu() {

    }

    public static RestaurantFragmentMenu newInstance(Serializable extras) {
        RestaurantFragmentMenu fragment = new RestaurantFragmentMenu();
        Bundle b = new Bundle();
        b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
        fragment.setArguments(b);
        return fragment;
    }

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
        mAllItems = null;
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
        loadMenuItem();
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        setRefreshing(false);

        mScreenDataItems = new ArrayList<>();

        int rowIndex = 0;
        int itemSpanCount = 0;
        for (ItemsInfo.Item item : mCurrentItem.data.items) {
            Bundle extras = new Bundle();
            extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
            extras.putString(RecyclerItemWrapper.ITEM_CATEGORY, item.getCategory());
            extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.getTitle());
            extras.putString(RecyclerItemWrapper.ITEM_PRICE, item.getPrice());
            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
            extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item);
            extras.putInt(RecyclerItemWrapper.ITEM_ROW, rowIndex);
            itemSpanCount += this.mSpanCount / this.mSpanLargeItems;
            if (itemSpanCount == this.mSpanCount) {
                rowIndex++;
                itemSpanCount = 0;
            }
            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeRestaurantGridItem, mSpanCount / mSpanLargeItems, extras));
        }

        if (this.mRecyclerAdapter == null) {
            GridLayoutManager manager = new GridLayoutManager(MainApplication.getContext(), mSpanCount);//);

            if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                this.mRecyclerAdapter = new RestaurantAdapter(MainApplication.getContext(), this, this);
            } else {
                this.mRecyclerAdapter = new CommonAdapter(MainApplication.getContext(), this, this);
            }

            manager.setSpanSizeLookup(new GridSpanSizeLookup(mRecyclerAdapter));
            this.mRecyclerView.setLayoutManager(manager);
            this.mRecyclerView.addItemDecoration(new MarginDecoration(MainApplication.getContext(), R.dimen.restaurant_item_spacing));
            this.mRecyclerView.setAdapter(mRecyclerAdapter);

            this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int lastPos = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                        if (lastPos != -1) {
                            if (lastPos == getItemCount() - 1 && getItemCount() < mCurrentItem.total_items) {
                                if (mLoadingStatus == LOADING_STATUS_UNKNOWN) {
                                    mLoadingStatus = LOADING_STATUS_MORE;
                                    loadMenuItem();
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

        this.mAppBar = (AppBarLayout) root.findViewById(R.id.app_bar);
        this.mNoDataLabel = (TextView) root.findViewById(R.id.no_data_label);
        this.mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);

        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            this.mRecyclerView.setBackgroundColor(Color.rgb(243, 243, 243));
        } else {
            this.mRecyclerView.setBackgroundColor(Color.WHITE);
        }
        this.mAppBar.setVisibility(View.GONE);
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
                    loadMenuItem();
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
            this.mScreenData = (MenuInfo.Menu) savedInstanceState.getSerializable(SCREEN_DATA);
        }
        if (savedInstanceState.containsKey(SCREEN_PAGE_ITEMS)) {
            this.mAllItems = (ArrayList<Bundle>) savedInstanceState.getSerializable(SCREEN_PAGE_ITEMS);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA_PAGE_DATA)) {
            this.mCurrentItem = (ItemsInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA_PAGE_DATA);
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

    void loadMenuItem() {
        try {
            ItemsInfo.Request requestParams = new ItemsInfo.Request();
            requestParams.menu_id = this.mScreenData.id;
            requestParams.pageindex = this.mPageIndex;
            requestParams.pagesize = this.mPageSize;

            Bundle params = new Bundle();
            params.putSerializable(Key.RequestObject, requestParams);
            ItemCommunicator communicator = new ItemCommunicator(
                    new TenpossCommunicator.TenpossCommunicatorListener() {
                        @Override
                        public void completed(TenpossCommunicator request, Bundle responseParams) {
                            mLoadingStatus = LOADING_STATUS_UNKNOWN;
                            setRefreshing(false);
                            int result = responseParams.getInt(Key.ResponseResult);
                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                if (resultApi == CommonResponse.ResultSuccess) {
                                    ItemsInfo.Response newPage = (ItemsInfo.Response) responseParams.getSerializable(Key.ResponseObject);

                                    if (newPage.data.items.size() > 0) {
                                        mPageIndex++;
                                    }

                                    //Append
                                    if (mCurrentItem == null) {
                                        mCurrentItem = newPage;
                                    } else {
                                        mCurrentItem.data.items.addAll(newPage.data.items);
                                    }

                                    previewScreenData();
                                } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                    refreshToken(new TenpossCallback() {
                                        @Override
                                        public void onSuccess(Bundle params) {
                                            loadMenuItem();
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
            case RecyclerItemTypeRestaurantGridItem: {
                ItemsInfo.Item itemData = (ItemsInfo.Item) item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.mActivityListener.showScreen(AbstractFragment.ITEM_SCREEN, itemData, null, false);
            }
            break;

            default: {
                Assert.assertFalse("" + item.itemType, false);
            }
            break;
        }
    }
}
