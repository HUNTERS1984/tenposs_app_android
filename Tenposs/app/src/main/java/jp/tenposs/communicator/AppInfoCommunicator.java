package jp.tenposs.communicator;

import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;

/**
 * Created by ambient on 8/5/16.
 */
public class AppInfoCommunicator extends TenpossCommunicator {
    public AppInfoCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        String strUrl;
        AppInfo.Request requestData = (AppInfo.Request) bundle.getSerializable(Key.RequestObject);
        strUrl = API_APPINFO + requestData.makeParams("GET");
//        strUrl = "http://ec2-54-204-210-230.compute-1.amazonaws.com/tenposs/api/public/index.php/api/v1/appinfo?store_id=0&token=7aef1eea1f967d7f8fbcb8cbe4639dd0&time=23423432423&sig=6a2383b4296f4b0c48883a3f8aae3522274d6237932f14f712aac12d057ce0qeqweq48";

        int result = CommunicationCode.ConnectionSuccess.ordinal();
        byte[] dataRequest = null;
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
            CommonResponse response = (AppInfo.Response) CommonObject.fromJSONString(strResponse, AppInfo.Response.class, null);
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
