package nilay.android.eventhook.model;

public class EventImageList {
    private String collegeName = "";
    private Event event;

    public EventImageList(String collegeName, Event event) {
        this.collegeName = collegeName;
        this.event = event;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
