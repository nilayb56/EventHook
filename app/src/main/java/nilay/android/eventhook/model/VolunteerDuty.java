package nilay.android.eventhook.model;

public class VolunteerDuty {
    private String event_id = "";
    private String duty_id = "";
    private String duty_name = "";
    private String duty_assign = "0";
    private String duty_repeat = "0";

    public VolunteerDuty() {
    }

    public VolunteerDuty(String event_id, String duty_id, String duty_name, String duty_assign, String duty_repeat) {
        this.event_id = event_id;
        this.duty_id = duty_id;
        this.duty_name = duty_name;
        this.duty_assign = duty_assign;
        this.duty_repeat = duty_repeat;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getDuty_id() {
        return duty_id;
    }

    public void setDuty_id(String duty_id) {
        this.duty_id = duty_id;
    }

    public String getDuty_name() {
        return duty_name;
    }

    public void setDuty_name(String duty_name) {
        this.duty_name = duty_name;
    }

    public String getDuty_assign() {
        return duty_assign;
    }

    public void setDuty_assign(String duty_assign) {
        this.duty_assign = duty_assign;
    }

    public String getDuty_repeat() {
        return duty_repeat;
    }

    public void setDuty_repeat(String duty_repeat) {
        this.duty_repeat = duty_repeat;
    }

    @Override
    public String toString() {
        return duty_name;
    }
}
