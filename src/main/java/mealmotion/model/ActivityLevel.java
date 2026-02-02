package mealmotion.model;

public enum ActivityLevel {
    SEDENTARY("Sedentary (little/no exercise)", 1.2),
    LIGHT("Light (1-3 days/week)", 1.375),
    MODERATE("Moderate (3-5 days/week)", 1.55),
    VERY("Very Active (6-7 days/week)", 1.725),
    EXTRA("Extra Active (physical job + training)", 1.9);

    private final String label;
    private final double factor;

    ActivityLevel(String label, double factor) {
        this.label = label;
        this.factor = factor;
    }

    public String label() {
        return label;
    }

    public double factor() {
        return factor;
    }

    @Override
    public String toString() {
        return label;
    }
}

