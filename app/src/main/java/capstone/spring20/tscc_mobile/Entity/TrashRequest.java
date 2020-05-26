package capstone.spring20.tscc_mobile.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrashRequest {

    @SerializedName("trashTypeId")
    @Expose
    private String trashTypeId;
    @SerializedName("trashSize")
    @Expose
    private String trashSize;
    @SerializedName("trashWidth")
    @Expose
    private String trashWidth;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("images")
    @Expose
    public List<String> images = new ArrayList<>();

    public String getTrashTypeId() {
        return trashTypeId;
    }



    public void setTrashTypeId(String trashTypeId) {
        this.trashTypeId = trashTypeId;
    }

    public String getTrashSize() {
        return trashSize;
    }

    public void setTrashSize(String trashSize) {
        this.trashSize = trashSize;
    }

    public String getTrashWidth() {
        return trashWidth;
    }

    public void setTrashWidth(String trashWidth) {
        this.trashWidth = trashWidth;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public TrashRequest(String trashTypeId, String trashSize, String trashWidth, double latitude, double longitude, List<String> images) {
        this.trashTypeId = trashTypeId;
        this.trashSize = trashSize;
        this.trashWidth = trashWidth;
        this.latitude = latitude;
        this.longitude = longitude;
        this.images = images;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
