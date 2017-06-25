package tpsoa.soa.com.parkings.model;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import tpsoa.soa.com.parkings.R;
import tpsoa.soa.com.parkings.service.ParkingFirebaseRequest;
import tpsoa.soa.com.parkings.service.PopupCallback;
import tpsoa.soa.com.parkings.view.TimePopup;

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
        //TODO obtenemos la lista de parkings, primera lista
        Item item = locationItems.get(groupPosition);

        TextView textView_title = (TextView) view.findViewById(R.id.parking_item_textview_info_text);
        textView_title.setText(item.getLocation_name());

        CardView cardView = (CardView) view.findViewById(R.id.parking_item_cardview);

        switch (item.getTime_list().size()) {
            case 1:
                cardView.setBackgroundColor(view.getResources().getColor(R.color.colorGreen));
                break;
            default:
                cardView.setBackgroundColor(view.getResources().getColor(R.color.colorYellow));
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

        //TODO obtenemos la segunda lista, lista de horarios registados
        final String item_name = locationItems.get(groupPosition).getLocation_id();
        final Time time = locationItems.get(groupPosition).getTime_list().get(childPosition);

        //TODO solo se realizan registro de horarios, no se modifican si aunque se equivoquen
        //en caso de erorr que registre uno nuevo
        if (! time.getOption_button()) {
            CardView cardView = (CardView) view.findViewById(R.id.time_item_cardview);
            ((CardView) view.findViewById(R.id.time_item_message)).setVisibility(View.GONE);

            cardView.setVisibility(View.VISIBLE);

            /**cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePopup(context, v, new PopupCallback() {
                        @Override
                        public void reservation(String item, String start_hour, String final_hour) {
                            Log.e("Modificar", start_hour + " " + final_hour);
                        }
                    }).init(item_name, time.getStart_time(), time.getFinal_time());
                }
            });*/

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
