package nilay.android.eventhook.model;

public class CollegeAdmin {
    private String user_id = "";
    private String user_name = "";
    private String email_id = "";
    private String college_name = "";
    private String idCard_URL = "";
    private String valid_user = "0";

    public CollegeAdmin() {
    }

    public CollegeAdmin(String user_id, String user_name, String email_id, String college_name, String idCard_URL) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.email_id = email_id;
        this.college_name = college_name;
        this.idCard_URL = idCard_URL;
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

    public String getCollege_name() {
        return college_name;
    }

    public void setCollege_name(String college_name) {
        this.college_name = college_name;
    }

    public String getIdCard_URL() {
        return idCard_URL;
    }

    public void setIdCard_URL(String idCard_URL) {
        this.idCard_URL = idCard_URL;
    }

    public String getValid_user() {
        return valid_user;
    }

    public void setValid_user(String valid_user) {
        this.valid_user = valid_user;
    }
}
