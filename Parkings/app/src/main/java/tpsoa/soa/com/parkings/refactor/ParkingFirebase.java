package tpsoa.soa.com.parkings.refactor;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import tpsoa.soa.com.parkings.model.ItemFirebase;
import tpsoa.soa.com.parkings.model.ParkingListFactory;
import tpsoa.soa.com.parkings.model.ParkingPoint;
import tpsoa.soa.com.parkings.model.RaspNotification;
import tpsoa.soa.com.parkings.model.Time;
import tpsoa.soa.com.parkings.model.User;
import tpsoa.soa.com.parkings.service.ParkingFirebaseRequest;


public class ParkingFirebase {
    public static String TAG = "ParkingFirebase";
    private Fragment fragment;
    private DatabaseReference database;
    private SharedPreferences preferences;
    private DatabaseReference reference;
    private ParkingFirebaseRequest.UserFirebaseResponse userFirebase;
    private ParkingFirebaseRequest.ParkingFirebaseResponse parkingFirebase;

    private List<User> users;
    private List<ParkingPoint> positions;
    private List<ItemFirebase> locationItems;
    private List<Time> times;

    public ParkingFirebase(Fragment fragment) {
        this.fragment = fragment;
        this.preferences = fragment.getActivity().getSharedPreferences(ParkingFirebase.TAG, fragment.getActivity().MODE_PRIVATE);
        this.users = new ArrayList<User>();
        this.positions = new ArrayList<ParkingPoint>();
        this.locationItems = new ArrayList<ItemFirebase>();
        this.times = new ArrayList<Time>();
    }

    public void setParkingFirebase(Fragment parkingFirebase) {
        this.parkingFirebase = (ParkingFirebaseRequest.ParkingFirebaseResponse) parkingFirebase;
    }

    public void setUserFirebase(Fragment userFirebase) {
        this.userFirebase = (ParkingFirebaseRequest.UserFirebaseResponse) fragment;
    }

    public void connect() {
        database = FirebaseDatabase.getInstance().getReference();
        reference = database.getDatabase().getReference();
    }

    /***
     * Tablas
     * Lista de horarios
     * parking_a/parkings/times/nodos
     *
     * Lista de parking
     * location_a/locations/nodos
     *
     * Lista de Informacion
     * locations/location_a/nodo
     */

    public void getUserList() {
        Log.e(ParkingFirebase.TAG, "users");
        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<User> userList = new ArrayList<User>();
                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                    User user = userDataSnapshot.getValue(User.class);
                    userList.add(user);
                }

                users = userList;

                Log.e("Json", new Gson().toJson(users));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean userExist(String user) {
        boolean exists = false;
        for (User userCompare: users) {
            if (userCompare != null && user != null
                    && userCompare.getUser_email() != null
                    && user.equals(userCompare.getUser_email())) {
                exists = true;
                Log.e("Encontrado", "Result OK");
                break;
            }
        }

        return exists;
    }

    public boolean userCompare(String user, String pass) {
        boolean exists = false;

        for (User userCompare: users) {
            if (userCompare != null && user != null
                    && userCompare.getUser_email() != null
                    && user.equals(userCompare.getUser_email())
                    && pass.equals(userCompare.getUser_password())) {
                exists = true;
                break;
            }
        }

        return exists;
    }

    public void createNewUser(final User newUser) {
        newUser.setUser_id(database.child("users").push().getKey());
        database.child("users").child(newUser.getUser_id()).setValue(newUser);
    }

