package capstone.spring20.tscc_mobile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import capstone.spring20.tscc_mobile.Api.ApiController;
import capstone.spring20.tscc_mobile.Api.TSCCClient;
import capstone.spring20.tscc_mobile.Entity.TrashRequest;
import capstone.spring20.tscc_mobile.adapter.PhotoAdapter;
import capstone.spring20.tscc_mobile.constant.TrashTypeConstant;
import capstone.spring20.tscc_mobile.dialog_custom.LoadingDialog;
import capstone.spring20.tscc_mobile.util.LocationUtil;
import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestActivity extends AppCompatActivity {
    String TAG = "RequestActivity";

    Spinner mType;
    EditText mWidth, mSize;
    Button mSubmit, mGallery;
    FusedLocationProviderClient mFusedLocationClient;
    double myLatitude;
    double myLongitude;
    Bitmap image;
    String imageEncoded;
    List<String> imageStringList = new ArrayList<>();
    List<Bitmap> imageList = new ArrayList<>();
    String token;
    GridView gridView;
    //    ImageView mImageView1;
    RecyclerView rcvPhoto;
    PhotoAdapter photoAdapter;
    List<Uri> selectedListUri;
    LoadingDialog loadingDialog = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        getSupportActionBar().setTitle("Báo cáo điểm rác");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupBasic();
        setupSpinner();

        //get image from previous activity
        Bundle data = getIntent().getExtras();
        if (data != null) {
            image = (Bitmap) data.get("image");
            imageList.add(image);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*try {
            if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                // When an Image is picked
                if(data.getData()!=null){
                    imageNum += 1;
                    mImageNum.setText("image number: " + imageNum);
                    Uri mImageUri=data.getData();
                    imageEncoded = mImageUri.getPath();

                    //BitmapFactory.Options options = new BitmapFactory.Options ();
                    //Bitmap img = BitmapFactory.decodeFile ( imageEncoded, options );

                    Bitmap img = MediaStore.Images.Media.getBitmap ( this.getContentResolver () ,mImageUri );
                    imageList.add ( img );
                    gridView = findViewById(R.id.gridview);
                    gridView.setAdapter ( new ImageAdapter ( this, imageList ) );
                } else {
                    // multi images
                    if (null != data.getClipData()) {
                        ClipData mClipData = data.getClipData();
                        //update image number
                        imageNum += mClipData.getItemCount();
                        mImageNum.setText("image number: " + imageNum);
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            Uri uri = mClipData.getItemAt(i).getUri();
                            Bitmap img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            imageList.add(img);
                        }
                        gridView = findViewById(R.id.gridview);
                        gridView.setAdapter ( new ImageAdapter ( this, imageList ) );
                    } else {
                        Toast.makeText(this, "You haven't picked any Image",
                                Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "st wrong", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void postTrashRequest(TrashRequest trashRequest) {
        TSCCClient client = ApiController.getTsccClient();
        Call<TrashRequest> call = client.sendTrashRequest(token, trashRequest);
        call.enqueue(new Callback<TrashRequest>() {
            @Override
            public void onResponse(Call<TrashRequest> call, Response<TrashRequest> response) {
                Toast.makeText(RequestActivity.this, "Gửi yêu cầu thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RequestActivity.this, MainActivity.class);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                loadingDialog.dismissDialog();
//                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<TrashRequest> call, Throwable t) {
                Toast.makeText(RequestActivity.this, "Đã có lỗi xảy ra, gửi yêu cầu thất bại", Toast.LENGTH_SHORT).show();

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                loadingDialog.dismissDialog();
                Intent intent = new Intent(RequestActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupBasic() {
        mType = findViewById(R.id.spType);
        mSize = findViewById(R.id.txtSize);
        mWidth = findViewById(R.id.txtWidth);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //mImageNum = findViewById(R.id.txtImageNum);

        //get jwt token
        SharedPreferences jwtSharedPreferences = this.getSharedPreferences("JWT", MODE_PRIVATE);
        token = jwtSharedPreferences.getString("token", "");
        /*
        //get current location
        SharedPreferences locationSharedPreferences = this.getSharedPreferences("Location", MODE_PRIVATE);
        myLatitude = locationSharedPreferences.getFloat("latitude", 0);
        myLongitude = locationSharedPreferences.getFloat("longitude", 0);
         */
        loadingDialog = new LoadingDialog(RequestActivity.this);

        mSubmit = findViewById(R.id.btnSendRequest);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                loadingDialog.startLoadingDialog();
                if (selectedListUri == null || selectedListUri.isEmpty()) {
                    //Toast.makeText ( RequestActivity.this, "Bạn cần cung cấp hình ảnh!", Toast.LENGTH_LONG ).show ();
                    new AlertDialog.Builder(RequestActivity.this)
                            .setTitle("Không thể gửi yêu cầu")
                            .setMessage("Bạn cần cung cấp hình ảnh để chúng tôi có thể xác nhận thông tin!")
                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(R.drawable.ic_error_50)
                            .show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    loadingDialog.dismissDialog();
                } else {
                    final String trashType = mType.getSelectedItem().toString();
                    final String trashSize = mSize.getText().toString();
                    final String trashWidth = mWidth.getText().toString();
                    for (Uri uri : selectedListUri) {
                        try {
                            imageList.add(MediaStore.Images.Media.getBitmap(RequestActivity.this.getContentResolver(), uri));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    for (Bitmap img : imageList) {
                        imageEncoded = convertBitmapToString(img);
                        imageStringList.add(imageEncoded);
                    }
                    //then get current location and check spam and send request to api
                    FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(RequestActivity.this);
                    mFusedLocationClient
                            .getLastLocation()
                            .addOnCompleteListener(new OnCompleteListener<Location>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Location> task) {
                                                           Location location = task.getResult();
                                                           if (location != null) {
                                                               Log.d(TAG, "onComplete get location: " + location.getLatitude() + ", " + location.getLongitude());
                                                               if (isSpam(location)) {
                                                                   Toast.makeText(RequestActivity.this, "Hôm nay bạn đã yêu cầu thu gom điểm rác này.", Toast.LENGTH_SHORT).show();
                                                                   loadingDialog.dismissDialog();
                                                               } else {
                                                                   TrashRequest trashRequest = new TrashRequest(TrashTypeConstant.getTrashTypeId(trashType), trashSize, trashWidth,
                                                                                                               location.getLatitude(), location.getLongitude(),
                                                                                                               imageStringList);
                                                                   postTrashRequest(trashRequest);
                                                               }
                                                           } else {
                                                               Toast.makeText(RequestActivity.this, "Gửi yêu cầu thất bại, không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
                                                           }
                                                       }
                                                   }
                            );

                }
            }
        });

        mGallery = findViewById(R.id.btnLibrary);
        rcvPhoto = findViewById(R.id.rcv_photo);

        photoAdapter = new PhotoAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        rcvPhoto.setLayoutManager(gridLayoutManager);
        rcvPhoto.setFocusable(false);
        rcvPhoto.setAdapter(photoAdapter);

        mGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);*/

                selectImages();
            }
        });
//        mImageView1 = findViewById(R.id.imageView1);
    }

    private void selectImages() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openBottomPicker();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(RequestActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void openBottomPicker() {
        TedBottomPicker.with(RequestActivity.this)
                //.setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .setSelectedUriList(selectedListUri)
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                        // here is selected image uri list
                        selectedListUri = uriList;
                        photoAdapter.setPhotos(uriList);
                    }
                });

    }

    private void setupSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> trashTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.trashType, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        trashTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the Adapter to the spinner
        mType.setAdapter(trashTypeAdapter);
    }

    private String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    private boolean isSpam(Location location) {
        SharedPreferences s = this.getSharedPreferences(java.time.LocalDate.now().toString(), MODE_PRIVATE);
        Map<String, String> locationMap = (Map<String, String>) s.getAll();
        if (LocationUtil.isClose(location, locationMap))
            return true;
        //ko phải spam nên lưu lại location
        SharedPreferences.Editor editor = s.edit();
        Gson gson = new Gson();
        editor.putString(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        editor.apply();
        return false;
    }
}
