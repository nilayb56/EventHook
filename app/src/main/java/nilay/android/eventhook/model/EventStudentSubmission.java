package nilay.android.eventhook.model;

public class EventStudentSubmission {

    private String content_url = "";

    public EventStudentSubmission(String content_url) {
        this.content_url = content_url;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }
}
