package life.nsu.sadchat.utils.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import life.nsu.sadchat.MessagingActivity;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        Log.d("FIREBASE_RESPONSE", "Message data payload: " + remoteMessage.getData());

        String receiver = remoteMessage.getData().get("receiver");
        String user = remoteMessage.getData().get("user");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && receiver.equals(firebaseUser.getUid())) {
            if (!firebaseUser.getUid().equals(user)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendNotification(remoteMessage);
                } else {
                    sendNotificationToOldVersions(remoteMessage);
                }
            }
        }
    }


    private void sendNotification(RemoteMessage remoteMessage) {
        String icon = remoteMessage.getData().get("icon");
        String user = remoteMessage.getData().get("user");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        Log.d("USER_ID", user);

        Bundle bundle = new Bundle();
        bundle.putString("userId", user);

        int pendingNotification = Integer.parseInt(user.replaceAll("[\\D]", ""));

        Intent intent = new Intent(this, MessagingActivity.class);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, pendingNotification, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationProvider notificationProvider = new NotificationProvider(this);
        Notification.Builder builder = notificationProvider.getNotificationProvider(title, body, pendingIntent, defaultSound, icon);

        int notificationStackSize = 0;

        if (pendingNotification > 0) {
            notificationStackSize = pendingNotification;
        }

        notificationProvider.getManager().notify(notificationStackSize, builder.build());
    }

    private void sendNotificationToOldVersions(RemoteMessage remoteMessage) {
        String NOTIFICATION_CHANNEL_ID = "life.nsu.sadchat";

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        Bundle bundle = new Bundle();
        bundle.putString("userId", user);

        int pendingNotification = Integer.parseInt(user.replaceAll("[\\D]", ""));

        Intent intent = new Intent(this, MessagingActivity.class);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, pendingNotification, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationStackSize = 0;

        if (pendingNotification > 0) {
            notificationStackSize = pendingNotification;
        }

        notificationManager.notify(notificationStackSize, builder.build());
    }

}
