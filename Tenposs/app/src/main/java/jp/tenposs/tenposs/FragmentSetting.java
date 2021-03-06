package jp.tenposs.tenposs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jp.tenposs.communicator.GetPushSettingsCommunicator;
import jp.tenposs.communicator.SetPushSettingsCommunicator;
import jp.tenposs.communicator.SignOutCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.PushInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.SwitchButton;

//import jp.tenposs.adapter.SettingsAdapter;

/**
 * Created by ambient on 8/4/16.
 */
public class FragmentSetting extends AbstractFragment implements View.OnClickListener {

    Button mEditProfileButton;
    SwitchButton mReceiveNewsSwitch;
    SwitchButton mReceiveCouponSwitch;

    SwitchButton mReceiveChatSwitch;
    SwitchButton mReceiveRankSwitch;

    Button mIssueButton;
    Button mCompanyInfoButton;
    Button mUserPrivacyButton;
    ViewGroup mSignOutLayout;
    Button mSignOutButton;

    PushInfo.Response mScreenData;
    PushInfo.Response mScreenDataTemp;

//    public static FragmentSetting newInstance(String title, int storeId) {
//        FragmentSetting fragment = new FragmentSetting();
//        Bundle b = new Bundle();
//        b.putString(AbstractFragment.SCREEN_TITLE, title);
//        b.putInt(AbstractFragment.APP_DATA_STORE_ID, storeId);
//        fragment.setArguments(b);
//        return fragment;
//    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            reloadSwitch();
        }
    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.setting);
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
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;

        if (isSignedIn() == true) {
            this.mReceiveNewsSwitch.setEnabled(true);
            this.mReceiveCouponSwitch.setEnabled(true);

            this.mReceiveNewsSwitch.setChecked(this.mScreenData.data.isNewsEnable());
            this.mReceiveCouponSwitch.setChecked(this.mScreenData.data.isCouponEnable());
            this.mSignOutLayout.setVisibility(View.VISIBLE);
        } else {
            this.mSignOutLayout.setVisibility(View.GONE);
            this.mReceiveNewsSwitch.setEnabled(false);
            this.mReceiveCouponSwitch.setEnabled(false);

        }

        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_setting, null);
        this.mEditProfileButton = (Button) mRoot.findViewById(R.id.edit_profile_button);
        this.mReceiveNewsSwitch = (SwitchButton) mRoot.findViewById(R.id.receive_news_switch);
        this.mReceiveCouponSwitch = (SwitchButton) mRoot.findViewById(R.id.receive_coupon_switch);
        this.mIssueButton = (Button) mRoot.findViewById(R.id.issue_button);
        this.mCompanyInfoButton = (Button) mRoot.findViewById(R.id.company_info_button);
        this.mUserPrivacyButton = (Button) mRoot.findViewById(R.id.user_privacy_button);
        this.mSignOutLayout = (ViewGroup) mRoot.findViewById(R.id.sign_out_layout);
        this.mSignOutButton = (Button) mRoot.findViewById(R.id.sign_out_button);

        this.mEditProfileButton.setOnClickListener(this);

        this.mReceiveNewsSwitch.setOnClickListener(this);
        this.mReceiveCouponSwitch.setOnClickListener(this);

        this.mIssueButton.setOnClickListener(this);
        this.mCompanyInfoButton.setOnClickListener(this);
        this.mUserPrivacyButton.setOnClickListener(this);
        this.mSignOutButton.setOnClickListener(this);

        return mRoot;
    }

    @Override
    protected void customResume() {
        //TODO:previewScreenData();
        if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusUnload && isSignedIn()) {
            String settings = this.mActivityListener.getSessionValue(Key.PushSettings, null);
            if (settings != null) {
                this.mScreenData = (PushInfo.Response) CommonObject.fromJSONString(settings, PushInfo.Response.class, null);
            }
            if (this.mScreenData == null) {
                Bundle params = new Bundle();

                Utils.showProgress(getContext(), getString(R.string.msg_loading));
                PushInfo.Request request = new PushInfo.Request();
                params.putSerializable(Key.RequestObject, request);
                params.putString(Key.TokenKey, Utils.getPrefString(getContext(), Key.TokenKey));
                GetPushSettingsCommunicator communicator = new GetPushSettingsCommunicator(
                        new TenpossCommunicator.TenpossCommunicatorListener() {
                            @Override
                            public void completed(TenpossCommunicator request, Bundle responseParams) {
                                if (isAdded() == false) {
                                    return;
                                }
                                Utils.hideProgress();
                                int result = responseParams.getInt(Key.ResponseResult);
                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                    if (resultApi == CommonResponse.ResultSuccess) {
                                        try {
                                            FragmentSetting.this.mScreenData = (PushInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                        } catch (Exception ignored) {
                                            FragmentSetting.this.mScreenData = new PushInfo.Response();
                                        }
                                        mActivityListener.setSessionValue(Key.PushSettings,
                                                CommonObject.toJSONString(FragmentSetting.this.mScreenData, FragmentSetting.this.mScreenData.getClass()));
                                        previewScreenData();
                                    } else {
                                        String strMessage = responseParams.getString(Key.ResponseMessage);
                                        errorWithMessage(responseParams, strMessage, null);
                                    }
                                } else {
                                    String strMessage = responseParams.getString(Key.ResponseMessage);
                                    errorWithMessage(responseParams, strMessage, null);
                                }
                            }
                        }
                );
                communicator.execute(params);
            } else {
                previewScreenData();
            }
        } else {
            previewScreenData();
        }
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (PushInfo.Response) savedInstanceState.getSerializable(SCREEN_DATA);

        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

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

    @Override
    public void onClick(View v) {
        if (v == this.mEditProfileButton) {
            if (isSignedIn() == true) {
                this.mActivityListener.showScreen(AbstractFragment.PROFILE_SCREEN, null, null, false);
            } else {
                this.mActivityListener.showScreen(AbstractFragment.SIGN_IN_SCREEN, null, null, false);
            }

        } else if (v == this.mIssueButton) {
            this.mActivityListener.showScreen(AbstractFragment.CHANGE_DEVICE_SCREEN, null, null, false);

        } else if (v == this.mCompanyInfoButton) {
            this.mActivityListener.showScreen(AbstractFragment.COMPANY_INFO_SCREEN, null, null, false);

        } else if (v == this.mUserPrivacyButton) {
            this.mActivityListener.showScreen(AbstractFragment.USER_PRIVACY_SCREEN, null, null, false);
        } else if (v == this.mReceiveNewsSwitch) {
            this.mScreenDataTemp = this.mScreenData.copy();
            this.mScreenDataTemp.data.enableNews(this.mReceiveNewsSwitch.isChecked());
            updatePushSettings();
        } else if (v == this.mReceiveCouponSwitch) {
            this.mScreenDataTemp = this.mScreenData.copy();
            this.mScreenDataTemp.data.enableCoupon(this.mReceiveCouponSwitch.isChecked());
            updatePushSettings();
        } else if (v == this.mSignOutButton) {
            //TODO:
            performSignOut();
        }
    }

    private void updatePushSettings() {
        Utils.showProgress(getContext(), getString(R.string.msg_updating));

        Bundle params = new Bundle();
        PushInfo.Request request = new PushInfo.Request();
        request.token = Utils.getPrefString(getContext(), Key.TokenKey);
        request.chat = this.mScreenDataTemp.data.isChatEnable() ? 1 : 0;
        request.news = this.mScreenDataTemp.data.isNewsEnable() ? 1 : 0;
        request.coupon = this.mScreenDataTemp.data.isCouponEnable() ? 1 : 0;
        request.ranking = this.mScreenDataTemp.data.isRankingEnable() ? 1 : 0;

        params.putString(Key.TokenKey, Utils.getPrefString(getContext(), Key.TokenKey));
        params.putSerializable(Key.RequestObject, request);
        SetPushSettingsCommunicator communicator = new SetPushSettingsCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        if (isAdded() == false) {
                            return;
                        }
                        Utils.hideProgress();
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                mScreenData = mScreenDataTemp.copy();
                            } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                refreshToken(new TenpossCallback() {
                                    @Override
                                    public void onSuccess(Bundle params) {
                                        updatePushSettings();
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
                        reloadSwitch();
                    }
                }
        );
        communicator.execute(params);
    }

    private void reloadSwitch() {
        if (isSignedIn() == true && this.mScreenData != null) {
            this.mReceiveNewsSwitch.setEnabled(true);
            this.mReceiveCouponSwitch.setEnabled(true);

            this.mReceiveNewsSwitch.setChecked(this.mScreenData.data.isNewsEnable());
            this.mReceiveCouponSwitch.setChecked(this.mScreenData.data.isCouponEnable());
        } else {
            this.mReceiveNewsSwitch.setEnabled(false);
            this.mReceiveCouponSwitch.setEnabled(false);
        }
    }

    void performSignOut() {
        Utils.showAlert(
                getContext(),
                getString(R.string.info),
                getString(R.string.msg_sign_out_confirm),
                getString(R.string.yes),
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: {
                                SignOutCommunicator communicator = new SignOutCommunicator(
                                        new TenpossCommunicator.TenpossCommunicatorListener() {
                                            @Override
                                            public void completed(TenpossCommunicator request, Bundle responseParams) {
                                                Utils.hideProgress();
                                                int result = responseParams.getInt(Key.ResponseResult);
                                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                                    if (resultApi == CommonResponse.ResultSuccess || resultApi == CommonResponse.ResultErrorInvalidToken) {
                                                        mActivityListener.stopServices();
                                                        //clear token and user profile
                                                        mActivityListener.updateUserInfo(null);
                                                        mActivityListener.showFirstFragment();
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
                                Bundle params = new Bundle();
                                params.putString(Key.TokenKey, Utils.getPrefString(getContext(), Key.TokenKey));
                                Utils.showProgress(getContext(), getString(R.string.msg_signing_out));
                                communicator.execute(params);
                            }
                            break;

                            case DialogInterface.BUTTON_NEGATIVE: {
                                //Do nothing
                            }
                            break;
                        }
                    }
                });
    }
}
