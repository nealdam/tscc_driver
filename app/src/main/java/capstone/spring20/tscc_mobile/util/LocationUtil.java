package capstone.spring20.tscc_mobile.util;

import android.location.Location;
import android.util.Log;

import java.util.Map;

public class LocationUtil {
    public static boolean isClose(Location startLoc, Map<String, String> locationMap) {
        if (locationMap.isEmpty())
            return false;
        for (Map.Entry<String,String> entry : locationMap.entrySet()) {
            double distance = Haversine.distance(startLoc.getLatitude(), startLoc.getLongitude(),
                                                Float.parseFloat(entry.getKey()),  Float.parseFloat(entry.getValue()));
            Log.d("DISTANCE: ", "distance: " + distance);
            if (distance <= 0.005)
                return true;
        }

        return false;
    }
}
