package soa.mvp.parkingfinder.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raulvillca on 28/5/17.
 */

public class ParkingPositionListFactory {
    public static List<ParkingPoint> getList() {
        List<ParkingPoint> positions = new ArrayList<>();
        ParkingPoint position = new ParkingPoint();
        position.setParking_id("unlam");
        position.setLatitude(-34.670366999999999);
        position.setLongitude(-58.563585000000003);
        position.setDescription("Unlam - Florencio Varela");

        positions.add(position);

        return positions;
    }
}
