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
import android.widget.EditText;
import android.widget.TextView;

import jp.tenposs.communicator.SignInCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.utils.FontIcon;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/15/16.
 */
public class FragmentSignInEmail extends AbstractFragment implements View.OnClickListener {

    EditText mEmailEdit;
    EditText mPasswordEdit;

    TextView mGotoSignUpLabel;
    Button mSignInButton;
    Button mRegisterButton;


//    public static FragmentSignInEmail newInstance(Serializable extras) {
//        FragmentSignInEmail fragment = new FragmentSignInEmail();
//        return fragment;
//    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.sign_in);
        mToolbarSettings.toolbarLeftIcon = "flaticon-close";
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
            mRoot = inflater.inflate(R.layout.restaurant_fragment_signin_email, null);
        } else {
            mRoot = inflater.inflate(R.layout.fragment_signin_email, null);
        }
        this.mEmailEdit = (EditText) mRoot.findViewById(R.id.email_edit);
        this.mPasswordEdit = (EditText) mRoot.findViewById(R.id.password_edit);
        this.mGotoSignUpLabel = (TextView) mRoot.findViewById(R.id.go_to_sign_up_label);
        this.mSignInButton = (Button) mRoot.findViewById(R.id.sign_in_button);
        this.mRegisterButton = (Button) mRoot.findViewById(R.id.register_button);
        this.mSignInButton.setOnClickListener(this);


        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            if (this.mRegisterButton != null) {
                this.mRegisterButton.setOnClickListener(this);
                this.mRegisterButton.setTextColor(Color.WHITE);
            }
            this.mGotoSignUpLabel.setVisibility(View.INVISIBLE);
        } else {
            Utils.setTextViewHTML(mGotoSignUpLabel, getString(R.string.not_sign_up),
                    new ClickableSpan() {
                        public void onClick(View view) {
                            close();
                            mActivityListener.showScreen(AbstractFragment.SIGN_UP_SCREEN, null, null, false);
                        }
                    });
        }
        if (BuildConfig.DEBUG) {
            mEmailEdit.setText("quanlh218@gmail.com");
            mPasswordEdit.setText("123456");
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
            //this.mScreenData = (TopInfo.Response.ApplicationInfo) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {

        if (refreshing == true) {

        }
    }

    @Override
    boolean canCloseByBackpressed() {
        return true;
    }

    @Override
    public void onClick(View v) {
        final View clickedView = v;
        if (v == mSignInButton) {
            if (checkInput() == true) {
                Bundle params = new Bundle();

                SignInInfo.Request request = new SignInInfo.Request();
                request.email = mEmailEdit.getEditableText().toString();
                request.setPassword(mPasswordEdit.getEditableText().toString());
                request.role = SignInInfo.user;
                params.putSerializable(Key.RequestObject, request);
                SignInCommunicator communicator = new SignInCommunicator(
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
                                        SignInInfo.Response response = (SignInInfo.Response) responseParams.get(Key.ResponseObject);
                                        Utils.setPrefString(getContext(), Key.TokenKey, response.data.token);
                                        Utils.setPrefString(getContext(), Key.RefreshToken, response.data.refresh_token);
                                        Utils.setPrefString(getContext(), Key.RefreshTokenHref, response.data.access_refresh_token_href);
                                        close();
                                    } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                        refreshToken(new TenpossCallback() {
                                            @Override
                                            public void onSuccess(Bundle params) {
                                                onClick(clickedView);
                                            }

                                            @Override
                                            public void onFailed(Bundle params) {
                                                //Logout, then do something
                                                mActivityListener.logoutBecauseExpired();
                                            }
                                        });
                                    } else {
                                        Utils.hideProgress();
                                        String msg = CommonResponse.getErrorMessage(getContext(), result);

                                        Utils.showAlert(getContext(),
                                                getString(R.string.error),
                                                msg,
                                                getString(R.string.close),
                                                null,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                }
                                        );
                                    }
                                } else {
                                    Utils.hideProgress();
                                    String strMessage = responseParams.getString(Key.ResponseMessage);
                                    errorWithMessage(responseParams, strMessage, null);
                                }
                            }
                        });
                Utils.showProgress(getContext(), getString(R.string.msg_signing_in));
                communicator.execute(params);
            }
        } else if (v == this.mRegisterButton) {
//            close();
            mActivityListener.showScreen(AbstractFragment.SIGN_UP_SCREEN, null, null, false);
        }
    }

