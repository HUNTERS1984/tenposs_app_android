package jp.tenposs.communicator;

import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.UpdateProfileInfo;

/**
 * Created by ambient on 9/16/16.
 */

public class UpdateProfileCommunicator extends TenpossCommunicator {
    public UpdateProfileCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
        this.mMethod = METHOD_POST_MULTIPART;
    }

    @Override
    protected boolean request(Bundle bundle) {
        String strUrl;
        UpdateProfileInfo.Request requestData = (UpdateProfileInfo.Request) bundle.getSerializable(Key.RequestObject);
        bundle.putSerializable(Key.RequestFormData, requestData.getFormData());
        bundle.putInt(Key.RequestWriteTimeout, 30);
        strUrl = API_UPDATE_PROFILE;
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
            if (response == null) {
                response = (CommonResponse) CommonObject.fromJSONString(strResponse, CommonResponse.class, null);
            }
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
