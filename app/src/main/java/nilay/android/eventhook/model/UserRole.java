package nilay.android.eventhook.model;

public class UserRole {

    String role_id="";
    String role_name="";

    public UserRole() {
    }

    public UserRole(String role_id, String role_name) {
        this.role_id = role_id;
        this.role_name = role_name;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
}
