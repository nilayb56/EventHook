package nilay.android.eventhook.model;

public class ApproveUser {
    private String clg_name = "";
    private String user_id = "";
    private String user_name = "";
    private String email_id = "";
    private String event_id = "";
    private String img_url = "";

    public ApproveUser() {
    }

    public ApproveUser(String user_id, String user_name, String email_id, String event_id) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.email_id = email_id;
        this.event_id = event_id;
    }

    public ApproveUser(String clg_name, String user_id, String user_name, String email_id, String img_url) {
        this.clg_name = clg_name;
        this.user_id = user_id;
        this.user_name = user_name;
        this.email_id = email_id;
        this.img_url = img_url;
    }

    public String getClg_name() {
        return clg_name;
    }

    public void setClg_name(String clg_name) {
        this.clg_name = clg_name;
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

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
