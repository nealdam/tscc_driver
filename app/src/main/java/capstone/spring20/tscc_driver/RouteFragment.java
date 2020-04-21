package capstone.spring20.tscc_driver;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import capstone.spring20.tscc_driver.Api.ApiController;
import capstone.spring20.tscc_driver.Api.TSCCDriverClient;
import capstone.spring20.tscc_driver.entity.RouteNotification;
import capstone.spring20.tscc_driver.entity.TrashArea;
import capstone.spring20.tscc_driver.util.IconUtil;
import capstone.spring20.tscc_driver.util.LocationUtil;
import capstone.spring20.tscc_driver.util.MyDatabaseHelper;
import capstone.spring20.tscc_driver.util.ParseUtil;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RouteFragment extends Fragment implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks, GoogleMap.OnMarkerClickListener {

    String TAG = "RouteFragment";
    GoogleMap mMap;
    RouteNotification route;
    MyDatabaseHelper db;

    String originString, destinationString, waypointsString, locationsString, trashAreaIdListString, collectJobId;
    LatLng origin, destination;
    List<LatLng> waypoints, locations;
    List<TrashArea> trashAreaList = new ArrayList<>();
    String[] trashIdArray;
    PolylineOptions polylineOptions = new PolylineOptions();
    LocationManager locationManager;
    Map<Integer, Marker> markerDict = new HashMap<>();
    int STATUS_DONE_CODE = 4, STATUS_CANCELED_CODE = 3;
    Button mComplete;
    SharedPreferences sharedPreferences;
    Context myContext;
    View rootView;
    TSCCDriverClient client;
    String jwtToken;

    public RouteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        myContext = (Activity) context;
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @AfterPermissionGranted(123)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_route, container, false);

        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            SupportMapFragment mapFragment = (SupportMapFragment)
                    getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            EasyPermissions.requestPermissions(this, "Bạn cần có vị trí để sử dụng ứng dụng.", 123, perms);
        }

        setupBasic();

        return rootView;
    }

    private void setupBasic() {
        //bỏ strict để có thể call api sync
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        client = ApiController.getTsccDriverClient();

        sharedPreferences = myContext.getSharedPreferences("JWT", MODE_PRIVATE);
        jwtToken = sharedPreferences.getString("token", "");

        db = new MyDatabaseHelper(getActivity());
        route = db.getActiveRouteNotification();
        getDataFromNotificationMessage();
        setupTrashAreaList();

        mComplete = rootView.findViewById(R.id.btnComplete);
        mComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deactive route
                if (route != null) {
                    MyDatabaseHelper db = new MyDatabaseHelper(myContext);
                    db.deactiveRouteNotification(route.getId());
                    //complete collectJob status
                    Call<String> call = client.completeCollectJob(jwtToken, Integer.parseInt(collectJobId));
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                        }
                    });
                }
                //reset UI
                mMap.clear();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            mMap.setOnMarkerClickListener(this);
            // move camera vào my current location
            mMap.setMyLocationEnabled(true);
            LocationManager lm = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);
            assert lm != null;
            @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            //zoom map to my location
            assert location != null;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            //check xem có đang thu tuyến rác nào ko
            if (route != null) { //show marker và đường đi
                showData();
            }
        }
    }

    private void showData() {
        mMap.clear();
        //điểm đầu và cuối
        mMap.addMarker(new MarkerOptions()
                .position(origin)
                .title("begin")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.addMarker(new MarkerOptions()
                .position(destination)
                .title("end")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        // tạo marker cho trash area
        int index = 1;
        for (TrashArea t : trashAreaList) {
            MarkerOptions options;
            if (t.getStatus().getName().equals("PROCESSING"))
                options = createMakerOptions(t, index++);
            else
                options = createMakerOptions(t, 0);
            mMap.addMarker(options);
        }
        // vẽ tuyến đường
        polylineOptions.addAll(locations);
        Polyline line = mMap.addPolyline(polylineOptions);
        line.setWidth(6);
        line.setColor(Color.BLUE);
    }

    public void getDataFromNotificationMessage() {
        if (route != null) {
            //get data from notification messsage
            originString = route.getOrigin();
            destinationString = route.getDestination();
            waypointsString = route.getWaypoints();
            locationsString = route.getLocations();
            trashAreaIdListString = route.getTrashAreaIdList();
            collectJobId = route.getCollectJobId();
            //convert location string to latLng
            origin = LocationUtil.stringToLatLng(originString);
            destination = LocationUtil.stringToLatLng(destinationString);
            waypoints = LocationUtil.stringToList(waypointsString);
            locations = LocationUtil.stringToList(locationsString);
            //convert string to array
            trashIdArray = trashAreaIdListString.split(",");
        }
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(getActivity(), PopupActivity.class);
        intent.putExtra("trashAreaId", marker.getTitle());
        startActivity(intent);
        return true;
    }

    private MarkerOptions createMakerOptions(TrashArea t, int index) {
        MarkerOptions options;
        LatLng location = new LatLng(t.getLatitude(), t.getLongitude());
        String iconName = IconUtil.getIconName(t, index);
        try {
            options = new MarkerOptions()
                    .position(location)
                    .title(String.valueOf(t.getId())) // gán trash id vô marker title
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.recy_processing30));
                    .icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier(iconName, "drawable", R.drawable.class.getPackage().getName())));
        } catch (Exception e) {
            options = new MarkerOptions()
                    .position(location)
                    .title(String.valueOf(t.getId())) // gán trash id vô marker title
                    .icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier("default_image", "drawable", R.drawable.class.getPackage().getName())));

        }

        return options;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            setupTrashAreaList();
            checkIsJobComplete();
            showData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void setupTrashAreaList() {
        if (trashIdArray != null) {
            trashAreaList.clear();
            for (String id : trashIdArray) {
                Call<TrashArea> call = client.getTrashAreaById(jwtToken, ParseUtil.tryParseStringtoInt(id, 0));
                try {
                    TrashArea t = call.execute().body();
                    if (t != null)
                        trashAreaList.add(t);
                } catch (IOException e) {
                    Log.e(TAG, "setupTrashAreaList: " + e);
                }
            }
        }
    }

    private void checkIsJobComplete() {
        boolean isComplete = true;

        for (TrashArea t : trashAreaList) {
            if (t.getStatus().getName().equals("PROCESSING"))
                isComplete = false;
        }

        if (isComplete)
            mComplete.setVisibility(View.VISIBLE);
    }


}
