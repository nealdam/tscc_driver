package capstone.spring20.tscc_driver.firebase;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String TAG = "FirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}
