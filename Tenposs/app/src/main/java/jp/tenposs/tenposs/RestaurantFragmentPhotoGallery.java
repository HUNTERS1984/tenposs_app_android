package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.adapter.AbstractRecyclerAdapter;
import jp.tenposs.adapter.RecyclerDataSource;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.adapter.RestaurantAdapter;
import jp.tenposs.adapter.widget.TwoWayView;
import jp.tenposs.communicator.PhotoInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.PhotoInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;


/**
 * Created by ambient on 7/27/16.
 */
public class RestaurantFragmentPhotoGallery extends AbstractFragment
        implements RecyclerDataSource, OnCommonItemClickListener {

    TextView mNoDataLabel;
    TwoWayView mRecyclerView;
    AbstractRecyclerAdapter mRecyclerAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    PhotoInfo.Response mCurrentItem;

    /**
     * Fragment Override
     */

//    public static RestaurantFragmentPhotoGallery newInstance(String title, int storeId) {
//        RestaurantFragmentPhotoGallery fragment = new RestaurantFragmentPhotoGallery();
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
        mToolbarSettings.toolbarTitle = getString(R.string.photo_gallery);
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
        loadPhotoCatItem();
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        setRefreshing(false);

        mScreenDataItems = new ArrayList<>();

        int rowIndex = 0;
        int itemSpanCount = 0;
        int rowSpan = 1;
        int columnSpan = 1;

        int count = 0;
        for (PhotoInfo.Photo photo : mCurrentItem.data.photos) {
            Bundle extras = new Bundle();

            extras.putInt(RecyclerItemWrapper.ITEM_ID, photo.id);
            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.PHOTO_ITEM_SCREEN);
            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, photo.getImageUrl());
            extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, photo.getImageUrl());
            extras.putInt(RecyclerItemWrapper.ITEM_ROW, rowIndex);

            extras.putInt(RecyclerItemWrapper.ITEM_LAYOUT_SPAN_ABLE, 1);
            int type = (count % 10);
            switch (type) {
                case 0:
                case 1:
                case 2:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8: {
                    rowSpan = 1;
                    columnSpan = 1;
                }
                break;

                case 3: {
                    rowSpan = 2;
                    columnSpan = 2;
                }
                break;

                case 9: {
                    rowSpan = 2;
                    columnSpan = 3;
                }
                break;
            }
            extras.putInt(RecyclerItemWrapper.ITEM_ROW_SPAN, rowSpan);
            extras.putInt(RecyclerItemWrapper.ITEM_COLUMN_SPAN, columnSpan);

            itemSpanCount += this.mSpanCount / this.mSpanSmallItems;
            if (itemSpanCount == this.mSpanCount) {
                rowIndex++;
                itemSpanCount = 0;
            }
            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeRestaurantGridImage, mSpanCount / mSpanSmallItems, extras));
            count++;
        }

        if (this.mRecyclerAdapter == null) {
            this.mRecyclerAdapter = new RestaurantAdapter(getActivity(), this, this);
            //this.mRecyclerView.addItemDecoration(new MarginDecoration(getActivity(), R.dimen.item_margin));
            this.mRecyclerView.setAdapter(mRecyclerAdapter);

            this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        int lastPos = ((SpannableGridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
//                        if (lastPos != -1) {
//                            if (lastPos == getItemCount() - 1 && getItemCount() < mCurrentItem.data.total_photos) {
//                                if (mLoadingStatus == LOADING_STATUS_UNKNOWN) {
//                                    mLoadingStatus = LOADING_STATUS_MORE;
//                                    loadPhotoCatItem();
//                                }
//                            }
//                        }
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
        View root = inflater.inflate(R.layout.restaurant_fragment_photo_gallery, null);

        this.mNoDataLabel = (TextView) root.findViewById(R.id.no_data_label);
        this.mRecyclerView = (TwoWayView) root.findViewById(R.id.recycler_view);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);

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
                    loadPhotoCatItem();
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
            this.mCurrentItem = (PhotoInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putInt(APP_DATA_STORE_ID, this.mStoreId);
        if (this.mCurrentItem != null) {
            outState.putSerializable(SCREEN_DATA, this.mCurrentItem);
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


    void loadPhotoCatItem() {
        try {
            PhotoInfo.Request requestParams = new PhotoInfo.Request();
            requestParams.category_id = 0;
            requestParams.pageindex = this.mPageIndex;
            requestParams.pagesize = this.mPageSize;

            Bundle params = new Bundle();
            params.putSerializable(Key.RequestObject, requestParams);
            PhotoInfoCommunicator communicator = new PhotoInfoCommunicator(
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
                                    PhotoInfo.Response newPage = (PhotoInfo.Response) responseParams.getSerializable(Key.ResponseObject);

                                    if (newPage.data.photos.size() > 0) {
                                        mPageIndex++;
                                    }

                                    //Append
                                    if (mCurrentItem == null) {
                                        mCurrentItem = newPage;
                                    } else {
                                        mCurrentItem.data.photos.addAll(newPage.data.photos);
                                    }

                                    previewScreenData();
                                } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                    refreshToken(new TenpossCallback() {
                                        @Override
                                        public void onSuccess(Bundle params) {
                                            loadPhotoCatItem();
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
            case RecyclerItemTypeRestaurantGridImage: {
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
}
