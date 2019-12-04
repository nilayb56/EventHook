package nilay.android.eventhook.model;

public class Attendance {
    private String groupid = "";
    private String groupname = "";
    private String userid = "";
    private String username = "";
    private String emailid = "";
    private String selected = "0";

    public Attendance(String groupid, String groupname, String userid, String username, String emailid) {
        this.groupid = groupid;
        this.groupname = groupname;
        this.userid = userid;
        this.username = username;
        this.emailid = emailid;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
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

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return groupname + "\n" + username + "\n" + emailid;
    }
}
