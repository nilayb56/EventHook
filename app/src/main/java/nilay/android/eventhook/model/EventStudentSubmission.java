package nilay.android.eventhook.model;

public class EventStudentSubmission {

    private String submission_id = "";
    private String user_id = "";
    private String event_id = "";
    private String theme_id = "0";
    private String content_url = "";
    private Float ratings = 0f;

    public EventStudentSubmission() {
    }

    public EventStudentSubmission(String submission_id, String user_id, String event_id, String theme_id, String content_url) {
        this.submission_id = submission_id;
        this.user_id = user_id;
        this.event_id = event_id;
        this.theme_id = theme_id;
        this.content_url = content_url;
    }

    public EventStudentSubmission(String submission_id, String user_id, String event_id, String theme_id, String content_url, Float ratings) {
        this.submission_id = submission_id;
        this.user_id = user_id;
        this.event_id = event_id;
        this.theme_id = theme_id;
        this.content_url = content_url;
        this.ratings = ratings;
    }

    public String getSubmission_id() {
        return submission_id;
    }

    public void setSubmission_id(String submision_id) {
        this.submission_id = submision_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getTheme_id() {
        return theme_id;
    }

    public void setTheme_id(String theme_id) {
        this.theme_id = theme_id;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public Float getRatings() {
        return ratings;
    }

    public void setRatings(Float ratings) {
        this.ratings = ratings;
    }
}
