package jp.tenposs.communicator;

import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.SignUpInfo;

/**
 * Created by ambient on 7/26/16.
 */
public class SignUpCommunicator extends TenpossCommunicator {

    public SignUpCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
        mMethod = METHOD_POST;
    }

    @Override
    protected boolean request(Bundle bundle) {
        String strUrl;
        SignUpInfo.Request requestData = (SignUpInfo.Request) bundle.getSerializable(Key.RequestObject);
        //strUrl = API_SIGNUP + requestData.makeParams("METHOD_POST");
        strUrl = API_SIGN_UP;
        bundle.putSerializable(Key.RequestFormData, requestData.getFormData());
        int result = CommunicationCode.ConnectionSuccess.ordinal();
        OutputStream output;

        try {
            output = new ByteArrayOutputStream();
        } catch (Exception e) {
            bundle.putString(Key.ResponseMessage, e.getMessage());
            bundle.putInt(Key.ResponseResult, CommunicationCode.GeneralError.ordinal());
            return false;
        }
        result = request(strUrl, output, bundle);
        if (result == CommunicationCode.ConnectionSuccess.ordinal()) {
            String strResponse = output.toString();
            CommonResponse response = (SignUpInfo.Response) CommonObject.fromJSONString(strResponse, SignUpInfo.Response.class, null);
            if (response == null) {
                response = (CommonResponse) CommonObject.fromJSONString(strResponse, CommonResponse.class, null);
            }
            if (response != null) {
                bundle.putInt(Key.ResponseResult, CommunicationCode.ConnectionSuccess.ordinal());
                bundle.putInt(Key.ResponseResultApi, response.code);

                if (response.code == CommonResponse.ResultSuccess) {
                    bundle.putSerializable(Key.ResponseObject, response);
                } else {
                    bundle.putString(Key.ResponseMessage, response.message);
                }
            } else {
                bundle.putInt(Key.ResponseResult, CommunicationCode.ConnectionFailed.ordinal());
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
            bundle.putInt(Key.ResponseResult, CommunicationCode.GeneralError.ordinal());
            return false;
        }
        return true;
    }

}
