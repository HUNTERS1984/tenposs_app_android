package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import jp.tenposs.communicator.ItemDetailCommunicator;
import jp.tenposs.communicator.ItemRelatedCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.ItemDetailInfo;
import jp.tenposs.datamodel.ItemRelatedInfo;
import jp.tenposs.datamodel.ItemsInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 7/27/16.
 */
public class RestaurantFragmentItemDetail extends AbstractFragment implements RecyclerDataSource, OnCommonItemClickListener {

    final static String RELATED_ITEMS = "RELATED_ITEMS";
    final static String TOTAL_RELATED_ITEMS = "TOTAL_RELATED_ITEMS";

    ItemsInfo.Item mScreenData;
    ArrayList<ItemsInfo.Item> mRelatedItems;
    int mTotalRelatedItems;

    int mScreenDataId;
    String mScreenTitle;

    AbstractRecyclerAdapter mRecyclerAdapter;
    TextView mNoDataLabel;
    RecyclerView mRecyclerView;

    private RestaurantFragmentItemDetail() {

    }

    public static RestaurantFragmentItemDetail newInstance(Serializable extras) {
        RestaurantFragmentItemDetail fragment = new RestaurantFragmentItemDetail();
        Bundle b = new Bundle();
        ItemsInfo.Item item = (ItemsInfo.Item) extras;
        b.putInt(AbstractFragment.SCREEN_DATA_ID, item.id);
        b.putString(AbstractFragment.SCREEN_TITLE, item.getTitle());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = "";
        mToolbarSettings.toolbarLeftIcon = "flaticon-close";
        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void clearScreenData() {
        RestaurantFragmentItemDetail.this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusUnload;
        RestaurantFragmentItemDetail.this.mScreenData = null;
    }

    @Override
    protected void reloadScreenData() {
        if (this.mScreenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        //loadAppInfo();
        loadItemDetail();
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        mScreenDataItems = new ArrayList<>();

        if (this.mScreenData != null) {
            Bundle extras;

            //Image
            extras = new Bundle();
            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, mScreenData.getImageUrl());
            extras.putString(RecyclerItemWrapper.ITEM_CATEGORY, mScreenData.getCategory());
            extras.putString(RecyclerItemWrapper.ITEM_TITLE, mScreenData.getTitle());
            extras.putString(RecyclerItemWrapper.ITEM_PRICE, mScreenData.getPrice());
            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeRestaurantProductInfo, mSpanCount, extras));


            //Detail
            extras = new Bundle();
            extras.putSerializable(RecyclerItemWrapper.ITEM_CATEGORY, "商品詳細");
            extras.putSerializable(RecyclerItemWrapper.ITEM_DESCRIPTION, mScreenData.getDescription());
            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeRestaurantProductDetail, mSpanCount, extras));

            //Related
            if (mRelatedItems != null && mRelatedItems.size() > 0) {
                /**
                 * Header
                 */
                extras = new Bundle();
                extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.related));
                mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, mSpanCount, extras));

                /**
                 * Content
                 */
                for (ItemsInfo.Item item : mRelatedItems) {
                    extras = new Bundle();
                    extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
                    extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.ITEM_SCREEN);
                    extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.getTitle());
                    extras.putString(RecyclerItemWrapper.ITEM_PRICE, item.getPrice());
                    extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
                    extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item);
                    mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeRestaurantGridItem, mSpanCount / mSpanLargeItems, extras));
                }

                if (this.mTotalRelatedItems > this.mRelatedItems.size()) {
                    extras = new Bundle();
                    extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.more));
                    extras.putInt(RecyclerItemWrapper.ITEM_BACKGROUND, R.drawable.bg_button);
                    extras.putInt(RecyclerItemWrapper.ITEM_TEXT_COLOR, R.color.button_text_color);
                    mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, mSpanCount, extras));
                }
            }
        }

        if (this.mRecyclerAdapter == null) {
            GridLayoutManager manager = new GridLayoutManager(getActivity(), mSpanCount);//);
            this.mRecyclerAdapter = new RestaurantAdapter(getActivity(), this, this);
            manager.setSpanSizeLookup(new GridSpanSizeLookup(mRecyclerAdapter));
            this.mRecyclerView.setLayoutManager(manager);
            this.mRecyclerView.addItemDecoration(new MarginDecoration(getActivity(), R.dimen.restaurant_item_spacing));
            this.mRecyclerView.setAdapter(mRecyclerAdapter);
        } else {
            this.mRecyclerAdapter.notifyDataSetChanged();
        }
        if (this.mScreenDataItems.size() == 0) {
            this.mNoDataLabel.setVisibility(View.VISIBLE);
        } else {
            this.mNoDataLabel.setVisibility(View.GONE);
        }

        mToolbarSettings.toolbarTitle = this.mScreenTitle;
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_recycler_view, null);
        this.mNoDataLabel = (TextView) mRoot.findViewById(R.id.no_data_label);
        this.mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.recycler_view);
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            this.mRecyclerView.setBackgroundColor(Color.rgb(243, 243, 243));
        } else {
            this.mRecyclerView.setBackgroundColor(Color.WHITE);
        }

        return mRoot;
    }

    void loadItemDetail() {
        ItemDetailInfo.Request requestParams = new ItemDetailInfo.Request();
        requestParams.token = Utils.getPrefString(getContext(), Key.TokenKey);
        requestParams.item_id = this.mScreenDataId;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        Utils.showProgress(getContext(), getString(R.string.msg_loading));
        ItemDetailCommunicator communicatior = new ItemDetailCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                if (isAdded() == false) {
                    return;
                }
                Utils.hideProgress();
                int result = responseParams.getInt(Key.ResponseResult);
                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                    if (resultApi == CommonResponse.ResultSuccess) {
                        ItemDetailInfo.Response response = (ItemDetailInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                        mScreenData = response.data.items;
                        mRelatedItems = response.data.items_related;
                        mTotalRelatedItems = response.data.total_items_related;
                        previewScreenData();
                    } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                        refreshToken(new TenpossCallback() {
                            @Override
                            public void onSuccess(Bundle params) {
                                loadItemDetail();
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
        communicatior.execute(params);
    }

    void loadItemRelated() {
        ItemRelatedInfo.Request requestParams = new ItemRelatedInfo.Request();
        requestParams.token = Utils.getPrefString(getContext(), Key.TokenKey);
        requestParams.item_id = this.mScreenDataId;
        requestParams.pageindex = this.mPageIndex;
        requestParams.pagesize = this.mPageSize;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        Utils.showProgress(getContext(), getString(R.string.msg_loading));
        ItemRelatedCommunicator communicatior = new ItemRelatedCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                if (isAdded() == false) {
                    return;
                }
                Utils.hideProgress();
                int result = responseParams.getInt(Key.ResponseResult);
                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                    if (resultApi == CommonResponse.ResultSuccess) {
                        ItemRelatedInfo.Response response = (ItemRelatedInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                        mRelatedItems = response.data.items;
                        previewScreenData();
                    } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                        refreshToken(new TenpossCallback() {
                            @Override
                            public void onSuccess(Bundle params) {
                                loadItemRelated();
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
        communicatior.execute(params);
    }

    @Override
    protected void customResume() {
        if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            loadItemDetail();

        } else if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //just waiting

        } else {
            //reload screen, this case application return from background or from other activity
            previewScreenData();
        }
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA_ID)) {
            this.mScreenDataId = savedInstanceState.getInt(SCREEN_DATA_ID);
        }

        if (savedInstanceState.containsKey(SCREEN_TITLE)) {
            this.mScreenTitle = savedInstanceState.getString(SCREEN_TITLE);
        }

        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (ItemsInfo.Item) savedInstanceState.getSerializable(SCREEN_DATA);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA_PAGE_INDEX)) {
            this.mPageIndex = savedInstanceState.getInt(SCREEN_DATA_PAGE_INDEX);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA_PAGE_SIZE)) {
            this.mPageSize = savedInstanceState.getInt(SCREEN_DATA_PAGE_SIZE);
        }

        if (savedInstanceState.containsKey(RELATED_ITEMS)) {
            this.mRelatedItems = (ArrayList<ItemsInfo.Item>) savedInstanceState.getSerializable(RELATED_ITEMS);
        }
        if (savedInstanceState.containsKey(TOTAL_RELATED_ITEMS)) {
            this.mTotalRelatedItems = savedInstanceState.getInt(TOTAL_RELATED_ITEMS);
        }


    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putInt(SCREEN_DATA_ID, this.mScreenDataId);
        if (this.mScreenData != null) {
            outState.putSerializable(SCREEN_DATA, this.mScreenData);
        }
        outState.putInt(SCREEN_DATA_PAGE_INDEX, this.mPageIndex);
        outState.putInt(SCREEN_DATA_PAGE_SIZE, this.mPageSize);
        if (this.mRelatedItems != null) {
            outState.putSerializable(RELATED_ITEMS, this.mRelatedItems);
        }
        outState.putInt(TOTAL_RELATED_ITEMS, this.mTotalRelatedItems);
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return true;
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

            case RecyclerItemTypeProductImage: {
                //Do nothing
            }
            break;

            case RecyclerItemTypeProductDescription: {
                //Do nothing
            }
            break;

            case RecyclerItemTypeProductTitle: {
                //Do nothing
                //showPurchase
                int screenId = extraData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = extraData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.mActivityListener.showScreen(screenId, extras, null, false);
            }
            break;

            case RecyclerItemTypeHeader: {
                //Do nothing
            }
            break;

            case RecyclerItemTypeRestaurantGridItem: {
                //TODO: Related items, need to load item detail then show info, api dang bi loi
                //load relate item details?
                ItemsInfo.Item relatedItem = (ItemsInfo.Item) item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);

                RestaurantFragmentItemDetail fragmentProduct = RestaurantFragmentItemDetail.newInstance(relatedItem);
                mActivityListener.showFragment(fragmentProduct, RestaurantFragmentItemDetail.class.getCanonicalName() + System.currentTimeMillis(), true);
            }
            break;

            case RecyclerItemTypeFooter: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.mActivityListener.showScreen(screenId, extras, null, false);
            }
            break;

            default: {
                Assert.assertFalse("Should never be here " + item.itemType, false);
            }
            break;
        }
    }
}