//    public void getUserDetail() {
//        String token = Utils.getPrefString(getContext(), Key.TokenKey);
//        if (token.length() > 0) {
//            Bundle params = new Bundle();
//            UserInfo.Request request = new UserInfo.Request();
//            request.token = token;
//            params.putSerializable(Key.RequestObject, request);
//
//            UserInfoCommunicator communicator = new UserInfoCommunicator(
//                    new TenpossCommunicator.TenpossCommunicatorListener() {
//                        @Override
//                        public void completed(TenpossCommunicator request, Bundle responseParams) {
//                            Utils.hideProgress();
//                            int result = responseParams.getInt(Key.ResponseResult);
//                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
//                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
//                                if (resultApi == CommonResponse.ResultSuccess) {
//                                    //Update User profile
//
//                                    UserInfo.Response data = (UserInfo.Response) responseParams.getSerializable(Key.ResponseObject);
//                                    Utils.setPrefString(getContext(), Key.UserProfile, CommonObject.toJSONString(data.data.user, SignInInfo.User.class));
//                                    close();
//                                } else {
//                                    //show alert error
//
//                                }
//                            } else if (result == TenpossCommunicator.CommunicationCode.ConnectionTimedOut.ordinal()) {
//                                Utils.showAlert(getContext(),
//                                        getString(R.string.warning),
//                                        getString(R.string.msg_connection_timedout_try_again),
//                                        getString(R.string.retry),
//                                        getString(R.string.exit),
//                                        new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                switch (which) {
//                                                    case DialogInterface.BUTTON_POSITIVE: {
//                                                        getUserDetail();
//                                                    }
//                                                    break;
//
//                                                    case DialogInterface.BUTTON_NEGATIVE: {
//                                                        //Do nothing
//                                                    }
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                );
//
//                            } else {
//                                //TODO: show alert error
//                            }
//                        }
//                    });
//            communicator.execute(params);
//        } else {
//            Utils.Assert("Should never be here!");
//            Utils.hideProgress();
//        }
////        setPref(Key.UserProfile, CommonObject.toJSONString(response.data, response.data.getClass()));
////        mActivityListener.updateUserInfo(response.data);
//    }

    private boolean checkInput() {
        Utils.hideKeyboard(this.getActivity(), null);
        String email = mEmailEdit.getEditableText().toString();
        if (email.length() <= 0 || Utils.validateEmailAddress(email) == false) {
            Utils.showAlert(getContext(),
                    getString(R.string.warning),
                    getString(R.string.msg_input_a_valid_email_address),
                    getString(R.string.close),
                    null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE: {
                                    mEmailEdit.requestFocus();
                                }
                                break;
                            }
                        }
                    });
            return false;
        }
        String password = mPasswordEdit.getEditableText().toString();
        if (password.length() <= 0) {
            Utils.showAlert(getContext(),
                    getString(R.string.warning),
                    getString(R.string.msg_input_password),
                    getString(R.string.close),
                    null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE: {
                                    mPasswordEdit.requestFocus();
                                }
                                break;
                            }
                        }
                    });
            return false;
        }
        return true;
    }

    @Override
    protected void updateToolbar() {
        try {
            int toolbarIconColor;
            int toolbarTitleColor;
            this.mToolbar.setBackgroundColor(Color.alpha(0));
            if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                toolbarIconColor = Color.WHITE;//Utils.getColor(getContext(), R.color.restaurant_toolbar_icon_color);
                toolbarTitleColor = Color.WHITE;//Utils.getColor(getContext(), R.color.restaurant_toolbar_text_color);
            } else {
                this.mToolbar.setBackgroundColor(this.mToolbarSettings.getToolbarBackgroundColor());
                toolbarIconColor = this.mToolbarSettings.getToolbarIconColor();
                toolbarTitleColor = this.mToolbarSettings.getToolbarTitleColor();
            }

            if (this.mLeftToolbarButton != null) {
                this.mLeftToolbarButton.setImageBitmap(FontIcon.imageForFontIdentifier(getActivity().getAssets(),
                        this.mToolbarSettings.toolbarLeftIcon,
                        Utils.NavIconSize,
                        Color.argb(0, 0, 0, 0),
                        toolbarIconColor,
                        FontIcon.FLATICON
                ));
            }
            if (this.mRightToolbarButton != null && this.mToolbarSettings.toolbarRightIcon != null) {
                this.mRightToolbarLayout.setVisibility(View.VISIBLE);
                this.mRightToolbarButton.setImageBitmap(FontIcon.imageForFontIdentifier(getActivity().getAssets(),
                        this.mToolbarSettings.toolbarRightIcon,
                        Utils.NavIconSize,
                        Color.argb(0, 0, 0, 0),
                        toolbarIconColor,
                        FontIcon.FLATICON
                ));
            }

            if (this.mTitleToolbarLabel != null) {
                this.mTitleToolbarLabel.setText(mToolbarSettings.toolbarTitle);
                try {
                    Utils.setTextAppearanceTitle(getContext(), this.mTitleToolbarLabel, mToolbarSettings.getToolbarTitleFontSize());
//                    Typeface type = Utils.getTypeFaceForFont(getActivity(), mToolbarSettings.getToolBarTitleFont());
//                    this.mTitleToolbarLabel.setTypeface(type);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                this.mTitleToolbarLabel.setTextColor(toolbarTitleColor);
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

            if (this.mScreenToolBarHidden == false) {
                this.mToolbar.setVisibility(View.VISIBLE);
            } else {
                this.mActivityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                this.mToolbar.setVisibility(View.GONE);
            }

        } catch (Exception ignored) {

        }
    }
}
