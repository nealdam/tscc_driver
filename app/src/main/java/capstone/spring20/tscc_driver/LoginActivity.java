package capstone.spring20.tscc_driver;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //firebase auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            //xu ly Route notification
            Intent mainIntent = getIntent();
            if (mainIntent.getStringExtra("origin") != null) {
                routeNotificationHandle();
            } else { //nếu ko có cần xử lý route thì chuyển tới Main activity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }

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
        Intent intent = new Intent(this, RouteActivity.class);
        intent.putExtra("origin", mainIntent.getStringExtra("origin"));
        intent.putExtra("destination", mainIntent.getStringExtra("destination"));
        intent.putExtra("waypoints", mainIntent.getStringExtra("waypoints"));
        intent.putExtra("locations", mainIntent.getStringExtra("locations"));
        intent.putExtra("trashAreaIdList", mainIntent.getStringExtra("trashAreaIdList"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                return;

            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "sign in canceled", Toast.LENGTH_LONG).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "no internet connection", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(this, "unknow error", Toast.LENGTH_LONG).show();
            }
        }
    }
}
