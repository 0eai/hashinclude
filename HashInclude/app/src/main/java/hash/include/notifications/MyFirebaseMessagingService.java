package hash.include.notifications;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import hash.include.R;
import hash.include.activity.EventDetailsActivity;
import hash.include.activity.HashActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String topic = remoteMessage.getData().get("topic");
        // Check if message contains a data payload.

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d("notification", remoteMessage.getData().get("topic"));
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (topic != null) {
                topic = topic.trim();
                Log.w("notification", topic);

               /* if (topic == "event") {
                    //Log.w("notification event", topic);
                    showEventNotification(remoteMessage.getData().get("id"), remoteMessage.getData().get("title"), remoteMessage.getData().get("eventType"));
                }
                if (topic == "pointer") {
                    //Log.w("notification pointer", topic);
                    showPointerNotification(remoteMessage.getData().get("id"), remoteMessage.getData().get("title"), remoteMessage.getData().get("about"));
                }
                if (topic == "session") {
                    //Log.w("notification session", topic);
                    showSessionNotification(remoteMessage.getData().get("id"), remoteMessage.getData().get("title"), remoteMessage.getData().get("text"));
                }
                if (topic == "message") {
                    //Log.w("notification message", topic);
                    showMessegeNotification(remoteMessage.getData().get("id"), remoteMessage.getData().get("text"));
                }*/
            }
        }
        showMessegeNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"));
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("notification", "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }
    }

    private void showPointerNotification(String id, String title, String des) {
        Intent intent = new Intent(this, HashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_logo_app)
                .setContentText(des)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    private void showEventNotification(String id, String title, String des) {
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra(EventDetailsActivity.EXTRA, id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_logo_app)
                .setContentText(des)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    private void showMessegeNotification( String id, String text) {
        Intent intent = new Intent(this, HashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(id)
                .setSmallIcon(R.drawable.ic_logo_app)
                .setContentText( text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    private void showSessionNotification(String id, String title, String text) {
        Intent intent = new Intent(this, HashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_logo_app)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}