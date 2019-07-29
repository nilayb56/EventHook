package nilay.android.eventhook.model;

public class Event {
    String event_id;
    String event_name;
    String college_id;
    Integer group_event;
    Integer group_members;
    Integer mode_private;
    String reg_start_date;
    String reg_end_date;
    String event_date;
    String cancel_date;
    String event_fees;

    public Event() {
    }

    public Event(String event_id, String event_name, String college_id, Integer group_event, Integer group_members, Integer mode_private, String reg_start_date, String reg_end_date, String event_date, String cancel_date, String event_fees) {
        this.event_id = event_id;
        this.event_name = event_name;
        this.college_id = college_id;
        this.group_event = group_event;
        this.group_members = group_members;
        this.mode_private = mode_private;
        this.reg_start_date = reg_start_date;
        this.reg_end_date = reg_end_date;
        this.event_date = event_date;
        this.cancel_date = cancel_date;
        this.event_fees = event_fees;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getCollege_id() {
        return college_id;
    }

    public void setCollege_id(String college_id) {
        this.college_id = college_id;
    }

    public Integer getGroup_event() {
        return group_event;
    }

    public void setGroup_event(Integer group_event) {
        this.group_event = group_event;
    }

    public Integer getGroup_members() {
        return group_members;
    }

    public void setGroup_members(Integer group_members) {
        this.group_members = group_members;
    }

    public Integer getMode_private() {
        return mode_private;
    }

    public void setMode_private(Integer mode_private) {
        this.mode_private = mode_private;
    }

    public String getReg_start_date() {
        return reg_start_date;
    }

    public void setReg_start_date(String reg_start_date) {
        this.reg_start_date = reg_start_date;
    }

    public String getReg_end_date() {
        return reg_end_date;
    }

    public void setReg_end_date(String reg_end_date) {
        this.reg_end_date = reg_end_date;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getCancel_date() {
        return cancel_date;
    }

    public void setCancel_date(String cancel_date) {
        this.cancel_date = cancel_date;
    }

    public String getEvent_fees() {
        return event_fees;
    }

    public void setEvent_fees(String event_fees) {
        this.event_fees = event_fees;
    }

    @Override
    public String toString() {
        return "Event{" +
                "event_id='" + event_id + '\'' +
                ", event_name='" + event_name + '\'' +
                ", college_id='" + college_id + '\'' +
                ", group_event=" + group_event +
                ", group_members=" + group_members +
                ", mode_private=" + mode_private +
                ", reg_start_date='" + reg_start_date + '\'' +
                ", reg_end_date='" + reg_end_date + '\'' +
                ", event_date='" + event_date + '\'' +
                ", cancel_date='" + cancel_date + '\'' +
                ", event_fees='" + event_fees + '\'' +
                '}';
    }
}