    /***
     * NODO TABLA locations
     * obtenemos las posiciones de todos los marcadores para ubicarlos en el mapa
     */
    public void getPositionList () {

        database.child("locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<ParkingPoint> positionList = new ArrayList<ParkingPoint>();

                for (DataSnapshot reservationDataSnapshot : dataSnapshot.getChildren()) {
                    ParkingPoint position = reservationDataSnapshot.getValue(ParkingPoint.class);

                    positionList.add(position);
                }

                if (positions.isEmpty()) {
                    positions = positionList;

                    for (ParkingPoint point: positions)
                        parkingFirebase.pointResponse(point);

                } else {
                    boolean isNewPosition = true;

                    for (ParkingPoint newPosition: positionList) {
                        for (ParkingPoint position: positions) {
                            if (position.getParking_id().equals(newPosition.getParking_id())) {
                                position.setLongitude(newPosition.getLongitude());
                                position.setLatitude(newPosition.getLatitude());
                                position.setDescription(newPosition.getDescription());
                                isNewPosition = false;
                            }
                        }

                        if (isNewPosition) {
                            positions.add(newPosition);

                            parkingFirebase.pointResponse(newPosition);
                        }
                    }
                }

                Log.e("Json", new Gson().toJson(positions));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("getPositionList", databaseError.getMessage());
            }
        });
    }

    /***
     * NODO TABLA location_a/locations/nodos
     * obtenemos las listas de parcelas con el estado de cada una
     * @param location_id
     */
    public void getItemList(final String location_id) {
        //location_a/locations/nodos
        Log.e(ParkingFirebase.TAG, "parking_id=" + location_id + "/locations");
        database.child(location_id).child("locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.e("JsonParser", dataSnapshot.toString());
                List<ItemFirebase> itemList = new ArrayList<ItemFirebase>();
                for (DataSnapshot reservationDataSnapshot : dataSnapshot.getChildren()) {
                    ItemFirebase locationItem = reservationDataSnapshot.getValue(ItemFirebase.class);

                    itemList.add(locationItem);
                }

                if (locationItems.isEmpty()) {
                    locationItems = itemList;

                    for (ItemFirebase item: locationItems)
                        parkingFirebase.parkingResponse(item.getLocation_id(), item);

                } else {
                    boolean isNewItem = true;

                    for (ItemFirebase newItem: itemList) {
                        for (ItemFirebase item: locationItems) {
                            if (item.getLocation_id().equals(newItem.getLocation_id())) {
                                item.setLocation_state(newItem.getLocation_state());
                                item.setLocation_name(newItem.getLocation_name());

                                isNewItem = false;
                            }
                        }

                        if (isNewItem) {
                            locationItems.add(newItem);

                            parkingFirebase.parkingResponse(newItem.getLocation_id(), newItem);
                        }
                    }

                }

                Log.e("JsonParkings", new Gson().toJson(locationItems));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("getItemList", databaseError.getMessage());
            }
        });
    }

    /***
     * NODO TABLA parking_a/parkings/times/nodos
     * Obtenemos la lista de horarios de cada parcela
     * @param parking_id
     * @param location_id
     */
    public void getReservationList(final String location_id, final String parking_id) {

        //parking_a/parkings/times/nodos
        Log.e(ParkingFirebase.TAG + ":getReservationList", location_id + " / " + parking_id);
        Log.e("PathReservation", database.child(parking_id).child("parkings").child("times").toString());
        database.child(parking_id).child("parkings").child("times").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Time> timeList = new ArrayList<Time>();
                for (DataSnapshot reservationDataSnapshot : dataSnapshot.getChildren()) {
                    Time time = reservationDataSnapshot.getValue(Time.class);

                    timeList.add(time);
                }

                if (times.isEmpty()) {
                    times = timeList;

                    //enviamos todos los tiempos reservados al fragmento principal
                    for (Time time: timeList)
                        parkingFirebase.timeListener(parking_id, location_id, time);

                } else {

                    if (times.size() >= timeList.size()) {
                        parkingFirebase.updateTimeListListener(parking_id, location_id, timeList);
                    } else {

                        for (Time newTime : timeList) {
                            boolean isNewTime = true;
                            for (Time time : times) {
                                if (time.getTime_id().equals(newTime.getTime_id())) {
                                    time.setUser_gcm(newTime.getUser_gcm());
                                    time.setStart_time(newTime.getStart_time());
                                    time.setFinal_time(newTime.getFinal_time());

                                    isNewTime = false;
                                }
                            }

                            if (isNewTime) {
                                times.add(newTime);

                                parkingFirebase.timeListener(parking_id, location_id, newTime);
                            }
                        }
                    }
                }

                Log.e("JsonTimes", new Gson().toJson(times));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("getReservationList", databaseError.getMessage());
            }
        });
    }

    /***
     * publicar en firebase una notificacion para que la placa evalue los datos
     * y muestre el nombre del usuario
     *
     */
    public void sendingNotificationToParking() {
        User user = null;
        String json = preferences.getString("user", null);
        if (json != null) {
            user = new Gson().fromJson(json, User.class);
        } else {
            user = new User();
            user.setUser_fullname("Mr. X");
        }
        //notifications/nodo
        RaspNotification notification = new RaspNotification();
        notification.setFullname(user.getUser_fullname());
        notification.setId(user.getUser_id());
        notification.setState(true);
        database.child("notifications")
                .child(user.getUser_id()).setValue(notification);
    }

    public void createDB(ParkingPoint position) {

        //locations/location_a/nodo
        database.child("locations")
                .child(position.getParking_id()).setValue(position);

        //unlam/locations/nodos
        ItemFirebase nuevo = new ItemFirebase();
        nuevo.setLocation_id("parking_a");
        nuevo.setLocation_name("Parcela A");
        nuevo.setLocation_state(0);
        database.child("unlam")
                .child("locations")
                .child(nuevo.getLocation_id()).setValue(nuevo);

        //parking_a/parkings/times/nodos
        Time time = new Time();
        time.setFinal_time("19:00");
        time.setStart_time("15:00");
        time.setOption_button(false);
        time.setUser_gcm("token");
        time.setTime_id(database
                .child("parking_a")
                .child("parkings")
                .child("times").push().getKey());

        database.child("parking_a")
                .child("parkings")
                .child("times")
                .child(time.getTime_id()).setValue(time);
    }

    public void createNewReservation(String location_id, String parking_id, Time time) {
        Log.e(ParkingFirebase.TAG, parking_id + " " + location_id);
        //parking_a/parkings/times/nodos
        time.setTime_id(database
                .child(parking_id)
                .child("parkings")
                .child("times").push().getKey());

        database.child(parking_id)
                .child("parkings")
                .child("times")
                .child(time.getTime_id()).setValue(time);
    }

    public boolean removeItem(String parking_id, String location_id, Time time_item_list, Time time) {

        if (time_item_list.getUser_gcm().equals(time.getUser_gcm())) {
            database.child(parking_id).child("parkings").child("times").child(time.getUser_gcm()).removeValue();
            //database.child(location_id).child(parking_id).child("times").child(time.getTime_id()).setValue(null);
            return true;
        } else
            return false;
    }

    /***
     * Si el login es true, entonces el usuario tiene contraseña
     * sino el inicio fue con huella digital
     *
     * @param user
     * @param type_login
     */
    public void saveLogIn(User user, boolean type_login) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("user", new Gson().toJson(user));
        edit.putString("user_fcm", user.getUser_gcm());
        edit.putBoolean("type_login", type_login);
        edit.commit();
    }

    public boolean getTypeLogin() {
        return preferences.getBoolean("type_login", true);
    }

    public void setGCM(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_gcm", token);
        editor.commit();
    }

    /***
     * guardamos en SharedPreferences el estado del login
     * @param typeLogin
     */
    public void setLogin(boolean typeLogin) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLogged", typeLogin);
        editor.commit();
    }
}
