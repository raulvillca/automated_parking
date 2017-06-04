package soa.mvp.parkingfinder.model;

import java.util.List;

/**
 * Created by raulvillca on 18/5/17.
 */

public class LocationResponse {
    private List<Item> location_list;

    public List<Item> getLocation_list() {
        return location_list;
    }

    public void setLocation_list(List<Item> location_list) {
        this.location_list = location_list;
    }
}
