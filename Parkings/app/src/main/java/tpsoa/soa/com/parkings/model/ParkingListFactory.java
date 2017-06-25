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

        items.add(new Item("parking_a", "Parcela A", getTimeList(), 1));
        items.add(new Item("parking_b", "Parcela B", new ArrayList<Time>(), 0));

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
        time.setStart_time("7:00");
        time.setFinal_time("12:00");
        time.setOption_button(false);
        time.setUser_gcm("");

        times.add(time);

        time = new Time();

        time.setUser_gcm("");
        time.setOption_button(false);
        time.setTime_id("2");
        time.setStart_time("15:00");
        time.setFinal_time("18:00");
        times.add(time);

        return times;
    }
}
