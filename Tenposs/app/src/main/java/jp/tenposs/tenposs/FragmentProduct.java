package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import jp.tenposs.datamodel.ItemInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;

/**
 * Created by ambient on 7/27/16.
 */
public class FragmentProduct extends AbstractFragment implements CommonAdapter.CommonDataSource, OnCommonItemClickListener {
    ItemInfo.Item screenData;

    CommonAdapter recyclerAdapter;
    RecyclerView recyclerView;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = "";
        toolbarSettings.toolbarLeftIcon = "flaticon-back";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {
        Bundle extras;
        screenDataItems = new ArrayList<>();

        //Image
        extras = new Bundle();
        extras.putString(RecyclerItemWrapper.ITEM_IMAGE, screenData.getImageUrl());
        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeProductImage, spanCount, extras));

        //title
        extras = new Bundle();
        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, screenData);
        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeProductTitle, spanCount, extras));

        //purchase
        extras = new Bundle();
        extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.PURCHASE_SCREEN);
        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, screenData);
        extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.purchase));
        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, spanCount, extras));

        //Description
        extras = new Bundle();
        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, screenData);
        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeProductDescription, spanCount, extras));


        //Related
        if (screenData.rel_items != null && screenData.rel_items.size() > 0) {
            /**
             * Header
             */
            extras = new Bundle();
            extras.putString(RecyclerItemWrapper.ITEM_TITLE, "Related");
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, spanCount, extras));

            /**
             * Content
             */
            for (ItemInfo.RelateItem item : screenData.rel_items) {
                extras = new Bundle();
                extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
                extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.ITEM_SCREEN);
                extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.title);
                extras.putString(RecyclerItemWrapper.ITEM_DESCRIPTION, item.price);
                extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
                extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item);
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGrid, spanCount / spanLargeItems, extras));
            }

            /**
             * Footer

             extras = new Bundle();
             extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.more));
             screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, spanCount, extras));
             */
        }
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

        toolbarSettings.toolbarTitle = screenData.title;
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_product, null);
        this.recyclerView = (RecyclerView) mRoot.findViewById(R.id.recycler_view);
        return mRoot;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.screenData = (ItemInfo.Item) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    public int getItemCount() {
        return screenDataItems.size();
    }

    @Override
    public RecyclerItemWrapper getItemData(int position) {
        return screenDataItems.get(position);
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
                //TODO: Related items, need to load item detail then show info
                screenData = (ItemInfo.Item) item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                previewScreenData();
            }
            break;

            case RecyclerItemTypeFooter: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                this.activityListener.showScreen(screenId, null);
            }
            break;

            default: {
                Assert.assertFalse("" + item.itemType, false);
            }
            break;
        }

    }
}
