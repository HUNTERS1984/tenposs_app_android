package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import jp.tenposs.communicator.ItemInfoCommunicator;
import jp.tenposs.communicator.MenuInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.ItemInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.MenuInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.utils.FlatIcon;


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
    ItemInfo.Response currentItem;
    MenuInfo.Menu currentMenu;
    int currentMenuIndex;

    ArrayList<Bundle> allItems;

    int storeId;

    /**
     * Fragment Override
     */

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = getString(R.string.menu);
        toolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
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
        setRefreshing(false);

        updateNavigation();

        screenDataItems = new ArrayList<>();

        for (ItemInfo.Item item : currentItem.data.items) {
            Bundle extras = new Bundle();
            extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
            extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.title);
            extras.putString(RecyclerItemWrapper.ITEM_DESCRIPTION, item.price);
            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
            extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item);
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGrid, spanCount / spanLargeItems, extras));
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
        updateToolbar();
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

    @Override
    protected void customResume() {
        if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            this.swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    setRefreshing(true);
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

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(APP_DATA_STORE_ID)) {
            this.storeId = savedInstanceState.getInt(APP_DATA_STORE_ID);
        }

        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.screenData = (MenuInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
        }
        if (savedInstanceState.containsKey(SCREEN_PAGE_ITEMS)) {
            this.allItems = (ArrayList<Bundle>) savedInstanceState.getSerializable(SCREEN_PAGE_ITEMS);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA_PAGE_DATA)) {
            this.currentItem = (ItemInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA_PAGE_DATA);
        }
        if (savedInstanceState.containsKey(SCREEN_DATA_PAGE_INDEX)) {
            this.currentMenuIndex = savedInstanceState.getInt(SCREEN_DATA_PAGE_INDEX);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putInt(APP_DATA_STORE_ID, this.storeId);

        outState.putSerializable(SCREEN_DATA, this.screenData);
        outState.putSerializable(SCREEN_PAGE_ITEMS, this.allItems);
        outState.putSerializable(SCREEN_DATA_PAGE_DATA, this.currentItem);
        outState.putInt(SCREEN_DATA_PAGE_INDEX, this.currentMenuIndex);
    }


    @Override
    void setRefreshing(boolean refreshing) {
        this.swipeRefreshLayout.setRefreshing(refreshing);
    }

    void loadMenuData() {
        MenuInfo.Request requestParams = new MenuInfo.Request();
        requestParams.store_id = this.storeId;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        MenuInfoCommunicator communicator = new MenuInfoCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                screenData = (MenuInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                currentMenuIndex = 0;
                                allItems = new ArrayList<>();
                                for (int i = 0; i < screenData.data.menus.size(); i++) {
                                    Bundle menuData = new Bundle();
                                    menuData.putInt("PAGE_INDEX", 1);
                                    menuData.putInt("PAGE_SIZE", 20);
                                    allItems.add(menuData);
                                }
                                loadMenuItem(currentMenuIndex);
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

    void loadMenuItem(int menuIndex) {
        try {
            Bundle menuData = allItems.get(menuIndex);
            this.currentMenu = screenData.data.menus.get(menuIndex);
            if (menuData.containsKey("PAGE_DATA")) {
                this.currentItem = (ItemInfo.Response) menuData.getSerializable("PAGE_DATA");
            } else {
                ItemInfo.Request requestParams = new ItemInfo.Request();
                requestParams.menu_id = currentMenu.id;
                requestParams.pageindex = menuData.getInt("PAGE_INDEX");
                requestParams.pagesize = menuData.getInt("PAGE_SIZE");

                Bundle params = new Bundle();
                params.putSerializable(Key.RequestObject, requestParams);
                ItemInfoCommunicator communicator = new ItemInfoCommunicator(
                        new TenpossCommunicator.TenpossCommunicatorListener() {
                            @Override
                            public void completed(TenpossCommunicator request, Bundle responseParams) {
                                int result = responseParams.getInt(Key.ResponseResult);
                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                    if (resultApi == CommonResponse.ResultSuccess) {
                                        currentItem = (ItemInfo.Response) responseParams.getSerializable(Key.ResponseObject);
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
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    boolean hasPrevious() {
        return (currentMenuIndex > 0);
    }

    boolean hasNext() {
        return (currentMenuIndex < (this.screenData.data.menus.size() - 1));
    }

    void previousMenu() {
        if (hasPrevious() == false) {
            return;
        }
        try {
            if (currentMenuIndex > 0) {
                currentMenuIndex--;
            }
        } catch (Exception ignored) {

        }
        loadMenuItem(currentMenuIndex);
    }

    void nextMenu() {
        if (hasNext() == false) {
            return;
        }
        try {
            if (currentMenuIndex < this.screenData.data.menus.size() - 1) {
                currentMenuIndex++;
            }
        } catch (Exception ignored) {

        }
        this.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
                loadMenuItem(currentMenuIndex);
            }
        });
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

        previousButton.setImageBitmap(FlatIcon.fromFlatIcon(getContext().getAssets(),
                "flaticon-back",
                20,
                Color.argb(0, 0, 0, 0),
                previousButtonColor
        ));

        nextButton.setImageBitmap(FlatIcon.fromFlatIcon(getContext().getAssets(),
                "flaticon-next",
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
        RecyclerItemWrapper item = getItemData(position);

        switch (item.itemType) {
            case RecyclerItemTypeItemGrid: {
                int id = item.itemData.getInt(RecyclerItemWrapper.ITEM_ID);
                ItemInfo.Item itemData = (ItemInfo.Item) item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.activityListener.showScreen(AbstractFragment.ITEM_SCREEN, itemData);
            }
            break;

            default: {
                Assert.assertFalse("" + item.itemType, false);
            }
            break;
        }
    }
}
