package soa.mvp.parkingfinder.refactor;

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

import soa.mvp.parkingfinder.model.ItemFirebase;
import soa.mvp.parkingfinder.model.ParkingPoint;
import soa.mvp.parkingfinder.model.Time;
import soa.mvp.parkingfinder.model.User;
import soa.mvp.parkingfinder.service.ParkingFirebaseRequest;

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

    public void getUserList() {
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

    public void getPositionList () {
        database.child("parkings").addValueEventListener(new ValueEventListener() {
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

    public void getItemList(final String parking_id) {
        Log.e("PathReservation", database.child(parking_id).child("locations").toString());
        database.child(parking_id).child("locations").addValueEventListener(new ValueEventListener() {
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
                        parkingFirebase.parkingResponse(parking_id, item);

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

                            parkingFirebase.parkingResponse(parking_id, newItem);
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

    public void getReservationList(final String parking_id, final String location_id) {
        Log.e("PathReservation", database.child(location_id).child(parking_id).child("times").toString());
        database.child(location_id).child(parking_id).child("times").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Time> timeList = new ArrayList<Time>();
                for (DataSnapshot reservationDataSnapshot : dataSnapshot.getChildren()) {
                    Time time = reservationDataSnapshot.getValue(Time.class);

                    timeList.add(time);
                }

                if (times.isEmpty()) {
                    times = timeList;

                    Log.e("Mensaje", "Nuevo");
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

    public void createParkings(ParkingPoint position) {

        database.child("parkings")
                .child(position.getParking_id()).setValue(position);
    }

    public void createLocationItem(String position_id, ItemFirebase location) {

        database.child(position_id)
                .child("locations")
                .child(location.getLocation_id()).setValue(location);
    }

    public void createNewReservation(String parking_id, String location_id, Time time) {
        time.setTime_id(database
                .child(location_id)
                .child(parking_id)
                .child("times").push().getKey());

        database.child(location_id)
                .child(parking_id)
                .child("times")
                .child(time.getTime_id()).setValue(time);
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

    public void setLogin(boolean typeLogin) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLogged", typeLogin);
        editor.commit();
    }
}
