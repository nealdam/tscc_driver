package capstone.spring20.tscc_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import capstone.spring20.tscc_driver.Api.ApiController;
import capstone.spring20.tscc_driver.Api.TSCCDriverClient;
import capstone.spring20.tscc_driver.entity.TrashArea;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrashAreaDetailActivity extends AppCompatActivity {

    Spinner mType, mWidth, mSize;
    TrashArea trashArea;
    Button mDone, mReport;
    TSCCDriverClient client;
    String trashAreaId;
    int STATUS_DONE_CODE = 4, STATUS_CANCELED_CODE = 3;
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
                }

                @Override
                public void onFailure(Call<TrashArea> call, Throwable t) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            });
        }

    }
    public void setupBasic() {
        client = ApiController.getTsccDriverClient();
        mType = findViewById(R.id.spType);
        mSize = findViewById(R.id.spSize);
        mWidth = findViewById(R.id.spWidth);
        mDone = findViewById(R.id.btnDone);
        mReport = findViewById(R.id.btnReport);
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

    public void setupSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> trashTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.trashType, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> trashWidthAdapter = ArrayAdapter.createFromResource(this,
                R.array.trashWidth, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> trashSizeAdapter = ArrayAdapter.createFromResource(this,
                R.array.trashSize, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        trashTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trashSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trashWidthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the Adapter to the spinner
        mType.setAdapter(trashTypeAdapter);
        mSize.setAdapter(trashSizeAdapter);
        mWidth.setAdapter(trashWidthAdapter);
    }
    @Override
    public void onBackPressed() {
        // đặt resultCode là Activity.RESULT_CANCELED thể hiện
        // đã thất bại khi người dùng click vào nút Back.
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}
