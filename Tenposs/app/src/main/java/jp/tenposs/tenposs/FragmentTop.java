package jp.tenposs.tenposs;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.adapter.AbstractRecyclerAdapter;
import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.GridSpanSizeLookup;
import jp.tenposs.adapter.MarginDecoration;
import jp.tenposs.adapter.RecyclerDataSource;
import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.adapter.RestaurantAdapter;
import jp.tenposs.communicator.NewsCategoryCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.communicator.TopInfoCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.ItemsInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.NewsCategoryInfo;
import jp.tenposs.datamodel.NewsInfo;
import jp.tenposs.datamodel.PhotoInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.TopInfo;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.utils.Utils;


/**
 * Created by ambient on 7/26/16.
 */
public class FragmentTop
        extends AbstractFragment
        implements RecyclerDataSource, OnCommonItemClickListener {

    TopInfo.Response mScreenData = null;

    NewsCategoryInfo.Response mNewsCategory;

    RecyclerView mRecyclerView;
    AbstractRecyclerAdapter mRecyclerAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView mNoDataLabel;

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = AppData.sharedInstance().mAppInfo.getAppSetting().getTitle();
        if (this.mShowFromSideMenu == true) {
            mToolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
            mToolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
        } else {
            mToolbarSettings.toolbarLeftIcon = "flaticon-back";
            mToolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
        }
    }

    @Override
    protected void clearScreenData() {
        this.mScreenData = null;
        this.mScreenDataItems = new ArrayList<>();
        if (this.mRecyclerAdapter != null) {
            this.mRecyclerAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void reloadScreenData() {
        if (this.mScreenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        loadTopInfo();
//        loadAppInfo();
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        if (this.mScreenData != null) {
            mScreenDataItems = new ArrayList<>();
            for (AppInfo.TopComponent component : AppData.sharedInstance().mAppInfo.getTopComponents()) {
                buildItemForComponent(component);
            }

            this.mSwipeRefreshLayout.setRefreshing(false);

            if (this.mRecyclerAdapter == null) {
                GridLayoutManager manager = new GridLayoutManager(getActivity(), mSpanCount);//);
                if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                    this.mRecyclerAdapter = new RestaurantAdapter(getActivity(), this, this);
                    manager.setSpanSizeLookup(new GridSpanSizeLookup(mRecyclerAdapter));
                    this.mRecyclerView.setLayoutManager(manager);
                    this.mRecyclerView.addItemDecoration(new MarginDecoration(getActivity(), R.dimen.restaurant_item_spacing));
                } else {
                    this.mRecyclerAdapter = new CommonAdapter(getActivity(), this, this);
                    manager.setSpanSizeLookup(new GridSpanSizeLookup(mRecyclerAdapter));
                    this.mRecyclerView.setLayoutManager(manager);
                    this.mRecyclerView.addItemDecoration(new MarginDecoration(getActivity(), R.dimen.common_item_spacing));
                }

                this.mRecyclerView.setAdapter(mRecyclerAdapter);
            } else {
                this.mRecyclerAdapter.notifyDataSetChanged();
            }

            if (mScreenDataItems.size() > 0) {
                this.mNoDataLabel.setVisibility(View.GONE);
            } else {
                this.mNoDataLabel.setVisibility(View.VISIBLE);
            }
        } else {
            this.mNoDataLabel.setVisibility(View.VISIBLE);
        }

        this.mToolbarSettings.appSetting = AppData.sharedInstance().mAppInfo.getAppSetting();

        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_recycler_view_refresh, null);
        this.mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.recycler_view);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) mRoot.findViewById(R.id.swipe_refresh_layout);
        this.mNoDataLabel = (TextView) mRoot.findViewById(R.id.no_data_label);
        this.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTop.this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusUnload;
                clearScreenData();
                reloadScreenData();
            }
        });
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            this.mRecyclerView.setBackgroundColor(Color.rgb(243, 243, 243));
        } else {
            this.mRecyclerView.setBackgroundColor(Color.WHITE);
        }
        return mRoot;
    }

    @Override
    protected void customResume() {
        if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            this.mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    setRefreshing(true);
                    loadTopInfo();
                }
            });

        } else if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //just waiting

        } else {
            //reload screen, this case application return from background or from other activity
            previewScreenData();
        }
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (TopInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
        }
        if (savedInstanceState.containsKey(APP_DATA_STORE_ID)) {
            this.mStoreId = savedInstanceState.getInt(APP_DATA_STORE_ID);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        if (this.mScreenData != null) {
            outState.putSerializable(SCREEN_DATA, this.mScreenData);
        }
        outState.putInt(APP_DATA_STORE_ID, this.mStoreId);
    }

    @Override
    void setRefreshing(boolean refreshing) {
        this.mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    boolean canCloseByBackpressed() {
        return false;
    }

    @Override
    public int getItemCount() {
        return mScreenDataItems.size();
    }

    @Override
    public RecyclerItemWrapper getItemData(int position) {
        return mScreenDataItems.get(position);
    }

    @Override
    public void onCommonItemClick(int position, Bundle extraData) {
        RecyclerItemWrapper item = getItemData(position);

        switch (item.itemType) {
            case RecyclerItemTypeTop: {
                if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                    int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                    this.mActivityListener.showScreen(screenId, null, null, false);
                }
            }
            break;

            case RecyclerItemTypeHeader: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                this.mActivityListener.showScreen(screenId, null, null, false);
            }
            break;

            case RecyclerItemTypeList: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.mActivityListener.showScreen(screenId, extras, null, false);

            }
            break;

            case RecyclerItemTypeStore: {
                //Do nothing
                TopInfo.Contact contact = (TopInfo.Contact)
                        item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);

                String type = extraData.getString(RecyclerItemWrapper.ITEM_TYPE);
                if (type != null) {
                    try {
                        if (type.compareTo("show_map") == 0) {
                            Uri gmmIntentUri = Uri.parse("geo:" + contact.getLocation());
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(mapIntent);
                            } else {
                                String url = "http://maps.google.com?q=" + contact.getLocation();
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(url));
                                startActivity(intent);
                            }
                        } else if (type.compareTo("call_phone") == 0) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + contact.tel));
                            startActivity(intent);

                            /*Utils.setPrefString(getContext(), Key.PHONE_NUMBER, contact.tel);
                            if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // Should we show an explanation?
                                if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), android.Manifest.permission.CALL_PHONE)) {
                                    // Show an explanation to the user *asynchronously* -- don't block
                                    // this thread waiting for the user's response! After the user
                                    // sees the explanation, try again to request the permission.
                                    Utils.showAlert(
                                            getContext(),
                                            null,
                                            "'" + getString(R.string.app_name) + "' need to access 'CALL_PHONE' permission to complete this action.",
                                            null,
                                            getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ActivityCompat.requestPermissions(getActivity(),
                                                            new String[]{android.Manifest.permission.CALL_PHONE},
                                                            MainActivity.CALL_PHONE_REQUEST);
                                                }
                                            });
                                } else {
                                    // No explanation needed, we can request the permission.
                                    ActivityCompat.requestPermissions((Activity) this.mActivityListener,
                                            new String[]{android.Manifest.permission.CALL_PHONE},
                                            MainActivity.CALL_PHONE_REQUEST);
                                }
                                return;
                            } else {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + contact.tel));
                                this.startActivity(intent);
                            }*/
                        }
                    } catch (Exception ignored) {
                        Utils.log(ignored);
                    }
                }

            }
            break;

            case RecyclerItemTypeGridItem: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.mActivityListener.showScreen(screenId, extras, null, false);
            }
            break;

            case RecyclerItemTypeGridImage: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = item.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.mActivityListener.showScreen(screenId, extras, null, false);
            }
            break;

            case RecyclerItemTypeFooter: {
                int screenId = item.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                this.mActivityListener.showScreen(screenId, null, null, false);
            }
            break;

            case RecyclerItemTypeRestaurantRecyclerHorizontal:
            case RecyclerItemTypeRestaurantRecyclerVertical: {
                int screenId = extraData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                Serializable extras = extraData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                this.mActivityListener.showScreen(screenId, extras, null, false);
            }
            break;

            default: {
                Assert.assertFalse("Should never be here " + item.itemType, false);
            }
            break;
        }
    }

    void loadTopInfo() {
        Bundle params = new Bundle();
        TopInfo.Request requestParams = new TopInfo.Request();
        params.putSerializable(Key.RequestObject, requestParams);
        TopInfoCommunicator communicator = new TopInfoCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        if (isAdded() == false) {
                            return;
                        }
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                FragmentTop.this.mScreenData = (TopInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                loadNewsCategory();
                            } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                refreshToken(new TenpossCallback() {
                                    @Override
                                    public void onSuccess(Bundle params) {
                                        loadTopInfo();
                                    }

                                    @Override
                                    public void onFailed(Bundle params) {
                                        //Logout, then do something
                                        mActivityListener.logoutBecauseExpired();
                                    }
                                });
                            } else {

                                String strMessage = responseParams.getString(Key.ResponseMessage);
                                errorWithMessage(responseParams, strMessage, new TenpossCallback() {
                                    @Override
                                    public void onSuccess(Bundle params) {
                                        loadTopInfo();
                                    }

                                    @Override
                                    public void onFailed(Bundle params) {
                                        previewScreenData();

                                    }
                                });
                            }
                        } else {
                            String strMessage = responseParams.getString(Key.ResponseMessage);
                            errorWithMessage(responseParams, strMessage, new TenpossCallback() {
                                @Override
                                public void onSuccess(Bundle params) {
                                    loadTopInfo();
                                }

                                @Override
                                public void onFailed(Bundle params) {
                                    previewScreenData();

                                }
                            });
                        }
                    }
                });
        communicator.execute(params);
    }

    void loadNewsCategory() {
        NewsCategoryInfo.Request requestParams = new NewsCategoryInfo.Request();
        requestParams.store_id = this.mStoreId;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        NewsCategoryCommunicator communicator = new NewsCategoryCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        if (isAdded() == false) {
                            return;
                        }
                        mLoadingStatus = LOADING_STATUS_UNKNOWN;
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                mNewsCategory = (NewsCategoryInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                previewScreenData();
                            } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                refreshToken(new TenpossCallback() {
                                    @Override
                                    public void onSuccess(Bundle params) {
                                        loadNewsCategory();
                                    }

                                    @Override
                                    public void onFailed(Bundle params) {
                                        //Logout, then do something
                                        mActivityListener.logoutBecauseExpired();
                                    }
                                });
                            } else {
                                String strMessage = responseParams.getString(Key.ResponseMessage);
                                errorWithMessage(responseParams, strMessage, null);
                            }
                        } else {
                            String strMessage = responseParams.getString(Key.ResponseMessage);
                            errorWithMessage(responseParams, strMessage, null);
                        }
                    }
                });
        communicator.execute(params);
    }

    void buildItemForComponent(AppInfo.TopComponent component) {
        Bundle extras;
        if (component.id == mScreenData.data.images.top_id) {
            if (mScreenData.data.images != null && mScreenData.data.images.size() > 0) {
                if (component != null) {
                    extras = new Bundle();
                    extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, mScreenData.data.images.data);
                    mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeTop, mSpanCount, extras));
                }
            }
        } else if (component.id == mScreenData.data.items.top_id) {

            if (mScreenData.data.items != null && mScreenData.data.items.size() > 0) {
                if (component != null) {
                    /**
                     * Header
                     */
                    extras = new Bundle();
                    extras.putString(RecyclerItemWrapper.ITEM_TITLE, component.name);
                    extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.MENU_SCREEN);

                    mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, mSpanCount, extras));

                    /**
                     * Content
                     */
                    if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                        extras = new Bundle();
                        extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.ITEM_SCREEN);
                        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, mScreenData.data.items.data);
                        extras.putSerializable(RecyclerItemWrapper.ITEM_CLASS, ItemsInfo.Item.class);

                        mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeRestaurantRecyclerHorizontal, mSpanCount, extras));

                    } else {
                        int rowIndex = 0;
                        int itemSpanCount = 0;
                        for (ItemsInfo.Item item : mScreenData.data.items.data) {
                            extras = new Bundle();
                            extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
                            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.ITEM_SCREEN);
                            extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.getTitle());
                            extras.putString(RecyclerItemWrapper.ITEM_PRICE, item.getPrice());
                            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
                            extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item);

                            extras.putInt(RecyclerItemWrapper.ITEM_ROW, rowIndex);
                            itemSpanCount += this.mSpanCount / this.mSpanLargeItems;
                            if (itemSpanCount == this.mSpanCount) {
                                rowIndex++;
                                itemSpanCount = 0;
                            }
                            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeGridItem, mSpanCount / mSpanLargeItems, extras));
                        }


                        if (component.showViewMore() == true) {
                            /**
                             * Footer
                             */
                            extras = new Bundle();
                            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.MENU_SCREEN);
                            extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.more));
                            extras.putInt(RecyclerItemWrapper.ITEM_BACKGROUND, R.drawable.bg_button);
                            extras.putInt(RecyclerItemWrapper.ITEM_TEXT_COLOR, R.color.button_text_color);
                            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, mSpanCount, extras));
                        }
                    }
                }
            }
        } else if (component.id == mScreenData.data.photos.top_id) {
            if (mScreenData.data.photos != null && mScreenData.data.photos.size() > 0) {
                if (component != null) {
                    /**
                     * Header
                     */
                    extras = new Bundle();
                    extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.PHOTO_SCREEN);
                    extras.putString(RecyclerItemWrapper.ITEM_TITLE, component.name);

                    mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, mSpanCount, extras));

                    /**
                     * Content
                     */
                    if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                        extras = new Bundle();
                        extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.PHOTO_ITEM_SCREEN);
                        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, mScreenData.data.photos.data);
                        extras.putSerializable(RecyclerItemWrapper.ITEM_CLASS, PhotoInfo.Photo.class);

                        mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeRestaurantRecyclerHorizontal, mSpanCount, extras));

                    } else {
                        int rowIndex = 0;
                        int itemSpanCount = 0;
                        for (PhotoInfo.Photo item : mScreenData.data.photos.data) {
                            extras = new Bundle();
                            extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
                            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.PHOTO_ITEM_SCREEN);
                            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
                            extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item.getImageUrl());

                            extras.putInt(RecyclerItemWrapper.ITEM_ROW, rowIndex);
                            itemSpanCount += this.mSpanCount / this.mSpanSmallItems;
                            if (itemSpanCount == this.mSpanCount) {
                                rowIndex++;
                                itemSpanCount = 0;
                            }
                            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeGridImage, mSpanCount / mSpanSmallItems, extras));
                        }

                        if (component.showViewMore() == true) {
                            /**
                             * Footer
                             */
                            extras = new Bundle();
                            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.PHOTO_SCREEN);
                            extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.more));
                            extras.putInt(RecyclerItemWrapper.ITEM_BACKGROUND, R.drawable.bg_button);
                            extras.putInt(RecyclerItemWrapper.ITEM_TEXT_COLOR, R.color.button_text_color);
                            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, mSpanCount, extras));
                        }
                    }
                }
            }
        } else if (component.id == mScreenData.data.news.top_id) {
            if (mScreenData.data.news != null && mScreenData.data.news.size() > 0) {
                /**
                 * Header
                 */
                if (component != null) {
                    extras = new Bundle();
                    extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.NEWS_SCREEN);
                    extras.putString(RecyclerItemWrapper.ITEM_TITLE, component.name);

                    mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeHeader, mSpanCount, extras));

                    /**
                     * Content
                     */
                    if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                        for (NewsInfo.News item : mScreenData.data.news.data) {
                            item.setCategory(this.mNewsCategory.data.news_categories);
                        }

                        extras = new Bundle();
                        extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.NEWS_DETAILS_SCREEN);
                        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, mScreenData.data.news.data);
                        extras.putSerializable(RecyclerItemWrapper.ITEM_CLASS, NewsInfo.News.class);

                        mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeRestaurantRecyclerVertical, mSpanCount, extras));

                    } else {
                        for (NewsInfo.News item : mScreenData.data.news.data) {
                            extras = new Bundle();
                            item.setCategory(this.mNewsCategory.data.news_categories);
                            extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
                            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.NEWS_DETAILS_SCREEN);
                            extras.putString(RecyclerItemWrapper.ITEM_CATEGORY, item.getCategory());
                            extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.getTitle());
