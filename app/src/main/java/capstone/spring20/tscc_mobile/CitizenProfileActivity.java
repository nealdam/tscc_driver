package capstone.spring20.tscc_mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import capstone.spring20.tscc_mobile.Api.ApiController;
import capstone.spring20.tscc_mobile.Api.TSCCClient;
import capstone.spring20.tscc_mobile.Entity.Citizen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CitizenProfileActivity extends AppCompatActivity {

    TextView mName, mRole, mEmail, mPhoneNumber;
    ImageView mAvatar;
    Button mLogout, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Tài khoản");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        setupBasic();
        //call api
        TSCCClient client = ApiController.getTsccClient();
        Call<Citizen> call = client.getCitizenByEmail(email);
        call.enqueue(new Callback<Citizen>() {
            @Override
            public void onResponse(Call<Citizen> call, Response<Citizen> response) {
                //check id có tồn tại
                if (response.isSuccessful() && response.body() != null && response.code() == 200) {
                    Citizen citizen = response.body();
                    mName.setText(citizen.getName());
                    mEmail.setText(citizen.getEmail());
                    mPhoneNumber.setText(citizen.getPhone());
                }
            }
            @Override
            public void onFailure(Call<Citizen> call, Throwable t) {
            }
        });

    }

    private void setupBasic() {
        mName = findViewById(R.id.txtName);
        mEmail = findViewById(R.id.txtEmail);
        mPhoneNumber = findViewById(R.id.txtPhoneNumber);
        mAvatar = findViewById(R.id.ivAvatar);
        mLogout = findViewById(R.id.btnlogout);

        mAvatar.setImageDrawable(getResources().getDrawable(R.drawable.user));

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(CitizenProfileActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(CitizenProfileActivity.this, WelcomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });
    }

}
