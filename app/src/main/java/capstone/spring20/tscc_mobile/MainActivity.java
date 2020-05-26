package capstone.spring20.tscc_mobile;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    String TAG = "MainActivity";
    Button mCamera;
    FloatingActionButton mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCamera = findViewById(R.id.btnCamera);
        mProfile = findViewById(R.id.btnProfile);

        setupBasic ();

        getJWTAndSavetoSharedPreference();

//        saveCurrentLocation();
    }
/*
    private void saveCurrentLocation() {
        SharedPreferences shared = this.getSharedPreferences("Location", MODE_PRIVATE);
        final SharedPreferences.Editor editor = shared.edit();

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                                                                         @Override
                                                                         public void onComplete(@NonNull Task<Location> task) {
                                                                             Location location = task.getResult();
                                                                             if (location != null) {
                                                                                 editor.putFloat("latitude", (float) location.getLatitude());
                                                                                 editor.putFloat("longitude", (float) location.getLongitude());
                                                                                 editor.apply();
                                                                             }
                                                                         }
                                                                     }
        );
    }
*/
    @AfterPermissionGranted(123)
    private void setupBasic() {
        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] perms = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
                if (EasyPermissions.hasPermissions(MainActivity.this, perms)) {
                    Intent intent = new Intent( MainActivity.this, RequestActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    EasyPermissions.requestPermissions(MainActivity.this, "Bạn cần cấp quyền sử dụng máy ảnh.", 123, perms);
                }
            }
        });
        
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CitizenProfileActivity.class);
                startActivity(intent);
            }
        });
        /*mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });*/
    }

    private void getJWTAndSavetoSharedPreference() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = this.getSharedPreferences("JWT", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (user != null) {
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String token = Objects.requireNonNull(task.getResult()).getToken();
                                token = "Bearer " + token;
                                editor.putString("token", token);
                                editor.apply();
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

        }
        //camera
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");

            Intent intent = new Intent(this, RequestActivity.class);
            intent.putExtra("image", image);
            startActivity(intent);
            return;

        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, MainActivity.this);
    }
}
