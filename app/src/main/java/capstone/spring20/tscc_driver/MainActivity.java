package capstone.spring20.tscc_driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import capstone.spring20.tscc_driver.Api.ApiController;
import capstone.spring20.tscc_driver.Api.TSCCDriverClient;
import capstone.spring20.tscc_driver.entity.Employee;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    String TAG = "MainActivity";
    Button mNotification;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);
        
        /*mNotification = findViewById(R.id.btnNotification);
        mNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new test()).commit();
            navigationView.setCheckedItem(R.id.nav_trash_route);
        }

        getJWTAndSavetoSharedPreference();
        sendFCMTokentoServer();
    }

    private void sendFCMTokentoServer() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful())
                                Log.d(TAG, "get fcm token fail");
                            String token = task.getResult().getToken();
                            Log.d(TAG, "fcm token: " + token);
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            TSCCDriverClient client = ApiController.getTsccDriverClient();
                            Call<Employee> call = client.updateFCMToken(email, token);
                            call.enqueue(new Callback<Employee>() {
                                @Override
                                public void onResponse(Call<Employee> call, Response<Employee> response) {
                                }
                                @Override
                                public void onFailure(Call<Employee> call, Throwable t) {
                                }
                            });
                        }
                    });

        }
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
                                String token = task.getResult().getToken();
                                token  = "Bearer " + token;
                                editor.putString("token", token);
                                editor.commit();
                            } else {
                                Log.d(TAG, task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Change fragment
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_trash_route:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MapsActivity()).commit();
                break;
            case R.id.nav_notification:
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new test()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
