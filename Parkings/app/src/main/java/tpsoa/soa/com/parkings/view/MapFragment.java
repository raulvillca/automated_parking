package tpsoa.soa.com.parkings.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import tpsoa.soa.com.parkings.R;
import tpsoa.soa.com.parkings.model.Item;
import tpsoa.soa.com.parkings.model.ItemFirebase;
import tpsoa.soa.com.parkings.model.ParkingPoint;
import tpsoa.soa.com.parkings.model.ParkingPositionListFactory;
import tpsoa.soa.com.parkings.model.Time;
import tpsoa.soa.com.parkings.refactor.ParkingFirebase;
import tpsoa.soa.com.parkings.service.ParkingFirebaseRequest;
import tpsoa.soa.com.parkings.service.ParkingRequest;

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
    List<ParkingPoint> parkings;

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
        //firebase.createDB(ParkingPositionListFactory.getList().get(0));
        firebase.getPositionList();

        //obtenemos el mapa y le seteamos nuestro contexto, esto sobre escribe en onMapReady
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.id_fragment_map)).getMapAsync(this);

    }

    /***
     * Obtenemos el resultado de la peticion de permiso para usar el mapa de googlemaps
     * si el codigo es 10 (es el mismo Codigo que enviamos) entonces centramos nuestra posicion
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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
            //Si se aceptaron los permisos entonces debemos mostrar nuestra ubicacion
            //volviendo a pedir a los servicios de ubicacion nuestra posicion
            getLocation();
        }
    }

    private void setParkings(List<ParkingPoint> list, int zoom) {
        if(mMap != null) {
            parkings = list;
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
                            .title("Estacionamiento")
                            .snippet(parkings.get(i).getDescription())
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

                }
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.parking_not_found), Toast.LENGTH_LONG).show();
            }

            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                builder.include(latLng);

                Log.e(MapFragment.TAG, "Establecer zoom");
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 500, 500, zoom));
            }
        } else {
            Log.e(MapFragment.TAG, "No hay mapa");
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

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        10).show();
            } else {
                Log.i(TAG, "Este dispositivo no soporta esta version del mapa.");
            }
        }

        // pedimos permisos para usar la geolocalizacion del dispositivo
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

        //TODO preseteamos algunas herramientos que el mapa nos puede proveer
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        //obtenemos la posicion pidiendo informacion a los
        //servicios de posicionamiento
        getLocation();

        positions = ParkingPositionListFactory.getList();
        //le enviamos la lista de estacionamientos y los ubicamos en el mapa
        setParkings(positions, 10);
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

    /***
     * Realizamos la escucha de cada marcador, icono de globo,
     * en nuestro mapa y deplegamos un ExpandableListView
     * con los parkings y tiempos asociados a cada uno
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        fragment = (ParkingListFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(ParkingListFragment.TAG);

        //TODO Si el fragmento no existe entonces creamos una lisa y la mostramos,
        //si el fragmento existe pero no esa visible solo la mostramos
        //sino significa que ya fue creado y existe entonces no hacemos nada
        if (fragment == null) {
            Bundle bundle = new Bundle();
            bundle.putString("items", new Gson().toJson(items));
            fragment = new ParkingListFragment();
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.from_top_to_bottom, R.anim.from_bottom_to_top)
                    .add(R.id.content_main, fragment, ParkingListFragment.TAG)
                    .addToBackStack(ParkingListFragment.TAG)
                    .commit();

            Log.e(MapFragment.TAG, "No esta visible");
        } else if(fragment != null && ! fragment.isVisible()) {
            Bundle bundle = new Bundle();
            bundle.putString("items", new Gson().toJson(items));
            fragment = new ParkingListFragment();
            fragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.from_top_to_bottom, R.anim.from_bottom_to_top)
                    .add(R.id.content_main, fragment, ParkingListFragment.TAG)
                    .addToBackStack(ParkingListFragment.TAG)
                    .commit();
        } else {
            Log.e(MapFragment.TAG, "Ya esta visible");
        }

        return true;
    }



    /*      */
    @Override
    public void getParkingListResponse(List<ParkingPoint> positionList) {
        setParkings(positionList, 10);
    }

    @Override
    public void pointResponse(ParkingPoint point) {
        firebase.getItemList(point.getParking_id());
    }

    @Override
    public void parkingResponse(String parking_id, ItemFirebase itemFirebase) {
        firebase.getReservationList(parking_id, itemFirebase.getLocation_id());

        Log.e(MapFragment.TAG, parking_id + " " + new Gson().toJson(itemFirebase));
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

    /***
     * mostramos informacion del mapa, enviamos informacion a firebase del usuario
     * que agito el celular y centramos el mapa junto con el parking
     */
    public void actions() {
        marker.showInfoWindow();

        firebase.sendingNotificationToParking();

    }
}
