package capstone.spring20.tscc_driver.entity;

import java.io.Serializable;
import java.util.Date;

public class RouteNotification implements Serializable, Comparable<RouteNotification> {
    int id;
    String origin, destination, waypoints, locations, trashAreaIdList;
    Date receivedDate;
    boolean active;

    public RouteNotification(String origin, String destination, String waypoints, String locations, String trashAreaIdList, Date receivedDate, boolean active) {
        this.origin = origin;
        this.destination = destination;
        this.waypoints = waypoints;
        this.locations = locations;
        this.trashAreaIdList = trashAreaIdList;
        this.receivedDate = receivedDate;
        this.active = active;
    }

    public RouteNotification(String origin, String destination, String waypoints, String locations, String trashAreaIdList) {
        this.origin = origin;
        this.destination = destination;
        this.waypoints = waypoints;
        this.locations = locations;
        this.trashAreaIdList = trashAreaIdList;
    }

    public RouteNotification(int id, String origin, String destination, String waypoints, String locations, String trashAreaIdList, Date receivedDate) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.waypoints = waypoints;
        this.locations = locations;
        this.trashAreaIdList = trashAreaIdList;
        this.receivedDate = receivedDate;
    }
    public RouteNotification() {
    }

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(String waypoints) {
        this.waypoints = waypoints;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public String getTrashAreaIdList() {
        return trashAreaIdList;
    }

    public void setTrashAreaIdList(String trashAreaIdList) {
        this.trashAreaIdList = trashAreaIdList;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    @Override
    public int compareTo(RouteNotification o) {
        return o.getReceivedDate().compareTo(getReceivedDate());
    }

    @Override
    public String toString() {
        return "RouteNotification{" +
                "receivedDate=" + receivedDate +
                '}';
    }
}
