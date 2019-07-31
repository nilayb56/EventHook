package nilay.android.eventhook.model;

public class Users {

    String user_id;
    String user_name;
    String email_id;
    String password;
    String role_id;
    String college_id;
    String temp;

    public Users() {
    }

    public Users(String user_id, String user_name, String email_id, String password, String role_id, String college_id, String temp) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.email_id = email_id;
        this.password = password;
        this.role_id = role_id;
        this.college_id = college_id;
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

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "Users{" +
                "user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                ", email_id='" + email_id + '\'' +
                ", password='" + password + '\'' +
                ", role_id='" + role_id + '\'' +
                ", college_id='" + college_id + '\'' +
                ", temp='" + temp + '\'' +
                '}';
    }
}
