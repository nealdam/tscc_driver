package capstone.spring20.tscc_driver.util;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class LocationUtil {
    //convert location string dạng "111, 113" thành obj LatLng
    public static LatLng stringToLatLng(String locationString) {
        String[] a = locationString.split(",");
        double lat = Double.valueOf(a[0]);
        double lng = Double.valueOf(a[1]);
        return new LatLng(lat, lng);
    }
    //convert location string dạng "111, 123|112, 124|113, 125" thành List<>
    public static List<LatLng> stringToList(String locationString) {
        String[] locations = locationString.split("\\|");
        List<LatLng> list = new ArrayList<>();
        for (String s : locations) {
            LatLng l = stringToLatLng(s);
            list.add(l);
        }
        return list;
    }

}
