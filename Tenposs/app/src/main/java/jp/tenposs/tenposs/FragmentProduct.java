package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.communicator.ItemDetailCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.ItemDetail;
import jp.tenposs.datamodel.ItemsInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;

/**
 * Created by ambient on 7/27/16.
 */
public class FragmentProduct extends AbstractFragment implements CommonAdapter.CommonDataSource, OnCommonItemClickListener {
    ItemsInfo.Item mScreenData;

    CommonAdapter mRecyclerAdapter;
    RecyclerView mRecyclerView;

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = "";
        mToolbarSettings.toolbarLeftIcon = "flaticon-back";
        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void clearScreenData() {

    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        Bundle extras;
        mScreenDataItems = new ArrayList<>();

        //Image
        extras = new Bundle();
        extras.putString(RecyclerItemWrapper.ITEM_IMAGE, mScreenData.getImageUrl());
        mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeProductImage, mSpanCount, extras));

        //title
        extras = new Bundle();
        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, mScreenData);
        mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeProductTitle, mSpanCount, extras));

        //purchase
        extras = new Bundle();
        extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.ITEM_PURCHASE_SCREEN);
        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, mScreenData);
        extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.purchase));
        mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, mSpanCount, extras));

        //Description
        extras = new Bundle();
        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, mScreenData);
        mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeProductDescription, mSpanCount, extras));


        //Related
        if (mScreenData.rel_items != null && mScreenData.rel_items.size() > 0) {
            /**
             * Header
             */
            extras = new Bundle();
            extras.putString(RecyclerItemWrapper.ITEM_TITLE, "Related");
            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, mSpanCount, extras));

            /**
             * Content
             */
            for (ItemsInfo.Item item : mScreenData.rel_items) {
                extras = new Bundle();
                extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
                extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.ITEM_SCREEN);
                extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.title);
                extras.putString(RecyclerItemWrapper.ITEM_DESCRIPTION, item.getPrice());
                extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
                extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item);
                mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGrid, mSpanCount / mSpanLargeItems, extras));
            }

            /**
             * Footer

             extras = new Bundle();
             extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.more));
             mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, mSpanCount, extras));
             */
        }
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

        mToolbarSettings.toolbarTitle = mScreenData.title;
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_product, null);
        this.mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.recycler_view);
        return mRoot;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (ItemsInfo.Item) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

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
            }
            break;

            case RecyclerItemTypeHeader: {
                //Do nothing
            }
            break;

            case RecyclerItemTypeItemGrid: {
                //TODO: Related items, need to load item detail then show info, api dang bi loi
                //load relate item details?
                ItemsInfo.Item relatedItem = (ItemsInfo.Item) item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);

                ItemDetail.Request requestParams = new ItemDetail.Request();
                requestParams.token = getKeyString(Key.TokenKey);
                requestParams.item_id = relatedItem.id;

                Bundle params = new Bundle();
                params.putSerializable(Key.RequestObject, requestParams);
                showProgress(getString(R.string.msg_loading));
                ItemDetailCommunicator communicatior = new ItemDetailCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        hideProgress();
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                ItemDetail.Response response = (ItemDetail.Response) responseParams.getSerializable(Key.ResponseObject);
//                                mAllItems = new ArrayList<>();
//                                if (mScreenData.data.photo_categories.size() > 0) {
//                                    for (int i = 0; i < mScreenData.data.photo_categories.size(); i++) {
//                                        Bundle photoCategory = new Bundle();
//                                        photoCategory.putInt(SCREEN_DATA_PAGE_INDEX, 1);
//                                        photoCategory.putInt(SCREEN_DATA_PAGE_SIZE, DEFAULT_RECORD_PER_PAGE);
//                                        mAllItems.add(photoCategory);
//                                    }
//                                    loadPhotoCatItem(mCurrentPhotoCatIndex);
//                                } else {
//                                    mSubToolbar.setVisibility(View.GONE);
//                                }
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
                communicatior.execute(params);
            }
            break;

            case RecyclerItemTypeFooter: {
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
