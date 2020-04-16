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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import capstone.spring20.tscc_driver.Api.ApiController;
import capstone.spring20.tscc_driver.Api.TSCCDriverClient;
import capstone.spring20.tscc_driver.entity.RouteNotification;
import capstone.spring20.tscc_driver.util.LocationUtil;
import capstone.spring20.tscc_driver.util.MyDatabaseHelper;
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
public class RouteFragment extends Fragment implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    String TAG = "RouteFragment";
    GoogleMap mMap;
    RouteNotification route;
    MyDatabaseHelper db;

    String originString, destinationString, waypointsString, locationsString, trashAreaIdListString, collectJobId;
    LatLng origin, destination;
    List<LatLng> waypoints, locations;
    String[] trashIdArray;
    PolylineOptions polylineOptions = new PolylineOptions();
    LocationManager locationManager;
    Map<Integer, Marker> markerDict = new HashMap<>();
    int STATUS_DONE_CODE = 4, STATUS_CANCELED_CODE = 3;
    Button mComplete;
    SharedPreferences sharedPreferences;
    Context myContext;
    View rootView;

    public RouteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        myContext = (Activity) context;
        super.onAttach(context);
    }
    @AfterPermissionGranted(123)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_route, container, false);

        onMissionComplete();

        return rootView;
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            SupportMapFragment mapFragment = (SupportMapFragment)
                    getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            EasyPermissions.requestPermissions(this, "Bạn cần có vị trí để sử dụng ứng dụng.", 123, perms);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

            mMap = googleMap;
            if (mMap != null) {
                // move camera vào my current location
                mMap.setMyLocationEnabled(true);
                LocationManager lm = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);
                @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                //zoom map to my location
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(17)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                //check xem có đang thu tuyến rác nào ko
                db = new MyDatabaseHelper(getActivity());
                route = db.getActiveRouteNotification();
                if (route != null) { //lấy data, add marker và đường đi vào map
                    getDataFromNotificationMessage();

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) { // click marker để show trash area detail
                            Intent intent = new Intent(getActivity(), PopupActivity.class);
                            intent.putExtra("trashAreaId", marker.getTitle());
                            startActivity(intent);
                            return true;

                        }
                    });
                    // Add markers in locations and move the camera
                    mMap.addMarker(new MarkerOptions()
                            .position(origin)
                            .title("begin")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mMap.addMarker(new MarkerOptions()
                            .position(destination)
                            .title("end")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    // tạo marker cho trash area
                    for (int i = 0; i < waypoints.size(); i++) {
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(waypoints.get(i))
                                .title(trashIdArray[i]) // gán trash id vô marker title
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        markerDict.put(Integer.parseInt(trashIdArray[i]), marker); //save trashAreaId:marker theo dạng key:value
                    }
                    // vẽ tuyến đường
                    polylineOptions.addAll(locations);
                    Polyline line = mMap.addPolyline(polylineOptions);
                    line.setWidth(5);
                    line.setColor(Color.BLUE);


                }
            }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

        }
    }

    public void getDataFromNotificationMessage() {
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

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void onMissionComplete() {
        Button mComplete = rootView.findViewById(R.id.btnComplete);
        mComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deactive route
                if (route != null) {
                    MyDatabaseHelper db = new MyDatabaseHelper(myContext);
                    db.deactiveRouteNotification(route.getId());
                    //complete collectJob status
                    sharedPreferences = myContext.getSharedPreferences("JWT", MODE_PRIVATE);
                    String token = sharedPreferences.getString("token", "");
                    TSCCDriverClient client = ApiController.getTsccDriverClient();
                    Call<String> call = client.completeCollectJob(token, Integer.parseInt(collectJobId));
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
    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }*/
}
