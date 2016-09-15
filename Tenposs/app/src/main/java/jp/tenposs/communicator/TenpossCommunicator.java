package jp.tenposs.communicator;

import android.os.AsyncTask;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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

    public final static String API_ADDRESS_DEV = "http://dev.tenposs.jp/api";
    //public final static String API_ADDRESS = "http://ec2-54-204-210-230.compute-1.amazonaws.com/tenposs/api/public/index.php/api/v1";
    //public final static String API_ADDRESS = "http://54.153.78.127/api/v1";

    //public final static String BASE_ADDRESS = "http://ec2-54-204-210-230.compute-1.amazonaws.com/";
    public final static String BASE_ADDRESS = "https://api.ten-po.com/";

    private final static String API_ADDRESS = BASE_ADDRESS + "api/v1";

    public final static String API_SIGN_IN = API_ADDRESS + "/signin?";
    public final static String API_SOCIAL_SIGN_IN = API_ADDRESS + "/social_login?";
    public final static String API_SIGN_UP = API_ADDRESS + "/signup?";
    public final static String API_SIGN_OUT = API_ADDRESS + "/signout?";
    public final static String API_PROFILE = API_ADDRESS + "/profile?";
    public final static String API_UPDATE_PROFILE = API_ADDRESS + "/update_profile?";

    public final static String API_TOP = API_ADDRESS + "/top?";
    public final static String API_MENU = API_ADDRESS + "/menu?";
    public final static String API_APPINFO = API_ADDRESS + "/appinfo?";
    public final static String API_ITEMS = API_ADDRESS + "/items?";
    public final static String API_ITEMS_DETAIL = API_ADDRESS + "/items/detail?";
    public final static String API_ITEMS_RELATE = API_ADDRESS + "/items/related?";
    public final static String API_PHOTO_CAT = API_ADDRESS + "/photo_cat?";
    public final static String API_PHOTO = API_ADDRESS + "/photo?";
    public final static String API_NEWS = API_ADDRESS + "/news?";
    public final static String API_NEWS_DETAIL = API_ADDRESS + "/news/detail?";
    public final static String API_RESERVE = API_ADDRESS + "/reserve?";
    public final static String API_COUPON = API_ADDRESS + "/coupon?";
    public final static String API_COUPON_DETAIL = API_ADDRESS + "/coupon/detail?";
    public final static String API_APPUSER = API_ADDRESS + "/appuser?";
    public final static String API_SETPUSHKEY = API_ADDRESS + "/setpushkey?";
    public final static String API_STAFF_CATEGORY = API_ADDRESS + "/staff_categories?";
    public final static String API_STAFFS = API_ADDRESS + "/staffs?";

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
    private Bundle mBundle;
    protected String mMethod = "GET";
    private OkHttpClient mClient;
    private Call mCall;

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

        int connectionTimeout = mBundle.getInt(Key.RequestConnectionTimeout);
        int readTimeout = mBundle.getInt(Key.RequestConnectionTimeout);
        int writeTimeout = mBundle.getInt(Key.RequestConnectionTimeout);

        if (connectionTimeout == 0)
            connectionTimeout = 10;

        if (readTimeout == 0)
            readTimeout = 30;

        if (writeTimeout == 0)
            writeTimeout = 10;

        String strTemp = url.toLowerCase(Locale.US);

        if (strTemp.contains("http://") == false && strTemp.contains("https://") == false)
            url = "http://" + url;

        try {

            Request request = null;
            Request.Builder requestBuilder = null;
            RequestBody requestBody = null;
            Response response = null;

            requestBuilder = new Request.Builder().url(url);
            if (this.mMethod == "POST") {
                FormBody.Builder builder = new FormBody.Builder();
                HashMap<String, String> params = (HashMap<String, String>) this.mBundle.get(Key.RequestFormData);
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    builder.add(key, params.get(key));
                }
                requestBody = builder.build();
                requestBuilder.post(requestBody);
            } else if (this.mMethod == "POST_MULTIPART") {
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                HashMap<String, String> params = (HashMap<String, String>) this.mBundle.get(Key.RequestFormData);
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    String value = params.get(key);
                    if (key.compareTo("avatar") == 0) {
                        //File
                        File file = new File(value);
                        String fileName = file.getName();
                        builder.addFormDataPart("image", fileName,
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
        } catch (Exception e) {
            // TODO: handle exception
        }
        return this.cancel(mayInterruptIfRunning);
    }
}
