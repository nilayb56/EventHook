package nilay.android.eventhook.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import nilay.android.eventhook.collegeadmin.ApproveCoordinatorAdapter;
import nilay.android.eventhook.interfaces.CollegeData;

public class ClgAdminViewModel extends ViewModel implements CollegeData {
    private String roleid = "";
    private String collegeid = "";
    private String collegename = "";
    private String eventid = "";
    private String eventname = "";
    private String newRegisteredEventId = "";
    private MutableLiveData<String> stateChange;
    private MutableLiveData<String> eventRegState;

    public MutableLiveData<String> getStateChange() {
        if(stateChange==null){
            MutableLiveData<String> state = new MutableLiveData<>();
            state.setValue("0");
            this.stateChange = state;
        }
        return stateChange;
    }

    public MutableLiveData<String> getEventRegState() {
        if(eventRegState==null){
            MutableLiveData<String> state = new MutableLiveData<>();
            state.setValue("0");
            this.eventRegState = state;
        }
        return eventRegState;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    @Override
    public void clgdata(String id, String name) {
        this.collegeid = id;
        this.collegename = name;
    }

    public String getCollegeid() {
        return collegeid;
    }

    public String getCollegename() {
        return collegename;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    public String getNewRegisteredEventId() {
        return newRegisteredEventId;
    }

    public void setNewRegisteredEventId(String newRegisteredEventId) {
        this.newRegisteredEventId = newRegisteredEventId;
    }
}
