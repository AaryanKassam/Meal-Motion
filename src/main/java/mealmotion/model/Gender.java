package mealmotion.model;

public enum Gender {
    MALE("Male", 5),
    FEMALE("Female", -161),
    OTHER("Other", 0);

    private final String label;
    private final int mifflinStJeorConstant;

    Gender(String label, int mifflinStJeorConstant) {
        this.label = label;
        this.mifflinStJeorConstant = mifflinStJeorConstant;
    }

    public String label() {
        return label;
    }

    public int mifflinStJeorConstant() {
        return mifflinStJeorConstant;
    }

    @Override
    public String toString() {
        return label;
    }
}

