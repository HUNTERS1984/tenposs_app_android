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

import jp.tenposs.communicator.SignInCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/15/16.
 */
public class FragmentSignInEmail extends AbstractFragment implements View.OnClickListener {

    EditText emailEdit;
    EditText passwordEdit;

    TextView gotoSignUpLabel;
    Button signInButton;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = getString(R.string.sign_in);
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
        View mRoot = inflater.inflate(R.layout.fragment_signin_email, null);
        emailEdit = (EditText) mRoot.findViewById(R.id.email_edit);
        passwordEdit = (EditText) mRoot.findViewById(R.id.password_edit);
        gotoSignUpLabel = (TextView) mRoot.findViewById(R.id.go_to_sign_up_label);
        signInButton = (Button) mRoot.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        Utils.setTextViewHTML(gotoSignUpLabel, getString(R.string.not_sign_up),
                new ClickableSpan() {
                    public void onClick(View view) {
                        close();
                        activityListener.showScreen(AbstractFragment.SIGN_UP_SCREEN, null);
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
        if (v == signInButton) {
            if (checkInput() == true) {
                Bundle params = new Bundle();
                SignInInfo.Request request = new SignInInfo.Request();
                request.email = emailEdit.getEditableText().toString();
                request.setPassword(passwordEdit.getEditableText().toString());
                params.putSerializable(Key.RequestObject, request);
                SignInCommunicator communicator = new SignInCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                //TODO:
                                SignInInfo.Response response = (SignInInfo.Response) responseParams.get(Key.ResponseObject);
                                String token = response.data.token;
                                SignInInfo.Profile profile = response.data.profile;
                                setKeyString(Key.TokenKey, token);
                                setKeyString(Key.Profile, CommonObject.toJSONString(profile, profile.getClass()));
                                activityListener.updateUserInfo(profile);
                                close();

                            } else {
                                showAlert(getString(R.string.error),
                                        getString(R.string.msg_unable_to_sign_in),
                                        getString(R.string.close),
                                        null,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }
                                );
                                //String strMessage = responseParams.getString(Key.ResponseMessage);
                                //errorWithMessage(responseParams, strMessage);
                            }
                        } else {
                            String strMessage = responseParams.getString(Key.ResponseMessage);
                            errorWithMessage(responseParams, strMessage);
                        }
                    }
                });
                communicator.execute(params);
            }
        }
    }

    private boolean checkInput() {
        Utils.hideKeyboard(this.getActivity(), null);
        String email = emailEdit.getEditableText().toString();
        if (email.length() <= 0 || Utils.validateEmailAddress(email) == false) {
            showAlert(getString(R.string.warning), getString(R.string.msg_input_a_valid_email_address), getString(R.string.close), null, new DialogInterface.OnClickListener() {
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
        String password = passwordEdit.getEditableText().toString();
        if (password.length() <= 0) {
            showAlert(getString(R.string.warning), getString(R.string.msg_input_password), getString(R.string.close), null, new DialogInterface.OnClickListener() {
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
        return true;
    }


}
