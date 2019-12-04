package nilay.android.eventhook.model;

public class Coordinator {
    private String user_id="";
    private String event_id="";
    private String valid_user="0";

    public Coordinator() {
    }

    public Coordinator(String user_id, String event_id, String valid_user) {
        this.user_id = user_id;
        this.event_id = event_id;
        this.valid_user = valid_user;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    @Override
    public String toString() {
        return user_id;
    }
}
