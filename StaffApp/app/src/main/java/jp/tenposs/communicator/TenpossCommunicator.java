package jp.tenposs.communicator;

import android.os.AsyncTask;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import jp.tenposs.datamodel.Key;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ambient on 7/25/16.
 */
public abstract class TenpossCommunicator extends AsyncTask<Bundle, Integer, Bundle> {

    public final static String METHOD_GET = "METHOD_GET";
    public final static String METHOD_POST = "METHOD_POST";
    public final static String METHOD_POST_MULTIPART = "METHOD_POST_MULTIPART";

    public final static String WEB_ADDRESS = "https://ten-po.com/";
    public final static String DOMAIN_ADDRESS = "https://api.ten-po.com/";
    public final static String DOMAIN_POINT_ADDRESS = "https://apipoints.ten-po.com/";
    public final static String DOMAIN_STAFF_ADDRESS = "https://apistaffs.ten-po.com/";
    public final static String DOMAIN_AUTH_ADDRESS = "https://auth.ten-po.com/";


    //POST API
    public final static String API_SIGN_IN = DOMAIN_AUTH_ADDRESS + "v1/auth/login";
    public final static String API_SETPUSHKEY = DOMAIN_STAFF_ADDRESS + "set_push_key";
    public final static String API_COUPON_ACCEPT = DOMAIN_STAFF_ADDRESS + "coupon_accept";

    //GET API
    public final static String API_PROFILE = DOMAIN_AUTH_ADDRESS + "v1/profile?";
    public final static String API_ACCESS_TOKEN = DOMAIN_AUTH_ADDRESS + "v1/auth/access_token?";
    public final static String API_COUPON = DOMAIN_ADDRESS + "/coupon?";
    public final static String API_COUPON_REQUEST = DOMAIN_STAFF_ADDRESS + "list_user_request?";

    public enum CommunicationCode {

        GeneralError,
        ConnectionFailed,
        ConnectionAborted,
        ConnectionInvalidInput, //
        ConnectionMalformedURL, // invalid URL format
        ConnectionInterrupt, // connection is interrupted
        ConnectionTimedOut, // connection is timed-out
        ConnectionErrorOpenInput, // cannot send data to the server.
        ConnectionErrorOpenOutput, // cannot open output.
        ConnectionErrorReadInput, // cannot read response from the server.
        ConnectionErrorWriteOutput, // cannot save to output

        ConnectionSuccess;
    }

    public interface TenpossCommunicatorListener {
        void completed(TenpossCommunicator request, Bundle responseParams);
    }

    private TenpossCommunicatorListener mListener;
    protected Bundle mBundle;
    protected String mMethod = METHOD_GET;
    protected boolean mIncludeTokenHeader = true;
    protected OkHttpClient mClient;
    protected Call mCall;

    protected abstract boolean request(Bundle bundle);

    public TenpossCommunicator(TenpossCommunicatorListener listener) {
        this.mListener = listener;
    }


    @Override
    protected Bundle doInBackground(Bundle... params) {
        Bundle bundle = params[0];
        request(bundle);
        return bundle;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
    }

    @Override
    protected void onPostExecute(Bundle result) {
        if (this.mListener != null) {
            this.mListener.completed(this, result);
        }
    }


    protected int request(String url, OutputStream output, Bundle bundle) {
        this.mBundle = bundle;

        if (this.mIncludeTokenHeader == true) {
            HashMap<String, String> header = new HashMap<>();
            String token = bundle.getString(Key.TokenKey);
            header.put("Authorization", "Bearer " + token);
            bundle.putSerializable(Key.RequestHeader, header);
        }

        int connectionTimeout = mBundle.getInt(Key.RequestConnectionTimeout);
        int readTimeout = mBundle.getInt(Key.RequestConnectionTimeout);
        int writeTimeout = mBundle.getInt(Key.RequestConnectionTimeout);

        if (connectionTimeout == 0)
            connectionTimeout = 60;

        if (readTimeout == 0)
            readTimeout = 60;

        if (writeTimeout == 0)
            writeTimeout = 60;

        String strTemp = url.toLowerCase(Locale.US);

        if (strTemp.contains("http://") == false && strTemp.contains("https://") == false)
            url = "http://" + url;

        System.out.println(url);
        try {

            Request request = null;
            Request.Builder requestBuilder = null;
            RequestBody requestBody = null;
            Response response = null;

            requestBuilder = new Request.Builder().url(url);
            if (this.mBundle.containsKey(Key.RequestHeader)) {
                HashMap<String, String> params = (HashMap<String, String>) this.mBundle.get(Key.RequestFormData);
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    requestBuilder.header(key, params.get(key));
                }
            }

            if (this.mMethod == METHOD_POST) {
                FormBody.Builder builder = new FormBody.Builder();
                HashMap<String, String> params = (HashMap<String, String>) this.mBundle.get(Key.RequestFormData);
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    builder.add(key, params.get(key));
                }
                requestBody = builder.build();
                requestBuilder.post(requestBody);
            } else if (this.mMethod == METHOD_POST_MULTIPART) {
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                HashMap<String, String> params = (HashMap<String, String>) this.mBundle.get(Key.RequestFormData);
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    String value = params.get(key);
                    if (key.compareTo("avatar") == 0) {
                        //File
                        File file = new File(value);
//                        String fileName = file.getName();
                        String fileName = "avatar.png";
                        builder.addFormDataPart(key, fileName,
                                RequestBody.create(MediaType.parse("image/png"),
                                        file));
                    } else {
                        builder.addFormDataPart(key, value);
                    }
                }
                requestBody = builder.build();
                requestBuilder.post(requestBody);
            }

            this.mClient = new OkHttpClient().newBuilder()
                    .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                    .readTimeout(readTimeout, TimeUnit.SECONDS).build();

            request = requestBuilder.build();
            try {
                mCall = this.mClient.newCall(request);
                response = this.mCall.execute();
                String str = response.body().string();
                byte[] data = str.getBytes();
                output.write(data, 0, data.length);
                return CommunicationCode.ConnectionSuccess.ordinal();

            } catch (SocketTimeoutException timeout) {
                mBundle.putInt(Key.ResponseResult, CommunicationCode.ConnectionTimedOut.ordinal());
                mBundle.putString(Key.ResponseMessage, timeout.getLocalizedMessage());
                return CommunicationCode.ConnectionTimedOut.ordinal();

            } catch (IOException e) {
                e.printStackTrace();
                mBundle.putInt(Key.ResponseResult, CommunicationCode.GeneralError.ordinal());
                mBundle.putString(Key.ResponseMessage, e.getLocalizedMessage());
                return CommunicationCode.GeneralError.ordinal();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mBundle.putInt(Key.ResponseResult, CommunicationCode.GeneralError.ordinal());
            mBundle.putString(Key.ResponseMessage, ex.getLocalizedMessage());
            return CommunicationCode.GeneralError.ordinal();
        }
    }

    public boolean cancelRequest(boolean mayInterruptIfRunning) {
        try {
            mCall.cancel();
        } catch (Exception ignored) {
        }
        return this.cancel(mayInterruptIfRunning);
    }
}
