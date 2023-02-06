package ash.appopen.opener;

import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
      String CHANNEL_1_ID = "1";
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        getFireBaseMessage(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody());
    }

    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    public void getFireBaseMessage(String title, String msg) {
        NotificationManagerCompat.from(this).notify(101, new NotificationCompat.Builder(this, CHANNEL_1_ID).setSmallIcon((int) R.drawable.ic_notification).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).setContentTitle(title).setContentText(msg).setPriority(1).setColor(Color.parseColor("#535ede")).setAutoCancel(true).build());
    }


}

