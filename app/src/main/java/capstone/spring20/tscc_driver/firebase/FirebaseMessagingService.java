package capstone.spring20.tscc_driver.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import capstone.spring20.tscc_driver.RouteActivity;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String TAG = "FirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: ");
        //nếu message có data thì đưa data qua RouteActivity xử lý
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            Intent intent = new Intent(this, RouteActivity.class);
            intent.putExtra("origin", data.get("origin"));
            intent.putExtra("destination", data.get("destination"));
            intent.putExtra("waypoints", data.get("waypoints"));
            intent.putExtra("locations", data.get("locations"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "onNewToken: " + s);
    }
}
