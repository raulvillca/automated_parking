package tpsoa.soa.com.parkings.model;

public class ItemFirebase {
    private String location_id;
    private String location_name;
    private int location_state;

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
}
