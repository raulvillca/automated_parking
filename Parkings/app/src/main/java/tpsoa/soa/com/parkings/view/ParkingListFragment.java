package tpsoa.soa.com.parkings.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import tpsoa.soa.com.parkings.R;
import tpsoa.soa.com.parkings.model.Item;
import tpsoa.soa.com.parkings.model.ParkingListAdapter;
import tpsoa.soa.com.parkings.model.ParkingListFactory;
import tpsoa.soa.com.parkings.model.Time;
import tpsoa.soa.com.parkings.refactor.ParkingFirebase;
import tpsoa.soa.com.parkings.service.ParkingFirebaseRequest;

public class ParkingListFragment extends Fragment implements ParkingFirebaseRequest.ParkingRegister {
    public static String TAG = "ParkingListFragment";
    private ExpandableListView expandableListView;
    private List<Item> list;
    private ParkingFirebase firebase;

    public ParkingListFragment() {
        list = ParkingListFactory.getItemList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.parking_list, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        String jsonItems = bundle.getString("items");
        list = new Gson().fromJson(jsonItems, new TypeToken<List<Item>>(){}.getType());

        firebase = new ParkingFirebase(this);
        firebase.connect();

        expandableListView = (ExpandableListView) getActivity().findViewById(R.id.fragment_expandable_list_location);

        getLocationListResponse(list);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //TODO si no hay horarios registrados mostrar mensaje
                if (list.get(groupPosition).getTime_list().isEmpty())
                    Snackbar.make(v, getString(R.string.parking_item_message), Snackbar.LENGTH_LONG).show();

                return false;
            }
        });
    }

    public void getLocationListResponse(List<Item> itemList) {

        for (Item item: itemList) {
            if ( ! item.getTime_list().isEmpty()) {
                Time time = new Time();
                time.setOption_button(true);
                List<Time> times = item.getTime_list();
                if ( ! times.isEmpty() && times.get(times.size() - 1).getOption_button() != true)
                    item.getTime_list().add(time);
            } else {
                Time time = new Time();
                time.setOption_button(true);
                List<Time> times = item.getTime_list();
                item.setTime_list(times);
            }
        }


        Log.e(ParkingListFragment.TAG, "Recargar " + new Gson().toJson(itemList));
        //TODO Rearmamos una lista con el item agregar y la pasamos a nuestra VIEW ExpandableListView
        expandableListView.setAdapter(new ParkingListAdapter(getActivity(), itemList, this));
    }

    /****
     * Registramos un nuevo horario con el token del usuario en firebase
     * @param item_name
     * @param start_time
     * @param final_time
     */
    @Override
    public void doingRegister(String item_name, String start_time, String final_time) {
        Time time = new Time();
        time.setStart_time(start_time);
        time.setFinal_time(final_time);
        time.setOption_button(false);
        time.setUser_gcm(FirebaseInstanceId.getInstance().getToken());
        Log.e(ParkingListFragment.TAG, " " + item_name + " " + start_time + " " + final_time);
        firebase.createNewReservation("unlam", item_name, time);
    }
}
