package jp.tenposs.staffapp;

import android.os.Bundle;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import jp.tenposs.communicator.SetPushKeyCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.SetPushKeyInfo;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 9/15/16.
 */
public class TenpossInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();

        registerToken(token);
    }

    private void registerToken(String firebaseToken) {

        Bundle params = new Bundle();
        SetPushKeyInfo.Request request = new SetPushKeyInfo.Request();
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
                                SetPushKeyInfo.Response response = (SetPushKeyInfo.Response) responseParams.getSerializable(Key.ResponseObject);
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

