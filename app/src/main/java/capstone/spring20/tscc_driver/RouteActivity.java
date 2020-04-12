package capstone.spring20.tscc_driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import capstone.spring20.tscc_driver.entity.RouteNotification;
import capstone.spring20.tscc_driver.entity.TrashArea;
import capstone.spring20.tscc_driver.util.LocationUtil;
import capstone.spring20.tscc_driver.util.MyDatabaseHelper;
import okhttp3.Route;

public class RouteActivity extends Fragment implements OnMapReadyCallback {

    String TAG = "RouteActivity";

    private GoogleMap mMap;
    String originString, destinationString, waypointsString, locationsString, trashAreaIdListString;
    LatLng origin, destination;
    List<LatLng> waypoints, locations;
    String[] trashIdArray;
    PolylineOptions polylineOptions = new PolylineOptions();
    LocationManager locationManager;
    Map<Integer, Marker> markerDict = new HashMap<>();
    int STATUS_DONE_CODE = 4, STATUS_CANCELED_CODE = 3;
    Button mComplete;
    RouteNotification routeNotification;

    private FragmentActivity myContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) myContext.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);
        getDataFromNotificationMessage();

        mComplete = myContext.findViewById(R.id.btnComplete);
        mComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update property Active = false
                if (routeNotification != null) {
                    MyDatabaseHelper db = new MyDatabaseHelper(myContext);
                    db.deactiveRouteNotification(routeNotification.getId());
                }
                //quay lại màn hình trước
                Intent intent = new Intent(myContext, NotificationActivity.class);
                startActivity(intent);
                myContext.finish();
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        myContext = (FragmentActivity) context;
        super.onAttach(context);
    }

/*    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getDataFromNotificationMessage();

        mComplete = findViewById(R.id.btnComplete);
        mComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update property Active = false
                if (routeNotification != null) {
                    MyDatabaseHelper db = new MyDatabaseHelper(RouteActivity.this);
                    db.deactiveRouteNotification(routeNotification.getId());
                }
                //quay lại màn hình trước
                Intent intent = new Intent(RouteActivity.this, NotificationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }*/

    private void updateLocationOnChange() {
        if (myContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && myContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(myContext, "location change", Toast.LENGTH_SHORT).show();
                LatLng l = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(l));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    public void getDataFromNotificationMessage(){
        //get data from notification messsage
        routeNotification = (RouteNotification) myContext.getIntent().getSerializableExtra("routeNotification");
        originString = routeNotification.getOrigin();
        destinationString = routeNotification.getDestination();
        waypointsString = routeNotification.getWaypoints();
        locationsString = routeNotification.getLocations();
        trashAreaIdListString = routeNotification.getTrashAreaIdList();
        //convert location string to latLng
        origin = LocationUtil.stringToLatLng(originString);
        destination = LocationUtil.stringToLatLng(destinationString);
        waypoints = LocationUtil.stringToList(waypointsString);
        locations = LocationUtil.stringToList(locationsString);
        //convert string to array
        trashIdArray = trashAreaIdListString.split(",");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) { // click marker để show trash area detail
                Intent intent = new Intent(myContext, TrashAreaDetailActivity.class);
                intent.putExtra("trashAreaId", marker.getTitle());
                startActivityForResult(intent, 1);
                return true;
            }
        });*/
        // Add markers in locations and move the camera
        /*mMap.addMarker(new MarkerOptions()
                .position(origin)
                .title("begin")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));*/
        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        /*mMap.addMarker(new MarkerOptions()
                .position(destination)
                .title("end")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));*/
        // tạo marker cho trash area
        /*for (int i = 0; i < waypoints.size(); i++) {
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
        line.setColor(Color.BLUE);*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            int result = data.getIntExtra("result", -1);
            //get marker đã được update status
            String id = data.getStringExtra("trashAreaId");
            Marker marker = markerDict.get(Integer.parseInt(id));
            if (marker != null) {
                // nếu DONE thì đổi marker màu green
                if (result == STATUS_DONE_CODE) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else { //nếu REPORT thì đổi marker màu đỏ
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                //xóa item khỏi markerDict
                removeItemFromMarkerDict(Integer.parseInt(id));
            }

        } else {
            Toast.makeText(myContext, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeItemFromMarkerDict(int id) {
        markerDict.remove(id);
        if (markerDict.isEmpty()) {
            mComplete.setVisibility(View.VISIBLE);
        }
    }
}