//                            extras.putString(RecyclerItemWrapper.ITEM_PRICE, item.getPrice());
                            extras.putString(RecyclerItemWrapper.ITEM_DESCRIPTION, item.getDescription());
                            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
                            extras.putString(RecyclerItemWrapper.ITEM_CREATE_DATE_TIME, item.getLastModifyDate());
                            extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item);

                            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeList, mSpanCount, extras));
                        }

                        if (component.showViewMore() == true) {
                            /**
                             * Footer
                             */
                            extras = new Bundle();
                            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.NEWS_SCREEN);
                            extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.more));
                            extras.putInt(RecyclerItemWrapper.ITEM_BACKGROUND, R.drawable.bg_button);
                            extras.putInt(RecyclerItemWrapper.ITEM_TEXT_COLOR, R.color.button_text_color);
                            mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, mSpanCount, extras));
                        }
                    }
                }
            }
        } else if (component.id == mScreenData.data.contact.top_id) {
            if (mScreenData.data.contact != null && mScreenData.data.contact.size() > 0) {
                if (component != null) {
//                        //TODO:
//                    if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
//
//                    } else {
                    for (TopInfo.Contact contact : mScreenData.data.contact.data) {
                        //TopInfo.Contact contact = mScreenData.data.contact.data.get(0);
                        /**
                         * Content
                         */
                        extras = new Bundle();
                        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, contact);
                        mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeStore, mSpanCount, extras));

                        /**
                         * Footer
                         */
                        extras = new Bundle();
                        extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.RESERVE_SCREEN);
                        extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.reserve));
                        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, contact);
                        extras.putInt(RecyclerItemWrapper.ITEM_BACKGROUND, R.drawable.bg_button_2);
                        extras.putInt(RecyclerItemWrapper.ITEM_TEXT_COLOR, R.color.colorNavBackground);
                        mScreenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, mSpanCount, extras));
                    }
                }
            }
        }
    }
}
