package capstone.spring20.tscc_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import capstone.spring20.tscc_driver.Api.ApiController;
import capstone.spring20.tscc_driver.Api.TSCCDriverClient;
import capstone.spring20.tscc_driver.entity.Status;
import capstone.spring20.tscc_driver.entity.TrashArea;
import capstone.spring20.tscc_driver.util.ParseUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrashAreaDetailActivity extends AppCompatActivity {

    TrashArea trashArea;
    Button mDone, mReport, btnBack;
    TSCCDriverClient client;
    String trashAreaId;
    int STATUS_DONE_CODE = 4, STATUS_CANCELED_CODE = 3;
    EditText mTrashType, mTrashSize, mTrashWidth;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_area_detail);
        setupBasic();

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrashAreaDetailActivity.this.onBackPressed();
            }
        });
        //lấy trashArea obj từ server
        trashAreaId = getIntent().getStringExtra("trashAreaId");
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
            mTrashType.setText(trashArea.getType().getName(), TextView.BufferType.EDITABLE);
            mTrashSize.setText(trashArea.getSize().getName(), TextView.BufferType.EDITABLE);
            mTrashWidth.setText(trashArea.getWidth().getName(), TextView.BufferType.EDITABLE);
        }
    }

    public void setupBasic() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("JWT", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        client = ApiController.getTsccDriverClient();
        mDone = findViewById(R.id.btnDone);
        mReport = findViewById(R.id.btnReport);
        mTrashSize = findViewById(R.id.txtTrashSize);
        mTrashType = findViewById(R.id.txtTrashType);
        mTrashWidth = findViewById(R.id.txtTrashWidth);
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
                    Toast.makeText(TrashAreaDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        // đặt resultCode là Activity.RESULT_CANCELED thể hiện
        // đã thất bại khi người dùng click vào nút Back.
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}
