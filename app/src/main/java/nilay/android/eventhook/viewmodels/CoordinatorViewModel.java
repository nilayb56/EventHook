package nilay.android.eventhook.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import nilay.android.eventhook.model.Users;

public class CoordinatorViewModel extends ViewModel {
    private String roleid = "";
    private String userid = "";
    private String username = "";
    private String collegeid = "";
    private String collegename = "";
    private String eventid = "";
    private String eventname = "";
    private boolean eventIsGroup = false;
    private boolean updateResult = false;
    private List<Users> users;
    private MutableLiveData<String> stateChange;
    private MutableLiveData<String> assignState;

    public MutableLiveData<String> getStateChange() {
        if(stateChange==null){
            MutableLiveData<String> state = new MutableLiveData<>();
            state.setValue("0");
            this.stateChange = state;
        }
        return stateChange;
    }

    public MutableLiveData<String> getAssignState() {
        if(assignState==null){
            MutableLiveData<String> state = new MutableLiveData<>();
            state.setValue("0");
            this.assignState = state;
        }
        return assignState;
    }

    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCollegeid() {
        return collegeid;
    }

    public void setCollegeid(String collegeid) {
        this.collegeid = collegeid;
    }

    public String getCollegename() {
        return collegename;
    }

    public void setCollegename(String collegename) {
        this.collegename = collegename;
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

    public boolean isEventIsGroup() {
        return eventIsGroup;
    }

    public void setEventIsGroup(boolean eventIsGroup) {
        this.eventIsGroup = eventIsGroup;
    }

    public boolean isUpdateResult() {
        return updateResult;
    }

    public void setUpdateResult(boolean updateResult) {
        this.updateResult = updateResult;
    }
}
