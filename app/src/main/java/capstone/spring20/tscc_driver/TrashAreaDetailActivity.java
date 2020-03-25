package capstone.spring20.tscc_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class TrashAreaDetailActivity extends AppCompatActivity {

    Spinner mType, mWidth, mSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_area_detail);
        setupBasic();
        setupSpinner();
    }
    public void setupBasic(){
        mType = findViewById(R.id.spType);
        mSize = findViewById(R.id.spSize);
        mWidth = findViewById(R.id.spWidth);
//        mSubmit = findViewById(R.id.btnSendRequest);
//        mGallery = findViewById(R.id.btnLibrary);
//        mImageNum = findViewById(R.id.txtImageNum);
//        mImageView = findViewById(R.id.imageView);
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
}
