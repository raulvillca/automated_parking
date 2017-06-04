package soa.mvp.parkingfinder.model;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import soa.mvp.parkingfinder.R;
import soa.mvp.parkingfinder.service.ParkingFirebaseRequest;
import soa.mvp.parkingfinder.service.PopupCallback;
import soa.mvp.parkingfinder.view.TimePopup;

/**
 * Created by raulvillca on 18/5/17.
 */

public class ParkingListAdapter extends BaseExpandableListAdapter {

    private static String TAG = "ParkingListAdapter";
    private List<Item> locationItems;
    private Context context;
    private LayoutInflater inflater;
    private ParkingFirebaseRequest.ParkingRegister register;

    public ParkingListAdapter(Context context, List<Item> locationItems, ParkingFirebaseRequest.ParkingRegister register) {
        this.locationItems = locationItems;
        this.register = register;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return locationItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return locationItems.get(groupPosition).getTime_list().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return locationItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return locationItems.get(groupPosition).getTime_list().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            view = inflater.inflate(R.layout.parking_item, null);
        } else {
            view = convertView;
        }

        Item item = locationItems.get(groupPosition);

        TextView textView_title = (TextView) view.findViewById(R.id.parking_item_textview_info_text);
        textView_title.setText(item.getLocation_name());

        CardView cardView = (CardView) view.findViewById(R.id.parking_item_cardview);

        switch (item.getLocation_state()) {
            case 0:
                cardView.setBackgroundColor(view.getResources().getColor(R.color.colorGreen));
                break;
            case 1:
                cardView.setBackgroundColor(view.getResources().getColor(R.color.colorYellow));
                break;
            case 2:
                cardView.setBackgroundColor(view.getResources().getColor(R.color.colorRed));
                break;
        }
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            view = inflater.inflate(R.layout.time_item, null);
        } else {
            view = convertView;
        }

        final String item_name = locationItems.get(groupPosition).getLocation_id();
        final Time time = locationItems.get(groupPosition).getTime_list().get(childPosition);

        if (! time.getOption_button()) {
            CardView cardView = (CardView) view.findViewById(R.id.time_item_cardview);
            ((CardView) view.findViewById(R.id.time_item_message)).setVisibility(View.GONE);

            cardView.setVisibility(View.VISIBLE);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePopup(context, v, new PopupCallback() {
                        @Override
                        public void reservation(String item, String start_hour, String final_hour) {
                            Log.e("Modificar", start_hour + " " + final_hour);
                        }
                    }).init(item_name, time.getStart_time(), time.getFinal_time());
                    Snackbar.make(v, "Modificar", Snackbar.LENGTH_LONG).show();
                }
            });

            TextView textView_start_time = (TextView) view.findViewById(R.id.time_item_start_time);
            TextView textView_final_time = (TextView) view.findViewById(R.id.time_item_final_time);
            textView_start_time.setText(time.getStart_time());
            textView_final_time.setText(time.getFinal_time());

        } else {
            ((CardView) view.findViewById(R.id.time_item_cardview)).setVisibility(View.GONE);
            CardView cardView = (CardView) view.findViewById(R.id.time_item_message);
            cardView.setVisibility(View.VISIBLE);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePopup(context, v, new PopupCallback() {
                        @Override
                        public void reservation(String item, String start_hour, String final_hour) {
                            register.doingRegister(item, start_hour, final_hour);
                            Log.e("REGISTRAR", start_hour + " " + final_hour);
                        }
                    }).init(item_name);
                    Snackbar.make(v, "Agregar nuevo", Snackbar.LENGTH_LONG).show();
                }
            });
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
