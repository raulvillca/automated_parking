package soa.mvp.parkingfinder.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import soa.mvp.parkingfinder.MainActivity;
import soa.mvp.parkingfinder.R;
import soa.mvp.parkingfinder.model.Item;
import soa.mvp.parkingfinder.model.ItemFirebase;
import soa.mvp.parkingfinder.model.ParkingPoint;
import soa.mvp.parkingfinder.model.ParkingPositionListFactory;
import soa.mvp.parkingfinder.model.Time;
import soa.mvp.parkingfinder.refactor.ParkingFirebase;
import soa.mvp.parkingfinder.service.ParkingFirebaseRequest;
import soa.mvp.parkingfinder.service.ParkingRequest;

public class MapFragment extends Fragment implements
        GoogleMap.OnMarkerClickListener,
        LocationListener,
        OnMapReadyCallback,
        ParkingRequest,
        ParkingFirebaseRequest.ParkingFirebaseResponse<ParkingPoint, ItemFirebase, Time, List<Time>> {
    public static String TAG = "MapFragment";
    private GoogleMap mMap;
    private Marker marker;
    private Location location;
    private LocationManager locationManager;
    private List<ParkingPoint> positions;

    private ParkingFirebase firebase;
    private List<Item> items;

    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 30;
    public static final long MIN_TIME_BW_UPDATES = 1000 * 45;

    private ParkingListFragment fragment;

    public MapFragment() {
        items = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebase = new ParkingFirebase(this);
        firebase.connect();
        firebase.setParkingFirebase(this);
        firebase.getPositionList();

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.id_fragment_map)).getMapAsync(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            getLocation();

        }
    }

    private void setParkings(List<ParkingPoint> list) {
        if(mMap != null) {
            List<ParkingPoint> parkings = list;
            mMap.clear();

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            if ( parkings != null) {
                //Cargamos parkings
                for (int i = 0; i < parkings.size(); i++) {
                    LatLng latLngParking = new LatLng(Double.valueOf(parkings.get(i).getLatitude()), Double.valueOf(parkings.get(i).getLongitude()));
                    builder.include(latLngParking);

                    mMap.setOnMarkerClickListener(this);
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(latLngParking)
                            .title("Estacionamiento en " + parkings.get(i).getDescription())
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

                }
            } else {
                View coornatorLayoutView = getActivity().findViewById(R.id.layout_fragment_map);
                Snackbar.make(coornatorLayoutView, getResources().getString(R.string.parking_not_found), 8000)
                        .setAction("", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .setDuration(6500)
                        .show();
            }

            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                builder.include(latLng);

                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 500, 500, 10));
            }
        }
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getContext()
                    .getSystemService(getContext().LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // Si no hay proveedor habilitado
                //obtengo mi posicion
                LatLng latLng = new LatLng(-34.670367, -58.563585);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                mMap.animateCamera(cameraUpdate);
            } else {
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            positionUpdate(location);
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                } else if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                positionUpdate(location);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(MapFragment.TAG + "::getLocation", e.getMessage());
        }
        return location;
    }

    private void positionUpdate(Location location) {

        if (location != null) {
            //obtengo mi posicion
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);

            //TODO si quieren que se enfoquen siempre el punto
            mMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //A partir de la api 23, se tiene que pedir permisos al dispositivo
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET}, 10);

            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        getLocation();

        positions = ParkingPositionListFactory.getList();
        setParkings(positions);
    }

    @Override
    public void onLocationChanged(Location location) {
        positionUpdate(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        getLocation();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        fragment = (ParkingListFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(ParkingListFragment.TAG);

        if (fragment == null) {
            Bundle bundle = new Bundle();
            bundle.putString("items", new Gson().toJson(items));
            fragment = new ParkingListFragment();
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack(ParkingListFragment.TAG)
                    .add(R.id.content_main, fragment, ParkingListFragment.TAG)
                    .commit();

            Log.e(MapFragment.TAG, "No esta visible");
        } else if(fragment != null && ! fragment.isVisible()) {
            Bundle bundle = new Bundle();
            bundle.putString("items", new Gson().toJson(items));
            fragment = new ParkingListFragment();
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack(ParkingListFragment.TAG)
                    .add(R.id.content_main, fragment, ParkingListFragment.TAG)
                    .commit();
        } else {
            Log.e(MapFragment.TAG, "Ya esta visible");
        }

        return true;
    }

    @Override
    public void getParkingListResponse(List<ParkingPoint> positionList) {
        setParkings(positionList);
    }

    @Override
    public void pointResponse(ParkingPoint point) {
        firebase.getItemList(point.getParking_id());
    }

    @Override
    public void parkingResponse(String parking_id, ItemFirebase itemFirebase) {
        firebase.getReservationList(parking_id, itemFirebase.getLocation_id());

        items.add(
                new Item(itemFirebase.getLocation_id(),
                        itemFirebase.getLocation_name(),
                        new ArrayList<Time>(),
                        itemFirebase.getLocation_state()));
    }

    @Override
    public void onChangeListener(ItemFirebase item) {

    }

    @Override
    public void timeListener(String parking_id, String location_id, Time time) {

        for (Item item: items) {
            if ( ! item.getTime_list().isEmpty() &&
                    item.getTime_list().get(item.getTime_list().size() - 1).getOption_button()) {
                item.getTime_list().remove(item.getTime_list().size() - 1);
            }

            if (item.getLocation_id().equals(location_id)) {
                item.getTime_list().add(time);
            }

        }

        fragment = (ParkingListFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(ParkingListFragment.TAG);

        if (fragment != null && fragment.isVisible()) {

            fragment.getLocationListResponse(items);
            Log.e(MapFragment.TAG, "RefrescarLista");
        }

    }

    @Override
    public void updateTimeListListener(String parking_id, String location_id, List<Time> times) {

        for (Item item: items) {
            if ( ! item.getTime_list().isEmpty() &&
                    item.getTime_list().get(item.getTime_list().size() - 1).getOption_button()) {
                item.getTime_list().remove(item.getTime_list().size() - 1);
            }

            if (item.getLocation_id().equals(location_id)) {
                item.setTime_list(times);
            }

        }

        fragment = (ParkingListFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(ParkingListFragment.TAG);

        if (fragment != null && fragment.isVisible()) {
            fragment.getLocationListResponse(items);
        }
    }

    public void showInfoWindow() {
        marker.showInfoWindow();
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 12 ) );
    }
}
