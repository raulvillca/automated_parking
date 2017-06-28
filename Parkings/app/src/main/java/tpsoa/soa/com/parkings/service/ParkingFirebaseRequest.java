package tpsoa.soa.com.parkings.service;

import java.util.List;

import tpsoa.soa.com.parkings.model.ItemFirebase;
import tpsoa.soa.com.parkings.model.Time;

public interface ParkingFirebaseRequest {
    interface UserFirebaseResponse<T>{
        void userResponse(T user);
    }

    interface ParkingFirebaseResponse<T, U, V, W>{
        void pointResponse(T point);
        void parkingResponse(String parking_id, U item);
        void timeResponse(String parking_id, String location_id, List<Time> times);
        void onChangeListener(U item);
        void timeListener(String parking_id, String location_id, V time);
        void updateTimeListListener(String parking_id, String location_id, W time);
    }

    interface ParkingRegister {
        void doingRegister(String item_name, String start_time, String final_time);
    }
}
