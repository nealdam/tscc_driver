package capstone.spring20.tscc_driver;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import capstone.spring20.tscc_driver.Api.ApiController;
import capstone.spring20.tscc_driver.Api.TSCCDriverClient;
import capstone.spring20.tscc_driver.entity.Status;
import capstone.spring20.tscc_driver.entity.TrashArea;
import capstone.spring20.tscc_driver.util.ParseUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopupActivity extends AppCompatActivity {

    TrashArea trashArea;
    Button mDone, mReport, btnDetail;
    TSCCDriverClient client;
    String trashAreaId;
    int STATUS_DONE_CODE = 4, STATUS_CANCELED_CODE = 3;
    EditText mTrashType, mTrashSize, mTrashWidth;
    TextView txtAddress;
    String token;
    List<TrashArea> trashAreaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        setupBasic();

        //setup popup
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .2));

        if (trashAreaId != null) {
            Call<TrashArea> call = client.getTrashAreaById(token, ParseUtil.tryParseStringtoInt(trashAreaId, 0));
            call.enqueue(new Callback<TrashArea>() {
                @Override
                public void onResponse(Call<TrashArea> call, Response<TrashArea> response) {
                    trashArea = response.body();
                    showTrashAreaInformation();
                }

                @Override
                public void onFailure(Call<TrashArea> call, Throwable t) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            });
        }
    }

    private void showTrashAreaInformation() {
        if (trashArea != null) {
            txtAddress.setText(trashArea.getStreetNumber() + " " + trashArea.getStreet() + ", " + trashArea.getDistrict() + ", " + trashArea.getCity());
        }
    }

    public void setupBasic() {
        trashAreaId = getIntent().getStringExtra("trashAreaId");
        trashAreaList = (List<TrashArea>) getIntent().getSerializableExtra("trashAreaList");

        SharedPreferences sharedPreferences = this.getSharedPreferences("JWT", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        client = ApiController.getTsccDriverClient();
        mDone = findViewById(R.id.btnDone);
        mReport = findViewById(R.id.btnReport);
        btnDetail = findViewById(R.id.btnDetail);
        txtAddress = findViewById(R.id.txtTrashAddress);
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTrashAreaStatus(STATUS_DONE_CODE);
            }
        });
        mReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTrashAreaStatus(STATUS_CANCELED_CODE);

            }
        });
        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PopupActivity.this, TrashAreaDetailActivity.class);
                intent.putExtra("trashAreaId", trashAreaId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateTrashAreaStatus(final int statusCode) {
        if (trashArea != null && trashAreaId != null) {
            Status status = new Status();
            status.setId(statusCode);
            trashArea.setStatus(status);
            Call<TrashArea> call = client.updateTrashAreaStatus(token, Integer.parseInt(trashAreaId), trashArea);
            call.enqueue(new Callback<TrashArea>() {
                @Override
                public void onResponse(Call<TrashArea> call, Response<TrashArea> response) {
                    Intent data = new Intent();
                    data.putExtra("result", statusCode);
                    data.putExtra("trashAreaId", trashAreaId);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }

                @Override
                public void onFailure(Call<TrashArea> call, Throwable t) {
                    Toast.makeText(PopupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
