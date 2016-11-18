package jp.tenposs.tenposs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;

import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.SignUpInfo;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/15/16.
 */
public class FragmentSignUp extends AbstractFragment implements View.OnClickListener {

    EditText mEmailEdit;
    EditText mNameEdit;
    EditText mPasswordEdit;
    EditText mPasswordConfirmEdit;

    TextView mGotoSignInLabel;
    Button mNextButton;

    private FragmentSignUp() {

    }

    public static FragmentSignUp newInstance(Serializable extras) {
        FragmentSignUp fragment = new FragmentSignUp();
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
        mToolbarSettings.toolbarTitle = getString(R.string.sign_up_1);
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
        if(AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            mRoot = inflater.inflate(R.layout.restaurant_fragment_signup, null);
        }else {
            mRoot = inflater.inflate(R.layout.fragment_signup, null);
        }
        mEmailEdit = (EditText) mRoot.findViewById(R.id.email_edit);
        mNameEdit = (EditText) mRoot.findViewById(R.id.name_edit);
        mPasswordEdit = (EditText) mRoot.findViewById(R.id.password_edit);
        mPasswordConfirmEdit = (EditText) mRoot.findViewById(R.id.password_confirm_edit);
        mGotoSignInLabel = (TextView) mRoot.findViewById(R.id.go_to_sign_in_label);
        mNextButton = (Button) mRoot.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(this);

        Utils.setTextViewHTML(mGotoSignInLabel, getString(R.string.already_sign_up),
                new ClickableSpan() {
                    public void onClick(View view) {
                        close();
                        mActivityListener.showScreen(AbstractFragment.SIGN_IN_EMAIL_SCREEN, null);
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
            //this.mScreenData = (TopInfo.Response.ResponseData) savedInstanceState.getSerializable(SCREEN_DATA);
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
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == mNextButton) {
            if (checkInput() == true) {
                SignUpInfo.Request request = new SignUpInfo.Request();
                request.email = mEmailEdit.getEditableText().toString();
                request.name = mNameEdit.getEditableText().toString();
                request.setPassword(mPasswordEdit.getEditableText().toString());

                this.mActivityListener.showScreen(AbstractFragment.SIGN_UP_NEXT_SCREEN, request);
            }
        }
//        if (v == mSignUpButton) {
//            if (checkInput() == true) {
//                Bundle params = new Bundle();
//                SignUpInfo.Request request = new SignUpInfo.Request();
//                request.email = mEmailEdit.getEditableText().toString();
//                request.name = mNameEdit.getEditableText().toString();
//                request.setPassword(mPasswordEdit.getEditableText().toString());
//                params.putSerializable(Key.RequestObject, request);
//                SignUpCommunicator communicator = new SignUpCommunicator(
//                        new TenpossCommunicator.TenpossCommunicatorListener() {
//                            @Override
//                            public void completed(TenpossCommunicator request, Bundle responseParams) {
//                                hideProgress();
//                                int result = responseParams.getInt(Key.ResponseResult);
//                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
//                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
//                                    if (resultApi == CommonResponse.ResultSuccess) {
//                                        SignUpInfo.Response response = (SignUpInfo.Response) responseParams.getSerializable(Key.ResponseObject);
//                                        String token = response.data.token;
//                                        SignInInfo.Profile profile = response.data.profile;
//                                        setPref(Key.TokenKey, token);
//                                        setPref(Key.UserProfile, CommonObject.toJSONString(response.data, response.data.getClass()));
//                                        mActivityListener.updateUserInfo(response.data);
//                                        close();
//                                    } else {
//                                        Utils.showAlert(getContext(),
//                                                getString(R.string.error),
//                                                getString(R.string.msg_unable_to_sign_up),
//                                                getString(R.string.close),
//                                                null,
//                                                new DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(DialogInterface dialog, int which) {
//
//                                                    }
//                                                });
//                                    }
//                                } else {
//                                    String strMessage = responseParams.getString(Key.ResponseMessage);
//                                    errorWithMessage(responseParams, strMessage);
//                                }
//                            }
//                        });
//                showProgress(getString(R.string.msg_signing_up));
//                communicator.execute(params);
//            }
//        }
    }

    private boolean checkInput() {
        Utils.hideKeyboard(this.getActivity(), null);
        String email = mEmailEdit.getEditableText().toString();
        String name = mNameEdit.getEditableText().toString();
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
        if (name.length() <= 0) {
            Utils.showAlert(getContext(),
                    getString(R.string.warning),
                    getString(R.string.msg_input_name),
                    getString(R.string.close),
                    null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE: {
                                    mNameEdit.requestFocus();
                                }
                                break;
                            }
                        }
                    });
            return false;
        }
        String password = mPasswordEdit.getEditableText().toString();
        String passwordConfirm = mPasswordConfirmEdit.getEditableText().toString();
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
        if (passwordConfirm.length() <= 0) {
            Utils.showAlert(getContext(),
                    getString(R.string.warning),
                    getString(R.string.msg_input_password_confirm),
                    getString(R.string.close),
                    null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE: {
                                    mPasswordConfirmEdit.requestFocus();
                                }
                                break;
                            }
                        }
                    });
            return false;
        }
        if (password.compareTo(passwordConfirm) != 0) {
            Utils.showAlert(getContext(),
                    getString(R.string.warning),
                    getString(R.string.msg_input_correct_confirm_password),
                    getString(R.string.close),
                    null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE: {
                                    mPasswordConfirmEdit.requestFocus();
                                }
                                break;
                            }
                        }
                    });
            return false;
        }


        return true;
    }
}
