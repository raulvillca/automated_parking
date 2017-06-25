package tpsoa.soa.com.parkings.model;


public class User {
    private String user_id;
    private String user_email;
    private String user_fullname;
    private String user_password;
    private boolean user_access_noun;
    private String user_gcm;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_fullname() {
        return user_fullname;
    }

    public void setUser_fullname(String user_fullname) {
        this.user_fullname = user_fullname;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public boolean isUser_access_noun() {
        return user_access_noun;
    }

    public void setUser_access_noun(boolean user_access_noun) {
        this.user_access_noun = user_access_noun;
    }

    public String getUser_gcm() {
        return user_gcm;
    }

    public void setUser_gcm(String user_gcm) {
        this.user_gcm = user_gcm;
    }
}
