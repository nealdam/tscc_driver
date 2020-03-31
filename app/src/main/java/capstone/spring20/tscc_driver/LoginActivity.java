package capstone.spring20.tscc_driver;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import capstone.spring20.tscc_driver.entity.RouteNotification;
import capstone.spring20.tscc_driver.util.MyDatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //tạo sqlite
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        Log.d(TAG, db.getDatabaseName());
        //firebase auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            //save Route notification vo db
            if (getIntent().getStringExtra("origin") != null)
                routeNotificationHandle();
            // chuyển tới Main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();


        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    //new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build()
                            ))
                            .build(),
                    1);
        }
    }

    private void routeNotificationHandle() {
        Intent mainIntent = getIntent();
        String origin = mainIntent.getStringExtra("origin");
        String destination = mainIntent.getStringExtra("destination");
        String waypoints = mainIntent.getStringExtra("waypoints");
        String locations = mainIntent.getStringExtra("locations");
        String trashAreaIdList = mainIntent.getStringExtra("trashAreaIdList");

        RouteNotification route = new RouteNotification(origin, destination, waypoints, locations, trashAreaIdList);

        MyDatabaseHelper db = new MyDatabaseHelper(this);
        db.addRouteNotification(route);

        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(1, resultCode, data);
        if (requestCode == 1) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //go to detail info activity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "sign in canceled", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(this, "unknow error", Toast.LENGTH_LONG).show();
            }
        }
    }
}
