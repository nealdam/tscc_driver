package capstone.spring20.tscc_driver.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import capstone.spring20.tscc_driver.Api.ApiController;
import capstone.spring20.tscc_driver.Api.TSCCDriverClient;
import capstone.spring20.tscc_driver.MainActivity;
import capstone.spring20.tscc_driver.R;
import capstone.spring20.tscc_driver.entity.Employee;
import capstone.spring20.tscc_driver.entity.RouteNotification;
import capstone.spring20.tscc_driver.util.MyDatabaseHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String TAG = "FirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: ");
        //nếu message có data thì save vo db
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            String origin = data.get("origin");
            String destination = data.get("destination");
            String waypoints = data.get("waypoints");
            String locations = data.get("locations");
            String trashAreaIdList = data.get("trashAreaIdList");
            String collectJobId = data.get("collectJobId");

            RouteNotification route = new RouteNotification(origin, destination, waypoints, locations, trashAreaIdList, collectJobId);
            //save vao db
            MyDatabaseHelper db = new MyDatabaseHelper(this);
            db.addRouteNotification(route);
            //show noti
            sendNotification("Tuyến đường mới", "Thông tin về tuyến đường mới");
        }
    }


    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "onNewToken: " + s);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            TSCCDriverClient client = ApiController.getTsccDriverClient();
            SharedPreferences sharedPreferences = this.getSharedPreferences("JWT", MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");
            Call<Employee> call = client.updateFCMToken(token, email, s);
            call.enqueue(new Callback<Employee>() {
                @Override
                public void onResponse(Call<Employee> call, Response<Employee> response) {
                }
                @Override
                public void onFailure(Call<Employee> call, Throwable t) {
                }
            });
        }

    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("aa", "test");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
