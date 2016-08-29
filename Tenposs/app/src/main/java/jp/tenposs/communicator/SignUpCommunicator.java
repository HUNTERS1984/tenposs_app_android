package jp.tenposs.communicator;

import android.os.Bundle;

/**
 * Created by ambient on 7/26/16.
 */
public class SignUpCommunicator extends TenpossCommunicator {

    public SignUpCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {

        String strUrl = API_SIGNUP;

        strUrl += "";

/*
        strUrl += requestBuilder(bundle);
        byte[] dataRequest = null;
        int result = CommunicationCode.ret_code_success.ordinal();
        OutputStream output = null;

        try {
            output = new ByteArrayOutputStream();
        } catch (Exception e) {
            bundle.putString(Key.ResponseMessage, e.getMessage());
            bundle.putInt(Key.ResponseResult, CommunicationCode.GeneralError.ordinal());
            return CommunicationCode.ConnectionOpenOutput.ordinal();
        }
        result = request(strUrl, output, dataRequest, bundle);

        // neu connection thanh cong thi tinh den du lieu cua service tra ve
        if (result == CommunicationCode.ConnectionSuccess.ordinal()) {
            // TODO: read Response
            String strResponse = output.toString();
            VideoObjectResult data = (VideoObjectResult) CommonObject.fromJSONString(
                    strResponse
                    , VideoObjectResult.class
                    , CommonObject.buildCustomDeserializer(VideoObjectResult.customTypeJsonDeserializer(), VideoObjectResult.customElementName()));
            VideoContainer requestData = (VideoContainer) bundle.get(VKey.KeyRequestObject);

            if (data.err == 0 && data.videos != null) {
                if (requestData != null) {
                    for (VideoObject video : data.videos) {
                        requestData.addVideo(video);
                        requestData.increaseVideoIndex(1);
                    }
                }
            }

            bundle.putInt(VKey.KeyResponseResult, data.err);
            bundle.put(VKey.KeyResponseObject, data);

        }

        try {
            if (output != null) {
                output.flush();
                output.close();
            }
        } catch (Exception e) {
            bundle.putString(VKey.KeyResponseData, e.getMessage());
            bundle.putInt(VKey.KeyResponseResult, CommunicationCode.GeneralError.ordinal());
            return CommunicationCode.ConnectionWriteOutput.ordinal();
        }*/

        return true;
    }

}
