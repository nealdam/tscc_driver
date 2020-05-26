package capstone.spring20.tscc_driver.entity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class CollectJobResponse implements Serializable, Comparable<CollectJobResponse>  {

    @SerializedName("creatAt")
    @Expose
    private String creatAt;
    @SerializedName("trashStatus")
    @Expose
    private String trashStatus;

    public String getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(String creatAt) {
        this.creatAt = creatAt;
    }

    public String getTrashStatus() {
        return trashStatus;
    }

    public void setTrashStatus(String trashStatus) {
        this.trashStatus = trashStatus;
    }

    @Override
    public String toString() {
        return "CollectJobResponse{" +
                "creatAt=" + creatAt +
                ", trashStatus='" + trashStatus + '\'' +
                '}';
    }

    @Override
    public int compareTo(CollectJobResponse c) {
        return c.getCreatAt().compareTo(this.getCreatAt());
    }
}
