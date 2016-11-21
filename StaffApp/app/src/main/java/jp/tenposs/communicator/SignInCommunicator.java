package jp.tenposs.communicator;

import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.SignInInfo;

/**
 * Created by ambient on 10/17/16.
 */

public class SignInCommunicator extends TenpossCommunicator {

    public SignInCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
        mMethod = METHOD_POST;
        mIncludeTokenHeader = false;
    }

    @Override
    protected boolean request(Bundle bundle) {

        String strUrl;
        SignInInfo.Request requestData = (SignInInfo.Request) bundle.getSerializable(Key.RequestObject);
        bundle.putSerializable(Key.RequestFormData, requestData.getFormData());
        strUrl = API_SIGN_IN;

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
            CommonResponse response = (SignInInfo.Response) CommonObject.fromJSONString(strResponse, SignInInfo.Response.class, null);
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


/*
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient.Builder().build();
        // login
        String jsonContent = "{\"email\":\"quanlh218@gmail.com\",\"password\":\"123456\",\"source\":\"mobile\"}";
        String url = "https://apistaffs.ten-po.com/login";
        RequestBody body = RequestBody.create(JSON, jsonContent);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        String dataRes = "";
        try {
            Response response = client.newCall(request).execute();
            dataRes = response.body().string();
            System.out.println(dataRes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // end login

        //set push key
        com.google.gson.JsonParser jsonParser = new com.google.gson.JsonParser();
        JsonObject jsonObject = jsonParser.parse(dataRes).getAsJsonObject();
        String token = jsonObject.get("data").getAsJsonObject().get("token").getAsString();
        String urlSetKey = "https://apistaffs.ten-po.com/set_push_key";
        String jsonBodySetKey = "{\"client\":\"0\",\"key\":\"3423423\"}";
        RequestBody bodySetKey = RequestBody.create(JSON, jsonBodySetKey);
        Request request_set_key = new Request.Builder()
                .url(urlSetKey)
                .post(bodySetKey)
                .header("Authorization", "Bearer " + token)
                .build();
        String dataResSetKey = "";
        try {
            Response responseSetKey = client.newCall(request_set_key).execute();
            dataResSetKey = responseSetKey.body().string();
            System.out.println(dataResSetKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //end set push key
 */