package nilay.android.eventhook.model;

public class EventTheme {
    private String event_id = "";
    private String theme_id = "";
    private String theme_name = "";

    public EventTheme() {
    }

    public EventTheme(String event_id, String theme_id, String theme_name) {
        this.event_id = event_id;
        this.theme_id = theme_id;
        this.theme_name = theme_name;
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

    public String getTheme_name() {
        return theme_name;
    }

    public void setTheme_name(String theme_name) {
        this.theme_name = theme_name;
    }

    @Override
    public String toString() {
        return theme_name;
    }
}
