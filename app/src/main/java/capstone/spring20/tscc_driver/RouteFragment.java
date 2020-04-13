package capstone.spring20.tscc_driver;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import capstone.spring20.tscc_driver.util.LocationUtil;
import capstone.spring20.tscc_driver.util.MyDatabaseHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class RouteFragment extends Fragment implements OnMapReadyCallback {

    String TAG = "RouteActivity";
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


    public RouteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_route, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        //check xem có đang thu tuyến rác nào ko
        db = new MyDatabaseHelper(getActivity());
        route = db.getActiveRouteNotification();
        if (route != null) { //lấy data, add marker và đường đi vào map
            getDataFromNotificationMessage();

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) { // click marker để show trash area detail
                    Intent intent = new Intent(getActivity(), TrashAreaDetailActivity.class);
                    intent.putExtra("trashAreaId", marker.getTitle());
                    startActivityForResult(intent, 1);
                    return true;

                }
            });
            // Add markers in locations and move the camera
            mMap.addMarker(new MarkerOptions()
                    .position(origin)
                    .title("begin")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
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


        } else {
            // TODO hiện vị trí hiện tại
            Toast.makeText(getActivity(), "NO ROUTE", Toast.LENGTH_SHORT).show();
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
}
