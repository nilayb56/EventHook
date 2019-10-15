package nilay.android.eventhook.model;

public class UserParticipation {
    private String user_id = "";
    private String user_name = "";
    private String event_id = "";
    private String group_id = "0";
    private String group_name = "0";
    private String fest_attendance = "0";
    private String event_attendance = "0";
    private String reg_status = "1";
    private String payment_status = "0";
    private String before_event = "0";
    private String event_day = "0";
    private Integer content_submission = 0;

    public UserParticipation() {
    }

    public UserParticipation(String user_id, String user_name) {
        this.user_id = user_id;
        this.user_name = user_name;
    }

    public UserParticipation(String user_id, String user_name, String event_id) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.event_id = event_id;
    }

    public UserParticipation(String user_id, String user_name, String event_id, String group_id, String group_name) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.event_id = event_id;
        this.group_id = group_id;
        this.group_name = group_name;
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

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getFest_attendance() {
        return fest_attendance;
    }

    public void setFest_attendance(String fest_attendance) {
        this.fest_attendance = fest_attendance;
    }

    public String getEvent_attendance() {
        return event_attendance;
    }

    public void setEvent_attendance(String event_attendance) {
        this.event_attendance = event_attendance;
    }

    public String getReg_status() {
        return reg_status;
    }

    public void setReg_status(String reg_status) {
        this.reg_status = reg_status;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getBefore_event() {
        return before_event;
    }

    public void setBefore_event(String before_event) {
        this.before_event = before_event;
    }

    public String getEvent_day() {
        return event_day;
    }

    public void setEvent_day(String event_day) {
        this.event_day = event_day;
    }

    public Integer getContent_submission() {
        return content_submission;
    }

    public void setContent_submission(Integer content_submission) {
        this.content_submission = content_submission;
    }

    @Override
    public String toString() {
        return user_name;
    }
}
