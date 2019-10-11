package nilay.android.eventhook.model;

public class Users {

    private String user_id;
    private String user_name;
    private String email_id;
    private String password;
    private String role_id;
    private String college_id;
    private String reg_date;
    private String temp;

    public Users() {
    }

    public Users(String user_id, String user_name, String email_id, String password, String role_id, String college_id, String reg_date, String temp) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.email_id = email_id;
        this.password = password;
        this.role_id = role_id;
        this.college_id = college_id;
        this.reg_date = reg_date;
        this.temp = temp;
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

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public String getCollege_id() {
        return college_id;
    }

    public void setCollege_id(String college_id) {
        this.college_id = college_id;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return user_name + "\n("+email_id+")";
    }
}
