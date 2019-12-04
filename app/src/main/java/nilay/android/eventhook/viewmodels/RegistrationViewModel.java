package nilay.android.eventhook.viewmodels;

import android.widget.AdapterView;

import androidx.lifecycle.ViewModel;
import androidx.viewpager.widget.ViewPager;

import nilay.android.eventhook.interfaces.GroupInfo;
import nilay.android.eventhook.interfaces.SelectedCollege;
import nilay.android.eventhook.interfaces.UserType;
import nilay.android.eventhook.registration.RegistrationAdapter;

public class RegistrationViewModel extends ViewModel implements UserType, SelectedCollege, GroupInfo {
    private String eventid = "";
    private String isGroupEvent = "";
    private String eventFees = "";

    private String roleid = "";
    private String rolename = "";

    private int spinnerPos = 0;
    private String collegeid = "";
    private String collegename = "";
    private AdapterView adapterView;

    private String userid = "";
    private String username = "";
    private String email_id = "";
    private boolean validEmail;
    private boolean noListedClg = false;
    private boolean userExists = false;

    private String groupid = "";
    private String groupname = "";
    private Integer minmembers = 0;
    private Integer maxmembers = 0;

    private Integer visibleMemberCount = 0;
    private Integer viewPagerPos = 0;
    private RegistrationAdapter registrationAdapter;
    private boolean emailConfirmed = false;
    private boolean leaderRegFlag = false;
    private boolean regFlag = false;

    public void setEventid(String id) {
        this.eventid = id;
    }

    public String getEventid() {
        return eventid;
    }

    public String getIsGroupEvent() {
        return isGroupEvent;
    }

    public void setIsGroupEvent(String isGroupEvent) {
        this.isGroupEvent = isGroupEvent;
    }

    public String getEventFees() {
        return eventFees;
    }

    public void setEventFees(String eventFees) {
        this.eventFees = eventFees;
    }

    @Override
    public void userType(String id, String type) {
        this.roleid = id;
        this.rolename = type;
    }

    public String getRoleid() {
        return roleid;
    }

    public String getRolename() {
        return rolename;
    }

    @Override
    public void selectedCollege(String id, String name) {
        this.collegeid = id;
        this.collegename = name;
    }

    public String getCollegeid() {
        return collegeid;
    }

    public String getCollegename() {
        return collegename;
    }

    public int getSpinnerPos() {
        return spinnerPos;
    }

    public void setSpinnerPos(int spinnerPos) {
        this.spinnerPos = spinnerPos;
    }

    public AdapterView getAdapterView() {
        return adapterView;
    }

    public void setAdapterView(AdapterView adapterView) {
        this.adapterView = adapterView;
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

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public boolean isValidEmail() {
        return validEmail;
    }

    public void setValidEmail(boolean validEmail) {
        this.validEmail = validEmail;
    }

    public boolean isNoListedClg() {
        return noListedClg;
    }

    public void setNoListedClg(boolean noListedClg) {
        this.noListedClg = noListedClg;
    }

    public boolean isUserExists() {
        return userExists;
    }

    public void setUserExists(boolean userExists) {
        this.userExists = userExists;
    }

    @Override
    public void setGroupInfo(String groupId, String groupName) {
        this.groupid = groupId;
        this.groupname = groupName;
    }

    public String getGroupid() {
        return groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public Integer getMinmembers() {
        return minmembers;
    }

    public void setMinmembers(Integer minmembers) {
        this.minmembers = minmembers;
    }

    public Integer getMaxmembers() {
        return maxmembers;
    }

    public void setMaxmembers(Integer maxmembers) {
        this.maxmembers = maxmembers;
    }


    public Integer getVisibleMemberCount() {
        return visibleMemberCount;
    }

    public void setVisibleMemberCount(Integer visibleMemberCount) {
        this.visibleMemberCount = visibleMemberCount;
    }

    public Integer getViewPagerPos() {
        return viewPagerPos;
    }

    public void setViewPagerPos(Integer viewPagerPos) {
        this.viewPagerPos = viewPagerPos;
    }

    public RegistrationAdapter getRegistrationAdapter() {
        return registrationAdapter;
    }

    public void setRegistrationAdapter(RegistrationAdapter registrationAdapter) {
        this.registrationAdapter = registrationAdapter;
    }


    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public boolean isLeaderRegFlag() {
        return leaderRegFlag;
    }

    public void setLeaderRegFlag(boolean leaderRegFlag) {
        this.leaderRegFlag = leaderRegFlag;
    }

    public boolean isRegFlag() {
        return regFlag;
    }

    public void setRegFlag(boolean regFlag) {
        this.regFlag = regFlag;
    }

}
