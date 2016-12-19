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

import java.util.ArrayList;

import jp.tenposs.adapter.AbstractRecyclerAdapter;
import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.GridSpanSizeLookup;
import jp.tenposs.adapter.MarginDecoration;
import jp.tenposs.adapter.RecyclerDataSource;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.communicator.NewsCategoryCommunicator;
import jp.tenposs.communicator.NewsInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.NewsCategoryInfo;
import jp.tenposs.datamodel.NewsInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.utils.FontIcon;
import jp.tenposs.utils.Utils;


/**
 * Created by ambient on 7/27/16.
 */
public class FragmentNews extends AbstractFragment implements RecyclerDataSource, OnCommonItemClickListener, View.OnClickListener {

    ProgressBar mLoadingIndicator;
    LinearLayout mSubToolbar;
    ImageButton mPreviousButton;
    TextView mTitleLabel;
    ImageButton mNextButton;
    TextView mNoDataLabel;
    RecyclerView mRecyclerView;
    AbstractRecyclerAdapter mRecyclerAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    NewsCategoryInfo.Response mScreenData;
    NewsInfo.Response mCurrentItem;
    NewsCategoryInfo.Category mCurrentNewsCat;
    int mCurrentNewsCatIndex;


    ArrayList<Bundle> mAllItems;

