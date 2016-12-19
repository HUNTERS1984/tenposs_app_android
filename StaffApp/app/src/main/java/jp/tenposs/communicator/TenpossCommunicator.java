package jp.tenposs.communicator;

import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import jp.tenposs.datamodel.Key;
import jp.tenposs.staffapp.BuildConfig;
import jp.tenposs.utils.Utils;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by ambient on 7/25/16.
 */
public abstract class TenpossCommunicator extends AsyncTask<Bundle, Integer, Bundle> {

    public final static int AUTH_NONE = 0;
    public final static int AUTH_BASIC = 1;
    public final static int AUTH_TOKEN = 2;

    public final static String METHOD_GET = "METHOD_GET";
    public final static String METHOD_POST = "METHOD_POST";
    public final static String METHOD_POST_MULTIPART = "METHOD_POST_MULTIPART";

    public final static String WEB_ADDRESS = "https://ten-po.com/";

    public final static String DOMAIN_ADDRESS = "https://api.ten-po.com/";
    public final static String DOMAIN_POINT_ADDRESS = "https://apipoints.ten-po.com/";
    public final static String DOMAIN_STAFF_ADDRESS = "https://apistaffs.ten-po.com/";
    public final static String DOMAIN_AUTH_ADDRESS = "https://auth.ten-po.com/";
    public final static String DOMAIN_NOTIFICATION_ADDRESS = "https://apinotification.ten-po.com/";

    //Authentication
    public final static String API_SIGN_UP = DOMAIN_ADDRESS + "api/v2/signup?";//POST_AUTH_TOKEN
    public final static String API_SIGN_IN = DOMAIN_AUTH_ADDRESS + "v1/auth/login?";//POST_AUTH_BASIC
    public final static String API_SIGN_OUT = DOMAIN_AUTH_ADDRESS + "v1/auth/signout";//POST_AUTH_TOKEN

    public final static String API_SOCIAL_SIGN_IN = DOMAIN_ADDRESS + "api/v2/signup_social";//POST_AUTH_BASIC
//    public final static String API_ACCESS_TOKEN = DOMAIN_AUTH_ADDRESS + "v1/auth/access_token?";

    //User App
    public final static String API_PROFILE = DOMAIN_ADDRESS + "api/v2/profile?";
    public final static String API_UPDATE_PROFILE = DOMAIN_ADDRESS + "api/v2/update_profile";
    public final static String API_SOCIAL_PROFILE = DOMAIN_ADDRESS + "api/v2/social_profile";
    public final static String API_SOCIAL_PROFILE_CANCEL = DOMAIN_ADDRESS + "api/v2/social_profile_cancel";

    public final static String API_COMPLETE_PROFILE = DOMAIN_ADDRESS + "api/v2/update_profile_social_signup";

    public final static String API_TOP = DOMAIN_ADDRESS + "api/v1/top?";
    public final static String API_MENU = DOMAIN_ADDRESS + "api/v1/menu?";
    public final static String API_APP_INFO = DOMAIN_ADDRESS + "api/v1/appinfo?";
    public final static String API_ITEMS = DOMAIN_ADDRESS + "api/v1/items?";
    public final static String API_ITEMS_DETAIL = DOMAIN_ADDRESS + "api/v1/item_detail?";
    public final static String API_ITEMS_RELATED = DOMAIN_ADDRESS + "api/v1/item_related?";
    public final static String API_PHOTO_CAT = DOMAIN_ADDRESS + "api/v1/photo_cat?";
    public final static String API_PHOTO = DOMAIN_ADDRESS + "api/v1/photo?";
    public final static String API_NEWS = DOMAIN_ADDRESS + "api/v1/news?";
    public final static String API_NEWS_CATEGORY = DOMAIN_ADDRESS + "api/v1/news_cat?";
    public final static String API_RESERVE = DOMAIN_ADDRESS + "api/v1/reserve?";
    public final static String API_COUPON = DOMAIN_ADDRESS + "api/v1/coupon?";
    public final static String API_COUPON_DETAIL_ANOMYMOUS = DOMAIN_ADDRESS + "api/v2/coupon_detail";
    public final static String API_COUPON_DETAIL = DOMAIN_ADDRESS + "api/v2/coupon_detail_login";
    public final static String API_STAFF_CATEGORY = DOMAIN_ADDRESS + "api/v1/staff_categories?";
    public final static String API_STAFFS = DOMAIN_ADDRESS + "api/v1/staffs?";

    //Staff App
    public final static String API_STAFF_COUPON_ACCEPT = DOMAIN_STAFF_ADDRESS + "coupon_accept?";
    public final static String API_STAFF_COUPON_REQUEST = DOMAIN_STAFF_ADDRESS + "list_user_request?";

