package tpsoa.soa.com.parkings.model;

import java.util.ArrayList;
import java.util.List;

public class ParkingListFactory {
    /***
     * Creamos una lista de items por defecto para la ExpandableListView
     * fragment_expandable_list_location
     * @return
     */
    public static List<Item> getItemList() {
        List<Item> items = new ArrayList<>();

        items.add(new Item("parking_a", "Parcela A", getTimeList(), 0));
        items.add(new Item("parking_b", "Parcela B", getTimeList(), 0));

        return items;
    }

    /***
     * Creamos lista de SubItems para la ExpandableListView
     * fragment_expandable_list_location
     * @return
     */
    public static List<Time> getTimeList() {
        List<Time> times = new ArrayList<>();

        Time time = new Time();
        time.setTime_id("1");
        time.setOption_button(true);
        time.setUser_gcm("");

        times.add(time);

        return times;
    }
}
