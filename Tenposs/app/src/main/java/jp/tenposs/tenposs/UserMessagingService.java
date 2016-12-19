package jp.tenposs.tenposs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.NotificationInfo;

/**
 * Created by ambient on 8/25/16.
 */
public class UserMessagingService extends FirebaseMessagingService {
    private static final String TAG = "UserMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //subtitle=, msgcnt=1, image_url=http://ten-po.com/uploads/a750b885277b5e322d4aa159f1b15796.jpg, id=197, type=coupon, title=coupon_name_05_, vibrate=1, message=, tickerText=
        String msg;
        try {
            msg = remoteMessage.getData().get("message");
            if (msg != null) {
                NotificationInfo notificationInfo = (NotificationInfo) CommonObject.fromJSONString(msg, NotificationInfo.class, null);
                Log.i(TAG, msg);
                showNotification(notificationInfo);
            }
        } catch (Exception ignored) {

        }
    }

    private void showNotification(NotificationInfo notificationInfo) {
        if (/*applicationInForeground*/ true) {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle(notificationInfo.title)
                    .setContentText(notificationInfo.message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            manager.notify(0, builder.build());
        } else {
            if (notificationInfo.isNewsNotification()) {

            } else if (notificationInfo.isCouponNotification()) {
            }
        }
    }
}