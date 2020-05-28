package capstone.spring20.tscc_driver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import capstone.spring20.tscc_driver.Api.ApiController;
import capstone.spring20.tscc_driver.Api.TSCCDriverClient;
import capstone.spring20.tscc_driver.entity.ApiResponse;
import capstone.spring20.tscc_driver.entity.RouteNotification;
import capstone.spring20.tscc_driver.util.MyDatabaseHelper;
import capstone.spring20.tscc_driver.util.ParseUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelRouteActivity extends AppCompatActivity {

    boolean isReasonChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_route);

        getSupportActionBar().setTitle("Huỷ chuyến thu gom");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RadioGroup rbGroup = findViewById(R.id.rbGroup);
        rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isReasonChecked = true;
            }
        });

        RadioButton rbOther = findViewById(R.id.rbOther);
        final EditText mReason = findViewById(R.id.iReason);
        rbOther.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mReason.setEnabled(true);
                } else {
                    mReason.setEnabled(false);
                }
            }
        });

        SharedPreferences sharedPreferences = this.getSharedPreferences("JWT", MODE_PRIVATE);
        final String jwtToken = sharedPreferences.getString("token", "");
        final MyDatabaseHelper db = new MyDatabaseHelper(this);
        final RouteNotification route = db.getActiveRouteNotification();
        if (route != null) {
            Button mSubmit = findViewById(R.id.btnSendReason);
            mSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isReasonChecked) {
                        String collectJobId = route.getCollectJobId();
                        TSCCDriverClient client = ApiController.getTsccDriverClient();
                        Call<ApiResponse> call = client.cancelCollectJob(jwtToken, ParseUtil.tryParseStringtoInt(collectJobId, 0));
                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                Toast.makeText(CancelRouteActivity.this, "Đã hủy nhiệm vụ", Toast.LENGTH_SHORT).show();
                                //deactive route
                                db.deactiveRouteNotification(route.getId());

                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new RouteFragment()).commit();
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                Toast.makeText(CancelRouteActivity.this, "Có lỗi xảy ra, không thể hoàn thành tác vụ", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(CancelRouteActivity.this, "Xin nhập lý do", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Hiện tại không có nhiệm vụ nào", Toast.LENGTH_SHORT).show();
        }

    }


}
