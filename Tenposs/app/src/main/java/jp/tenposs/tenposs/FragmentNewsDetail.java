package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.datamodel.NewsInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;

/**
 * Created by ambient on 8/24/16.
 */
public class FragmentNewsDetail
        extends
        AbstractFragment
        implements
        CommonAdapter.CommonDataSource,
        OnCommonItemClickListener {


    RecyclerView recyclerView;
    CommonAdapter recyclerAdapter;

    NewsInfo.News screenData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spanCount = 6;
    }

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = "News";
        toolbarSettings.toolbarIcon = "ti-arrow-left";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void reloadScreenData() {
        if (this.screenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
    }

    @Override
    protected void previewScreenData() {
        Bundle extras;
        screenDataItems = new ArrayList<>();

        //Image
        extras = new Bundle();
        extras.putString(RecyclerItemWrapper.ITEM_IMAGE, screenData.getImageUrl());
        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeNewsImage, spanCount, extras));

        //title
        extras = new Bundle();
        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, screenData);
        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeNewsTitle, spanCount, extras));

        //Description
        extras = new Bundle();
        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, screenData);
        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeNewsDescription, spanCount, extras));

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

        setRefreshing(false);
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_news, null);

        this.recyclerView = (RecyclerView) mRoot.findViewById(R.id.recycler_view);

        return mRoot;
    }

    @Override
    protected void customResume() {
        /*if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        } else if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //just waiting
        } else {
            //reload screen, this case application return from background or from other activity
            previewScreenData();
        }*/
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.screenData = (NewsInfo.News) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void setRefreshing(boolean refreshing) {

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

    }
}
