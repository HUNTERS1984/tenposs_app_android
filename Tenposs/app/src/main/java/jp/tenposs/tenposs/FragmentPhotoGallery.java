package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import jp.tenposs.communicator.PhotoCategoryInfoCommunicator;
import jp.tenposs.communicator.PhotoInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.PhotoCategoryInfo;
import jp.tenposs.datamodel.PhotoInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.utils.ThemifyIcon;


/**
 * Created by ambient on 7/27/16.
 */
public class FragmentPhotoGallery extends AbstractFragment implements View.OnClickListener, CommonAdapter.CommonDataSource, OnCommonItemClickListener {

    ImageButton previousButton;
    TextView titleLabel;
    ImageButton nextButton;
    RecyclerView recyclerView;
    CommonAdapter recyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    PhotoCategoryInfo.Response screenData;
    PhotoInfo.Response screenItem;
    PhotoCategoryInfo.PhotoCat currentPhotoCat;
    int currentPhotoCatIndex;

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
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA)) {
//                this.screenData = (ArrayList<AppInfo.Response.ResponseData.Menu>) savedInstanceState.getSerializable(SCREEN_DATA);
            }
        }
    }

    @Override
    void setRefreshing(boolean refreshing) {
        this.swipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = "Photo Gallery";
        toolbarSettings.toolbarIcon = "ti-menu";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
    }

    @Override
    protected void reloadScreenData() {
        if (this.screenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        loadPhotoCatData();
    }

    @Override
    protected void previewScreenData() {
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        setRefreshing(false);

        updateNavigation();

        screenDataItems = new ArrayList<>();

        for (PhotoInfo.Photo photo : screenItem.data.photos) {
            Bundle extras = new Bundle();
            extras.putInt(RecyclerItemWrapper.ITEM_ID, photo.id);
            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.PHOTO_ITEM_SCREEN);
            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, photo.getImageUrl());
            screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeItemGridImageOnly, spanCount / 3, extras));
        }

        titleLabel.setText(currentPhotoCat.name);
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

    @Override
    protected void customResume() {
        if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            this.swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    setRefreshing(true);
                    currentPhotoCatIndex = 0;
                    loadPhotoCatData();
                }
            });

        } else if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //just waiting

        } else {
            //reload screen, this case application return from background or from other activity
            previewScreenData();
        }
    }

    void loadPhotoCatData() {
        PhotoCategoryInfo.Request requestParams = new PhotoCategoryInfo.Request();
        requestParams.store_id = 1;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        PhotoCategoryInfoCommunicator communicator = new PhotoCategoryInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                int result = responseParams.getInt(Key.ResponseResult);
                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                    if (resultApi == CommonResponse.ResultSuccess) {
                        screenData = (PhotoCategoryInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                        loadPhotoCatItem(currentPhotoCatIndex);
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

    void loadPhotoCatItem(int menuIndex) {
        try {
            currentPhotoCat = screenData.data.photo_categories.get(menuIndex);
            PhotoInfo.Request requestParams = new PhotoInfo.Request();
            requestParams.category_id = currentPhotoCat.id;
            requestParams.pageindex = 1;
            requestParams.pagesize = 20;

            Bundle params = new Bundle();
            params.putSerializable(Key.RequestObject, requestParams);
            PhotoInfoCommunicator communicator = new PhotoInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
                @Override
                public void completed(TenpossCommunicator request, Bundle responseParams) {
                    int result = responseParams.getInt(Key.ResponseResult);
                    if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                        int resultApi = responseParams.getInt(Key.ResponseResultApi);
                        if (resultApi == CommonResponse.ResultSuccess) {
                            screenItem = (PhotoInfo.Response) responseParams.getSerializable(Key.ResponseObject);
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
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    boolean hasPrevious() {
        return (currentPhotoCatIndex > 0);
    }

    boolean hasNext() {
        return (currentPhotoCatIndex < (this.screenData.data.photo_categories.size() - 1));
    }

    void previousMenu() {
        if (hasPrevious() == false) {
            return;
        }
        try {
            if (currentPhotoCatIndex > 0) {
                currentPhotoCatIndex--;
            }
        } catch (Exception ignored) {

        }
        loadPhotoCatItem(currentPhotoCatIndex);
    }

    void nextMenu() {
        if (hasNext() == false) {
            return;
        }
        try {
            if (currentPhotoCatIndex < this.screenData.data.photo_categories.size() - 1) {
                currentPhotoCatIndex++;
            }
        } catch (Exception ignored) {

        }
        this.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
                loadPhotoCatItem(currentPhotoCatIndex);
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
        RecyclerItemWrapper item = getItemData(position);
        switch (item.itemType) {
            case RecyclerItemTypeItemGridImageOnly: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = screenItem.data.photos;
                this.activityListener.showScreen(screenId, extras);
            }
            break;
            default: {
                Assert.assertFalse("" + item.itemType, false);
            }
            break;
        }
        System.out.println(item.itemType);
    }
}
