package jp.tenposs.tenposs;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;

import jp.tenposs.communicator.SignUpCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.datamodel.SignUpInfo;
import jp.tenposs.utils.FontIcon;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/15/16.
 */
public class FragmentSignUpNext extends AbstractFragment implements View.OnClickListener {
    TextView mGotoSignInLabel;
    Button mSignUpButton;

    SignUpInfo.Request mScreenData;

    private FragmentSignUpNext() {

    }

    public static FragmentSignUpNext newInstance(Serializable extras) {
        FragmentSignUpNext fragment = new FragmentSignUpNext();
        Bundle b = new Bundle();
        b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.sign_up_2);
        mToolbarSettings.toolbarLeftIcon = "flaticon-back";
        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
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
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot;
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            mRoot = inflater.inflate(R.layout.restaurant_fragment_signup_next, null);
        } else {
            mRoot = inflater.inflate(R.layout.fragment_signup_next, null);
        }

        mGotoSignInLabel = (TextView) mRoot.findViewById(R.id.go_to_sign_in_label);
        mSignUpButton = (Button) mRoot.findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(this);

        Utils.setTextViewHTML(mGotoSignInLabel, getString(R.string.already_sign_up),
                new ClickableSpan() {
                    public void onClick(View view) {
                        close();
                        mActivityListener.showScreen(AbstractFragment.SIGN_IN_EMAIL_SCREEN, null, null);
                    }
                });
        return mRoot;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (SignUpInfo.Request) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        if (this.mScreenData != null) {
            outState.putSerializable(SCREEN_DATA, mScreenData);
        }
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == mSignUpButton) {
            Bundle params = new Bundle();
            params.putSerializable(Key.RequestObject, this.mScreenData);
            SignUpCommunicator communicator = new SignUpCommunicator(
                    new TenpossCommunicator.TenpossCommunicatorListener() {
                        @Override
                        public void completed(TenpossCommunicator request, Bundle responseParams) {
                            Utils.hideProgress();
                            int result = responseParams.getInt(Key.ResponseResult);
                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                if (resultApi == CommonResponse.ResultSuccess) {
                                    SignUpInfo.Response response = (SignUpInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                    String token = response.data.token;
                                    SignInInfo.Profile profile = response.data.profile;
                                    setPref(Key.TokenKey, token);
                                    setPref(Key.UserProfile, CommonObject.toJSONString(response.data, response.data.getClass()));
                                    mActivityListener.updateUserInfo(response.data);
                                    close();
                                } else {
                                    Utils.showAlert(getContext(),
                                            getString(R.string.error),
                                            getString(R.string.msg_unable_to_sign_up),
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
                                errorWithMessage(responseParams, strMessage);
                            }
                        }
                    });
            Utils.showProgress(getContext(), getString(R.string.msg_signing_up));
            communicator.execute(params);
        }
    }

    @Override
    protected void updateToolbar() {
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            try {
                this.mToolbar.setBackgroundColor(Color.alpha(0));
                if (this.mLeftToolbarButton != null) {
                    this.mLeftToolbarButton.setImageBitmap(FontIcon.imageForFontIdentifier(getActivity().getAssets(),
                            this.mToolbarSettings.toolbarLeftIcon,
                            Utils.NavIconSize,
                            Color.argb(0, 0, 0, 0),
                            Color.WHITE,
                            FontIcon.FLATICON
                    ));
                }

                if (this.mTitleToolbarLabel != null) {
                    this.mTitleToolbarLabel.setText(mToolbarSettings.toolbarTitle);
                    try {
                        Utils.setTextAppearanceTitle(getContext(), this.mTitleToolbarLabel, mToolbarSettings.getToolbarTitleFontSize());
                        //Typeface type = Utils.getTypeFaceForFont(getActivity(), mToolbarSettings.getToolBarTitleFont());
                        //this.mTitleToolbarLabel.setTypeface(type);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    this.mTitleToolbarLabel.setTextColor(Color.WHITE);
                }

                if (this.mToolbarSettings.toolbarType == ToolbarSettings.LEFT_MENU_BUTTON) {
                    if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusLoaded) {
                        this.mActivityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    } else {
                        this.mActivityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    }

                } else {
                    this.mActivityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }

                if (this.mScreenToolBarHidden == true) {
                    this.mToolbar.setVisibility(View.VISIBLE);
                } else {
                    this.mActivityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    this.mToolbar.setVisibility(View.GONE);
                }

            } catch (Exception ignored) {

            }
        } else {
            super.updateToolbar();
        }
    }
}

