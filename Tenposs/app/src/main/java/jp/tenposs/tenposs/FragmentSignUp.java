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

import jp.tenposs.communicator.SignUpCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.datamodel.SignUpInfo;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/15/16.
 */
public class FragmentSignUp extends AbstractFragment implements View.OnClickListener {

    EditText emailEdit;
    EditText nameEdit;
    EditText passwordEdit;
    EditText passwordConfirmEdit;

    TextView gotoSignInLabel;
    Button signUpButton;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = getString(R.string.sign_up);
        toolbarSettings.toolbarLeftIcon = "flaticon-close";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_signup, null);
        emailEdit = (EditText) mRoot.findViewById(R.id.email_edit);
        nameEdit = (EditText) mRoot.findViewById(R.id.name_edit);
        passwordEdit = (EditText) mRoot.findViewById(R.id.password_edit);
        passwordConfirmEdit = (EditText) mRoot.findViewById(R.id.password_confirm_edit);
        gotoSignInLabel = (TextView) mRoot.findViewById(R.id.go_to_sign_in_label);
        signUpButton = (Button) mRoot.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(this);

        Utils.setTextViewHTML(gotoSignInLabel, getString(R.string.already_sign_up),
                new ClickableSpan() {
                    public void onClick(View view) {
                        close();
                        activityListener.showScreen(AbstractFragment.SIGN_IN_EMAIL_SCREEN, null);
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
            //this.screenData = (TopInfo.Response.ResponseData) savedInstanceState.getSerializable(SCREEN_DATA);
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
        if (v == signUpButton) {
            if (checkInput() == true) {
                Bundle params = new Bundle();
                SignUpInfo.Request request = new SignUpInfo.Request();
                request.email = emailEdit.getEditableText().toString();
                request.name = nameEdit.getEditableText().toString();
                request.setPassword(passwordEdit.getEditableText().toString());
                params.putSerializable(Key.RequestObject, request);
                SignUpCommunicator communicator = new SignUpCommunicator(
                        new TenpossCommunicator.TenpossCommunicatorListener() {
                            @Override
                            public void completed(TenpossCommunicator request, Bundle responseParams) {
                                hideProgress();
                                int result = responseParams.getInt(Key.ResponseResult);
                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                    if (resultApi == CommonResponse.ResultSuccess) {
                                        SignUpInfo.Response response = (SignUpInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                        String token = response.data.token;
                                        SignInInfo.Profile profile = response.data.profile;
                                        setKeyString(Key.TokenKey, token);
                                        setKeyString(Key.Profile, CommonObject.toJSONString(profile, profile.getClass()));
                                        activityListener.updateUserInfo(profile);
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
                showProgress(getString(R.string.msg_signing_up));
                communicator.execute(params);
            }
        }
    }

    private boolean checkInput() {
        Utils.hideKeyboard(this.getActivity(), null);
        String email = emailEdit.getEditableText().toString();
        String name = nameEdit.getEditableText().toString();
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
                                    emailEdit.requestFocus();
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
                                    nameEdit.requestFocus();
                                }
                                break;
                            }
                        }
                    });
            return false;
        }
        String password = passwordEdit.getEditableText().toString();
        String passwordConfirm = passwordConfirmEdit.getEditableText().toString();
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
                                    passwordEdit.requestFocus();
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
                                    passwordConfirmEdit.requestFocus();
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
                                    passwordConfirmEdit.requestFocus();
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
