package nilay.android.eventhook.model;

public class Volunteer {
    private String user_id="";
    private String user_name="";
    private String event_id="";
    private String valid_user="0";
    private String duty_id="0";
    private double latitude = 0d;
    private double longitude = 0d;
    private String mobile_number = "";

    public Volunteer() {
    }

    public Volunteer(String user_id, String event_id, String valid_user, String duty_id) {
        this.user_id = user_id;
        this.event_id = event_id;
        this.valid_user = valid_user;
        this.duty_id = duty_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getValid_user() {
        return valid_user;
    }

    public void setValid_user(String valid_user) {
        this.valid_user = valid_user;
    }

    public String getDuty_id() {
        return duty_id;
    }

    public void setDuty_id(String duty_id) {
        this.duty_id = duty_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    @Override
    public String toString() {
        return user_id;
    }
}
