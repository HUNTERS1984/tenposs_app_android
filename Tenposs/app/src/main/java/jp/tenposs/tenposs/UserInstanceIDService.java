package jp.tenposs.tenposs;

import android.os.Bundle;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import jp.tenposs.communicator.RefreshTokenCommunicator;
import jp.tenposs.communicator.SetPushKeyCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.RefreshTokenInfo;
import jp.tenposs.datamodel.SetPushKeyInfo;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 9/15/16.
 */
public class UserInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "UserInstanceIDService";

    @Override
    public void onTokenRefresh() {
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            Utils.log(TAG, token);
            registerToken(token);
        } catch (Exception ignored) {
            Utils.log(ignored);
        }
    }

    protected void refreshToken(final AbstractFragment.TenpossCallback callback) {
        RefreshTokenInfo.Request request = new RefreshTokenInfo.Request();
        Bundle params = new Bundle();
        request.access_refresh_token_href = Utils.getPrefString(getApplicationContext(), Key.RefreshTokenHref);
        params.putSerializable(Key.RequestObject, request);
        new RefreshTokenCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                Utils.hideProgress();
                int result = responseParams.getInt(Key.ResponseResult);
                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                    if (resultApi == CommonResponse.ResultSuccess) {
                        SignInInfo.Response response = (SignInInfo.Response) responseParams.get(Key.ResponseObject);
                        Utils.setPrefString(getApplicationContext(), Key.TokenKey, response.data.token);
                        Utils.setPrefString(getApplicationContext(), Key.RefreshToken, response.data.refresh_token);
                        Utils.setPrefString(getApplicationContext(), Key.RefreshTokenHref, response.data.access_refresh_token_href);
                        callback.onSuccess(responseParams);

                    } else {
                        callback.onFailed(responseParams);
                    }
                } else {
                    callback.onFailed(responseParams);
                }
            }
        }).execute(params);
    }

    private void registerToken(final String firebaseToken) {
        Utils.setPrefString(this.getApplicationContext(), Key.FireBaseTokenKey, firebaseToken);
        Bundle params = new Bundle();
        SetPushKeyInfo.Request request = new SetPushKeyInfo.Request();
        request.token = Utils.getPrefString(this.getApplicationContext(), Key.TokenKey);
        request.key = firebaseToken;
        params.putSerializable(Key.RequestObject, request);
        params.putString(Key.TokenKey, Utils.getPrefString(getApplicationContext(), Key.TokenKey));
        SetPushKeyCommunicator communicator = new SetPushKeyCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                //Update User profile
                                SetPushKeyInfo.Response response = (SetPushKeyInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                            } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                refreshToken(new AbstractFragment.TenpossCallback() {
                                    @Override
                                    public void onSuccess(Bundle params) {
                                        registerToken(firebaseToken);
                                    }

                                    @Override
                                    public void onFailed(Bundle params) {
                                        //TODO: Logout, then do something
                                        //mActivityListener.logoutBecauseExpired();
                                    }
                                });
                            } else {

                                String strMessage = responseParams.getString(Key.ResponseMessage);
//                                errorWithMessage(responseParams, strMessage);
                            }
                        } else {
                            String strMessage = responseParams.getString(Key.ResponseMessage);
//                            errorWithMessage(responseParams, strMessage);
                        }
                    }
                });
//        showProgress(getString(R.string.msg_loading_profile));
        communicator.execute(params);
    }
}

