package capstone.spring20.tscc_driver;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import capstone.spring20.tscc_driver.util.LocationUtil;

public class RouteActivity extends FragmentActivity implements OnMapReadyCallback {

    String TAG = "RouteActivity";

    private GoogleMap mMap;
    String originString, destinationString, waypointsString, locationsString;
    LatLng origin, destination;
    List<LatLng> waypoints, locations;
    PolylineOptions polylineOptions = new PolylineOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get data from notification messsage
        Log.d(TAG, "onCreate: "+TAG);
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
        // Add a marker in a location and move the camera
        mMap.addMarker(new MarkerOptions().position(origin));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
        // vẽ tuyến đường
        polylineOptions.addAll(locations);
        Polyline line = mMap.addPolyline(polylineOptions);
        line.setWidth(5);
        line.setColor(Color.BLUE);
    }
}
