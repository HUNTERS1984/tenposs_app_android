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

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.communicator.StaffCategoryCommunicator;
import jp.tenposs.communicator.StaffInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.StaffCategoryInfo;
import jp.tenposs.datamodel.StaffInfo;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.utils.FlatIcon;


/**
 * Created by ambient on 7/27/16.
 */
public class FragmentStaff extends AbstractFragment implements View.OnClickListener, CommonAdapter.CommonDataSource, OnCommonItemClickListener {

    ImageButton previousButton;
    TextView titleLabel;
    ImageButton nextButton;
    RecyclerView recyclerView;
    CommonAdapter recyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    StaffCategoryInfo.Response screenData;
    StaffInfo.Response screenItem;
    StaffCategoryInfo.StaffCategory currentStaffCat;
    int currentStaffCatIndex;

    ArrayList<Bundle> staffCategories;

    int storeId;

    /**
     * Fragment Override
     */

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = getString(R.string.staff);
        toolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
    }

    @Override
    protected void reloadScreenData() {
        if (this.screenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        loadStaffCatData();
    }

    @Override
    protected void previewScreenData() {
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        setRefreshing(false);

        updateNavigation();

        screenDataItems = new ArrayList<>();

        for (StaffInfo.Staff staff : screenItem.data.staffs) {
            Bundle extras = new Bundle();
            extras.putInt(RecyclerItemWrapper.ITEM_ID, staff.id);
            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.STAFF_DETAIL_SCREEN);
            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, staff.getImageUrl());
            extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, staff);
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGridImageOnly, spanCount / spanSmallItems, extras));
        }

        titleLabel.setText(currentStaffCat.name);
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
                    currentStaffCatIndex = 0;
                    loadStaffCatData();
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
            this.screenData = (StaffCategoryInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
        }
        if (savedInstanceState.containsKey(SCREEN_PAGE_ITEMS)) {
            staffCategories = (ArrayList<Bundle>) savedInstanceState.getSerializable(SCREEN_PAGE_ITEMS);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putSerializable(SCREEN_DATA, screenData);
        outState.putSerializable(SCREEN_PAGE_ITEMS, staffCategories);

    }

    @Override
    void setRefreshing(boolean refreshing) {
        this.swipeRefreshLayout.setRefreshing(refreshing);
    }

    void loadStaffCatData() {
        StaffCategoryInfo.Request requestParams = new StaffCategoryInfo.Request();
        requestParams.store_id = 1;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        StaffCategoryCommunicator communicator = new StaffCategoryCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                screenData = (StaffCategoryInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                staffCategories = new ArrayList<>();
                                for (int i = 0; i < screenData.data.staff_categories.size(); i++) {
                                    Bundle staffCategory = new Bundle();
                                    staffCategory.putInt(SCREEN_DATA_PAGE_INDEX, 1);
                                    staffCategory.putInt(SCREEN_DATA_PAGE_SIZE, 20);
                                    staffCategories.add(staffCategory);
                                }
                                loadStaffCatItem(currentStaffCatIndex);
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

    void loadStaffCatItem(int menuIndex) {
        try {
            currentStaffCat = screenData.data.staff_categories.get(menuIndex);
            Bundle photoCategory = staffCategories.get(menuIndex);
            if (photoCategory.containsKey(SCREEN_DATA_PAGE_DATA)) {
                this.screenItem = (StaffInfo.Response) photoCategory.getSerializable(SCREEN_DATA_PAGE_DATA);
                previewScreenData();
            } else {
                StaffInfo.Request requestParams = new StaffInfo.Request();
                requestParams.category_id = currentStaffCat.id;
                requestParams.pageindex = photoCategory.getInt(SCREEN_DATA_PAGE_INDEX);
                requestParams.pagesize = photoCategory.getInt(SCREEN_DATA_PAGE_SIZE);

                Bundle params = new Bundle();
                params.putSerializable(Key.RequestObject, requestParams);
                params.putParcelable(Key.RequestData, photoCategory);
                StaffInfoCommunicator communicator = new StaffInfoCommunicator(
                        new TenpossCommunicator.TenpossCommunicatorListener() {
                            @Override
                            public void completed(TenpossCommunicator request, Bundle responseParams) {
                                int result = responseParams.getInt(Key.ResponseResult);
                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                    if (resultApi == CommonResponse.ResultSuccess) {
                                        screenItem = (StaffInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                        Bundle photoCategory = responseParams.getParcelable(Key.RequestData);
                                        photoCategory.putSerializable(SCREEN_DATA_PAGE_DATA, screenItem);
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
        return (currentStaffCatIndex > 0);
    }

    boolean hasNext() {
        return (currentStaffCatIndex < (this.screenData.data.staff_categories.size() - 1));
    }

    void previousMenu() {
        if (hasPrevious() == false) {
            return;
        }
        try {
            if (currentStaffCatIndex > 0) {
                currentStaffCatIndex--;
            }
        } catch (Exception ignored) {

        }
        loadStaffCatItem(currentStaffCatIndex);
    }

    void nextMenu() {
        if (hasNext() == false) {
            return;
        }
        try {
            if (currentStaffCatIndex < this.screenData.data.staff_categories.size() - 1) {
                currentStaffCatIndex++;
            }
        } catch (Exception ignored) {

        }
        this.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
                loadStaffCatItem(currentStaffCatIndex);
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
            case RecyclerItemTypeItemGridImageOnly: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.activityListener.showScreen(screenId, extras);
            }
            break;

            default: {
                Assert.assertFalse("" + item.itemType, false);
            }
            break;
        }
    }
}
