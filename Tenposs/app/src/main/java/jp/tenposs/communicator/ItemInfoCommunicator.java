package jp.tenposs.communicator;

import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.ItemInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.NewsInfo;

/**
 * Created by ambient on 8/10/16.
 */
public class ItemInfoCommunicator extends TenpossCommunicator {

    public ItemInfoCommunicator(TenpossCommunicator.TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {
        String strUrl;
        ItemInfo.Request requestData = (ItemInfo.Request) bundle.getSerializable(Key.RequestObject);
        strUrl = API_ITEMS + requestData.makeParams("GET");
//        String strUrl = bundle.getString(GammaKey.KeyRequestURL);
        //http://54.153.78.127/api/news?store_id=1&pageindex=1&pagesize=20
        //String strUrl = "http://ec2-54-204-210-230.compute-1.amazonaws.com/tenposs/api/public/index.php/api/v1/news?store_id=1&token=7aef1eea1f967d7f8fbcb8cbe4639dd0&time=23423432423&sig=6a2383b4296f4b0c48883a3f8aae3522274d6237932f14f712aac12d057ce0qeqweq48&pageindex=1&pagesize=20";
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
        result = request(strUrl, output, dataRequest, bundle);
        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
            String strResponse = output.toString();
            ItemInfo.Response response = (ItemInfo.Response) CommonObject.fromJSONString(strResponse, ItemInfo.Response.class, null);
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
    //http://ec2-54-204-210-230.compute-1.amazonaws.com/tenposs/api/public/index.php/api/v1/news?store_id=1&token=7aef1eea1f967d7f8fbcb8cbe4639dd0&time=23423432423&sig=6a2383b4296f4b0c48883a3f8aae3522274d6237932f14f712aac12d057ce0qeqweq48&pageindex=1&pagesize=20
}
