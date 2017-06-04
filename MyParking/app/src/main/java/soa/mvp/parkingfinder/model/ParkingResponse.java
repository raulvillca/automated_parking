package soa.mvp.parkingfinder.model;

import java.util.List;

/**
 * Created by raulvillca on 18/5/17.
 */

public class ParkingResponse {
    private List<ParkingPoint> parking_list;

    public List<ParkingPoint> getParking_list() {
        return parking_list;
    }

    public void setParking_list(List<ParkingPoint> parking_list) {
        this.parking_list = parking_list;
    }
}
