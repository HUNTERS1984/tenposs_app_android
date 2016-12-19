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
import jp.tenposs.communicator.NewsInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.NewsInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;


/**
 * Created by ambient on 7/27/16.
 */
public class RestaurantFragmentNews
        extends AbstractFragment
        implements RecyclerDataSource, OnCommonItemClickListener {

    TextView mNoDataLabel;
    RecyclerView mRecyclerView;
    AbstractRecyclerAdapter mRecyclerAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    NewsInfo.Response mScreenData;

    /**
     * Fragment Override
     */

//    public static RestaurantFragmentNews newInstance(String title, int storeId) {
//        RestaurantFragmentNews fragment = new RestaurantFragmentNews();
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
        loadNewsInfo();
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        setRefreshing(false);

        mScreenDataItems = new ArrayList<>();
        //3 item dau tien de vao top

        ArrayList<NewsInfo.News> datas = new ArrayList<>();
        for (int i = 0; i < 3 && i < mScreenData.data.news.size(); i++) {
            NewsInfo.News news = mScreenData.data.news.get(i);
            datas.add(news);
        }

        if (datas.size() > 0) {
            Bundle extras = new Bundle();
            extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, datas);
            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeRestaurantNewsTop, mSpanCount, extras));


            if (mScreenData.data.news.size() > 3) {
                extras = new Bundle();
                extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.NEWS_SCREEN);
                try {
                    AppInfo.SideMenu menu = AppData.sharedInstance().mAppInfo.getSideMenuById(AbstractFragment.NEWS_SCREEN);
                    extras.putString(RecyclerItemWrapper.ITEM_TITLE, menu.name);
                } catch (Exception ignored) {

                }

                mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, mSpanCount, extras));

                ArrayList<NewsInfo.News> news = new ArrayList<>();
                for (int i = 3; i < mScreenData.data.news.size(); i++) {
                    NewsInfo.News item = mScreenData.data.news.get(i);
                    news.add(item);
                }


                extras = new Bundle();
                extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.NEWS_DETAILS_SCREEN);
                extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, news);
                extras.putSerializable(RecyclerItemWrapper.ITEM_CLASS, NewsInfo.News.class);

                mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeRestaurantRecyclerVertical, mSpanCount, extras));
            }
        } else {
            //TODO:
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
                            if (lastPos == getItemCount() - 1 && getItemCount() < mScreenData.totalnew) {
                                if (mLoadingStatus == LOADING_STATUS_UNKNOWN) {
                                    mLoadingStatus = LOADING_STATUS_MORE;
                                    loadNewsInfo();
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
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
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
                    loadNewsInfo();
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
            this.mScreenData = (NewsInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
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

    void loadNewsInfo() {
        try {
            NewsInfo.Request requestParams = new NewsInfo.Request();
            requestParams.category_id = 0;
            requestParams.pageindex = this.mPageIndex;
            requestParams.pagesize = this.mPageSize;

            Bundle params = new Bundle();
            params.putSerializable(Key.RequestObject, requestParams);
            NewsInfoCommunicator communicator = new NewsInfoCommunicator(
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
                                    NewsInfo.Response newPage = (NewsInfo.Response) responseParams.getSerializable(Key.ResponseObject);

                                    if (newPage.data.news.size() > 0) {
                                        mPageIndex++;
                                    }

                                    //Append
                                    if (mScreenData == null) {
                                        mScreenData = newPage;
                                    } else {
                                        mScreenData.data.news.addAll(newPage.data.news);
                                    }
                                    previewScreenData();
                                } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                    refreshToken(new TenpossCallback() {
                                        @Override
                                        public void onSuccess(Bundle params) {
                                            loadNewsInfo();
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
        } catch (
                Exception ignored
                )

        {
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
            case RecyclerItemTypeRestaurantRecyclerVertical: {
                int screenId = extraData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = extraData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.mActivityListener.showScreen(screenId, extras, null, false);
            }
            break;
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
}
