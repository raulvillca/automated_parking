package soa.mvp.parkingfinder.service;

import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import soa.mvp.parkingfinder.R;
import soa.mvp.parkingfinder.model.ParkingPoint;
import soa.mvp.parkingfinder.model.ParkingResponse;

/**
 * Created by raulvillca on 18/5/17.
 */

public class IParkingRequest {
    public static String TAG = "IParkingRequest";
    private Fragment fragment;
    private ParkingRequest request;

    public IParkingRequest(Fragment fragment) {
        this.fragment = fragment;
        this.request = (ParkingRequest) fragment;
    }

    public void sentRequest() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(fragment.getString(R.string.URI_SERVER))
                .setLogLevel(RestAdapter.LogLevel.FULL).
                        setLog(new RestAdapter.Log() {
                            @Override
                            public void log(String msg) {
                                Log.e(TAG+"::MOSTRAR_LOG", msg);
                            }
                        })
                .build();

        ParkingService service = adapter.create(ParkingService.class);
        service.getParkingList(new Callback<ParkingResponse>() {
            @Override
            public void success(ParkingResponse response, Response response2) {
                returnReponse(response);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void returnReponse(ParkingResponse response) {

        if ( response.getParking_list() != null && ! response.getParking_list().isEmpty()) {

            List<ParkingPoint> positions = new ArrayList<ParkingPoint>();
            for (int i = 0; i < response.getParking_list().size(); i++) {
                if (response.getParking_list().get(i) != null)
                    positions.add(response.getParking_list().get(i));
                else
                    positions.add(new ParkingPoint());
            }
            request.getParkingListResponse(positions);
        }
    }
}
