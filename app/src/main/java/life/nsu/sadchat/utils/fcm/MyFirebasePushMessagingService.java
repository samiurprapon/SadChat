package life.nsu.sadchat.utils.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import life.nsu.sadchat.MessagingActivity;

public class MyFirebasePushMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(task -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                updateToken(task.getResult().getToken());
            }
        });

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String send = remoteMessage.getData().get("send");
        String sender = remoteMessage.getData().get("sender");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && send.equals(firebaseUser.getUid())) {
            if (!firebaseUser.getUid().equals(sender)) {
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

        Bundle bundle = new Bundle();
        bundle.putString("id", user);

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

        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        Bundle bundle = new Bundle();
        bundle.putString("id", user);


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

    private void updateToken(String refreshToken) {
        FirebaseDatabase.getInstance().getReference("tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new Token(refreshToken));
    }
}
