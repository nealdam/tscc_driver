package capstone.spring20.tscc_driver;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.Arrays;
import java.util.Objects;

import capstone.spring20.tscc_driver.entity.RouteNotification;
import capstone.spring20.tscc_driver.util.MyDatabaseHelper;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        askPermisson();


        //tạo sqlite
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        Log.d(TAG, db.getDatabaseName());
        //firebase auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            //save jwt vao sharedpreference
            getJWTAndSavetoSharedPreference();
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

    @AfterPermissionGranted(123)
    private void askPermisson() {
        //ask permisson
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
        } else {
            EasyPermissions.requestPermissions(this, "Bạn cần có vị trí để sử dụng ứng dụng.", 123, perms);
        }
    }

    private void routeNotificationHandle() {
        Intent mainIntent = getIntent();
        String origin = mainIntent.getStringExtra("origin");
        String destination = mainIntent.getStringExtra("destination");
        String waypoints = mainIntent.getStringExtra("waypoints");
        String locations = mainIntent.getStringExtra("locations");
        String trashAreaIdList = mainIntent.getStringExtra("trashAreaIdList");
        String collectJobId = mainIntent.getStringExtra("collectJobId");

        RouteNotification route = new RouteNotification(origin, destination, waypoints, locations, trashAreaIdList, collectJobId);

        MyDatabaseHelper db = new MyDatabaseHelper(this);
        db.deactiveAllRoute();
        db.addRouteNotification(route);

        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(1, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

        }
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

    private void getJWTAndSavetoSharedPreference() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            SharedPreferences sharedPreferences = this.getSharedPreferences("JWT", MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String jwtToken = Objects.requireNonNull(task.getResult()).getToken();
                                jwtToken = "Bearer " + jwtToken;
                                editor.putString("token", jwtToken);
                                editor.apply();
                            }
                        }
                    });
        }
    }


}
