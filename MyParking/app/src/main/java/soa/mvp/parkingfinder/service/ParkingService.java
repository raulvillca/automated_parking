package soa.mvp.parkingfinder.service;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import soa.mvp.parkingfinder.model.LocationResponse;
import soa.mvp.parkingfinder.model.ParkingResponse;

/**
 * Created by raulvillca on 16/5/17.
 */

public interface ParkingService {

    @GET("/api/json/get/bLGmdSubvS")
    void requestParking(Callback<LocationResponse> callback);

    @GET("/api/json/get/bGxLYgVnjC")
    void getParkingList(Callback<ParkingResponse> callback);
}
