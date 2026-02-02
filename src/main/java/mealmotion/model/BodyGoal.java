package mealmotion.model;

public enum BodyGoal {
    LEAN("Lean"),
    BULK("Bulk"),
    TONE("Tone");

    private final String label;

    BodyGoal(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}

