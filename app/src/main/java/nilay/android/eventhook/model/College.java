package nilay.android.eventhook.model;

public class College {
    String college_id;
    String college_name;

    public College() {
    }

    public College(String college_id, String college_name) {
        this.college_id = college_id;
        this.college_name = college_name;
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

    @Override
    public String toString() {
        return "College{" +
                "college_id='" + college_id + '\'' +
                ", college_name='" + college_name + '\'' +
                '}';
    }
}
