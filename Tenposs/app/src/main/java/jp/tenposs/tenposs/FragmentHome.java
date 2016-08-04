package jp.tenposs.tenposs;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.CommonDataSource;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.datamodel.AppSettings;
import jp.tenposs.datamodel.HomeScreenItem;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.HomeObject;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.UserObject;
import jp.tenposs.listener.OnCommonItemClickListener;


/**
 * Created by ambient on 7/26/16.
 */
public class FragmentHome
        extends
        AbstractFragment
        implements
        CommonDataSource,
        OnCommonItemClickListener {

    public HomeObject screenData;


    List<RecyclerItemWrapper> screenDataItems;

    RecyclerView recyclerView;
    CommonAdapter recyclerAdapter;

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
            this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            String userInfoStr = getKeyString(Key.UserObject);
            String sessionId = getKeyString(Key.API_SessionKey);

            UserObject userInfo = null;
            if (userInfoStr != "") {
                userInfo = (UserObject) CommonObject.fromJSONString(userInfoStr, UserObject.class, null);
            }
            if ((sessionId == "") || (userInfo == null) || (userInfo.userId == 0)) {
            } else {
                loadUserDetails();
            }
        } else {
            previewScreenData();
        }
        loadUserDetails();
    }

    @Override
    void loadSavedInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey(SCREEN_DATA)) {
                this.screenData = (HomeObject) savedInstanceState.getSerializable(SCREEN_DATA);
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
                this.screenData = (HomeObject) savedInstanceState.getSerializable(SCREEN_DATA);
            }
        } else {
            if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
                this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
                String userInfoStr = getKeyString(Key.UserObject);
                String sessionId = getKeyString(Key.API_SessionKey);

                UserObject userInfo = null;
                if (userInfoStr != "") {
                    userInfo = (UserObject) CommonObject.fromJSONString(userInfoStr, UserObject.class, null);
                }
                if ((sessionId == "") || (userInfo == null) || (userInfo.userId == 0)) {
                } else {
                    loadUserDetails();
                }
            }
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
        //TODO: fake data

        screenDataItems = new ArrayList<>();
        screenData = (HomeObject) CommonObject.fromJSONString("{\"items\":[" +
                        "{\"itemIcon\":\"ti-home\",\"itemName\":\"Home\",\"itemId\":-1}," +
                        "{\"itemIcon\":\"ti-menu-alt\",\"itemName\":\"Menu\",\"itemId\":1}," +
                        "{\"itemIcon\":\"ti-calendar\",\"itemName\":\"Reserve\",\"itemId\":2}," +
                        "{\"itemIcon\":\"ti-news\",\"itemName\":\"News\",\"itemId\":3}," +
                        "{\"itemIcon\":\"ti-gallery\",\"itemName\":\"Photo Gallery\",\"itemId\":4}," +
                        "{\"itemIcon\":\"ti-news\",\"itemName\":\"Coupon\",\"itemId\":5}," +
                        "{\"itemIcon\":\"ti-news\",\"itemName\":\"Chat\",\"itemId\":6}," +
                        "{\"itemIcon\":\"ti-settings\",\"itemName\":\"Setting\",\"itemId\":7}]," +
                        "\"hotProducts\":[{\"productImageUrl\":\"http://static.vn.zalora.net/p/eternal-coast-7766-526226-1.jpg\"," +
                        "\"productName\":\"Eternal Coast\"},{\"productImageUrl\":\"http://static.vn.zalora.net/p/demona-2572-755226-1.jpg\"," +
                        "\"productName\":\"Demona\"},{\"productImageUrl\":\"http://static.vn.zalora.net/p/quyen-1463-478885-1.jpg\"," +
                        "\"productName\":\"Quyên\"}]}",
                HomeObject.class,
                null);

        if (screenData.hotProducts != null && screenData.items.size() > 0) {
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeTopItem, 6, screenData.hotProducts));
        }
        for (HomeScreenItem item : screenData.items) {
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, 6, item.itemName));

            //for
            if (item.itemId % 3 == 0) {
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGrid, 3, item.itemName));
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGrid, 3, item.itemName));
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGrid, 3, item.itemName));
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGrid, 3, item.itemName));
            } else if (item.itemId % 3 == 1) {
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGridImageOnly, 2, item.itemName));
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGridImageOnly, 2, item.itemName));
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGridImageOnly, 2, item.itemName));
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGridImageOnly, 2, item.itemName));
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGridImageOnly, 2, item.itemName));
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGridImageOnly, 2, item.itemName));
            } else if (item.itemId % 3 == 2) {
                screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemMap, 6, item.itemName));
            }
            //endfor

            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, 6, item.itemName));
        }
            /*screenData = new HomeObject();
        screenData.hotProducts = new ArrayList<>();
        ProductObject productObject = new ProductObject();
        productObject.productName = "Eternal Coast";
        productObject.productImageUrl = "http://static.vn.zalora.net/p/eternal-coast-7766-526226-1.jpg";
        screenData.hotProducts.add(productObject);

        productObject = new ProductObject();
        productObject.productName = "Demona";
        productObject.productImageUrl = "http://static.vn.zalora.net/p/demona-2572-755226-1.jpg";
        screenData.hotProducts.add(productObject);

        productObject = new ProductObject();
        productObject.productName = "Quyên";
        productObject.productImageUrl = "http://static.vn.zalora.net/p/quyen-1463-478885-1.jpg";
        screenData.hotProducts.add(productObject);

        screenData.categories = new ArrayList<>();
        CategoryObject category = new CategoryObject();
        category.categoryName = "Home";
        category.categoryId = -1;
        category.categoryIcon = "ti-home";
        screenData.categories.add(category);

        category = new CategoryObject();
        category.categoryName = "Menu";
        category.categoryId = 1;
        category.categoryIcon = "ti-menu";
        screenData.categories.add(category);

        category = new CategoryObject();
        category.categoryName = "Reserve";
        category.categoryId = 2;
        category.categoryIcon = "ti-home";
        screenData.categories.add(category);


        category = new CategoryObject();
        category.categoryName = "News";
        category.categoryId = 3;
        category.categoryIcon = "ti-news";
        screenData.categories.add(category);

        category = new CategoryObject();
        category.categoryName = "Photo Gallery";
        category.categoryId = 4;
        category.categoryIcon = "ti-photo";
        screenData.categories.add(category);

        category = new CategoryObject();
        category.categoryName = "Staff";
        category.categoryId = 5;
        category.categoryIcon = "ti-news";
        screenData.categories.add(category);

        category = new CategoryObject();
        category.categoryName = "Coupon";
        category.categoryId = 6;
        category.categoryIcon = "ti-news";
        screenData.categories.add(category);

        category = new CategoryObject();
        category.categoryName = "Chat";
        category.categoryId = 7;
        category.categoryIcon = "ti-news";
        screenData.categories.add(category);

        category = new CategoryObject();
        category.categoryName = "Setting";
        category.categoryId = 8;
        category.categoryIcon = "ti-news";
        screenData.categories.add(category);

        String json = screenData.toJSONString();
        System.out.println(json);*/
    }

    @Override
    protected void previewScreenData() {
        this.recyclerAdapter = new CommonAdapter(getActivity(), this, this);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 6);//);
        manager.setSpanSizeLookup(new CommonAdapter.GridSpanSizeLookup(recyclerAdapter));
        this.recyclerView.setLayoutManager(manager);
        this.recyclerView.setAdapter(recyclerAdapter);
        this.activityListener.updateMenuItems(screenData);
    }

    @Override
    public View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_home, null);
        this.recyclerView = (RecyclerView) mRoot.findViewById(R.id.recycler_view);
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

    void loadUserDetails() {
        reloadScreenData();
        previewScreenData();
    }
}
