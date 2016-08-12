package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.communicator.AppInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.communicator.TopInfoCommunicator;
import jp.tenposs.communicator.UserInfoCommunicator;
import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.AppSettings;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.LoginInfo;
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

    ArrayList<AppInfo.Response.ResponseData.SideMenu> sideMenuInfo = null;
    AppInfo.Response appInfo = null;
    AppInfo.Response.ResponseData.Store storeInfo = null;

    TopInfo.Response topData = null;
    TopInfo.Response.ResponseData screenData = null;

    LoginInfo.Response userInfo = null;


    RecyclerView recyclerView;
    CommonAdapter recyclerAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Fragment Override
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SCREEN_DATA, this.screenData);
        outState.putInt(SCREEN_DATA_STATUS, this.screenDataStatus.ordinal());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spanCount = 6;
    }

    @Override
    void loadSavedInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey(SCREEN_DATA)) {
                this.screenData = (TopInfo.Response.ResponseData) savedInstanceState.getSerializable(SCREEN_DATA);
            }
        }
    }

    /**
     * AbstractFragment
     */

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA)) {
                this.screenData = (TopInfo.Response.ResponseData) savedInstanceState.getSerializable(SCREEN_DATA);
                startup();
            }
        } else {
            startup();
        }
    }

    /**
     * AbstractFragment
     */
    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings = new ToolbarSettings();
        toolbarSettings.toolbarTitle = "GLOBAL WORK";
        toolbarSettings.toolbarIcon = "ti-menu";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;

        toolbarSettings.settings = new AppSettings.Settings();
        toolbarSettings.settings.fontColor = "#00CECB";

        toolbarSettings.titleSettings = new AppSettings.Settings();
        toolbarSettings.titleSettings.fontColor = "#000000";
        toolbarSettings.titleSettings.fontSize = 20;
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

        if (screenData.images != null && screenData.images.size() > 0) {
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeTopItem, spanCount, screenData.images));
        }

        if (screenData.items != null && screenData.items.size() > 0) {
            RecyclerItemWrapper.RecyclerItemObject obj = new RecyclerItemWrapper.RecyclerItemObject();
            obj.title = "Recently";
            obj.id = AbstractFragment.MENU_SCREEN;

            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, spanCount, obj));
            for (TopInfo.Response.ResponseData.Item item : screenData.items) {
                obj = RecyclerItemWrapper.createItem(item.id, item.title, item.price, item.description, item.image_url);
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGrid, spanCount / 2, obj));
            }
            obj = new RecyclerItemWrapper.RecyclerItemObject();
            obj.title = "More";
            obj.id = AbstractFragment.MENU_SCREEN;

            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, spanCount, obj));
        }
        if (screenData.photos != null && screenData.photos.size() > 0) {
            RecyclerItemWrapper.RecyclerItemObject obj = new RecyclerItemWrapper.RecyclerItemObject();
            obj.title = "Photo Gallery";
            obj.id = AbstractFragment.PHOTO_SCREEN;

            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, spanCount, obj));
            for (TopInfo.Response.ResponseData.Photo item : screenData.photos) {
                obj = RecyclerItemWrapper.createItem(item.id, null, null, null, item.image_url);
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGridImageOnly, spanCount / 3, obj));
            }
            obj = new RecyclerItemWrapper.RecyclerItemObject();
            obj.title = "More";
            obj.id = AbstractFragment.PHOTO_SCREEN;
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, spanCount, obj));
        }

        if (screenData.news != null && screenData.news.size() > 0) {
            RecyclerItemWrapper.RecyclerItemObject obj = new RecyclerItemWrapper.RecyclerItemObject();
            obj.title = "News";
            obj.id = AbstractFragment.NEWS_SCREEN;
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, spanCount, obj));
            for (TopInfo.Response.ResponseData.News item : screenData.news) {
                //TODO: cho nay chua hoan thien
                obj = RecyclerItemWrapper.createItem(item.id, item.category_name, item.title, item.description, item.image_url);
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemList, spanCount, obj));
            }
            obj = new RecyclerItemWrapper.RecyclerItemObject();
            obj.title = "More";
            obj.id = AbstractFragment.NEWS_SCREEN;

            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, spanCount, obj));
        }

        /*TODO:if (screenData.addresses != null && screenData.addresses.size() > 0) {
            for (TopInfo.Response.ResponseData.Address item : screenData.addresses) {
                //TODO: cho nay chua hoan thien
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemStore, spanCount, item));
            }
        }*/

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

        this.activityListener.updateSideMenuItems(this.sideMenuInfo);
        this.activityListener.updateAppInfo(this.appInfo, storeInfo.id);
        this.activityListener.updateUserInfo(this.userInfo);
    }

    @Override
    public View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    public int getItemCount() {
        return screenDataItems.size();
    }

    @Override
    public RecyclerItemWrapper getItemData(int position) {
        return screenDataItems.get(position);
    }

    @Override
    public void onCommonItemClick(int position, Bundle extraData) {
        RecyclerItemWrapper item = (RecyclerItemWrapper) getItemData(position);

        switch (item.itemType) {
            case RecyclerItemTypeTopItem: {

            }
            break;

            case RecyclerItemTypeHeader: {
                RecyclerItemWrapper.RecyclerItemObject obj = (RecyclerItemWrapper.RecyclerItemObject) item.itemData;
                this.activityListener.showScreen(obj.id);
            }
            break;
            case RecyclerItemTypeItemList: {

            }
            break;
            case RecyclerItemTypeItemStore: {

            }
            break;
            case RecyclerItemTypeItemGrid: {

            }
            break;
            case RecyclerItemTypeItemGridImageOnly: {

            }
            break;
            case RecyclerItemTypeFooter: {

            }
            break;

            default: {

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

    void loadAppInfo() {
        Bundle params = new Bundle();
        AppInfo.Request requestParams = new AppInfo.Request();
        requestParams.app_id = 1;

        params.putSerializable(Key.RequestObject, requestParams);
        AppInfoCommunicator communicator = new AppInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
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
                            loadTopInfo(FragmentHome.this.storeInfo.id);
                        } else {
                            String strMessage = "Invalid response data!";
                            errorWithMessage(strMessage);
                        }
                    } else {
                        String strMessage = responseParams.getString(Key.ResponseMessage);
                        errorWithMessage(strMessage);
                    }
                } else {
                    String strMessage = responseParams.getString(Key.ResponseMessage);
                    errorWithMessage(strMessage);
                }
            }
        });
        communicator.execute(params);
    }

    void loadTopInfo(final int storeId) {
        Bundle params = new Bundle();
        TopInfo.Request requestParams = new TopInfo.Request();
        requestParams.app_id = 1;
        params.putSerializable(Key.RequestObject, requestParams);
        TopInfoCommunicator communicator = new TopInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                int result = responseParams.getInt(Key.ResponseResult);
                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                    if (resultApi == CommonResponse.ResultSuccess) {
                        FragmentHome.this.topData = (TopInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                        FragmentHome.this.screenData = FragmentHome.this.topData.data;

                        loadUserInfo();
                    } else {
                        String strMessage = responseParams.getString(Key.ResponseMessage);
                        errorWithMessage(strMessage);
                    }
                } else {
                    String strMessage = responseParams.getString(Key.ResponseMessage);
                    errorWithMessage(strMessage);
                }
            }
        });
        communicator.execute(params);
    }

    void loadUserInfo() {
        Bundle params = new Bundle();
        UserInfoCommunicator communicator = new UserInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                previewScreenData();
            }
        });
        communicator.execute(params);
    }
}
