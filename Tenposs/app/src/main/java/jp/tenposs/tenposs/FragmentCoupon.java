package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

import java.util.ArrayList;

import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.communicator.CouponInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.CouponInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;

/**
 * Created by ambient on 8/4/16.
 */
public class FragmentCoupon extends AbstractFragment implements CommonAdapter.CommonDataSource, OnCommonItemClickListener {

    RecyclerView recyclerView;
    CommonAdapter recyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    CouponInfo.Response screenData = null;
    int storeId;
    int pageIndex = 1;
    int pageSize = 20;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spanCount = 6;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA)) {
                this.screenData = (CouponInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
                startup();
            }
        } else {
            startup();
        }
    }

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = getString(R.string.coupon);
        toolbarSettings.toolbarLeftIcon= "flaticon-main-menu";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
    }

    @Override
    protected void reloadScreenData() {
        if (this.screenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        //loadAppInfo();
        loadCouponsInfo();
    }

    @Override
    protected void previewScreenData() {
        screenDataItems = new ArrayList<>();

        for (CouponInfo.Coupon item : screenData.data.coupons) {
            Bundle extras = new Bundle();
            extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
            extras.putString(RecyclerItemWrapper.ITEM_CATEGORY, "Category");
            extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.title);
            extras.putString(RecyclerItemWrapper.ITEM_DESCRIPTION, item.description);
            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, COUPON_DETAIL_SCREEN);
            extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item);
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemList, spanCount, extras));
        }

        this.swipeRefreshLayout.setRefreshing(false);
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;

        if (this.recyclerAdapter == null) {
            GridLayoutManager manager = new GridLayoutManager(getActivity(), spanCount);//);
            this.recyclerAdapter = new CommonAdapter(getActivity(), this, this);
            manager.setSpanSizeLookup(new CommonAdapter.GridSpanSizeLookup(recyclerAdapter));
            this.recyclerView.setLayoutManager(manager);
            this.recyclerView.addItemDecoration(new CommonAdapter.MarginDecoration(getActivity()));
            this.recyclerView.setAdapter(recyclerAdapter);
        } else {
            this.recyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_news, null);

        this.recyclerView = (RecyclerView) mRoot.findViewById(R.id.recycler_view);
        this.swipeRefreshLayout = (SwipeRefreshLayout) mRoot.findViewById(R.id.swipe_refresh_layout);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentCoupon.this.screenDataStatus = ScreenDataStatus.ScreenDataStatusUnload;
                reloadScreenData();
            }
        });
        return mRoot;
    }

    @Override
    protected void customResume() {

    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(APP_DATA_STORE_ID)) {
            this.storeId = savedInstanceState.getInt(APP_DATA_STORE_ID);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {
        this.swipeRefreshLayout.setRefreshing(refreshing);
    }


    @Override
    public int getItemCount() {
        return this.screenDataItems.size();
    }

    @Override
    public RecyclerItemWrapper getItemData(int position) {
        return this.screenDataItems.get(position);
    }

    @Override
    public void onCommonItemClick(int position, Bundle extraData) {
        RecyclerItemWrapper item = getItemData(position);

        switch (item.itemType) {
            case RecyclerItemTypeItemList: {
                int id = item.itemData.getInt(RecyclerItemWrapper.ITEM_ID);
                CouponInfo.Coupon coupon = this.screenData.getItemById(id);
                this.activityListener.showScreen(AbstractFragment.COUPON_DETAIL_SCREEN, coupon);
            }
            break;

            default: {
                Assert.assertFalse("" + item.itemType, false);
            }
            break;
        }
        System.out.println(item.itemType);
    }

    protected void startup() {
        if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            this.swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    FragmentCoupon.this.swipeRefreshLayout.setRefreshing(true);
                    loadCouponsInfo();
                }
            });

        } else if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //just waiting

        } else {
            //reload screen, this case application return from background or from other activity
            previewScreenData();
        }
    }

    void loadCouponsInfo() {
        Bundle params = new Bundle();
        CouponInfo.Request requestParams = new CouponInfo.Request();
        requestParams.store_id = this.storeId;
        requestParams.pageindex = this.pageIndex;
        requestParams.pagesize = this.pageSize;

        params.putSerializable(Key.RequestObject, requestParams);
        CouponInfoCommunicator communicator = new CouponInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                int result = responseParams.getInt(Key.ResponseResult);
                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                    if (resultApi == CommonResponse.ResultSuccess) {
                        FragmentCoupon.this.screenData = (CouponInfo.Response) responseParams.getSerializable(Key.ResponseObject);
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
}
