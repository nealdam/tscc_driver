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

import org.jetbrains.annotations.NotNull;

import capstone.spring20.tscc_driver.Api.ApiController;
import capstone.spring20.tscc_driver.Api.TSCCDriverClient;
import capstone.spring20.tscc_driver.entity.Status;
import capstone.spring20.tscc_driver.entity.TrashArea;
import capstone.spring20.tscc_driver.util.ParseUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportTrashActivity extends AppCompatActivity {

    EditText mReason;
    Button mSubmit;
    int STATUS_CANCELED_CODE = 3;
    boolean isReasonChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_trash);
        getSupportActionBar().setTitle("Báo cáo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupBasic();
    }

    private void setupBasic() {
        final TrashArea trashArea = (TrashArea) getIntent().getSerializableExtra("trashArea");
        final String trashAreaId = getIntent().getStringExtra("trashAreaId");
        mReason = findViewById(R.id.iReason);

        RadioGroup rbGroup = findViewById(R.id.rbGroup);
        rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isReasonChecked = true;
            }
        });

        RadioButton rbOther = findViewById(R.id.rbOther);
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

        mSubmit = findViewById(R.id.btnSendReason);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReasonChecked) {
                    reportToServer(trashAreaId, trashArea);
                } else {
                    Toast.makeText(ReportTrashActivity.this, "Xin nhập lý do", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void reportToServer(String id, TrashArea trashArea) {
        Status status = new Status();
        status.setId(STATUS_CANCELED_CODE);
        trashArea.setStatus(status);

        SharedPreferences sharedPreferences = this.getSharedPreferences("JWT", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        TSCCDriverClient client = ApiController.getTsccDriverClient();
        Call<TrashArea> call = client.updateTrashAreaStatus(token, ParseUtil.tryParseStringtoInt(id, 0), trashArea);
        call.enqueue(new Callback<TrashArea>() {
            @Override
            public void onResponse(Call<TrashArea> call, @NotNull Response<TrashArea> response) {
                if (response.code() == 200) {
                    Toast.makeText(ReportTrashActivity.this, "Báo cáo thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReportTrashActivity.this, "Đã có lỗi xảy ra, không thể hoàn thành tác vụ", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
            @Override
            public void onFailure(Call<TrashArea> call, Throwable t) {
                Toast.makeText(ReportTrashActivity.this, "Đã có lỗi xảy ra, không thể hoàn thành tác vụ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
