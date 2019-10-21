package nilay.android.eventhook.model;

public class UserLog {
    private String log_id = "";
    private String user_id = "";
    private String login_date = "";
    private String login_time = "";
    private String logout_time = "";

    public UserLog() {
    }

    public UserLog(String log_id, String user_id, String login_date, String login_time, String logout_time) {
        this.log_id = log_id;
        this.user_id = user_id;
        this.login_date = login_date;
        this.login_time = login_time;
        this.logout_time = logout_time;
    }

    public String getLog_id() {
        return log_id;
    }

    public void setLog_id(String log_id) {
        this.log_id = log_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLogin_date() {
        return login_date;
    }

    public void setLogin_date(String login_date) {
        this.login_date = login_date;
    }

    public String getLogin_time() {
        return login_time;
    }

    public void setLogin_time(String login_time) {
        this.login_time = login_time;
    }

    public String getLogout_time() {
        return logout_time;
    }

    public void setLogout_time(String logout_time) {
        this.logout_time = logout_time;
    }
}
