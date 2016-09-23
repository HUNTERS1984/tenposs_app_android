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

import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.communicator.PhotoCategoryInfoCommunicator;
import jp.tenposs.communicator.PhotoInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.PhotoCategoryInfo;
import jp.tenposs.datamodel.PhotoInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.utils.FlatIcon;


/**
 * Created by ambient on 7/27/16.
 */
public class FragmentPhotoGallery extends AbstractFragment implements View.OnClickListener, CommonAdapter.CommonDataSource, OnCommonItemClickListener {

    ProgressBar mLoadingIndicator;
    LinearLayout mSubToolbar;
    ImageButton mPreviousButton;
    TextView mTitleLabel;
    ImageButton mNextButton;
    RecyclerView mRecyclerView;
    CommonAdapter mRecyclerAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    PhotoCategoryInfo.Response mScreenData;
    PhotoInfo.Response mCurrentItem;
    PhotoCategoryInfo.PhotoCat mCurrentPhotoCat;
    int mCurrentPhotoCatIndex;


    ArrayList<Bundle> mAllItems;

    /**
     * Fragment Override
     */

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.photo_gallery);
        mToolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
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
        loadPhotoCatData();
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        mSubToolbar.setVisibility(View.VISIBLE);
        setRefreshing(false);

        updateNavigation();

        mScreenDataItems = new ArrayList<>();

        for (PhotoInfo.Photo photo : mCurrentItem.data.photos) {
            Bundle extras = new Bundle();
            extras.putInt(RecyclerItemWrapper.ITEM_ID, photo.id);
            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.PHOTO_ITEM_SCREEN);
            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, photo.getImageUrl());
            extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, photo.getImageUrl());
            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGridImageOnly, mSpanCount / mSpanSmallItems, extras));
        }

        mTitleLabel.setText(mCurrentPhotoCat.name);
        if (this.mRecyclerAdapter == null) {
            GridLayoutManager manager = new GridLayoutManager(getActivity(), mSpanCount);//);
            this.mRecyclerAdapter = new CommonAdapter(getActivity(), this, this);
            manager.setSpanSizeLookup(new CommonAdapter.GridSpanSizeLookup(mRecyclerAdapter));
            this.mRecyclerView.setLayoutManager(manager);
            this.mRecyclerView.addItemDecoration(new CommonAdapter.MarginDecoration(getActivity()));
            this.mRecyclerView.setAdapter(mRecyclerAdapter);

            this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int lastPos = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                        if (lastPos != -1) {
                            if (lastPos == getItemCount() - 1 && getItemCount() < mCurrentItem.data.total_photos) {
                                if (mLoadingStatus == LOADING_STATUS_UNKNOWN) {
                                    mLoadingIndicator.setVisibility(View.VISIBLE);
                                    mLoadingStatus = LOADING_STATUS_MORE;
                                    loadPhotoCatItem(mCurrentPhotoCatIndex);
                                }
                            }
                        }
                    }
                }
            });
        } else {
            this.mRecyclerAdapter.notifyDataSetChanged();
        }
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, null);

        this.mLoadingIndicator = (ProgressBar) root.findViewById(R.id.loading_indicator);
        this.mSubToolbar = (LinearLayout) root.findViewById(R.id.sub_toolbar);
        this.mPreviousButton = (ImageButton) root.findViewById(R.id.previous_button);
        this.mTitleLabel = (TextView) root.findViewById(R.id.title_label);
        this.mNextButton = (ImageButton) root.findViewById(R.id.next_button);
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
                    mCurrentPhotoCatIndex = 0;
                    loadPhotoCatData();
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
            this.mScreenData = (PhotoCategoryInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
        }
        if (savedInstanceState.containsKey(SCREEN_PAGE_ITEMS)) {
            this.mAllItems = (ArrayList<Bundle>) savedInstanceState.getSerializable(SCREEN_PAGE_ITEMS);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA_PAGE_DATA)) {
            this.mCurrentItem = (PhotoInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA_PAGE_DATA);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA_PAGE_INDEX)) {
            this.mCurrentPhotoCatIndex = savedInstanceState.getInt(SCREEN_DATA_PAGE_INDEX);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putInt(APP_DATA_STORE_ID, this.mStoreId);
        outState.putSerializable(SCREEN_DATA, this.mScreenData);
        outState.putSerializable(SCREEN_PAGE_ITEMS, this.mAllItems);
        outState.putSerializable(SCREEN_DATA_PAGE_DATA, this.mCurrentItem);
        outState.putInt(SCREEN_DATA_PAGE_INDEX, this.mCurrentPhotoCatIndex);
    }

    @Override
    void setRefreshing(boolean refreshing) {
        this.mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    boolean canCloseByBackpressed() {
        return false;
    }

    void loadPhotoCatData() {
        PhotoCategoryInfo.Request requestParams = new PhotoCategoryInfo.Request();
        requestParams.store_id = this.mStoreId;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        PhotoCategoryInfoCommunicator communicator = new PhotoCategoryInfoCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        mLoadingStatus = LOADING_STATUS_UNKNOWN;
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                mScreenData = (PhotoCategoryInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                mAllItems = new ArrayList<>();
                                if (mScreenData.data.photo_categories.size() > 0) {
                                    for (int i = 0; i < mScreenData.data.photo_categories.size(); i++) {
                                        Bundle photoCategory = new Bundle();
                                        photoCategory.putInt(SCREEN_DATA_PAGE_INDEX, 1);
                                        photoCategory.putInt(SCREEN_DATA_PAGE_SIZE, DEFAULT_RECORD_PER_PAGE);
                                        mAllItems.add(photoCategory);
                                    }
                                    loadPhotoCatItem(mCurrentPhotoCatIndex);
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
    }

    void loadPhotoCatItem(int photoCatIndex) {
        try {
            this.mCurrentPhotoCat = this.mScreenData.data.photo_categories.get(photoCatIndex);
            Bundle photoCategory = this.mAllItems.get(photoCatIndex);
            if (photoCategory.containsKey(SCREEN_DATA_PAGE_DATA) && this.mLoadingStatus == LOADING_STATUS_UNKNOWN) {
                this.mCurrentItem = (PhotoInfo.Response) photoCategory.getSerializable(SCREEN_DATA_PAGE_DATA);
                previewScreenData();
            } else {
                PhotoInfo.Request requestParams = new PhotoInfo.Request();
                requestParams.category_id = mCurrentPhotoCat.id;
                requestParams.pageindex = photoCategory.getInt(SCREEN_DATA_PAGE_INDEX);
                requestParams.pagesize = photoCategory.getInt(SCREEN_DATA_PAGE_SIZE);

                Bundle params = new Bundle();
                params.putSerializable(Key.RequestObject, requestParams);
                params.putParcelable(Key.RequestData, photoCategory);
                PhotoInfoCommunicator communicator = new PhotoInfoCommunicator(
                        new TenpossCommunicator.TenpossCommunicatorListener() {
                            @Override
                            public void completed(TenpossCommunicator request, Bundle responseParams) {
                                mLoadingStatus = LOADING_STATUS_UNKNOWN;
                                mLoadingIndicator.setVisibility(View.INVISIBLE);
                                int result = responseParams.getInt(Key.ResponseResult);
                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                    if (resultApi == CommonResponse.ResultSuccess) {
                                        PhotoInfo.Response newPage = (PhotoInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                        Bundle photoCategory = responseParams.getParcelable(Key.RequestData);

                                        if (newPage.data.photos.size() > 0) {
                                            int pageindex = photoCategory.getInt(SCREEN_DATA_PAGE_INDEX);
                                            pageindex++;
                                            photoCategory.putInt(SCREEN_DATA_PAGE_INDEX, pageindex);
                                        }

                                        //Append
                                        mCurrentItem = (PhotoInfo.Response) photoCategory.getSerializable(SCREEN_DATA_PAGE_DATA);
                                        if (mCurrentItem == null) {
                                            mCurrentItem = newPage;
                                        } else {
                                            mCurrentItem.data.photos.addAll(newPage.data.photos);
                                        }
                                        photoCategory.putSerializable(SCREEN_DATA_PAGE_DATA, mCurrentItem);

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
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    boolean hasPrevious() {
        return (mCurrentPhotoCatIndex > 0);
    }

    boolean hasNext() {
        return (mCurrentPhotoCatIndex < (this.mScreenData.data.photo_categories.size() - 1));
    }

    void previous() {
        if (hasPrevious() == false) {
            return;
        }
        try {
            if (mCurrentPhotoCatIndex > 0) {
                mCurrentPhotoCatIndex--;
            }
        } catch (Exception ignored) {

        }
        loadPhotoCatItem(mCurrentPhotoCatIndex);
    }

    void next() {
        if (hasNext() == false) {
            return;
        }
        try {
            if (mCurrentPhotoCatIndex < this.mScreenData.data.photo_categories.size() - 1) {
                mCurrentPhotoCatIndex++;
            }
        } catch (Exception ignored) {

        }
        this.mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
                loadPhotoCatItem(mCurrentPhotoCatIndex);
            }
        });
    }

    void updateNavigation() {
        int previousButtonColor = Color.rgb(128, 128, 128);
        if (hasPrevious() == true) {
            previousButtonColor = Color.parseColor("#00CECB");
        }

        int nextButtonColor = Color.rgb(128, 128, 128);
        if (hasNext() == true) {
            nextButtonColor = Color.parseColor("#00CECB");
        }

        mPreviousButton.setImageBitmap(FlatIcon.fromFlatIcon(getContext().getAssets(),
                "flaticon-back",
                40,
                Color.argb(0, 0, 0, 0),
                previousButtonColor
        ));

        mNextButton.setImageBitmap(FlatIcon.fromFlatIcon(getContext().getAssets(),
                "flaticon-next",
                40,
                Color.argb(0, 0, 0, 0),
                nextButtonColor
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
            case RecyclerItemTypeItemGridImageOnly: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.mActivityListener.showScreen(screenId, extras);
            }
            break;

            default: {
                Assert.assertFalse("" + item.itemType, false);
            }
            break;
        }
    }
}
