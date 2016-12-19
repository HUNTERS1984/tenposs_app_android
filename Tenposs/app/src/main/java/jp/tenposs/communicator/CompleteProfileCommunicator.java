package jp.tenposs.communicator;

import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.CompleteProfileInfo;
import jp.tenposs.datamodel.Key;

/**
 * Created by ambient on 12/13/16.
 */

public class CompleteProfileCommunicator extends TenpossCommunicator {
    public CompleteProfileCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
        this.mAuthorizationMode = AUTH_TOKEN;
        this.mMethod = METHOD_POST;
    }

    @Override
    protected boolean request(Bundle bundle) {
        String strUrl;
        CompleteProfileInfo.Request requestData = (CompleteProfileInfo.Request) bundle.getSerializable(Key.RequestObject);
        bundle.putSerializable(Key.RequestFormData, requestData.getFormData());
        strUrl = API_COMPLETE_PROFILE;
        int result;
        OutputStream output;

        try {
            output = new ByteArrayOutputStream();
        } catch (Exception e) {
            bundle.putString(Key.ResponseMessage, e.getMessage());
            bundle.putInt(Key.ResponseResult, TenpossCommunicator.CommunicationCode.GeneralError.ordinal());
            return false;
        }
        result = request(strUrl, output, bundle);
        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
            String strResponse = output.toString();
            CommonResponse response = (CommonResponse) CommonObject.fromJSONString(strResponse, CommonResponse.class, null);
            if (response != null) {
                bundle.putInt(Key.ResponseResult, TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal());
                bundle.putInt(Key.ResponseResultApi, response.code);

                if (response.code == CommonResponse.ResultSuccess) {
                    bundle.putSerializable(Key.ResponseObject, response);
                } else {
                    bundle.putString(Key.ResponseMessage, response.message);
                }
            } else {
                bundle.putInt(Key.ResponseResult, TenpossCommunicator.CommunicationCode.ConnectionFailed.ordinal());
                bundle.putString(Key.ResponseMessage, "Invalid response data!");
            }
        }

        try {
            if (output != null) {
                output.flush();
                output.close();
            }
        } catch (Exception e) {
            bundle.putString(Key.ResponseMessage, e.getMessage());
            bundle.putInt(Key.ResponseResult, TenpossCommunicator.CommunicationCode.GeneralError.ordinal());
            return false;
        }
        return true;
    }
}
