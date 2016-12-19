package jp.tenposs.tenposs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.Locale;

import jp.tenposs.communicator.ReserveInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ReserveInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.TopInfo;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 7/29/16.
 */
public class FragmentReserve extends FragmentWebView {
    ReserveInfo.Reserve mScreenData;
    TopInfo.Contact mStoreInfo;


//    public static FragmentReserve newInstance(@NonNull TopInfo.Contact storeInfo) {
//        FragmentReserve gm = new FragmentReserve();
//        Bundle b = new Bundle();
//        b.putSerializable(STORE_INFO, storeInfo);
//        gm.setArguments(b);
//        return gm;
//    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.reserve);
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

    }


    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void customResume() {
        if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            loadReserveInfo();

        } else if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //just waiting

        } else {
            //reload screen, this case application return from background or from other activity
            previewScreenData();
        }
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        super.loadSavedInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(SCREEN_DATA) == true) {
            this.mScreenData = (ReserveInfo.Reserve) savedInstanceState.getSerializable(SCREEN_DATA);
        }
        if (savedInstanceState.containsKey(STORE_INFO) == true) {
            this.mStoreInfo = (TopInfo.Contact) savedInstanceState.getSerializable(STORE_INFO);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        super.customSaveInstanceState(outState);
        if (this.mScreenData != null) {
            outState.putSerializable(SCREEN_DATA, this.mScreenData);
        }

        if (this.mStoreInfo != null) {
            outState.putSerializable(STORE_INFO, this.mStoreInfo);
        }
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            return true;
        } else {
            return false;
        }
    }

    void loadReserveInfo() {
        ReserveInfo.Request requestParams = new ReserveInfo.Request();
        if (mStoreInfo != null) {
            requestParams.store_id = mStoreInfo.id;
        } else {
            requestParams.store_id = this.mStoreId;
        }

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        ReserveInfoCommunicator communicator = new ReserveInfoCommunicator(
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
                                ReserveInfo.Response response = (ReserveInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                if (response.data.reserve.size() > 0) {
                                    mScreenData = response.data.reserve.get(0);
                                    String strUrl = mScreenData.reserve_url.toLowerCase(Locale.US);
                                    String strTemp = mScreenData.reserve_url.toLowerCase(Locale.US);

                                    if (strTemp.contains("http://") == false && strTemp.contains("https://") == false)
                                        mUrl = "http://" + strUrl;
                                    else
                                        mUrl = strUrl;

                                    previewScreenData();
                                } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                    refreshToken(new TenpossCallback() {
                                        @Override
                                        public void onSuccess(Bundle params) {
                                            loadReserveInfo();
                                        }

                                        @Override
                                        public void onFailed(Bundle params) {
                                            //Logout, then do something
                                            mActivityListener.logoutBecauseExpired();
                                        }
                                    });
                                } else {
                                    Utils.showAlert(getContext(),
                                            getString(R.string.error),
                                            getString(R.string.msg_invalid_response_data),
                                            getString(R.string.close),
                                            null,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                }
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
}
