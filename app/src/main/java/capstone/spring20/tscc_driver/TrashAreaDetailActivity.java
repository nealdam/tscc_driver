package capstone.spring20.tscc_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import capstone.spring20.tscc_driver.Api.ApiController;
import capstone.spring20.tscc_driver.Api.TSCCDriverClient;
import capstone.spring20.tscc_driver.entity.TrashArea;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrashAreaDetailActivity extends AppCompatActivity {

    TrashArea trashArea;
    Button mDone, mReport;
    TSCCDriverClient client;
    String trashAreaId;
    int STATUS_DONE_CODE = 4, STATUS_CANCELED_CODE = 3;
    EditText mTrashType, mTrashSize, mTrashWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_area_detail);
        setupBasic();
        //lấy trashArea obj từ server
        trashAreaId = getIntent().getStringExtra("trashAreaId");
        if (trashAreaId != null) {
            Call<TrashArea> call = client.getTrashAreaById(Integer.parseInt(trashAreaId));
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
            trashArea.setStatus(statusCode);
            Call<TrashArea> call = client.updateTrashAreaStatus(Integer.parseInt(trashAreaId), trashArea);
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
