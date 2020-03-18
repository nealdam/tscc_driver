package capstone.spring20.tscc_driver;

import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import capstone.spring20.tscc_driver.util.LocationUtil;

public class RouteActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    String TAG = "RouteActivity";

    private GoogleMap mMap;
    String originString, destinationString, waypointsString, locationsString;
    LatLng origin, destination;
    List<LatLng> waypoints, locations;
    PolylineOptions polylineOptions = new PolylineOptions();
    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getDataFromNotificationMessage();

        updateLocationOnChange();


    }

    private void updateLocationOnChange() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                Toast.makeText(RouteActivity.this, "location change", Toast.LENGTH_SHORT).show();
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
        Intent intent = getIntent();
        originString = intent.getStringExtra("origin");
        destinationString = intent.getStringExtra("destination");
        waypointsString = intent.getStringExtra("waypoints");
        locationsString = intent.getStringExtra("locations");
        //convert location string to latLng
        origin = LocationUtil.stringToLatLng(originString);
        destination = LocationUtil.stringToLatLng(destinationString);
        waypoints = LocationUtil.stringToList(waypointsString);
        locations = LocationUtil.stringToList(locationsString);
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
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(RouteActivity.this, "marker click: "+marker.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        // Add markers in locations and move the camera
        mMap.addMarker(new MarkerOptions().position(origin).title("begin"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        mMap.addMarker(new MarkerOptions().position(destination).title("end"));
        for (int i = 0; i < waypoints.size(); i++) {
            mMap.addMarker(new MarkerOptions().position(waypoints.get(i)).title("#"+(i+1)));
        }
        // vẽ tuyến đường
        polylineOptions.addAll(locations);
        Polyline line = mMap.addPolyline(polylineOptions);
        line.setWidth(5);
        line.setColor(Color.BLUE);
    }

    @Override
    public void onLocationChanged(Location location) {

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
}
