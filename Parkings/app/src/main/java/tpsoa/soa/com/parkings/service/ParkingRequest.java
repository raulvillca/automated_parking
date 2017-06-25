package tpsoa.soa.com.parkings.service;

import java.util.List;

import tpsoa.soa.com.parkings.model.ParkingPoint;

public interface ParkingRequest {
    void getParkingListResponse(List<ParkingPoint> positionList);
}
