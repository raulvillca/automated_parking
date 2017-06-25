package tpsoa.soa.com.parkings.model;

import java.util.ArrayList;
import java.util.List;

public class ParkingPositionListFactory {
    /***
     * Generamos lista de puntos donde hay estacionamientos
     * que funcionarian con el mismo sistema
     * @return
     */
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
