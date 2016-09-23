package jp.tenposs.tenposs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.Serializable;

import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.utils.Utils;

//import jp.tenposs.adapter.SettingsAdapter;

/**
 * Created by ambient on 8/4/16.
 */
public class FragmentSetting extends AbstractFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    Button mEditProfileButton;
    Switch mReceivePushSwitch;
    Switch mReceiveCouponSwitch;
    Button mIssueButton;
    Button mCompanyInfoButton;
    Button mUserPrivacyButton;

    SettingInfo mScreenData;

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
        this.mReceivePushSwitch.setChecked(this.mScreenData.receivePush);
        this.mReceiveCouponSwitch.setChecked(this.mScreenData.receiveCouponInformation);
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
        this.mReceivePushSwitch.setOnCheckedChangeListener(this);
        this.mReceiveCouponSwitch.setOnCheckedChangeListener(this);
        this.mIssueButton.setOnClickListener(this);
        this.mCompanyInfoButton.setOnClickListener(this);
        this.mUserPrivacyButton.setOnClickListener(this);

        String settings = getKeyString(Key.Settings);
        this.mScreenData = (SettingInfo) CommonObject.fromJSONString(settings, SettingInfo.class, null);
        if (this.mScreenData == null) {
            this.mScreenData = new SettingInfo();
        }

        return mRoot;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (SettingInfo) savedInstanceState.getSerializable(SCREEN_DATA);

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

        } else if (v == this.mCompanyInfoButton) {
            this.mActivityListener.showScreen(AbstractFragment.COMPANY_INFO_SCREEN, null);

        } else if (v == this.mUserPrivacyButton) {
            this.mActivityListener.showScreen(AbstractFragment.USER_PRIVACY_SCREEN, null);

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == this.mReceivePushSwitch) {
            this.mScreenData.receivePush = isChecked;
        } else if (buttonView == this.mReceiveCouponSwitch) {
            this.mScreenData.receiveCouponInformation = isChecked;
        }

        String settings = CommonObject.toJSONString(this.mScreenData, SettingInfo.class);
        setKeyString(Key.Settings, settings);
    }

    class SettingInfo implements Serializable {
        public boolean receivePush;
        public boolean receiveCouponInformation;
    }
}