    //Points
    public final static String API_GET_POINTS_OF_USER = DOMAIN_POINT_ADDRESS + "point?";
    public final static String API_SET_REQUEST_POINT_OF_USER = DOMAIN_POINT_ADDRESS + "point/request/user?";
    public final static String API_REQUEST_POIT_OF_CLIENT = DOMAIN_POINT_ADDRESS + "point/request/client?";
    public final static String API_APPROVE_REQUEST_POINT_OF_USER = DOMAIN_POINT_ADDRESS + "point/approve/request/user?";
    public final static String API_GET_LIST_REQUEST_POINT_OF_USER = DOMAIN_POINT_ADDRESS + "point/request/list?";
    public final static String API_GET_LIST_USE_POINT_OF_USER = DOMAIN_POINT_ADDRESS + "point/use/list?";
    public final static String API_SET_REQUEST_USE_POINT_OF_USER = DOMAIN_POINT_ADDRESS + "point/use/user?";
    public final static String API_APPROVE_REQUEST_USE_POINT_OF_USER = DOMAIN_POINT_ADDRESS + "point/approve/use/user?";

    //Notifications
    public final static String API_SET_PUSH_KEY_FOR_USER = DOMAIN_NOTIFICATION_ADDRESS + "v1/user/set_push_key?";//POST_AUTH_TOKEN
    public final static String API_SET_PUSH_KEY_FOR_STAFF = DOMAIN_NOTIFICATION_ADDRESS + "v1/staff/set_push_key?";
    public final static String API_GET_PUSH_SETTING_FOR_USER = DOMAIN_NOTIFICATION_ADDRESS + "v1/user/get_push_setting?";// /{app_id}";
    public final static String API_SET_PUSH_SETTING_FOR_USER = DOMAIN_NOTIFICATION_ADDRESS + "v1/user/set_push_setting?";
    public final static String API_SET_CONFIGURE_FOR_APP = DOMAIN_NOTIFICATION_ADDRESS + "v1/configure_notification?";
    public final static String API_NOTIFICATION_TO_USER = DOMAIN_NOTIFICATION_ADDRESS + "v1/user/notification?";

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
    protected String mMethod = METHOD_GET;
    protected int mAuthorizationMode = AUTH_NONE;
    private OkHttpClient mClient;
    private Call mCall;

    protected String Tag = "TenpossCommunicator : " + this.getClass().getSimpleName();

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

//        if (this.mAuthorizationMode == AUTH_TOKEN) {
//            HashMap<String, String> header = new HashMap<>();
//            String token = bundle.getString(Key.TokenKey);
//            header.put("Authorization", "Bearer " + token);
//            bundle.putSerializable(Key.RequestHeader, header);
//        }


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

        Utils.log(Tag, url);
        try {

            Request request;
            Request.Builder requestBuilder;
            RequestBody requestBody;
            Response response;

            requestBuilder = new Request.Builder().url(url);
//            if (this.mBundle.containsKey(Key.RequestHeader)) {
//                HashMap<String, String> params = (HashMap<String, String>) this.mBundle.get(Key.RequestHeader);
//                Set<String> keys = params.keySet();
//                for (String key : keys) {
//                    requestBuilder.header(key, params.get(key));
//                }
//            }

            if (this.mMethod == METHOD_POST) {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String jsonContent = "";
                try {
                    HashMap<String, String> params = (HashMap<String, String>) this.mBundle.getSerializable(Key.RequestFormData);
                    if (params != null) {
                        jsonContent = new JSONObject(params).toString();
                        Utils.log(Tag, jsonContent);
                    }
                } catch (Exception ignored) {
                    Utils.log(ignored);
                }
                requestBody = RequestBody.create(JSON, jsonContent);
                requestBuilder.post(requestBody);
            } else if (this.mMethod == METHOD_POST_MULTIPART) {
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                HashMap<String, String> params = (HashMap<String, String>) this.mBundle.getSerializable(Key.RequestFormData);
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    String value = params.get(key);
                    if (key.compareTo("avatar") == 0) {
                        File file = new File(value);
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

            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.connectTimeout(connectionTimeout, TimeUnit.SECONDS);
            builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
            builder.readTimeout(readTimeout, TimeUnit.SECONDS);
            if (this.mAuthorizationMode == AUTH_BASIC) {
                builder.authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        String credential = Credentials.basic("tenposs", "Tenposs@123");
                        return response.request().newBuilder().header("Authorization", credential).build();
                    }
                });
            } else if (this.mAuthorizationMode == AUTH_TOKEN) {
                String token = mBundle.getString(Key.TokenKey);
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            this.mClient = builder.build();

            request = requestBuilder.build();
            try {
                mCall = this.mClient.newCall(request);
                response = this.mCall.execute();
                byte[] data;
                if (BuildConfig.DEBUG) {
                    String str = response.body().string();
                    Utils.log(Tag, str);
                    data = str.getBytes();
                } else {
                    data = response.body().bytes();
                }
                if (data != null) {
                    output.write(data, 0, data.length);
                }
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
