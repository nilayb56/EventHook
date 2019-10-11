package nilay.android.eventhook.model;

public class GroupMaster {
    private String group_id="";
    private String event_id="";
    private String group_name="";
    private String group_leader="";
    private Integer payments_confirmed = 0;
    private Integer unregistered_count;

    public GroupMaster() {
    }

    public GroupMaster(String group_id, String group_name) {
        this.group_id = group_id;
        this.group_name = group_name;
    }

    public GroupMaster(String group_id, String event_id, String group_name, String group_leader, Integer payments_confirmed, Integer unregistered_count) {
        this.group_id = group_id;
        this.event_id = event_id;
        this.group_name = group_name;
        this.group_leader = group_leader;
        this.payments_confirmed = payments_confirmed;
        this.unregistered_count = unregistered_count;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_leader() {
        return group_leader;
    }

    public void setGroup_leader(String group_leader) {
        this.group_leader = group_leader;
    }

    public Integer getPayments_confirmed() {
        return payments_confirmed;
    }

    public void setPayments_confirmed(Integer payments_confirmed) {
        this.payments_confirmed = payments_confirmed;
    }

    public Integer getUnregistered_count() {
        return unregistered_count;
    }

    public void setUnregistered_count(Integer unregistered_count) {
        this.unregistered_count = unregistered_count;
    }

    @Override
    public String toString() {
        return group_name;
    }
}
