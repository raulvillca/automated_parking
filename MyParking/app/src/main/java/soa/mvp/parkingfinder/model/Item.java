package soa.mvp.parkingfinder.model;

import java.util.List;

/**
 * Created by raulvillca on 18/5/17.
 */

public class Item {
    private String location_id;
    private String location_name;
    private int location_state;
    private List<Time> time_list;

    public Item(String location_id, String location_name, List<Time> time_list, int state) {
        this.location_id = location_id;
        this.location_name = location_name;
        this.location_state = state;
        this.time_list = time_list;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public int getLocation_state() {
        return location_state;
    }

    public void setLocation_state(int location_state) {
        this.location_state = location_state;
    }

    public List<Time> getTime_list() {
        return time_list;
    }

    public void setTime_list(List<Time> time_list) {
        this.time_list = time_list;
    }
}
