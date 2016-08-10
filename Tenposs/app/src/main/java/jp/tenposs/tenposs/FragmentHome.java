package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.communicator.AppInfoCommunicator;
import jp.tenposs.communicator.SideMenuCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.communicator.TopInfoCommunicator;
import jp.tenposs.communicator.UserInfoCommunicator;
import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.AppSettings;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.LoginInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.SideMenuInfo;
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

    SideMenuInfo.Response menuInfo = null;
    AppInfo.Response appInfo = null;
    AppInfo.Response.ResponseData storeInfo = null;

    TopInfo.Response topData = null;
    TopInfo.Response.ResponseData screenData = null;

    LoginInfo.Response userInfo = null;

    List<RecyclerItemWrapper> screenDataItems = new ArrayList<>();

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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            startup();
        } else if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //Do nothing
        } else {
            previewScreenData();
        }
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
        //TODO: screenData = TopInfo.fakeData();
        screenDataItems = new ArrayList<>();

        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeTopItem, 6, screenData.images));

        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, 6, "Recentry"));
        for (TopInfo.Response.ResponseData.Item item : screenData.items) {
            RecyclerItemWrapper.RecyclerItemObject obj = RecyclerItemWrapper.createItem(item.id, item.title, item.description, item.image_url);
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGrid, 3, obj));
        }
        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, 6, "More"));

        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, 6, "Photo Gallery"));
        for (TopInfo.Response.ResponseData.Photo item : screenData.photos) {
            RecyclerItemWrapper.RecyclerItemObject obj = RecyclerItemWrapper.createItem(item.id, item.categoryname, "", item.image_url);
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGridImageOnly, 2, obj));
        }
        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, 6, "More"));


        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, 6, "News"));
        for (TopInfo.Response.ResponseData.News item : screenData.news) {
            RecyclerItemWrapper.RecyclerItemObject obj = RecyclerItemWrapper.createItem(item.id, item.title, item.description, item.image_url);
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemList, 6, obj));
        }
        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, 6, "More"));

        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemStore, 6, "map"));
        //screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, 6, "More"));
        //
        this.swipeRefreshLayout.setRefreshing(false);
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 6);//);

        this.recyclerAdapter = new CommonAdapter(getActivity(), this, this);
        manager.setSpanSizeLookup(new CommonAdapter.GridSpanSizeLookup(recyclerAdapter));
        this.recyclerView.setLayoutManager(manager);
        this.recyclerView.setAdapter(recyclerAdapter);

        this.activityListener.updateSideMenuItems(this.menuInfo);
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
    public Object getItemData(int position) {
        return screenDataItems.get(position);
    }

    @Override
    public void onCommonItemClick(int position, Bundle extraData) {

    }

    void startup() {
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

    void errorWithMessage(String message) {
        Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    void loadAppInfo() {
        Bundle params = new Bundle();
        AppInfoCommunicator communicator = new AppInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                int result = responseParams.getInt(Key.ResponseResult);
                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                    if (resultApi == CommonResponse.ResultSuccess) {
                        FragmentHome.this.appInfo = (AppInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                        if (FragmentHome.this.appInfo.data != null && FragmentHome.this.appInfo.data.size() > 0) {
                            FragmentHome.this.storeInfo = FragmentHome.this.appInfo.data.get(0);
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
        params.putString(Key.RequestObject, Integer.toString(storeId));
        TopInfoCommunicator communicator = new TopInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                int result = responseParams.getInt(Key.ResponseResult);
                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                    if (resultApi == CommonResponse.ResultSuccess) {
                        FragmentHome.this.topData = (TopInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                        FragmentHome.this.screenData = FragmentHome.this.topData.get(storeId);
                        loadSideMenuInfo();
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

    void loadSideMenuInfo() {
        Bundle params = new Bundle();
        SideMenuCommunicator communicator = new SideMenuCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                /*TODO: chua co API
                int result = responseParams.getInt(Key.ResponseResult);
                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                    if (resultApi == CommonResponse.ResultSuccess) {
                        FragmentHome.this.menuInfo = (SideMenuInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                        loadUserInfo();
                    } else {
                        String strMessage = responseParams.getString(Key.ResponseMessage);
                        errorWithMessage(strMessage);
                    }
                } else {
                    String strMessage = responseParams.getString(Key.ResponseMessage);
                    errorWithMessage(strMessage);
                }*/
                FragmentHome.this.menuInfo = SideMenuInfo.fakeData();
                loadUserInfo();
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
