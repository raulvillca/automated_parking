package soa.mvp.parkingfinder.service;

import android.app.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import soa.mvp.parkingfinder.MainActivity;
import soa.mvp.parkingfinder.R;

/**
 * Created by raulvillca on 16/5/17.
 */

public class NotificationService extends FirebaseMessagingService {
    private static String TAG = "NotificationService";
    // Key for the string that's delivered in the action's intent.
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    private static final String MESSAGE_REPLY = "REPLY";
    private RemoteInput remoteInput;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onCreate() {
        super.onCreate();
        String replyLabel = "Mensaje";
        remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e(TAG, "Aca se gestiona las notificaciones para el cliente");
        //Log.e(TAG, remoteMessage.getNotification().getTitle());
        Log.e(TAG, new Gson().toJson(remoteMessage.getNotification()));
        Log.e(TAG, remoteMessage.getData().get("message"));

        createNotification("My Parking", remoteMessage.getData().get("message"));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public void createNotification(String title, String message) {

        Intent replyIntent = new Intent(this, MainActivity.class);
        replyIntent.setAction(MESSAGE_REPLY);
        PendingIntent piReply = PendingIntent.getService(this, 0, replyIntent, 0);

        // Create the reply action and add the remote input.
        Notification.Action action = new Notification.Action.Builder(R.drawable.ic_speak, title, null)
                        .addRemoteInput(remoteInput)
                        .build();

        // Build the notification and add the action.
        Notification newMessageNotification =
                new Notification.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_add_alarm)
                        .setContentTitle(title)
                        .setContentText(message)
                        .addAction(action)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .build();

        // Issue the notification.
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, newMessageNotification);
    }
}
