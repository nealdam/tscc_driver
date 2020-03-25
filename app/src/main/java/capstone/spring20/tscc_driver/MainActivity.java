package capstone.spring20.tscc_driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //xu ly route notification
        routeNotificationHandle();

        //log lại fcm token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful())
                            Log.d(TAG, "get fcm token fail");
                        String token = task.getResult().getToken();
                        Log.d(TAG, "fcm token: " + token);
                    }
                });
    }

    private void routeNotificationHandle() {
        Intent mainIntent = getIntent();
        if (mainIntent.getStringExtra("origin") != null) {
            Intent intent = new Intent(this, RouteActivity.class);
            intent.putExtra("origin", mainIntent.getStringExtra("origin"));
            intent.putExtra("destination", mainIntent.getStringExtra("destination"));
            intent.putExtra("waypoints", mainIntent.getStringExtra("waypoints"));
            intent.putExtra("locations", mainIntent.getStringExtra("locations"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
