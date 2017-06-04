package soa.mvp.parkingfinder.model;

/**
 * Created by raulvillca on 18/5/17.
 */
public class Time {
    private String time_id;
    private String start_time;
    private String final_time;
    private String user_gcm;
    private boolean option_button;

    public String getTime_id() {
        return time_id;
    }

    public void setTime_id(String time_id) {
        this.time_id = time_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getFinal_time() {
        return final_time;
    }

    public void setFinal_time(String final_time) {
        this.final_time = final_time;
    }

    public String getUser_gcm() {
        return user_gcm;
    }

    public void setUser_gcm(String user_gcm) {
        this.user_gcm = user_gcm;
    }

    public boolean getOption_button() {
        return option_button;
    }

    public void setOption_button(boolean option_button) {
        this.option_button = option_button;
    }
}
