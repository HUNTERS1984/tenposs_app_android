package jp.tenposs.communicator;

import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;

/**
 * Created by ambient on 12/16/16.
 */

public class CouponDetailCommunicator extends TenpossCommunicator {
    public CouponDetailCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        String strUrl;
        //ItemDetailInfo.Request requestData = (ItemDetailInfo.Request) bundle.getSerializable(Key.RequestObject);
        strUrl = API_COUPON_DETAIL;// + requestData.makeParams();
        int result = TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal();
        OutputStream output = null;

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
//            CommonResponse response = (ItemDetailInfo.Response) CommonObject.fromJSONString(strResponse, ItemDetailInfo.Response.class, null);
//            if (response == null) {
//                response = (CommonResponse) CommonObject.fromJSONString(strResponse, CommonResponse.class, null);
//            }
//            if (response != null) {
//                bundle.putInt(Key.ResponseResult, TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal());
//                bundle.putInt(Key.ResponseResultApi, response.code);
//
//                if (response.code == CommonResponse.ResultSuccess) {
//                    bundle.putSerializable(Key.ResponseObject, response);
//                } else {
//                    bundle.putString(Key.ResponseMessage, response.message);
//                }
//            } else {
//                bundle.putInt(Key.ResponseResult, TenpossCommunicator.CommunicationCode.ConnectionFailed.ordinal());
//                bundle.putString(Key.ResponseMessage, "Invalid response data!");
//            }
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
