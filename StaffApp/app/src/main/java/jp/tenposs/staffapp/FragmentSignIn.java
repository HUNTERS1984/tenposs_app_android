package jp.tenposs.staffapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import jp.tenposs.communicator.SignInCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 10/14/16.
 */

public class FragmentSignIn extends AbstractFragment implements View.OnClickListener {

    EditText mEmailEdit;
    EditText mPasswordEdit;

    Button mSignInButton;

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {

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
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sign_in, null);
        mEmailEdit = (EditText) root.findViewById(R.id.email_edit);
        mPasswordEdit = (EditText) root.findViewById(R.id.password_edit);

        mSignInButton = (Button) root.findViewById(R.id.sign_in_button);

        mSignInButton.setOnClickListener(this);

        mEmailEdit.setText("quanlh218@gmail.com");
        mPasswordEdit.setText("123456");

        return root;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {

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
        if (v == mSignInButton) {
            performSignIn();
        }
    }

    boolean checkInput() {
        String email = this.mEmailEdit.getEditableText().toString();
        String password = this.mPasswordEdit.getEditableText().toString();

        if (email.length() > 0 && password.length() > 0) {
            return true;
        }
        return false;
    }

    void performSignIn() {
        if (checkInput() == true) {
            Bundle params = new Bundle();

            SignInInfo.Request request = new SignInInfo.Request();
            request.email = mEmailEdit.getEditableText().toString();
            request.password = mPasswordEdit.getEditableText().toString();
            request.source = "mobile";

            params.putSerializable(Key.RequestObject, request);
            showProgress("Signing in...");
            SignInCommunicator communicator = new SignInCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
                @Override
                public void completed(TenpossCommunicator request, Bundle responseParams) {
                    hideProgress();
                    int result = responseParams.getInt(Key.ResponseResult);
                    if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                        int resultApi = responseParams.getInt(Key.ResponseResultApi);
                        if (resultApi == CommonResponse.ResultSuccess) {
                            SignInInfo.Response response = (SignInInfo.Response) responseParams.get(Key.ResponseObject);
                            String token = response.data.token;
                            processToken(token);

                        } else {
                            String message = responseParams.getString(Key.ResponseMessage);
                            Log.i(Tag, "API return error " + resultApi + " - " + message);
                            Utils.showAlert(getContext(),
                                    getString(R.string.error),
                                    CommonResponse.getMessageByCode(getContext(), result),
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
                        String strMessage = responseParams.getString(Key.ResponseMessage);
                        errorWithMessage(responseParams, strMessage);
                    }
                }


            });
            communicator.execute(params);
        }
    }

    private void processToken(String token) {
        Utils.setPrefString(this.getContext(), Key.TokenKey, token);
        this.mActivityListener.showScreen(AbstractFragment.COUPON_REQUEST_SCREEN, null);
    }
}
