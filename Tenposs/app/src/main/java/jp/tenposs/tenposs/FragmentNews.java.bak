package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import junit.framework.Assert;

import java.util.ArrayList;

import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.communicator.NewsInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.NewsInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.utils.FlatIcon;


/**
 * Created by ambient on 7/27/16.
 */
public class FragmentNews
        extends
        AbstractFragment
        implements
        CommonAdapter.CommonDataSource,
        OnCommonItemClickListener,
        View.OnClickListener {

    ImageButton mPreviousButton;
    TextView mTitleLabel;
    ImageButton mNextButton;
    RecyclerView mRecyclerView;
    CommonAdapter mRecyclerAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    NewsInfo.Response mScreenData = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = 1;
        mPageSize = 20;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA)) {
                this.mScreenData = (NewsInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
                startup();
            }
        } else {
            startup();
        }
    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.news);
        mToolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
    }

    @Override
    protected void clearScreenData() {

    }

    @Override
    protected void reloadScreenData() {
        if (this.mScreenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        //loadAppInfo();
        loadNewsInfo();
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        mScreenDataItems = new ArrayList<>();

        for (NewsInfo.News item : mScreenData.data.news) {
            Bundle extras = new Bundle();
            extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
            extras.putString(RecyclerItemWrapper.ITEM_CATEGORY, getString(R.string.category_text));
            extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.title);
            extras.putString(RecyclerItemWrapper.ITEM_DESCRIPTION, item.description);
            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemList, mSpanCount, extras));
        }

        this.mSwipeRefreshLayout.setRefreshing(false);

        if (this.mRecyclerAdapter == null) {
            GridLayoutManager manager = new GridLayoutManager(getActivity(), mSpanCount);//);
            this.mRecyclerAdapter = new CommonAdapter(getActivity(), this, this);
            manager.setSpanSizeLookup(new CommonAdapter.GridSpanSizeLookup(mRecyclerAdapter));
            this.mRecyclerView.setLayoutManager(manager);
            this.mRecyclerView.addItemDecoration(new CommonAdapter.MarginDecoration(getActivity()));
            this.mRecyclerView.setAdapter(mRecyclerAdapter);
        } else {
            this.mRecyclerAdapter.notifyDataSetChanged();
        }
        updateNavigation();
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_news, null);

        mPreviousButton = (ImageButton) mRoot.findViewById(R.id.previous_button);
        mTitleLabel = (TextView) mRoot.findViewById(R.id.title_label);
        mNextButton = (ImageButton) mRoot.findViewById(R.id.next_button);

        this.mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.recycler_view);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) mRoot.findViewById(R.id.swipe_refresh_layout);
        this.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentNews.this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusUnload;
                reloadScreenData();
            }
        });
        mPreviousButton.setImageBitmap(FlatIcon.fromFlatIcon(getContext().getAssets(),
                "flaticon-back",
                40,
                Color.argb(0, 0, 0, 0),
                mToolbarSettings.appSetting.getToolbarIconColor()
        ));

        mNextButton.setImageBitmap(FlatIcon.fromFlatIcon(getContext().getAssets(),
                "flaticon-next",
                40,
                Color.argb(0, 0, 0, 0),
                mToolbarSettings.appSetting.getToolbarIconColor()
        ));
        mPreviousButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        return mRoot;
    }

    @Override
    protected void customResume() {

    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (NewsInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
        }
        if (savedInstanceState.containsKey(APP_DATA_STORE_ID)) {
            this.mStoreId = savedInstanceState.getInt(APP_DATA_STORE_ID);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA_PAGE_INDEX)) {
            this.mPageIndex = savedInstanceState.getInt(SCREEN_DATA_PAGE_INDEX);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA_PAGE_SIZE)) {
            this.mPageSize = savedInstanceState.getInt(SCREEN_DATA_PAGE_SIZE);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {
        this.mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    boolean canCloseByBackpressed() {
        return false;
    }


    @Override
    public int getItemCount() {
        return this.mScreenDataItems.size();
    }

    @Override
    public RecyclerItemWrapper getItemData(int position) {
        return this.mScreenDataItems.get(position);
    }

    @Override
    public void onCommonItemClick(int position, Bundle extraData) {
        RecyclerItemWrapper item = getItemData(position);

        switch (item.itemType) {
            case RecyclerItemTypeItemList: {
                int id = item.itemData.getInt(RecyclerItemWrapper.ITEM_ID);
                NewsInfo.News news = this.mScreenData.getItemById(id);
                this.mActivityListener.showScreen(AbstractFragment.NEWS_DETAILS_SCREEN, news);
            }
            break;

            default: {
                Assert.assertFalse("" + item.itemType, false);
            }
            break;
        }
    }

    protected void startup() {
        if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            this.mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    FragmentNews.this.mSwipeRefreshLayout.setRefreshing(true);
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

    void loadNewsInfo() {
        Bundle params = new Bundle();
        NewsInfo.Request requestParams = new NewsInfo.Request();
        requestParams.store_id = this.mStoreId;
        requestParams.pageindex = this.mPageIndex;
        requestParams.pagesize = this.mPageSize;

        params.putSerializable(Key.RequestObject, requestParams);
        NewsInfoCommunicator communicator = new NewsInfoCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                FragmentNews.this.mScreenData = (NewsInfo.Response) responseParams.getSerializable(Key.ResponseObject);
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

    boolean hasPrevious() {
        //return (menuIndex > 0);
        return false;

    }

    boolean hasNext() {
        return false;
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
                "ti-angle-left",
                20,
                Color.argb(0, 0, 0, 0),
                previousButtonColor
        ));

        mNextButton.setImageBitmap(FlatIcon.fromFlatIcon(getContext().getAssets(),
                "ti-angle-right",
                20,
                Color.argb(0, 0, 0, 0),
                nextButtonColor
        ));
    }

    @Override
    public void onClick(View v) {

    }
}
