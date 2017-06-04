package soa.mvp.parkingfinder.service;

/**
 * Created by raulvillca on 4/6/17.
 */

public interface PopupCallback {
    void reservation (String item_name, String start_hour, String final_hour);
}
