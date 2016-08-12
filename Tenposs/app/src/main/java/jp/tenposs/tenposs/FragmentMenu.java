package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.communicator.ItemInfoCommunicator;
import jp.tenposs.communicator.MenuInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppSettings;
import jp.tenposs.datamodel.ItemInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.MenuInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.utils.ThemifyIcon;


/**
 * Created by ambient on 7/27/16.
 */
public class FragmentMenu extends AbstractFragment implements View.OnClickListener, CommonAdapter.CommonDataSource, OnCommonItemClickListener {

    ImageButton previousButton;
    TextView titleLabel;
    ImageButton nextButton;
    RecyclerView recyclerView;
    CommonAdapter recyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    MenuInfo.Response screenData;
    ItemInfo.Response screenItem;
MenuInfo.Response.ResponseData.Menu currentMenu;
    int currentMenuIndex;

    /**
     * Fragment Override
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putSerializable(SCREEN_DATA, this.screenData);
        outState.putInt(SCREEN_DATA_STATUS, this.screenDataStatus.ordinal());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spanCount = 6;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA)) {
//                this.screenData = (ArrayList<AppInfo.Response.ResponseData.Menu>) savedInstanceState.getSerializable(SCREEN_DATA);
            }
        } else {
            Bundle argument = getArguments();
            if (argument != null && argument.containsKey(SCREEN_DATA)) {
//                this.screenData = (ArrayList<AppInfo.Response.ResponseData.Menu>) argument.getSerializable(SCREEN_DATA);
            }
        }
    }

    @Override
    void loadSavedInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA)) {
//                this.screenData = (ArrayList<AppInfo.Response.ResponseData.Menu>) savedInstanceState.getSerializable(SCREEN_DATA);
            }
        }
    }

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings = new ToolbarSettings();
        toolbarSettings.toolbarTitle = "Menu";
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
        loadMenuData();
    }

    @Override
    protected void previewScreenData() {
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        this.swipeRefreshLayout.setRefreshing(false);

        updateNavigation();

        screenDataItems = new ArrayList<>();

        for (ItemInfo.Response.ResponseData.Item item : screenItem.data.items) {
            RecyclerItemWrapper.RecyclerItemObject obj = RecyclerItemWrapper.createItem(item.id, item.title, item.price, null, item.image_url);
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGrid, spanCount / 2, obj));
        }

        titleLabel.setText(currentMenu.name);
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
        View mRoot = inflater.inflate(R.layout.fragment_menu, null);

        this.previousButton = (ImageButton) mRoot.findViewById(R.id.previous_button);
        this.titleLabel = (TextView) mRoot.findViewById(R.id.title_label);
        this.nextButton = (ImageButton) mRoot.findViewById(R.id.next_button);
        this.recyclerView = (RecyclerView) mRoot.findViewById(R.id.recycler_view);
        this.swipeRefreshLayout = (SwipeRefreshLayout) mRoot.findViewById(R.id.swipe_refresh_layout);
        this.previousButton.setOnClickListener(this);
        this.nextButton.setOnClickListener(this);

        return mRoot;
    }

    protected void startup() {
        if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            this.swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    FragmentMenu.this.swipeRefreshLayout.setRefreshing(true);
                    currentMenuIndex = 0;
                    loadMenuData();
                }
            });

        } else if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //just waiting

        } else {
            //reload screen, this case application return from background or from other activity
            previewScreenData();
        }
    }

    void loadMenuData() {
        MenuInfo.Request requestParams = new MenuInfo.Request();
        requestParams.store_id = 1;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        MenuInfoCommunicator communicator = new MenuInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                screenData = (MenuInfo.Response) responseParams.getSerializable(Key.ResponseObject);

                loadMenuItem(currentMenuIndex);
            }
        });
        communicator.execute(params);
    }

    void loadMenuItem(int menuIndex) {
        currentMenu = screenData.data.menus.get(menuIndex);
        ItemInfo.Request requestParams = new ItemInfo.Request();
        requestParams.menu_id = currentMenu.id;
        requestParams.pageindex = 1;
        requestParams.pagesize = 20;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        ItemInfoCommunicator communicator = new ItemInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                screenItem = (ItemInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                previewScreenData();
            }
        });
        communicator.execute(params);
    }

    boolean hasPrevious() {
        return (currentMenuIndex > 0);
    }

    boolean hasNext() {
        return (currentMenuIndex < (this.screenData.data.menus.size() - 1));
    }

    void previousMenu() {
        try {
            if (currentMenuIndex > 0) {
                currentMenuIndex--;
            }
        } catch (Exception ex) {

        }
        loadMenuItem(currentMenuIndex);
    }

    void nextMenu() {
        try {
            if (currentMenuIndex < this.screenData.data.menus.size() - 1) {
                currentMenuIndex++;
            }
        } catch (Exception ex) {

        }
        loadMenuItem(currentMenuIndex);
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

        previousButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getContext().getAssets(),
                "ti-angle-left",
                20,
                Color.argb(0, 0, 0, 0),
                previousButtonColor
        ));

        nextButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getContext().getAssets(),
                "ti-angle-right",
                20,
                Color.argb(0, 0, 0, 0),
                nextButtonColor
        ));
    }

    @Override
    public void onClick(View v) {
        if (v == previousButton) {
            previousMenu();

        } else if (v == nextButton) {
            nextMenu();
        }
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

    }
}