    /**
     * Fragment Override
     */


//    public static FragmentNews newInstance(String title, int storeId) {
//        FragmentNews fragment = new FragmentNews();
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
        mToolbarSettings.toolbarTitle = getString(R.string.news);
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
        loadNewsCategory();
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        setRefreshing(false);
        if (mScreenData != null && mScreenData.data != null && mScreenData.data.news_categories != null && mScreenData.data.news_categories.size() > 0) {
            this.mSubToolbar.setVisibility(View.VISIBLE);


            updateNavigation();
            enableControls(true);
            mScreenDataItems = new ArrayList<>();

            for (NewsInfo.News item : mCurrentItem.data.news) {
                item.setCategory(this.mCurrentNewsCat.name);
                Bundle extras = new Bundle();
                extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
                extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
                extras.putString(RecyclerItemWrapper.ITEM_CATEGORY, item.getCategory());
                extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.getTitle());
                extras.putString(RecyclerItemWrapper.ITEM_DESCRIPTION, item.getDescription());
                extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item);
                mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeList, mSpanCount, extras));
            }

            mTitleLabel.setText(mCurrentNewsCat.name);

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
                                if (lastPos == getItemCount() - 1 && getItemCount() < mCurrentItem.totalnew) {
                                    if (mLoadingStatus == LOADING_STATUS_UNKNOWN) {
                                        mLoadingIndicator.setVisibility(View.VISIBLE);
                                        mLoadingStatus = LOADING_STATUS_MORE;
                                        loadNewsInfo(mCurrentNewsCatIndex);
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
                    mCurrentNewsCatIndex = 0;
                    loadNewsCategory();
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
            this.mScreenData = (NewsCategoryInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
        }
        if (savedInstanceState.containsKey(SCREEN_PAGE_ITEMS)) {
            this.mAllItems = (ArrayList<Bundle>) savedInstanceState.getSerializable(SCREEN_PAGE_ITEMS);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA_PAGE_DATA)) {
            this.mCurrentItem = (NewsInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA_PAGE_DATA);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA_ITEM_INDEX)) {
            this.mCurrentNewsCatIndex = savedInstanceState.getInt(SCREEN_DATA_ITEM_INDEX);
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
        outState.putInt(SCREEN_DATA_ITEM_INDEX, this.mCurrentNewsCatIndex);
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

    void loadNewsCategory() {
        enableControls(false);
        NewsCategoryInfo.Request requestParams = new NewsCategoryInfo.Request();
        requestParams.store_id = this.mStoreId;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        NewsCategoryCommunicator communicator = new NewsCategoryCommunicator(
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
                                mScreenData = (NewsCategoryInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                mAllItems = new ArrayList<>();
                                if (mScreenData.data.news_categories.size() > 0) {
                                    for (int i = 0; i < mScreenData.data.news_categories.size(); i++) {
                                        Bundle newsCatData = new Bundle();
                                        newsCatData.putInt(SCREEN_DATA_PAGE_INDEX, 1);
                                        newsCatData.putInt(SCREEN_DATA_PAGE_SIZE, DEFAULT_RECORD_PER_PAGE);
                                        mAllItems.add(newsCatData);
                                    }
                                    loadNewsInfo(mCurrentNewsCatIndex);
                                } else {
                                    previewScreenData();
                                }
                            } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                refreshToken(new TenpossCallback() {
                                    @Override
                                    public void onSuccess(Bundle params) {
                                        loadNewsCategory();
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

    void loadNewsInfo(final int newsCatIndex) {
        try {
            this.mCurrentNewsCat = this.mScreenData.data.news_categories.get(newsCatIndex);
            Bundle menuData = this.mAllItems.get(newsCatIndex);
            if (menuData.containsKey(SCREEN_DATA_PAGE_DATA) && this.mLoadingStatus == LOADING_STATUS_UNKNOWN) {
                this.mCurrentItem = (NewsInfo.Response) menuData.getSerializable(SCREEN_DATA_PAGE_DATA);
                previewScreenData();
            } else {
                enableControls(false);
                NewsInfo.Request requestParams = new NewsInfo.Request();
                requestParams.category_id = mCurrentNewsCat.id;
                requestParams.pageindex = menuData.getInt(SCREEN_DATA_PAGE_INDEX);
                requestParams.pagesize = menuData.getInt(SCREEN_DATA_PAGE_SIZE);

                Bundle params = new Bundle();
                params.putSerializable(Key.RequestObject, requestParams);
                params.putParcelable(Key.RequestData, menuData);
                NewsInfoCommunicator communicator = new NewsInfoCommunicator(
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
                                        NewsInfo.Response newPage = (NewsInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                        Bundle menuData = responseParams.getParcelable(Key.RequestData);

                                        if (newPage.data.news.size() > 0) {
                                            int pageindex = menuData.getInt(SCREEN_DATA_PAGE_INDEX);
                                            pageindex++;
                                            menuData.putInt(SCREEN_DATA_PAGE_INDEX, pageindex);
                                        }

                                        //Append
                                        mCurrentItem = (NewsInfo.Response) menuData.getSerializable(SCREEN_DATA_PAGE_DATA);
                                        if (mCurrentItem == null) {
                                            mCurrentItem = newPage;
                                        } else {
                                            mCurrentItem.data.news.addAll(newPage.data.news);
                                        }
                                        menuData.putSerializable(SCREEN_DATA_PAGE_DATA, mCurrentItem);

                                        previewScreenData();
                                    } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                        refreshToken(new TenpossCallback() {
                                            @Override
                                            public void onSuccess(Bundle params) {
                                                loadNewsInfo(newsCatIndex);
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
        return (mCurrentNewsCatIndex > 0);
    }

    boolean hasNext() {
        return (mCurrentNewsCatIndex < (this.mScreenData.data.news_categories.size() - 1));
    }

    void previous() {
        if (hasPrevious() == false) {
            return;
        }
        try {
            if (mCurrentNewsCatIndex > 0) {
                mCurrentNewsCatIndex--;
            }
        } catch (Exception ignored) {

        }
        loadNewsInfo(mCurrentNewsCatIndex);
    }

    void next() {
        if (hasNext() == false) {
            return;
        }
        try {
            if (mCurrentNewsCatIndex < this.mScreenData.data.news_categories.size() - 1) {
                mCurrentNewsCatIndex++;
            }
        } catch (Exception ignored) {

        }
        this.mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
                loadNewsInfo(mCurrentNewsCatIndex);
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
            case RecyclerItemTypeList: {
                NewsInfo.News news = (NewsInfo.News) item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.mActivityListener.showScreen(AbstractFragment.NEWS_DETAILS_SCREEN, news, null, false);
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
