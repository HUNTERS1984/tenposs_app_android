package jp.tenposs.tenposs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import jp.tenposs.communicator.GetPushSettingsCommunicator;
import jp.tenposs.communicator.SetPushSettingsCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.PushInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.utils.Utils;

//import jp.tenposs.adapter.SettingsAdapter;

/**
 * Created by ambient on 8/4/16.
 */
public class FragmentSetting extends AbstractFragment implements View.OnClickListener {

    Button mEditProfileButton;
    Switch mReceivePushSwitch;
    Switch mReceiveCouponSwitch;
    Button mIssueButton;
    Button mCompanyInfoButton;
    Button mUserPrivacyButton;

    PushInfo.Response mScreenData;
    PushInfo.Response mScreenDataTemp;

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
        mToolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
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
            this.mReceivePushSwitch.setEnabled(true);
            this.mReceiveCouponSwitch.setEnabled(true);

            this.mReceivePushSwitch.setChecked(this.mScreenData.data.push_setting.isAtLeastOneEnable());
            this.mReceiveCouponSwitch.setChecked(this.mScreenData.data.push_setting.isCouponEnable());
        } else {
            this.mReceivePushSwitch.setEnabled(false);
            this.mReceiveCouponSwitch.setEnabled(false);
        }

        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_setting, null);
        this.mEditProfileButton = (Button) mRoot.findViewById(R.id.edit_profile_button);
        this.mReceivePushSwitch = (Switch) mRoot.findViewById(R.id.receive_push_switch);
        this.mReceiveCouponSwitch = (Switch) mRoot.findViewById(R.id.receive_coupon_switch);
        this.mIssueButton = (Button) mRoot.findViewById(R.id.issue_button);
        this.mCompanyInfoButton = (Button) mRoot.findViewById(R.id.company_info_button);
        this.mUserPrivacyButton = (Button) mRoot.findViewById(R.id.user_privacy_button);

        this.mEditProfileButton.setOnClickListener(this);

        this.mReceivePushSwitch.setOnClickListener(this);
        this.mReceiveCouponSwitch.setOnClickListener(this);

        this.mIssueButton.setOnClickListener(this);
        this.mCompanyInfoButton.setOnClickListener(this);
        this.mUserPrivacyButton.setOnClickListener(this);


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

                showProgress(getString(R.string.msg_loading));
                PushInfo.RequestGet request = new PushInfo.RequestGet();
                request.token = getPrefString(Key.TokenKey);
                params.putSerializable(Key.RequestObject, request);
                GetPushSettingsCommunicator communicator = new GetPushSettingsCommunicator(
                        new TenpossCommunicator.TenpossCommunicatorListener() {
                            @Override
                            public void completed(TenpossCommunicator request, Bundle responseParams) {
                                hideProgress();
                                int result = responseParams.getInt(Key.ResponseResult);
                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                    if (resultApi == CommonResponse.ResultSuccess) {
                                        FragmentSetting.this.mScreenData = (PushInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                        mActivityListener.setSessionValue(Key.PushSettings,
                                                CommonObject.toJSONString(FragmentSetting.this.mScreenData, FragmentSetting.this.mScreenData.getClass()));
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
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == this.mEditProfileButton) {
            // get to
            if (isSignedIn() == true) {
                this.mActivityListener.showScreen(AbstractFragment.PROFILE_SCREEN, null);
            } else {
                Utils.showAlert(this.getContext(),
                        getString(R.string.info),
                        getString(R.string.msg_not_sign_in),
                        getString(R.string.close),
                        null,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
            }

        } else if (v == this.mIssueButton) {
            this.mActivityListener.showScreen(AbstractFragment.ISSUE_INFO_SCREEN, null);

        } else if (v == this.mCompanyInfoButton) {
            this.mActivityListener.showScreen(AbstractFragment.COMPANY_INFO_SCREEN, null);

        } else if (v == this.mUserPrivacyButton) {
            this.mActivityListener.showScreen(AbstractFragment.USER_PRIVACY_SCREEN, null);
        } else if (v == this.mReceivePushSwitch) {
            this.mScreenDataTemp = this.mScreenData.copy();
            this.mScreenDataTemp.data.push_setting.enableAll(((Switch) v).isChecked());
            updatePushSettings();
        } else if (v == this.mReceiveCouponSwitch) {
            this.mScreenDataTemp = this.mScreenData.copy();
            this.mScreenDataTemp.data.push_setting.enableCoupon(((Switch) v).isChecked());
            updatePushSettings();
        }
    }

    private void updatePushSettings() {
        showProgress(getString(R.string.msg_updating));

        Bundle params = new Bundle();
        PushInfo.RequestSet request = new PushInfo.RequestSet();
        request.token = getPrefString(Key.TokenKey);
        request.chat = this.mScreenDataTemp.data.push_setting.isChatEnable() ? 1 : 0;
        request.news = this.mScreenDataTemp.data.push_setting.isNewsEnable() ? 1 : 0;
        request.coupon = this.mScreenDataTemp.data.push_setting.isCouponEnable() ? 1 : 0;
        request.ranking = this.mScreenDataTemp.data.push_setting.isRankingEnable() ? 1 : 0;

        params.putSerializable(Key.RequestObject, request);
        SetPushSettingsCommunicator communicator = new SetPushSettingsCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        hideProgress();
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                mScreenData = mScreenDataTemp.copy();

                            } else {
                                String strMessage = responseParams.getString(Key.ResponseMessage);
                                errorWithMessage(responseParams, strMessage);
                            }
                        } else {
                            String strMessage = responseParams.getString(Key.ResponseMessage);
                            errorWithMessage(responseParams, strMessage);
                        }
                        reloadSwitch();
                    }
                }
        );
        communicator.execute(params);
    }

    private void reloadSwitch() {
        if (isSignedIn() == true && this.mScreenData != null) {
            this.mReceivePushSwitch.setEnabled(true);
            this.mReceiveCouponSwitch.setEnabled(true);

            this.mReceivePushSwitch.setChecked(this.mScreenData.data.push_setting.isAtLeastOneEnable());
            this.mReceiveCouponSwitch.setChecked(this.mScreenData.data.push_setting.isCouponEnable());
        } else {
            this.mReceivePushSwitch.setEnabled(false);
            this.mReceiveCouponSwitch.setEnabled(false);
        }
    }
}
