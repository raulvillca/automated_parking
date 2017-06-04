package soa.mvp.parkingfinder.service;

import java.util.List;

import soa.mvp.parkingfinder.model.ParkingPoint;

public interface ParkingRequest {
    void getParkingListResponse(List<ParkingPoint> positionList);
}
