package nilay.android.eventhook.model;

public class EventResult {
    private String winner_id = "";
    private String winner_name = "";
    private String rank = "";

    public EventResult() {
    }

    public EventResult(String winner_id, String winner_name, String rank) {
        this.winner_id = winner_id;
        this.winner_name = winner_name;
        this.rank = rank;
    }

    public String getWinner_id() {
        return winner_id;
    }

    public void setWinner_id(String winner_id) {
        this.winner_id = winner_id;
    }

    public String getWinner_name() {
        return winner_name;
    }

    public void setWinner_name(String winner_name) {
        this.winner_name = winner_name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
