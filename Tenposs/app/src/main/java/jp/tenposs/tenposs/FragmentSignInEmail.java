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
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/15/16.
 */
public class FragmentSignInEmail extends AbstractFragment implements View.OnClickListener {

    EditText mEmailEdit;
    EditText mPasswordEdit;

    TextView mGotoSignUpLabel;
    Button mSignInButton;

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
        View mRoot = inflater.inflate(R.layout.fragment_signin_email, null);
        mEmailEdit = (EditText) mRoot.findViewById(R.id.email_edit);
        mPasswordEdit = (EditText) mRoot.findViewById(R.id.password_edit);
        mGotoSignUpLabel = (TextView) mRoot.findViewById(R.id.go_to_sign_up_label);
        mSignInButton = (Button) mRoot.findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);

        Utils.setTextViewHTML(mGotoSignUpLabel, getString(R.string.not_sign_up),
                new ClickableSpan() {
                    public void onClick(View view) {
                        close();
                        mActivityListener.showScreen(AbstractFragment.SIGN_UP_SCREEN, null);
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

        if (refreshing == true) {

        }
    }

    @Override
    boolean canCloseByBackpressed() {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == mSignInButton) {
            if (checkInput() == true) {
                Bundle params = new Bundle();

                SignInInfo.Request request = new SignInInfo.Request();
                request.email = mEmailEdit.getEditableText().toString();
                request.setPassword(mPasswordEdit.getEditableText().toString());
                params.putSerializable(Key.RequestObject, request);
                SignInCommunicator communicator = new SignInCommunicator(
                        new TenpossCommunicator.TenpossCommunicatorListener() {
                            @Override
                            public void completed(TenpossCommunicator request, Bundle responseParams) {
                                hideProgress();
                                int result = responseParams.getInt(Key.ResponseResult);
                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                    if (resultApi == CommonResponse.ResultSuccess) {
                                        SignInInfo.Response response = (SignInInfo.Response) responseParams.get(Key.ResponseObject);
                                        String token = response.data.token;
                                        setKeyString(Key.TokenKey, token);
                                        setKeyString(Key.UserProfile, CommonObject.toJSONString(response.data, response.data.getClass()));
                                        mActivityListener.updateUserInfo(response.data);
                                        close();

                                    } else {
                                        Utils.showAlert(getContext(),
                                                getString(R.string.error),
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
                showProgress(getString(R.string.msg_signing_in));
                communicator.execute(params);
            }
        }
    }

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


}
