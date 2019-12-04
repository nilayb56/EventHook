package nilay.android.eventhook.model;

public class College {
    private String college_id;
    private String college_name;
    private String college_dept = "";

    public College() {
    }

    public College(String college_id, String college_name) {
        this.college_id = college_id;
        this.college_name = college_name;
    }

    public College(String college_id, String college_name, String college_dept) {
        this.college_id = college_id;
        this.college_name = college_name;
        this.college_dept = college_dept;
    }

    public String getCollege_id() {
        return college_id;
    }

    public void setCollege_id(String college_id) {
        this.college_id = college_id;
    }

    public String getCollege_name() {
        return college_name;
    }

    public void setCollege_name(String college_name) {
        this.college_name = college_name;
    }

    public String getCollege_dept() {
        return college_dept;
    }

    public void setCollege_dept(String college_dept) {
        this.college_dept = college_dept;
    }

    @Override
    public String toString() {
        return college_name;
    }
}
