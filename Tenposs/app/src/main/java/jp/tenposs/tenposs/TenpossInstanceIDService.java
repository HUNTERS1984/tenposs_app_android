package jp.tenposs.tenposs;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import jp.tenposs.communicator.SetPushKeyCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by ambient on 9/15/16.
 */
public class TenpossInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();

        registerToken(token);
    }

    private void registerToken(String token) {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .build();
        Log.i("demo-register", token);
        //goi api set token

        Bundle params = new Bundle();
        SetPushKeyCommunicator communicator = new SetPushKeyCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        //TODO: chua lam xong
                    }
                });
        communicator.execute(params);
    }
}

