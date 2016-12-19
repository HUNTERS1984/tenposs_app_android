package jp.tenposs.staffapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/25/16.
 */
public class StaffMessagingService extends FirebaseMessagingService {
    private static final String TAG = "StaffMessagingService";
    public static final String INTENT_FILTER = "INTENT_FILTER";
    public static final String INTENT_DATA = "INTENT_DATA";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage != null && remoteMessage.getData() != null) {
            Utils.log(TAG, remoteMessage.getData().toString());
            showNotification(remoteMessage.getData());
        }
    }

    public static class CouponRequestObject {
        public String subtitle;
        public String app_user_id;
        public String msgcnt;
        public String image_url;
        public String id;
        public String code;
        public String type;
        public String title;
        public String vibrate;
        public String message;
        public String staff_id;
        public String coupon_id;
        public String tickerText;

        public CouponRequestObject(HashMap<String, String> map) {
            try {
                Field[] fields = this.getClass().getFields();
                for (Field field : fields) {
                    try {
                        if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                            continue;
                        }
                        String name = URLEncoder.encode(field.getName(), "UTF-8");
                        String value = map.get(name);
                        if (value != null) {
                            field.set(this, value);
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception ex) {
            }
        }
    }
    //{
    // subtitle=,
    // app_user_id=1,
    // msgcnt=1,
    // image_url=,
    // id=0,
    // code=234234234,
    // type=coupon_use,
    // title=fawfwefwfwef,
    // vibrate=1,
    // message=,
    // staff_id=11,
    // coupon_id=189,
    // tickerText=
    // }

    private void showNotification(Map<String, String> data) {
        String type = null;
        try {
            type = data.get("type");
        } catch (Exception ignored) {
            Utils.log(ignored);
        }
        if (type != null && type.compareTo("coupon_use") == 0) {
            if (MainApplication.isInterestingActivityVisible()) {
                try {
                    //show coupon if application is running or show Notification
                    Intent intent = new Intent(INTENT_FILTER);
                    HashMap<String, String> map = new HashMap<>(data);
                    intent.putExtra(INTENT_DATA, map);
                    sendBroadcast(intent);
                } catch (Exception ignored) {
                    Utils.log(ignored);
                }
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                HashMap<String, String> map = new HashMap<>(data);
                intent.putExtra(INTENT_DATA, map);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setContentTitle("Staff App")
                        .setContentText(getString(R.string.msg_have_use_coupon_request))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent);

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                manager.notify(0, builder.build());
            }
        }
    }
}
