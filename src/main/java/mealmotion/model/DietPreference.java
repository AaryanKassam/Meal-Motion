package mealmotion.model;

public enum DietPreference {
    NONE("None"),
    HALAL("Halal"),
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    GLUTEN_FREE("Gluten-Free");

    private final String label;

    DietPreference(String label) {
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

