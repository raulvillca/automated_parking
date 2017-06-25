package tpsoa.soa.com.parkings.view;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import tpsoa.soa.com.parkings.R;
import tpsoa.soa.com.parkings.service.PopupCallback;

public class TimePopup {
    private Context context;
    private View view;
    private AlertDialog.Builder builder;
    private String start_time;
    private String final_time;
    private TextView textView_start_time;
    private TextView textView_final_time;
    private PopupCallback callback;
    private String item_name;

    public TimePopup(Context context, View v, PopupCallback callback) {
        this.context = context;
        this.callback = callback;
        this.start_time = "00:00";
        this.final_time = "01:00";

        builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.parking_item_add_title_popup));

        view = LayoutInflater.from(context).inflate(R.layout.time_item, (ViewGroup) v, false);
        ((CardView) view.findViewById(R.id.time_item_cardview)).setCardElevation(0);
        ((CardView) view.findViewById(R.id.time_item_cardview)).setRadius(0);

        textView_start_time = (TextView) view.findViewById(R.id.time_item_start_time);
        textView_final_time = (TextView) view.findViewById(R.id.time_item_final_time);

        Calendar mcurrentTime = Calendar.getInstance();
        int hh = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int mm = mcurrentTime.get(Calendar.MINUTE);

        String hour = hh < 10 ? "0" + hh : hh + "";
        String minute = mm < 10 ? "0" + mm : mm + "";
        start_time = hour + ":" + minute;
        textView_start_time.setText(start_time);

        final_time = hour + ":" + minute;
        textView_final_time.setText(final_time);
    }

    public void init(final String item_name) {
        this.item_name = item_name;

        textView_start_time.setText(start_time);
        textView_final_time.setText(final_time);

        //TODO obtenemos informacion del tiempo de inicio
        textView_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String hour = selectedHour < 10 ? "0" + selectedHour : selectedHour + "";
                        String minute = selectedMinute < 10 ? "0" + selectedMinute : selectedMinute + "";
                        start_time = hour + ":" + minute;
                        textView_start_time.setText(start_time);
                    }
                }, hour, minute, true);
                mTimePicker.show();
            }
        });

        //TODO obtenemos informacion del tiempo de finalizacion
        textView_final_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String hour = selectedHour < 10 ? "0" + selectedHour : selectedHour + "";
                        String minute = selectedMinute < 10 ? "0" + selectedMinute : selectedMinute + "";
                        final_time = hour + ":" + minute;
                        textView_final_time.setText(final_time);
                    }
                }, hour, minute, true);
                mTimePicker.show();
            }
        });

        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.reservation(item_name, start_time, final_time);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
