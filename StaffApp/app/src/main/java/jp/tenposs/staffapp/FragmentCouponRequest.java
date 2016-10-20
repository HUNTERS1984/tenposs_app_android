package jp.tenposs.staffapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import jp.tenposs.adapter.CouponRequestAdapter;
import jp.tenposs.communicator.CouponInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.CouponInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;

/**
 * Created by ambient on 10/14/16.
 */

public class FragmentCouponRequest extends AbstractFragment {


    CouponInfo.Response mScreenData;

    ImageButton mRightToolbarButtonEx;

    SwipeRefreshLayout mSwipeRefreshLayout;
    ListView mListView;
    CouponRequestAdapter mListAdapter;

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.coupon_request);
        mToolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
    }

    @Override
    protected void clearScreenData() {
        this.mScreenData = null;
        this.mScreenDataItems = new ArrayList<>();
        if (this.mListAdapter != null) {
            this.mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void reloadScreenData() {
        if (this.mScreenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        loadCouponRequest();
    }

    @Override
    protected void previewScreenData() {
        setRefreshing(false);
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;

        this.mListAdapter = new CouponRequestAdapter(this.getContext(), this.mScreenData.getItems());
        this.mListView.setAdapter(this.mListAdapter);
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CouponInfo.Coupon item = mScreenData.data.coupons.get(position);
                //mActivityListener.showScreen(AbstractFragment.COUPON_DETAIL_SCREEN, item);
                showPopupCouponRequest(item);
            }
        });

    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coupon_request, null);
        this.mRightToolbarButtonEx = (ImageButton) root.findViewById(R.id.right_toolbar_button_ex);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        this.mListView = (ListView) root.findViewById(R.id.list_view);
        this.mRightToolbarButtonEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityListener.showScreen(AbstractFragment.QR_SCANNER_SCREEN, null);
            }
        });
        return root;
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
                    loadCouponRequest();
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
            this.mScreenData = (CouponInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putSerializable(SCREEN_DATA, this.mScreenData);
    }

    @Override
    void setRefreshing(boolean refreshing) {
        this.mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    boolean canCloseByBackpressed() {
        return false;
    }

    void loadCouponRequest() {
        Bundle params = new Bundle();
        CouponInfoCommunicator communicator = new CouponInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                int result = responseParams.getInt(Key.ResponseResult);
                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                    if (resultApi == CommonResponse.ResultSuccess) {
                        FragmentCouponRequest.this.mScreenData = (CouponInfo.Response) responseParams.getSerializable(Key.ResponseObject);
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

    private void showPopupCouponRequest(CouponInfo.Coupon item) {
        PopupCouponRequest popupCouponRequest = new PopupCouponRequest(this.getContext());
        popupCouponRequest.setData(item);
        popupCouponRequest.show();
    }
}
