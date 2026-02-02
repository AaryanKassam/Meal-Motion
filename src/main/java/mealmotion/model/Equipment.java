package mealmotion.model;

public enum Equipment {
    NONE("No equipment (bodyweight)"),
    DUMBBELLS("Dumbbells / bands"),
    FULL_GYM("Full gym access");

    private final String label;

    Equipment(String label) {
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

