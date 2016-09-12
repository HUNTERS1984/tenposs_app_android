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

//import jp.tenposs.adapter.SettingsAdapter;

/**
 * Created by ambient on 8/4/16.
 */
public class FragmentSetting extends AbstractFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    Button editProfileButton;
    Switch receivePushSwitch;
    Switch receiveCouponSwitch;
    Button issueButton;
    Button companyInfoButton;
    Button userPrivacyButton;

    SettingInfo screenData;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = getString(R.string.setting);
        toolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {
        this.receivePushSwitch.setChecked(this.screenData.receivePush);
        this.receiveCouponSwitch.setChecked(this.screenData.receiveCouponInformation);
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_setting, null);
        this.editProfileButton = (Button) mRoot.findViewById(R.id.edit_profile_button);
        this.receivePushSwitch = (Switch) mRoot.findViewById(R.id.receive_push_switch);
        this.receiveCouponSwitch = (Switch) mRoot.findViewById(R.id.receive_coupon_switch);
        this.issueButton = (Button) mRoot.findViewById(R.id.issue_button);
        this.companyInfoButton = (Button) mRoot.findViewById(R.id.company_info_button);
        this.userPrivacyButton = (Button) mRoot.findViewById(R.id.user_privacy_button);

        this.editProfileButton.setOnClickListener(this);
        this.receivePushSwitch.setOnCheckedChangeListener(this);
        this.receiveCouponSwitch.setOnCheckedChangeListener(this);
        this.issueButton.setOnClickListener(this);
        this.companyInfoButton.setOnClickListener(this);
        this.userPrivacyButton.setOnClickListener(this);

        String settings = getKeyString(Key.Settings);
        this.screenData = (SettingInfo) CommonObject.fromJSONString(settings, SettingInfo.class, null);
        if (this.screenData == null) {
            this.screenData = new SettingInfo();
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
            this.screenData = (SettingInfo) savedInstanceState.getSerializable(SCREEN_DATA);

        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    public void onClick(View v) {
        if (v == this.editProfileButton) {
            // get to
            String token = getKeyString(Key.TokenKey);
            String userProfile = getKeyString(Key.UserProfile);
            if (token.length() > 0 && userProfile.length() > 0) {
                this.activityListener.showScreen(AbstractFragment.PROFILE_SCREEN, null);
            } else {
                showAlert(getString(R.string.info),
                        getString(R.string.msg_not_sign_in),
                        getString(R.string.close),
                        null,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
            }

        } else if (v == this.issueButton) {

        } else if (v == this.companyInfoButton) {
            this.activityListener.showScreen(AbstractFragment.COMPANY_INFO_SCREEN, null);

        } else if (v == this.userPrivacyButton) {
            this.activityListener.showScreen(AbstractFragment.USER_PRIVACY_SCREEN, null);

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == this.receivePushSwitch) {
            this.screenData.receivePush = isChecked;
        } else if (buttonView == this.receiveCouponSwitch) {
            this.screenData.receiveCouponInformation = isChecked;
        }

        String settings = CommonObject.toJSONString(this.screenData, SettingInfo.class);
        setKeyString(Key.Settings, settings);
    }

    class SettingInfo implements Serializable {
        public boolean receivePush;
        public boolean receiveCouponInformation;
    }
}
