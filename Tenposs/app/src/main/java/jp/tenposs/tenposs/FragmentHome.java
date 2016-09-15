package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
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
import jp.tenposs.communicator.AppInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.communicator.TopInfoCommunicator;
import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.ItemInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.NewsInfo;
import jp.tenposs.datamodel.PhotoInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.TopInfo;
import jp.tenposs.listener.OnCommonItemClickListener;


/**
 * Created by ambient on 7/26/16.
 */
public class FragmentHome
        extends
        AbstractFragment
        implements
        CommonAdapter.CommonDataSource,
        OnCommonItemClickListener {

    ArrayList<AppInfo.SideMenu> sideMenuInfo = null;
    AppInfo.Response appInfo = null;
    AppInfo.Store storeInfo = null;

    TopInfo.Response topData = null;
    TopInfo.Response.ResponseData screenData = null;


    RecyclerView recyclerView;
    CommonAdapter recyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = "";
        toolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
    }

    @Override
    protected void reloadScreenData() {
        if (this.screenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        loadAppInfo();
    }

    @Override
    protected void previewScreenData() {
        screenDataItems = new ArrayList<>();
        Bundle extras;

//        //Mockup Data
//        screenData.images.data.add(new TopInfo.Image("http://wallpapers-and-backgrounds.net/wp-content/uploads/2016/01/bikini-hd-background_1.jpg"));
//        screenData.images.data.add(new TopInfo.Image("http://wallpapers-and-backgrounds.net/wp-content/uploads/2016/01/bikini-1080p-background_1.jpg"));
//        screenData.images.data.add(new TopInfo.Image("http://wallpapers-and-backgrounds.net/wp-content/uploads/2016/01/bikini-full-hd-background_1.jpg"));
//        screenData.images.data.add(new TopInfo.Image("http://wallpapers-and-backgrounds.net/wp-content/uploads/2016/01/bikini-1080p-wallpaper_1.jpg"));
//        screenData.images.data.add(new TopInfo.Image("http://wallpapers-and-backgrounds.net/wp-content/uploads/2016/01/bikini-sexy-background_1.jpg"));

        if (screenData.images != null && screenData.images.size() > 0) {
            AppInfo.TopComponent component = this.appInfo.data.getTopComponent(screenData.images.top_id);
            if (component != null) {
                extras = new Bundle();
                extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, screenData.images.data);

                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeTopItem, spanCount, extras));
            }
        }

        if (screenData.items != null && screenData.items.size() > 0) {
            AppInfo.TopComponent component = this.appInfo.data.getTopComponent(screenData.items.top_id);
            if (component != null) {
                /**
                 * Header
                 */
                extras = new Bundle();
                extras.putString(RecyclerItemWrapper.ITEM_TITLE, component.name);
                extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.MENU_SCREEN);

                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, spanCount, extras));

                /**
                 * Content
                 */
                for (ItemInfo.Item item : screenData.items.data) {
                    extras = new Bundle();
                    extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
                    extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.ITEM_SCREEN);
                    extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.title);
                    extras.putString(RecyclerItemWrapper.ITEM_DESCRIPTION, item.price);
                    extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
                    extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item);

                    screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGrid, spanCount / spanLargeItems, extras));
                }

                if (component.showViewMore() == true) {
                    /**
                     * Footer
                     */
                    extras = new Bundle();
                    extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.MENU_SCREEN);
                    extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.more));

                    screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, spanCount, extras));
                }
            }
        }
        if (screenData.photos != null && screenData.photos.size() > 0) {
            AppInfo.TopComponent component = this.appInfo.data.getTopComponent(screenData.photos.top_id);
            if (component != null) {
                /**
                 * Header
                 */
                extras = new Bundle();
                extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.PHOTO_SCREEN);
                extras.putString(RecyclerItemWrapper.ITEM_TITLE, component.name);

                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, spanCount, extras));

                /**
                 * Content
                 */
                for (PhotoInfo.Photo item : screenData.photos.data) {
                    extras = new Bundle();
                    extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
                    extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.PHOTO_ITEM_SCREEN);
                    extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
                    extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item.getImageUrl());

                    screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGridImageOnly, spanCount / spanSmallItems, extras));
                }

                if (component.showViewMore() == true) {
                    /**
                     * Footer
                     */
                    extras = new Bundle();
                    extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.PHOTO_SCREEN);
                    extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.more));

                    screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, spanCount, extras));
                }
            }
        }

        if (screenData.news != null && screenData.news.size() > 0) {
            AppInfo.TopComponent component = this.appInfo.data.getTopComponent(screenData.news.top_id);
            /**
             * Header
             */
            if (component != null) {
                extras = new Bundle();
                extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.NEWS_SCREEN);
                extras.putString(RecyclerItemWrapper.ITEM_TITLE, component.name);

                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, spanCount, extras));

                /**
                 * Content
                 */
                for (NewsInfo.News item : screenData.news.data) {
                    extras = new Bundle();
                    extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
                    extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.NEWS_DETAILS_SCREEN);
                    extras.putString(RecyclerItemWrapper.ITEM_CATEGORY, "Category");
                    extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.title);
                    extras.putString(RecyclerItemWrapper.ITEM_DESCRIPTION, item.description);
                    extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
                    extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item);

                    screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemList, spanCount, extras));
                }

                if (component.showViewMore() == true) {
                    /**
                     * Footer
                     */
                    extras = new Bundle();
                    extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.NEWS_SCREEN);
                    extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.more));

                    screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, spanCount, extras));
                }
            }
        }

        if (screenData.contact != null && screenData.contact.size() > 0) {
            AppInfo.TopComponent component = this.appInfo.data.getTopComponent(screenData.contact.top_id);
            if (component != null) {
                for (TopInfo.Contact contact : screenData.contact.data) {
                    /**
                     * Content
                     */
                    extras = new Bundle();
                    extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, contact);

                    screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemStore, spanCount, extras));

                    /**
                     * Footer
                     */
                    extras = new Bundle();
                    extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.RESERVE_SCREEN);
                    extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.reserve));
                    extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, contact);

                    screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, spanCount, extras));
                }
            }
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
        this.toolbarSettings.appSetting = this.appInfo.data.app_setting;
        this.activityListener.updateAppInfo(this.appInfo, storeInfo.id);
        this.activityListener.updateSideMenuItems(this.sideMenuInfo, isSignedIn());

        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_home, null);
        this.recyclerView = (RecyclerView) mRoot.findViewById(R.id.recycler_view);
        this.swipeRefreshLayout = (SwipeRefreshLayout) mRoot.findViewById(R.id.swipe_refresh_layout);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentHome.this.screenDataStatus = ScreenDataStatus.ScreenDataStatusUnload;
                reloadScreenData();
            }
        });
        return mRoot;
    }

    @Override
    protected void customResume() {
        if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            this.swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    FragmentHome.this.swipeRefreshLayout.setRefreshing(true);
                    loadAppInfo();
                }
            });

        } else if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //just waiting

        } else {
            //reload screen, this case application return from background or from other activity
            previewScreenData();
        }
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.screenData = (TopInfo.Response.ResponseData) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putSerializable(SCREEN_DATA, this.screenData);
    }

    @Override
    void setRefreshing(boolean refreshing) {
        this.swipeRefreshLayout.setRefreshing(refreshing);
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
            case RecyclerItemTypeTopItem: {
                //Do nothing
            }
            break;

            case RecyclerItemTypeHeader: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                this.activityListener.showScreen(screenId, null);
            }
            break;

            case RecyclerItemTypeItemList: {
                //TODO:
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.activityListener.showScreen(screenId, extras);

            }
            break;

            case RecyclerItemTypeItemStore: {
                //Do nothing
            }
            break;

            case RecyclerItemTypeItemGrid: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.activityListener.showScreen(screenId, extras);
            }
            break;

            case RecyclerItemTypeItemGridImageOnly: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.activityListener.showScreen(screenId, extras);
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

    void loadAppInfo() {
        Bundle params = new Bundle();
        AppInfo.Request requestParams = new AppInfo.Request();

        params.putSerializable(Key.RequestObject, requestParams);
        AppInfoCommunicator communicator = new AppInfoCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                FragmentHome.this.appInfo = (AppInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                if (FragmentHome.this.appInfo.data != null && FragmentHome.this.appInfo.data.stores.size() > 0) {
                                    FragmentHome.this.storeInfo = FragmentHome.this.appInfo.data.stores.get(0);
                                    FragmentHome.this.sideMenuInfo = FragmentHome.this.appInfo.data.side_menu;
                                    toolbarSettings.toolbarTitle = FragmentHome.this.appInfo.data.name;
                                    loadTopInfo(FragmentHome.this.storeInfo.id);
                                } else {
                                    String strMessage = "Invalid response data!";
                                    errorWithMessage(responseParams, strMessage);
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

    void loadTopInfo(final int storeId) {
        Bundle params = new Bundle();
        TopInfo.Request requestParams = new TopInfo.Request();
        params.putSerializable(Key.RequestObject, requestParams);
        TopInfoCommunicator communicator = new TopInfoCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                FragmentHome.this.topData = (TopInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                FragmentHome.this.screenData = FragmentHome.this.topData.data;
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
