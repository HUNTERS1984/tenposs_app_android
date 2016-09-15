package jp.tenposs.tenposs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by ambient on 8/25/16.
 */
public class TenpossMessagingService extends FirebaseMessagingService {
    private static final String TAG = "TenpossMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("demo-data: ", remoteMessage.getData().toString());

        showNotification(remoteMessage.getData().get("message"));
    }

    private void showNotification(String message) {
        Log.i("demo-message: ", message);
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("FCM Test")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0, builder.build());
    }
}
