package nilay.android.eventhook.model;

public class StudentRatingRecord {
    private Integer isRatings_given = 0;
    private Float ratings = 0f;

    public StudentRatingRecord(Integer isRatings_given, Float ratings) {
        this.isRatings_given = isRatings_given;
        this.ratings = ratings;
    }

    public Integer getIsRatings_given() {
        return isRatings_given;
    }

    public void setIsRatings_given(Integer isRatings_given) {
        this.isRatings_given = isRatings_given;
    }

    public Float getRatings() {
        return ratings;
    }

    public void setRatings(Float ratings) {
        this.ratings = ratings;
    }
}
