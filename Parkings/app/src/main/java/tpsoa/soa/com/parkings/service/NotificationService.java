package tpsoa.soa.com.parkings.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import tpsoa.soa.com.parkings.MainActivity;
import tpsoa.soa.com.parkings.R;

public class NotificationService extends FirebaseMessagingService {
    private static String TAG = "NotificationService";
    // Key for the string that's delivered in the action's intent.
    private static final String MESSAGE_REPLY = "REPLY";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //TODO remoteMessage recibe un mensaje del servidor de firebase con el mensaje que enviamos
        //Desde nuestra placa embebida
        createNotification(remoteMessage.getNotification().getTitle(), "Tu tiempo esta por agotarse!!");
    }

    public void createNotification(String title, String message) {

        Intent replyIntent = new Intent(this, MainActivity.class);
        replyIntent.setAction(MESSAGE_REPLY);
        PendingIntent piReply = PendingIntent.getService(this, 0, replyIntent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_add_alarm)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(piReply);

        Notification notification = notificationBuilder.getNotification();
        notification.defaults |= Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(001, notificationBuilder.build());
    }
}
