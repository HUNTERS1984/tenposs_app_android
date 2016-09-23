package jp.tenposs.communicator;

import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.StaffCategoryInfo;

/**
 * Created by ambient on 9/12/16.
 */

public class StaffCategoryCommunicator extends TenpossCommunicator {


    //http://54.204.210.230/api/v1/staff_categories?
    // app_id=2a33ba4ea5c9d70f9eb22903ad1fb8b2&
    // time=1473129477000&
    // sig=f83c8a20b0c4609a9e78a9a7172e0646d8f111cebf96152864f95b39b47a1d59&
    // store_id=1

    //http://54.204.210.230/api/v1/staffs?
    // app_id=2a33ba4ea5c9d70f9eb22903ad1fb8b2&
    // time=1473129477000&
    // sig=f83c8a20b0c4609a9e78a9a7172e0646d8f111cebf96152864f95b39b47a1d59&
    // store_id=1&
    // pageindex=1&
    // pagesize=20&
    // category_id=1

    public StaffCategoryCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        String strUrl;
        StaffCategoryInfo.Request requestData = (StaffCategoryInfo.Request) bundle.getSerializable(Key.RequestObject);
        strUrl = API_STAFF_CATEGORY + requestData.makeParams();
        int result = TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal();
        byte[] dataRequest = null;
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
            CommonResponse response = (StaffCategoryInfo.Response) CommonObject.fromJSONString(strResponse, StaffCategoryInfo.Response.class, null);
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
