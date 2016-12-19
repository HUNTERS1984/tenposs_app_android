package jp.tenposs.communicator;

import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.CouponAcceptInfo;
import jp.tenposs.datamodel.Key;

/**
 * Created by ambient on 10/28/16.
 */

public class CouponAcceptCommunicator extends TenpossCommunicator {
    public CouponAcceptCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
        mMethod = METHOD_POST;

    }

    @Override
    protected boolean request(Bundle bundle) {

        String strUrl;
        CouponAcceptInfo.Request request = new CouponAcceptInfo.Request();
        CouponAcceptInfo.Request requestData = (CouponAcceptInfo.Request) bundle.getSerializable(Key.RequestObject);
        bundle.putSerializable(Key.RequestFormData, requestData.getFormData());
        strUrl = API_SIGN_IN;

        int result;
        OutputStream output = null;

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
            CommonResponse response = (CommonResponse) CommonObject.fromJSONString(strResponse, CommonResponse.class, null);
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
