package jp.tenposs.tenposs;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;

import jp.tenposs.adapter.RecyclerDataSource;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.adapter.RestaurantCouponAdapter;
import jp.tenposs.communicator.CouponInfoCommunicator;
import jp.tenposs.communicator.StaffInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.CouponInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.StaffInfo;
import jp.tenposs.listener.BSSelectionListener;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/4/16.
 */
public class RestaurantFragmentCoupon
        extends AbstractFragment
        implements RecyclerDataSource, OnCommonItemClickListener, ViewPager.OnPageChangeListener, BSSelectionListener {

    CouponInfo.Response mScreenData = null;
    StaffInfo.Response mStaffs;

    TextView mNoDataLabel;
    ViewPager mViewPager;

    ImageView[] dots;
    LinearLayout pager_indicator;

    int dotsCount;
    RestaurantCouponAdapter mViewPagerAdapter;

    private RestaurantFragmentCoupon() {

    }

    public static RestaurantFragmentCoupon newInstance(String title, int storeId) {
        RestaurantFragmentCoupon fragment = new RestaurantFragmentCoupon();
        Bundle b = new Bundle();
        b.putString(AbstractFragment.SCREEN_TITLE, title);
        b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA)) {
                this.mScreenData = (CouponInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
                startup();
            }
        } else {
            startup();
        }
    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.coupon);
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate && this.mFirstScreen == false) {
            mToolbarSettings.toolbarLeftIcon = "flaticon-back";
            mToolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
        } else {
            mToolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
            mToolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
        }
    }

    @Override
    protected void clearScreenData() {

    }

    @Override
    protected void reloadScreenData() {
        if (this.mScreenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        loadCouponsInfo();
    }

    @Override
    protected void previewScreenData() {
        setRefreshing(false);
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;

        if (this.mScreenData.data.coupons.size() > 0) {
            this.mNoDataLabel.setVisibility(View.GONE);
            if (this.mViewPagerAdapter == null) {
//                this.mViewPagerAdapter =  new RestaurantCouponAdapter(this.getActivity().getSupportFragmentManager(), this.getActivity());
                this.mViewPagerAdapter = new RestaurantCouponAdapter(this.getActivity(), this.mScreenData.data.coupons, this);
            }
            this.mViewPager.setAdapter(this.mViewPagerAdapter);
            this.mViewPager.setOffscreenPageLimit(3);
            setUiPageViewController();
            this.mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.coupon_fragment_margin));
            this.mViewPager.removeOnPageChangeListener(this);
            this.mViewPager.addOnPageChangeListener(this);
//            this.mViewPager.setOnItem
        } else {
            this.mNoDataLabel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.restaurant_fragment_coupon, null);

        this.mNoDataLabel = (TextView) mRoot.findViewById(R.id.no_data_label);
        this.mViewPager = (ViewPager) mRoot.findViewById(R.id.view_pager);
        this.pager_indicator = (LinearLayout) mRoot.findViewById(R.id.view_pager_dots_layout);
        return mRoot;
    }

    @Override
    protected void customResume() {

    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(APP_DATA_STORE_ID)) {
            this.mStoreId = savedInstanceState.getInt(APP_DATA_STORE_ID);
        }

        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (CouponInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
        }

        if (savedInstanceState.containsKey(SCREEN_DATA_PAGE_INDEX)) {
            this.mPageIndex = savedInstanceState.getInt(SCREEN_DATA_PAGE_INDEX);
        }

        if (savedInstanceState.containsKey(SCREEN_DATA_PAGE_SIZE)) {
            this.mPageSize = savedInstanceState.getInt(SCREEN_DATA_PAGE_SIZE);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putInt(APP_DATA_STORE_ID, this.mStoreId);

        if (this.mScreenData != null) {
            outState.putSerializable(SCREEN_DATA, this.mScreenData);
        }
        outState.putInt(SCREEN_DATA_PAGE_INDEX, this.mPageIndex);
        outState.putInt(SCREEN_DATA_PAGE_SIZE, this.mPageSize);
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate && this.mFirstScreen == false) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return this.mScreenDataItems.size();
    }

    @Override
    public RecyclerItemWrapper getItemData(int position) {
        return this.mScreenDataItems.get(position);
    }

    @Override
    public void onCommonItemClick(int position, Bundle extraData) {
//        RecyclerItemWrapper item = getItemData(position);
//
//        switch (item.itemType) {
//            case RecyclerItemTypeList: {
//                int id = item.itemData.getInt(RecyclerItemWrapper.ITEM_ID);
//                CouponInfo.Coupon coupon = this.mScreenData.getItemById(id);
//                this.mActivityListener.showScreen(AbstractFragment.COUPON_DETAIL_SCREEN, coupon);
//            }
//            break;
//
//            default: {
//                Assert.assertFalse("" + item.itemType, false);
//            }
//            break;
//        }
        getStaffsIfNeeded();
    }

    protected void startup() {
        if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            loadCouponsInfo();

        } else if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //just waiting

        } else {
            //reload screen, this case application return from background or from other activity
            previewScreenData();
        }
    }

    void loadCouponsInfo() {
        Bundle params = new Bundle();
        CouponInfo.Request requestParams = new CouponInfo.Request();
        requestParams.store_id = this.mStoreId;
        requestParams.pageindex = this.mPageIndex;
        requestParams.pagesize = this.mPageSize;

        params.putSerializable(Key.RequestObject, requestParams);
        Utils.showProgress(getContext(), getString(R.string.msg_loading));
        CouponInfoCommunicator communicator = new CouponInfoCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        Utils.hideProgress();
                        if (isAdded() == false) {
                            return;
                        }
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                RestaurantFragmentCoupon.this.mScreenData = (CouponInfo.Response) responseParams.getSerializable(Key.ResponseObject);
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

    private void setUiPageViewController() {
        pager_indicator.removeAllViews();
        dotsCount = this.mViewPager.getAdapter().getCount();
        dots = new ImageView[dotsCount];
        Drawable selected;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            selected = getContext().getResources().getDrawable(R.drawable.restaurant_selected_item_dot);
        } else {
            selected = getContext().getResources().getDrawable(R.drawable.restaurant_selected_item_dot, null);
        }
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dots[i].setImageDrawable(getContext().getResources().getDrawable(R.drawable.nonselecteditem_dot, null));
            } else {
                dots[i].setImageDrawable(getContext().getResources().getDrawable(R.drawable.nonselecteditem_dot));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(selected);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Drawable nonSelected;
        Drawable selected;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            nonSelected = getContext().getResources().getDrawable(R.drawable.nonselecteditem_dot);
            selected = getContext().getResources().getDrawable(R.drawable.restaurant_selected_item_dot);
        } else {
            nonSelected = getContext().getResources().getDrawable(R.drawable.nonselecteditem_dot, null);
            selected = getContext().getResources().getDrawable(R.drawable.restaurant_selected_item_dot, null);
        }
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(nonSelected);
        }

        dots[position].setImageDrawable(selected);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private void getStaffsIfNeeded() {
        if (this.mStaffs == null) {
            StaffInfo.Request requestParams = new StaffInfo.Request();
            requestParams.category_id = 0;
            requestParams.pageindex = 1;
            requestParams.pagesize = 10000;

            Bundle params = new Bundle();
            Utils.showProgress(getContext(), getString(R.string.msg_loading));
            params.putSerializable(Key.RequestObject, requestParams);
            StaffInfoCommunicator communicator = new StaffInfoCommunicator(
                    new TenpossCommunicator.TenpossCommunicatorListener() {
                        @Override
                        public void completed(TenpossCommunicator request, Bundle responseParams) {
                            Utils.hideProgress();
                            int result = responseParams.getInt(Key.ResponseResult);
                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                if (resultApi == CommonResponse.ResultSuccess) {
                                    mStaffs = (StaffInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                    getStaffsIfNeeded();
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
        } else {
            RestaurantBottomSheetStaffSelection bottomSheet = RestaurantBottomSheetStaffSelection.newInstance(this.mStaffs, this);
            bottomSheet.show(getActivity().getSupportFragmentManager(), bottomSheet.getTag());
        }
    }

    @Override
    public void onItemSelect(int position, Serializable extras) {
        //Showpopup
    }

    @Override
    public void onCancel() {

    }
}
