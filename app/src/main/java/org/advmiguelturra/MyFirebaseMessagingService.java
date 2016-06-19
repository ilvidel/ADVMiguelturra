package org.advmiguelturra;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "ADV Firebase Srv";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See showNotification method below.
        String messageBody;
        if(remoteMessage.getFrom().startsWith("/topic")) {
            messageBody = remoteMessage.getData().get("message");
            sendNotification(remoteMessage);
        } else {
            messageBody = remoteMessage.getNotification().getBody();
            Log.i("ADV", "Ignoring non-topic message");
        }

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + messageBody);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remote FCM message body received.
     */
    private void sendNotification(RemoteMessage remote) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.adv_circle);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(remote.getData().get("title"))
                .setContentText(remote.getData().get("message"))
                .setSubText(remote.getData().get("subtext"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(Notification.CATEGORY_SOCIAL) //TODO This may come in handy in the future
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d("Firebase", "notifying " + remote.getData().get("title"));
        notificationManager.notify((int) remote.getSentTime(), notificationBuilder.build());
    }
}
