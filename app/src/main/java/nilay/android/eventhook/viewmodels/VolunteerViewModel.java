package nilay.android.eventhook.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;
import java.util.List;

import nilay.android.eventhook.model.GroupMaster;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.volunteer.AttndAdapter;
import nilay.android.eventhook.volunteer.ConfirmPaymentAdapter;

public class VolunteerViewModel extends ViewModel {
    private String roleid = "";
    private String userid = "";
    private String username = "";
    private String collegeid = "";
    private String collegename = "";
    private String eventid = "";
    private String eventname = "";
    private String ifgroup = "";
    private String dutyid = "";
    private String dutyname = "";
    private Date eventdate;
    private Date currentDate;
    private String rank = "";
    private AttndAdapter attndAdapter;
    private ConfirmPaymentAdapter paymentAdapter;
    private List<GroupMaster> groupList;
    private List<UserParticipation> participantList;
    private String winnerId = "";
    private String winnerName = "";
    private boolean quickReg = false;
    private boolean generalConfirmation = false;
    private String prevRegisteredEventId = "";
    private MutableLiveData<String> stateChange;

    public MutableLiveData<String> getStateChange() {
        if(stateChange==null){
            MutableLiveData<String> state = new MutableLiveData<>();
            state.setValue("0");
            this.stateChange = state;
        }
        return stateChange;
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

    public String getIfgroup() {
        return ifgroup;
    }

    public void setIfgroup(String ifgroup) {
        this.ifgroup = ifgroup;
    }

    public String getDutyid() {
        return dutyid;
    }

    public void setDutyid(String dutyid) {
        this.dutyid = dutyid;
    }

    public String getDutyname() {
        return dutyname;
    }

    public void setDutyname(String dutyname) {
        this.dutyname = dutyname;
    }

    public Date getEventdate() {
        return eventdate;
    }

    public void setEventdate(Date eventdate) {
        this.eventdate = eventdate;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }


    public AttndAdapter getAttndAdapter() {
        return attndAdapter;
    }

    public void setAttndAdapter(AttndAdapter attndAdapter) {
        this.attndAdapter = attndAdapter;
    }

    public ConfirmPaymentAdapter getPaymentAdapter() {
        return paymentAdapter;
    }

    public void setPaymentAdapter(ConfirmPaymentAdapter paymentAdapter) {
        this.paymentAdapter = paymentAdapter;
    }

    public List<GroupMaster> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupMaster> groupList) {
        this.groupList = groupList;
    }

    public List<UserParticipation> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(List<UserParticipation> participantList) {
        this.participantList = participantList;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public boolean isQuickReg() {
        return quickReg;
    }

    public void setQuickReg(boolean quickReg) {
        this.quickReg = quickReg;
    }

    public boolean isGeneralConfirmation() {
        return generalConfirmation;
    }

    public void setGeneralConfirmation(boolean generalConfirmation) {
        this.generalConfirmation = generalConfirmation;
    }

    public String getPrevRegisteredEventId() {
        return prevRegisteredEventId;
    }

    public void setPrevRegisteredEventId(String prevRegisteredEventId) {
        this.prevRegisteredEventId = prevRegisteredEventId;
    }
}
